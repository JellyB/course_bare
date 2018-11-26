package com.huatu.tiku.course.web.controller.v6;

import com.huatu.springboot.web.version.mapping.annotation.ApiVersion;
import com.huatu.tiku.common.bean.user.UserSession;
import com.huatu.tiku.course.netschool.api.v6.UserCourseServiceV6;
import com.huatu.tiku.course.spring.conf.aspect.mapParam.LocalMapParam;
import com.huatu.tiku.course.spring.conf.aspect.mapParam.LocalMapParamHandler;
import com.huatu.tiku.springboot.users.support.Token;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

/**
 * 描述：我的课程接口
 *
 * @author biguodong
 * Create time 2018-11-26 下午5:28
 **/

@Slf4j
@RestController
@RequestMapping("/my")
@ApiVersion("v6")
public class UserCourseControllerV6 {


    @Autowired
    private UserCourseServiceV6 userCourseService;


    /**
     * 获取我的学习日历接口
     * @param userSession
     * @param type
     * @return
     */
    @LocalMapParam
    @GetMapping(value = "learnCalendar")
    public Object obtainLearnCalendar(@Token UserSession userSession,
                                      @RequestParam(value = "type") String type){
        Map<String,Object> params = LocalMapParamHandler.get();
        return userCourseService.obtainLearnCalender(params);
    }

    /**
     * 获取已过期课程
     * @param userSession
     * @param page
     * @param pageSize
     * @return
     */
    @LocalMapParam
    @GetMapping(value = "expiredCourses")
    public Object obtainExpiredCourses(@Token UserSession userSession,
                                       @RequestParam(value = "page", defaultValue = "1") int page,
                                       @RequestParam(value = "pageSize", defaultValue = "20") int pageSize){

        Map<String,Object> params = LocalMapParamHandler.get();
        return userCourseService.obtainExpiredCourses(params);
    }


    /**
     * 我的课程筛选列表
     * @param userSession
     * @return
     */
    @LocalMapParam
    @GetMapping(value = "filteredCourses")
    public Object obtainFilteredCourses(@Token UserSession userSession){
        Map<String,Object> params = LocalMapParamHandler.get();
        return userCourseService.obtainCourseFilterList(params);
    }

    /**
     * 我的课程接口
     * @param userSession
     * @param examStatus
     * @param keyWord
     * @param priceStatus
     * @param recentlyStudy
     * @param studyStatus
     * @param teacherId
     * @param page
     * @param pageSize
     * @return
     */
    @LocalMapParam
    @GetMapping(value = "courses")
    public Object obtainMineCourses(
                                    @Token UserSession userSession,
                                    @RequestParam(value = "examStatus") String examStatus,
                                    @RequestParam(value = "keyWord") String keyWord,
                                    @RequestParam(value = "priceStatus") String priceStatus,
                                    @RequestParam(value = "recentlyStudy", defaultValue = "0") int recentlyStudy,
                                    @RequestParam(value = "studyStatus") String studyStatus,
                                    @RequestParam(value = "teacherId") String teacherId,
                                    @RequestParam(value = "page", defaultValue = "1") int page,
                                    @RequestParam(value = "pageSize", defaultValue = "20") int pageSize){
        Map<String,Object> params = LocalMapParamHandler.get();
        return userCourseService.obtainMineCourses(params);
    }

}
