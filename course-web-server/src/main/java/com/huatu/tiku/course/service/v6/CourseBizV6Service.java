package com.huatu.tiku.course.service.v6;

import com.alibaba.fastjson.JSONObject;
import com.huatu.common.utils.web.RequestUtil;
import com.huatu.springboot.degrade.core.Degrade;
import com.huatu.tiku.course.bean.CourseListV3DTO;
import com.huatu.tiku.course.bean.NetSchoolResponse;
import com.huatu.tiku.course.netschool.api.fall.CourseServiceV6FallBack;
import com.huatu.tiku.course.netschool.api.fall.UserCourseServiceV6FallBack;
import com.huatu.tiku.course.netschool.api.v6.CourseServiceV6;
import com.huatu.tiku.course.netschool.api.v6.UserCourseServiceV6;
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
 * 描述：
 *
 * @author biguodong
 * Create time 2019-01-16 下午1:46
 **/
@Service
@Slf4j
public class CourseBizV6Service {

    @Autowired
    private CourseServiceV6 courseService;

    @Autowired
    private UserCourseServiceV6 userCourseService;

    @Autowired
    private CourseServiceV6FallBack courseServiceV6FallBack;

    @Autowired
    private UserCourseServiceV6FallBack userCourseServiceV6FallBack;

    @Resource(name = "redisTemplate")
    private ValueOperations valueOperations;


    /**
     * 课程日历详情接口
     * @param params
     * @return
     */
    @Degrade(key = "calendarDetail", name = "课程日历详情")
    public Object calendarDetail(Map<String,Object> params){
        String cacheKey = CourseCacheKey.calendarDetailV6(RequestUtil.getParamSign(params));
        NetSchoolResponse netSchoolResponse = (NetSchoolResponse) valueOperations.get(cacheKey);
        if (null == netSchoolResponse) {
            netSchoolResponse = courseService.calendarDetail(params);
            if (null != netSchoolResponse && null != netSchoolResponse.getData()) {
                valueOperations.set(cacheKey, netSchoolResponse, 1, TimeUnit.MINUTES);
                courseServiceV6FallBack.setCalendarDetailStaticData(params, netSchoolResponse);
            }
        }
        return ResponseUtil.build(netSchoolResponse);
    }

    /**
     * 我的学习日历接口
     * @param params
     * @return
     */
    @Degrade(key = "obtainLearnCalender", name = "我的学习日历")
    public Object obtainLearnCalender(Map<String,Object> params){
        String cacheKey = CourseCacheKey.calendarLearnV6(RequestUtil.getParamSign(params));
        NetSchoolResponse netSchoolResponse =  (NetSchoolResponse)valueOperations.get(cacheKey);
        if(null == netSchoolResponse){
            netSchoolResponse = userCourseService.obtainLearnCalender(params);
            if(null != netSchoolResponse && null != netSchoolResponse.getData()){
                valueOperations.set(cacheKey, netSchoolResponse, 1, TimeUnit.MINUTES);
                userCourseServiceV6FallBack.setCalendarLearnStaticData(params, netSchoolResponse);
            }
        }
        return ResponseUtil.build(netSchoolResponse);
    }


    /**
     * 我的课程接口
     * @param params
     * @return
     */
    @Degrade(key = "obtainMineCourses", name = "我的课程")
    public Object obtainMineCourses(Map<String,Object> params){
        try{
            String cacheKey = CourseCacheKey.courseMineV6(RequestUtil.getParamSign(params));
            NetSchoolResponse netSchoolResponse = (NetSchoolResponse) valueOperations.get(cacheKey);
            if(null == netSchoolResponse){
                netSchoolResponse = userCourseService.obtainMineCourses(params);
                if(null != netSchoolResponse && null != netSchoolResponse.getData()){
                    valueOperations.set(cacheKey, netSchoolResponse, 1, TimeUnit.MINUTES);
                    userCourseServiceV6FallBack.setCourseMineStaticData(params, netSchoolResponse);
                }
            }
            return ResponseUtil.build(netSchoolResponse);
        }catch (Exception e){
            log.error("obtain mine course caught an unexpected error:{}", e);
            return ResponseUtil.build(NetSchoolResponse.newInstance(null));
        }

    }

    /**
     * 我的课程降级方法
     * @param params
     * @return
     */
    public Object obtainMineCoursesDegrade(Map<String,Object> params){
        log.warn("obtain mine courses from degrade...");
        NetSchoolResponse netSchoolResponse = userCourseServiceV6FallBack.obtainMineCourses(params);
        /*if (null == netSchoolResponse.getData()) {
            netSchoolResponse = courseService.calendarDetail(params);
            if(null != netSchoolResponse && null != netSchoolResponse.getData()){
                userCourseServiceV6FallBack.setCourseMineStaticData(params, netSchoolResponse);
            }
        }*/
        log.warn("obtainMineCoursesDegrade.data:{}", JSONObject.toJSONString(netSchoolResponse));
        return ResponseUtil.build(netSchoolResponse);
    }


    /**
     * 我的学习日历降级接口
     * @param params
     * @return
     */
    public Object obtainLearnCalenderDegrade(Map<String,Object> params){
        log.warn("obtain learn calender from degrade...");
        NetSchoolResponse netSchoolResponse = userCourseServiceV6FallBack.obtainLearnCalender(params);
        /*if (null == netSchoolResponse.getData()) {
            netSchoolResponse = userCourseService.obtainLearnCalender(params);
            if(null != netSchoolResponse && null != netSchoolResponse.getData()){
                userCourseServiceV6FallBack.setCalendarLearnStaticData(params, netSchoolResponse);
            }
        }*/
        log.warn("obtainLearnCalenderDegrade.data:{}", JSONObject.toJSONString(netSchoolResponse));
        return ResponseUtil.build(netSchoolResponse);
    }

    /**
     * 课程日历详情接口降级接口
     * @param params
     * @return
     */
    public Object calendarDetailDegrade(Map<String,Object> params){
        log.warn("obtain calendar detail from degrade...");
        NetSchoolResponse netSchoolResponse = courseServiceV6FallBack.calendarDetail(params);
        /*if (null == netSchoolResponse.getData()) {
            netSchoolResponse = courseService.calendarDetail(params);
            if(null != netSchoolResponse && null != netSchoolResponse.getData()){
                courseServiceV6FallBack.setCalendarDetailStaticData(params, netSchoolResponse);
            }
        }*/
        log.warn("calendarDetailDegrade.data:{}", JSONObject.toJSONString(netSchoolResponse));
        return ResponseUtil.build(netSchoolResponse);
    }
}
