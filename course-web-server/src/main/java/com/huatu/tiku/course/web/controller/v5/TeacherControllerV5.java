package com.huatu.tiku.course.web.controller.v5;

import com.huatu.springboot.web.version.mapping.annotation.ApiVersion;
import com.huatu.tiku.course.netschool.api.v5.TeacherServiceV5;
import com.huatu.tiku.course.spring.conf.aspect.mapParam.LocalMapParam;
import com.huatu.tiku.course.spring.conf.aspect.mapParam.LocalMapParamHandler;
import com.huatu.tiku.course.util.ResponseUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;

/**
 * Created by lijun on 2018/6/26
 */
@RestController
@RequestMapping("/teacher")
@ApiVersion("v5")
public class TeacherControllerV5 {

    @Autowired
    private TeacherServiceV5 teacherService;

    /**
     * 老师在售课程
     */
    @LocalMapParam(needUserName = false)
    @GetMapping("getPayClassesAll")
    public Object getPayClassesAll(
            @RequestParam String teacherName,
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int pageSize
    ) {
        HashMap<String, Object> map = LocalMapParamHandler.get();
        return ResponseUtil.build(teacherService.getPayClassesAll(map));
    }

    /**
     * 单个老师详情介绍
     */
    @GetMapping("{teacherId}/getTeacherDetail")
    public Object getTeacherDetail(@PathVariable int teacherId) {
        return ResponseUtil.build(teacherService.getTeacherDetail(teacherId));
    }
}
