package com.huatu.tiku.course.service;

import com.google.common.primitives.Ints;
import com.huatu.common.exception.BizException;
import com.huatu.common.utils.date.DateFormatUtil;
import com.huatu.common.utils.date.DateUtil;
import com.huatu.common.utils.date.TimeMark;
import com.huatu.common.utils.date.TimestampUtil;
import com.huatu.tiku.course.bean.CourseDetailV2DTO;
import com.huatu.tiku.course.bean.CourseDetailV3DTO;
import com.huatu.tiku.course.bean.CourseListV2DTO;
import com.huatu.tiku.course.bean.CourseListV3DTO;
import com.huatu.tiku.course.netschool.api.CourseServiceV1;
import com.huatu.tiku.course.util.ResponseUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.concurrent.ListenableFuture;
import org.springframework.util.concurrent.ListenableFutureCallback;

import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;

/**
 * @author hanchao
 * @date 2017/9/12 21:42
 */
@Service
@Slf4j
public class CourseBizService {
    @Autowired
    private CourseServiceV1 courseServiceV1;

    @Autowired
    private CourseAsyncService courseAsyncService;


    /**
     * 获取直播列表 v3
     * @param username
     * @param params
     * @return
     * @throws ExecutionException
     * @throws InterruptedException
     * @throws BizException
     */
    public CourseListV3DTO getCourseListV3(String username,Map<String,Object> params) throws ExecutionException, InterruptedException, BizException {
        TimeMark timeMark = TimeMark.newInstance();
        ListenableFuture<CourseListV3DTO> courseListFuture = courseAsyncService.getCourseListV3(params);
        ListenableFuture<Set<Integer>> userBuyFuture = courseAsyncService.getUserBuy(username);

        CourseListV3DTO courseList = courseListFuture.get();
        Set<Integer> userBuy = userBuyFuture.get();
        log.info(">>>>>>>>> courseListV3: concurent request complete,used {} mills,total cost {} mills...",timeMark.mills(),timeMark.totalMills());

        if(courseList == null){
            throw new BizException(ResponseUtil.ERROR_NULL_RESPONSE);
        }

        if(CollectionUtils.isNotEmpty(courseList.getResult())){
            for (CourseListV3DTO.CourseInfo courseInfo : courseList.getResult()) {
                courseInfo.setIsBuy(userBuy.contains(courseInfo.getRid())?1:0);//设置是否购买的状态
            }
        }
        log.info(">>>>>>>>> courseListV3: build response data complete,used {} mills,total cost {} mills...",timeMark.mills(),timeMark.totalMills());
        return courseList;
    }


