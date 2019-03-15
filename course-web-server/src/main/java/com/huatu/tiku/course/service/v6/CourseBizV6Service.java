package com.huatu.tiku.course.service.v6;

import com.alibaba.fastjson.JSONObject;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.huatu.common.ErrorResult;
import com.huatu.common.Result;
import com.huatu.common.exception.BizException;
import com.huatu.common.utils.web.RequestUtil;
import com.huatu.springboot.degrade.core.Degrade;
import com.huatu.tiku.course.bean.NetSchoolResponse;
import com.huatu.tiku.course.netschool.api.fall.CourseServiceV6FallBack;
import com.huatu.tiku.course.netschool.api.fall.UserCourseServiceV6FallBack;
import com.huatu.tiku.course.netschool.api.v6.CourseServiceV6;
import com.huatu.tiku.course.netschool.api.v6.UserCourseServiceV6;
import com.huatu.tiku.course.service.v1.AccessLimitService;
import com.huatu.tiku.course.util.CourseCacheKey;
import com.huatu.tiku.course.util.ResponseUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.Map;
import java.util.Random;

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

    @Autowired
    private AccessLimitService accessLimitService;

    /**
     * 课程日历详情接口
     * @param params
     * @return
     */
    @Degrade(key = "calendarDetail", name = "课程日历详情")
    public Object calendarDetail(Map<String,Object> params){
        try{
            String cacheKey = CourseCacheKey.calendarDetailV6(RequestUtil.getParamSign(params));
            NetSchoolResponse netSchoolResponse = (NetSchoolResponse) valueOperations.get(cacheKey);
            if(null == netSchoolResponse){
                netSchoolResponse = courseService.calendarDetail(params);
                if (null != netSchoolResponse && null != netSchoolResponse.getData()) {
                    courseServiceV6FallBack.setCalendarDetailStaticData(params, netSchoolResponse);
                }
            }
            return ResponseUtil.build(netSchoolResponse);
        }catch (Exception e){
            log.error("calendarDetail caught an exception and return from fallBack:{}", e);
            NetSchoolResponse netSchoolResponse = courseServiceV6FallBack.calendarDetail(params);
            return ResponseUtil.build(netSchoolResponse);
        }
    }

    /**
     * 我的学习日历接口
     * @param params
     * @return
     */
    @Degrade(key = "obtainLearnCalender", name = "我的学习日历")
    public Object obtainLearnCalender(Map<String,Object> params){
        try{
            String cacheKey = CourseCacheKey.calendarLearnV6(RequestUtil.getParamSign(params));
            NetSchoolResponse netSchoolResponse =  (NetSchoolResponse)valueOperations.get(cacheKey);
            if(null == netSchoolResponse){
                netSchoolResponse = userCourseService.obtainLearnCalender(params);
                if(null != netSchoolResponse && null != netSchoolResponse.getData()){
                    userCourseServiceV6FallBack.setCalendarLearnStaticData(params, netSchoolResponse);
                }
            }
            return ResponseUtil.build(netSchoolResponse);
        }catch (Exception e){
            log.error("obtainLearnCalender caught an exception and return from fallBack:{}", e);
            NetSchoolResponse netSchoolResponse = userCourseServiceV6FallBack.obtainLearnCalender(params);
            return ResponseUtil.build(netSchoolResponse);
        }
    }


    /**
     * 我的课程接口
     * @param params
     * @return
     */
    @Degrade(key = "obtainMineCourses", name = "我的课程")
    public Object obtainMineCourses(Map<String,Object> params){
        try{
            NetSchoolResponse netSchoolResponse = userCourseService.obtainMineCourses(params);
            if(null != netSchoolResponse && null != netSchoolResponse.getData()){
                userCourseServiceV6FallBack.setCourseMineStaticData(params, netSchoolResponse);
            }
            return ResponseUtil.build(netSchoolResponse);
        }catch (Exception e){
            log.error("obtainMineCourses caught an exception and return from fallBack:{}", e);
            NetSchoolResponse netSchoolResponse = userCourseServiceV6FallBack.obtainMineCourses(params);
            return ResponseUtil.build(netSchoolResponse);
        }
    }

    /**
     * 我的课程降级方法
     * @param params
     * @return
     */
    public Object obtainMineCoursesDegradeBack(Map<String,Object> params){
        if(accessLimitService.tryAccess()){
            NetSchoolResponse netSchoolResponse = userCourseServiceV6FallBack.obtainMineCourses(params);
            log.warn("obtainMineCoursesDegrade.data:{}", JSONObject.toJSONString(netSchoolResponse));
            return ResponseUtil.build(netSchoolResponse);
        }else{
            return new NetSchoolResponse<>(Result.SUCCESS_CODE, "当前请求的人数过多，请在5分钟后重试", Lists.newArrayList());
        }
    }

    /**
     * 我的课程降级方法
     * @param params
     * @return
     */
    public Object obtainMineCoursesDegrade(Map<String,Object> params){
        /**
         * 方案 1. 返回空数组，app 列表页面提示去选课
         */
        log.info("获取我的课程信息 -- 降级:{}", params);
        return new NetSchoolResponse<>(Result.SUCCESS_CODE, "当前请求的人数过多，请在5分钟后重试", Lists.newArrayList());

        /**
         * 方案 2. 返回自定义异常
         */
        /*ErrorResult errorResult = ErrorResult.create(10000010, "当前请求的人数过多，请在5分钟后重试", Lists.newArrayList());
        throw new BizException(errorResult);*/

        /**
         * 方案 3. 自定义message
         */
       /* Map<String,Object> result = Maps.newHashMap();
        result.put("code", Result.SUCCESS_CODE);
        result.put("data", null);
        result.put("message", "当前请求的人数过多，请在5分钟后重试");
        return result;*/
    }


    /**
     * 我的学习日历降级接口
     * @param params
     * @return
     */
    public Object obtainLearnCalenderDegrade(Map<String,Object> params){
        NetSchoolResponse netSchoolResponse = userCourseServiceV6FallBack.obtainLearnCalender(params);
        log.warn("obtainLearnCalenderDegrade.data:{}", JSONObject.toJSONString(netSchoolResponse));
        return ResponseUtil.build(netSchoolResponse);
    }

    /**
     * 课程日历详情接口降级接口
     * @param params
     * @return
     */
    public Object calendarDetailDegrade(Map<String,Object> params){
        NetSchoolResponse netSchoolResponse = courseServiceV6FallBack.calendarDetail(params);
        log.warn("calendarDetailDegrade.data:{}", JSONObject.toJSONString(netSchoolResponse));
        return ResponseUtil.build(netSchoolResponse);
    }
}
