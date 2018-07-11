package com.huatu.tiku.course.web.controller.v5;

import com.huatu.common.utils.collection.HashMapBuilder;
import com.huatu.springboot.web.version.mapping.annotation.ApiVersion;
import com.huatu.tiku.course.netschool.api.v5.TeacherServiceV5;
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
    @GetMapping("getPayClassesAll")
    public Object getPayClassesAll(
            @RequestHeader int terminal,
            @RequestParam String teacherName,
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int pageSize
    ) {
        HashMap<String, Object> map = HashMapBuilder.<String, Object>newBuilder()
                .put("terminal", terminal)
                .put("teacherName", teacherName)
                .put("page", page)
                .put("pageSize", pageSize)
                .build();
        return ResponseUtil.build(teacherService.getPayClassesAll(map));
    }

    /**
     * 单个老师详情介绍
     */
    @GetMapping("{teacherId}/getTeacherDetail")
    public Object getTeacherDetail(@PathVariable int teacherId){
        return ResponseUtil.build(teacherService.getTeacherDetail(teacherId));
    }
}
