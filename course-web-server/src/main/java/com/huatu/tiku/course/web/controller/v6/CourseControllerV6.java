package com.huatu.tiku.course.web.controller.v6;

import com.huatu.springboot.web.version.mapping.annotation.ApiVersion;
import com.huatu.tiku.common.bean.user.UserSession;
import com.huatu.tiku.course.bean.NetSchoolResponse;
import com.huatu.tiku.course.netschool.api.v6.CourseServiceV6;
import com.huatu.tiku.course.spring.conf.aspect.mapParam.LocalMapParam;
import com.huatu.tiku.course.spring.conf.aspect.mapParam.LocalMapParamHandler;
import com.huatu.tiku.course.util.ResponseUtil;
import com.huatu.tiku.springboot.users.support.Token;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

/**
 * 描述：课程接口v6
 *
 * @author biguodong
 * Create time 2018-11-26 下午4:22
 **/

@Slf4j
@RestController
@RequestMapping("/courses")
@ApiVersion("v6")
public class CourseControllerV6 {

    @Autowired
    private CourseServiceV6 courseService;


    /**
     * App课程列表
     * @param cateId
     * @return
     */
    @LocalMapParam
    @GetMapping(value = "list")
    public Object obtainCourseList(@RequestParam(value = "cateId") String cateId){
        Map<String,Object> params = LocalMapParamHandler.get();
        NetSchoolResponse netSchoolResponse = courseService.obtainCourseList(params);
        return ResponseUtil.build(netSchoolResponse);
    }


    /**
     * 日历详情
     * @param userSession
     * @param cv
     * @param date
     * @param id
     * @param page
     * @param pageSize
     * @return
     */
    @LocalMapParam
    @GetMapping(value = "calendarDetail")
    public Object obtainCalendarDetail(
            @Token UserSession userSession,
            @RequestHeader(value = "cv") String cv,
            @RequestHeader(value = "terminal") int terminal,
            @RequestParam(value = "date") String date,
            @RequestParam(value = "id", required = false) String id,
            @RequestParam(value = "page", defaultValue = "1") int page,
            @RequestParam(value = "pageSize", defaultValue = "20") int pageSize){
        Map<String,Object> params = LocalMapParamHandler.get();
        NetSchoolResponse netSchoolResponse = courseService.calendarDetail(params);
        return ResponseUtil.build(netSchoolResponse);
    }


    /**
     * 课程分类详情
     * @param cateId
     * @param cv
     * @param terminal
     * @param page
     * @param typeId
     * @return
     */
    @LocalMapParam
    @GetMapping(value = "typeDetail")
    public Object courseTypeDetail(@RequestHeader(value = "cv") String cv,
                                   @RequestHeader(value = "terminal") int terminal,
                                   @RequestParam(value = "cateId") int cateId,
                                   @RequestParam(value = "page", defaultValue = "1") int page,
                                   @RequestParam(value = "typeId") int typeId){
        Map<String,Object> params = LocalMapParamHandler.get();
        NetSchoolResponse netSchoolResponse = courseService.courseTypeDetail(params);
        return ResponseUtil.build(netSchoolResponse);
    }
    /**
     * 课程搜索接口
     * @param keyWord
     * @param page
     * @return
     */
    @LocalMapParam
    @GetMapping(value = "search")
    public  Object searchCourses(@RequestParam(value = "keyWord") String keyWord,
                                 @RequestParam(value = "cateId") int cateId,
                                 @RequestParam(value = "page", defaultValue = "1") int page){
        Map<String,Object> params = LocalMapParamHandler.get();
        NetSchoolResponse netSchoolResponse = courseService.searchCourses(params);
        return ResponseUtil.build(netSchoolResponse);
    }


    /**
     * 合集课程列表
     * @param collectId
     * @return
     */
    @LocalMapParam
    @GetMapping(value = "collectDetail")
    public Object collectDetail(@RequestParam(value = "collectId") long collectId,
                                @RequestParam(value = "page", defaultValue = "1") int page){
        Map<String, Object> params = LocalMapParamHandler.get();
        NetSchoolResponse netSchoolResponse = courseService.collectDetail(params);
        return ResponseUtil.build(netSchoolResponse);
    }

}
