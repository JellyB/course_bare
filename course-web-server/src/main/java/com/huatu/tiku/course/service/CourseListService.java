package com.huatu.tiku.course.service;

import com.huatu.common.spring.cache.Cached;
import com.huatu.common.utils.concurrent.ConcurrentBizLock;
import com.huatu.springboot.degrade.core.Degrade;
import com.huatu.tiku.course.bean.CourseListV3DTO;
import com.huatu.tiku.course.bean.NetSchoolResponse;
import com.huatu.tiku.course.netschool.api.fall.CourseServiceV3Fallback;
import com.huatu.tiku.course.netschool.api.v3.CourseServiceV3;
import com.huatu.tiku.course.util.CourseCacheKey;
import com.huatu.tiku.course.util.ResponseUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.Map;
import java.util.concurrent.TimeUnit;

/**
 * @author hanchao
 * @date 2017/10/31 17:35
 */
@Service
@Slf4j
public class CourseListService {
    @Autowired
    private CourseServiceV3 courseServiceV3;

    @Resource(name = "redisTemplate")
    private ValueOperations valueOperations;

    @Autowired
    private CourseServiceV3Fallback courseServiceV3Fallback;
    @Autowired
    private PromoteBizService promoteBizService;

    /**
     * 异步获取课程列表 v3
     *
     * @param params
     * @return 会有null
     */

    @Cached(name = "课程列表v3",
            key = "T(com.huatu.tiku.course.util.CourseCacheKey).courseListV3(T(com.huatu.common.utils.web.RequestUtil).getParamSign(#map))",
            params = {@Cached.Param(name = "查询参数", value = "map", type = Map.class)})
    @Degrade(name = "课程列表v3", key = "courseListV3")
    public CourseListV3DTO getCourseListV3(Map<String, Object> params) {

        params.remove("username");
        String cacheKey = CourseCacheKey.courseListV3(com.huatu.common.utils.web.RequestUtil.getParamSign(params));
        CourseListV3DTO result = (CourseListV3DTO) valueOperations.get(cacheKey);
        if (result == null) {
            NetSchoolResponse response = courseServiceV3.findLiveList(params);
            result = ResponseUtil.build(response, CourseListV3DTO.class, false);
            if (result != null) {
                result.setCacheTimestamp(System.currentTimeMillis());
                valueOperations.set(cacheKey, result, 10, TimeUnit.SECONDS);
            }
            //非fallback获取到，设置到fallback缓存
            if (!result.isCache()) {
                courseServiceV3Fallback.setLiveList(params, response);
            }
        } else {
            result.setCache(true);
        }
        return result;
    }

    /**
     * 课程列表降级方法
     *
     * @param params
     * @return
     */
    public CourseListV3DTO getCourseListV3Degrade(Map<String, Object> params) {
        params.remove("username");

        NetSchoolResponse liveList = courseServiceV3Fallback.findLiveList(params);
        if (liveList.getCode() == NetSchoolResponse.DEFAULT_ERROR.getCode()) {
            String key = "_mock_live_list$" + com.huatu.common.utils.web.RequestUtil.getParamSign(params);
            if (ConcurrentBizLock.tryLock(key)) {
                try {
                    liveList = courseServiceV3.findLiveList(params);
                    courseServiceV3Fallback.setLiveList(params, liveList);
                } catch (Exception e) {
                    e.printStackTrace();
                } finally {
                    ConcurrentBizLock.releaseLock(key);
                }
            }
        }
        CourseListV3DTO result = ResponseUtil.build(liveList, CourseListV3DTO.class, false);
        if (result != null) {
            result.setCache(true);
        }
        return result;
    }
}
