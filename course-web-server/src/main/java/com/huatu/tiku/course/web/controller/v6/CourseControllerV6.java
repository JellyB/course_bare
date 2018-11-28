package com.huatu.tiku.course.web.controller.v6;

import com.huatu.springboot.web.version.mapping.annotation.ApiVersion;
import com.huatu.tiku.course.bean.NetSchoolResponse;
import com.huatu.tiku.course.netschool.api.v6.CourseServiceV6;
import com.huatu.tiku.course.spring.conf.aspect.mapParam.LocalMapParam;
import com.huatu.tiku.course.spring.conf.aspect.mapParam.LocalMapParamHandler;
import com.huatu.tiku.course.util.ResponseUtil;
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
     * 日志详情
     * @param date
     * @param id
     * @param page
     * @param pageSize
     * @return
     */
    @LocalMapParam
    @GetMapping(value = "calendarDetail")
    public Object obtainCalendarDetail(@RequestParam(value = "date") String date,
                                       @RequestParam(value = "id") String id,
                                       @RequestParam(value = "page", defaultValue = "1") int page,
                                       @RequestParam(value = "pageSize", defaultValue = "30") int pageSize){
        Map<String,Object> params = LocalMapParamHandler.get();
        NetSchoolResponse netSchoolResponse = courseService.calendarDetail(params);
        return ResponseUtil.build(netSchoolResponse);
    }



}
