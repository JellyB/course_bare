package com.huatu.tiku.course.service.ic.v1.async;

import com.huatu.common.utils.collection.HashMapBuilder;
import com.huatu.tiku.course.ic.api.v1.IcUserCourseServiceV1;
import com.huatu.tiku.course.util.ZTKResponseUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.AsyncResult;
import org.springframework.stereotype.Component;
import org.springframework.util.concurrent.ListenableFuture;

import java.util.HashMap;
import java.util.Map;

/**
 * 面库 异步接口
 * Created by lijun on 2018/7/18
 */
@Slf4j
@Async
@Component
public class IcCourseAsyncService {

    @Autowired
    private IcUserCourseServiceV1 userCourseServiceV1;

    /**
     * 获取课程已购数量
     *
     * @param courseId 课程ID
     */
    @Async
    public ListenableFuture<Map<String, Integer>> getCourseCount(String courseId) {
        Object courseCountList = userCourseServiceV1.courseCountList(courseId);
        Object build = ZTKResponseUtil.build(courseCountList);
        if (null == build) {
            HashMap<String, Integer> hashMap = HashMapBuilder.<String, Integer>newBuilder()
                    .put(courseId, 0)
                    .build();
            return new AsyncResult(hashMap);
        } else {
            HashMap<String, Integer> result = (HashMap<String, Integer>) build;
            return new AsyncResult<>(result);
        }
    }

    /**
     * 判断 用户是否已经购买某课程
     *
     * @param userId 用户ID
     * @param courseId 课程ID
     * @return
     */
    @Async
    public ListenableFuture<Boolean> checkUserHasBuy(String userId, int courseId) {
        Object checkUserBuy = userCourseServiceV1.checkUserBuy(userId, courseId);
        Object build = ZTKResponseUtil.build(checkUserBuy);
        if (null == build) {
            return new AsyncResult<>(false);
        } else {
            Boolean hasBuy = (Boolean) build;
            return new AsyncResult<>(hasBuy);
        }
    }

}
