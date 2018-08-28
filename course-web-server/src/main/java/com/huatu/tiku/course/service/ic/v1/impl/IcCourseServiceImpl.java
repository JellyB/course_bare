package com.huatu.tiku.course.service.ic.v1.impl;

import com.google.common.collect.Lists;
import com.huatu.common.exception.BizException;
import com.huatu.tiku.course.bean.CourseDetailV3DTO;
import com.huatu.tiku.course.bean.NetSchoolResponse;
import com.huatu.tiku.course.ic.api.v1.IcUserCourseServiceV1;
import com.huatu.tiku.course.netschool.api.v5.CourseServiceV5;
import com.huatu.tiku.course.service.CourseAsyncService;
import com.huatu.tiku.course.service.CourseBizService;
import com.huatu.tiku.course.service.ic.v1.IcCourseService;
import com.huatu.tiku.course.service.ic.v1.async.IcCourseAsyncService;
import com.huatu.tiku.course.util.ResponseUtil;
import com.huatu.tiku.course.util.ZTKResponseUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.concurrent.ListenableFuture;
import org.springframework.util.concurrent.ListenableFutureCallback;

import java.util.*;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

/**
 * Created by lijun on 2018/7/18
 */
@Service
public class IcCourseServiceImpl implements IcCourseService {

    @Autowired
    private CourseServiceV5 courseService;

    @Autowired
    private CourseAsyncService courseAsyncService;

    @Autowired
    private IcCourseAsyncService icCourseAsyncService;

    @Autowired
    private IcUserCourseServiceV1 userCourseServiceV1;


    @Override
    public List<HashMap<String, Object>> icClassList(HashMap<String, Object> map) {
        NetSchoolResponse netSchoolResponse = courseService.icClassList(map);
        Object response = ResponseUtil.build(netSchoolResponse);
        // PHP添加了一层list
		if (null == response) {
			return Lists.newArrayList();
		} else {
			response = ((Map<String, Object>) response).get("list");
		}
        List<HashMap<String, Object>> result = (List<HashMap<String, Object>>) response;
        //修改 PHP 端数据
        String classIdList = result.stream()
                .map(course -> course.get("classId").toString())
                .collect(Collectors.joining(","));
        Object courseCountList = userCourseServiceV1.courseCountList(classIdList);
        Object build = ZTKResponseUtil.build(courseCountList);
        if (null != build) {
            HashMap<String, Object> buildResult = (HashMap<String, Object>) build;
            List<HashMap<String, Object>> list = result.stream()
                    //修改已购数量
                    .map(course -> {
                        Object count = buildResult.getOrDefault(course.get("classId"), 0);
                        course.put("count", count);
                        return course;
                    })
                    .collect(Collectors.toList());
            return list;
        }
        return result;
    }

    @Override
    public CourseDetailV3DTO getCourseDetail(int courseId, String userId) throws InterruptedException, ExecutionException {
        ListenableFuture<CourseDetailV3DTO> courseDetailV3 = courseAsyncService.getCourseDetailV3(courseId);
        ListenableFuture<Boolean> checkUserHasBuy = icCourseAsyncService.checkUserHasBuy(userId, courseId);
        ListenableFuture<Map<String, Integer>> courseCount = icCourseAsyncService.getCourseCount(String.valueOf(courseId));

        CountDownLatch countDownLatch = new CountDownLatch(3);

        courseDetailV3.addCallback(new RequestCountDownFutureCallback(countDownLatch));
        checkUserHasBuy.addCallback(new RequestCountDownFutureCallback(countDownLatch));
        courseCount.addCallback(new RequestCountDownFutureCallback(countDownLatch));

        countDownLatch.await(10, TimeUnit.SECONDS);//最多10秒等待

        CourseDetailV3DTO courseDetail = courseDetailV3.get();
        if (courseDetail == null) {
            throw new BizException(ResponseUtil.ERROR_PAGE_RESPONSE);
        }
        //详情
        CourseDetailV3DTO.ClassInfo classInfo = courseDetail.getClassInfo();
        //数量
        Map<String, Integer> courseCountMap = courseCount.get();
        Integer buyNum = courseCountMap.get(String.valueOf(courseId));
        //构建限购信息
        CourseBizService.buildCourseLimitStatus(classInfo, buyNum);
        //是否购买
        Boolean hasBuy = checkUserHasBuy.get();
        classInfo.setIsBuy(hasBuy ? 1 : 0);
        return courseDetail;
    }

    @Override
    public Object userBuyCourse(String userId) {
        Object userHasBuyList = userCourseServiceV1.userHasBuyList(userId);
        Object response = ZTKResponseUtil.build(userHasBuyList);
        if (null == response || ((Collection) response).size() == 0) {
            return Lists.newArrayList();
        }
        String hasBuyCourseIds = ((List<String>) response).stream().collect(Collectors.joining(","));
        NetSchoolResponse netSchoolResponse = courseService.courseInfoList(hasBuyCourseIds);
        return ResponseUtil.build(netSchoolResponse);
    }

    @Slf4j
    private static class RequestCountDownFutureCallback implements ListenableFutureCallback {
        private CountDownLatch countDownLatch;

        public RequestCountDownFutureCallback(CountDownLatch countDownLatch) {
            this.countDownLatch = countDownLatch;
        }

        @Override
        public void onFailure(Throwable ex) {
            log.error("request async error...", ex);
            countDownLatch.countDown();
        }

        @Override
        public void onSuccess(Object result) {
            countDownLatch.countDown();
        }
    }

}