    /**
     * 获取课程详情 v3
     * @param courseId
     * @param username
     * @return
     * @throws InterruptedException
     * @throws ExecutionException
     * @throws BizException
     */
    public CourseDetailV3DTO getCourseDetailV3(int courseId, String username) throws InterruptedException, ExecutionException, BizException {
        TimeMark timeMark = TimeMark.newInstance();

        ListenableFuture<CourseDetailV3DTO> courseDetailFuture = courseAsyncService.getCourseDetailV3(courseId);
        ListenableFuture<Set<Integer>> userBuyFuture = courseAsyncService.getUserBuy(username);
        ListenableFuture<Map<Integer, Integer>> courseLimitFuture = courseAsyncService.getCourseLimit(courseId);

        CountDownLatch countDownLatch = new CountDownLatch(3);
        courseDetailFuture.addCallback(new RequestCountDownFutureCallback(countDownLatch));
        userBuyFuture.addCallback(new RequestCountDownFutureCallback(countDownLatch));
        courseLimitFuture.addCallback(new RequestCountDownFutureCallback(countDownLatch));

        countDownLatch.await(10, TimeUnit.SECONDS);//最多10秒等待

        log.info(">>>>>>>>> courseDetail: concurent request complete,used {} mills...",timeMark.millsWithMark());

        CourseDetailV3DTO courseDetail = courseDetailFuture.get();
        if(courseDetail == null){
            throw new BizException(ResponseUtil.ERROR_NULL_RESPONSE);
        }
        CourseDetailV3DTO.ClassInfo classInfo = courseDetail.getClassInfo();

        int current = TimestampUtil.currentUnixTimeStamp();
        int totalNum = Integer.MAX_VALUE;//限制人数
        if(classInfo.getLimitUserCount() != null && classInfo.getLimitUserCount() != 0){
            totalNum = classInfo.getLimitUserCount();
        }

        int buyNum = 0;
        Map<Integer, Integer> courseLimit = courseLimitFuture.get();
        if(courseLimit.containsKey(courseId)){
            buyNum = courseLimit.get(courseId);
        }

        classInfo.setTotal(buyNum);

        //获取开始时间和结束时间
        int startTime = Optional.ofNullable(Ints.tryParse(classInfo.getStartTime())).orElse((int) (DateUtil.addDay(-1).getTime()/1000));
        int stopTime = Optional.ofNullable(Ints.tryParse(classInfo.getStopTime())).orElse((int) (DateUtil.addDay(1).getTime()/1000));

        int limitStatus = 0;

        if(startTime>current){
            classInfo.setLimitTimes(startTime- current);
            limitStatus = 2;//未开始
        }else if(current>=startTime && current< stopTime){
            classInfo.setLimitTimes(stopTime-current);
            if(totalNum > buyNum){
                limitStatus = 3;//抢购中，未售罄
            }else{
                limitStatus = 4;
            }
        }else{
            classInfo.setLimitTimes(0);
            if(totalNum <= buyNum){
                limitStatus = 8;//抢购结束
            }else{
                limitStatus = 7;
            }
        }

        classInfo.setLimitStatus(limitStatus);
        classInfo.setStartTime(DateFormatUtil.DEFAULT_FORMAT.format(startTime*1000L));
        classInfo.setStopTime(DateFormatUtil.DEFAULT_FORMAT.format(stopTime*1000L));

        log.info(">>>>>>>>> courseDetail: build response data complete,used {} mills,total cost {} mills...",timeMark.mills(),timeMark.totalMills());

        return courseDetail;
    }



    /**
     * 老版本获取课程列表
     * @param username
     * @param params
     * @return
     * @throws ExecutionException
     * @throws InterruptedException
     * @throws BizException
     */
    public CourseListV2DTO getCourseListV2(String username,Map<String,Object> params) throws ExecutionException, InterruptedException, BizException {
        TimeMark timeMark = TimeMark.newInstance();
        ListenableFuture<CourseListV2DTO> courseListFuture = courseAsyncService.getCourseListV2(params);
        ListenableFuture<Set<Integer>> userBuyFuture = courseAsyncService.getUserBuy(username);

        CourseListV2DTO courseList = courseListFuture.get();
        Set<Integer> userBuy = userBuyFuture.get();
        log.info(">>>>>>>>> courseList: concurent request complete,used {} mills,total cost {} mills...",timeMark.mills(),timeMark.totalMills());
        if(courseList == null){
            throw new BizException(ResponseUtil.ERROR_NULL_RESPONSE);
        }
        if(CollectionUtils.isNotEmpty(courseList.getResult())){
            for (CourseListV2DTO.CourseInfo courseInfo : courseList.getResult()) {
                courseInfo.setIsBuy(userBuy.contains(courseInfo.getRid())?1:0);//设置是否购买的状态
            }
        }
        log.info(">>>>>>>>> courseList: build response data complete,used {} mills,total cost {} mills...",timeMark.mills(),timeMark.totalMills());
        return courseList;
    }

    /**
     * 老版本获取课程详情
     * @param courseId
     * @param username
     * @return
     * @throws InterruptedException
     * @throws ExecutionException
     * @throws BizException
     */
    public CourseDetailV2DTO getCourseDetailV2(int courseId,String username) throws InterruptedException, ExecutionException, BizException {
        TimeMark timeMark = TimeMark.newInstance();

        ListenableFuture<CourseDetailV2DTO> courseDetailFuture = courseAsyncService.getCourseDetailV2(courseId);
        ListenableFuture<Set<Integer>> userBuyFuture = courseAsyncService.getUserBuy(username);
        ListenableFuture<Map<Integer, Integer>> courseLimitFuture = courseAsyncService.getCourseLimit(courseId);

        CountDownLatch countDownLatch = new CountDownLatch(3);
        courseDetailFuture.addCallback(new RequestCountDownFutureCallback(countDownLatch));
        userBuyFuture.addCallback(new RequestCountDownFutureCallback(countDownLatch));
        courseLimitFuture.addCallback(new RequestCountDownFutureCallback(countDownLatch));

        countDownLatch.await(10, TimeUnit.SECONDS);//最多10秒等待

        log.info(">>>>>>>>> courseDetail: concurent request complete,used {} mills...",timeMark.millsWithMark());

        CourseDetailV2DTO courseDetail = courseDetailFuture.get();
        if(courseDetail == null){
            throw new BizException(ResponseUtil.ERROR_NULL_RESPONSE);
        }
        CourseDetailV2DTO.ClassInfo classInfo = courseDetail.getClassInfo();

        int current = TimestampUtil.currentUnixTimeStamp();
        int totalNum = Integer.MAX_VALUE;//限制人数
        if(classInfo.getLimitUserCount() != null && classInfo.getLimitUserCount() != 0){
            totalNum = classInfo.getLimitUserCount();
        }

        int buyNum = 0;
        Map<Integer, Integer> courseLimit = courseLimitFuture.get();
        if(courseLimit.containsKey(courseId)){
            buyNum = courseLimit.get(courseId);
        }

        classInfo.setTotal(buyNum);

        int startTime = Optional.ofNullable(Ints.tryParse(classInfo.getStartTime())).orElse((int) (DateUtil.addDay(-1).getTime()/1000));
        int stopTime = Optional.ofNullable(Ints.tryParse(classInfo.getStopTime())).orElse((int) (DateUtil.addDay(1).getTime()/1000));

        int limitStatus = 0;
        if(startTime>current){
            classInfo.setLimitTimes(startTime- current);
            limitStatus = 2;//未开始
        }else if(current>=startTime && current< stopTime){
            classInfo.setLimitTimes(stopTime-current);
            if(totalNum > buyNum){
                limitStatus = 3;//抢购中，未售罄
            }else{
                limitStatus = 4;
            }
        }else{
            classInfo.setLimitTimes(0);
            if(totalNum <= buyNum){
                limitStatus = 8;//抢购结束
            }else{
                limitStatus = 7;
            }
        }
        classInfo.setLimitStatus(limitStatus);
        classInfo.setStartTime(DateFormatUtil.DEFAULT_FORMAT.format(startTime*1000L));
        classInfo.setStopTime(DateFormatUtil.DEFAULT_FORMAT.format(stopTime*1000L));
        courseDetail.setTeacher_informatioin(classInfo);
        courseDetail.setClassInfo(null);

        log.info(">>>>>>>>> courseDetail: build response data complete,used {} mills,total cost {} mills...",timeMark.mills(),timeMark.totalMills());

        return courseDetail;
    }




    @Slf4j
    private static class RequestCountDownFutureCallback implements ListenableFutureCallback {
        private CountDownLatch countDownLatch;

        public RequestCountDownFutureCallback(CountDownLatch countDownLatch){
            this.countDownLatch = countDownLatch;
        }

        @Override
        public void onFailure(Throwable ex) {
            log.error("request async error...",ex);
            countDownLatch.countDown();
        }

        @Override
        public void onSuccess(Object result) {
            countDownLatch.countDown();
        }
    }
}
