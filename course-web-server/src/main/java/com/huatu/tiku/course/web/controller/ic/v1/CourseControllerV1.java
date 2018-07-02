package com.huatu.tiku.course.web.controller.ic.v1;

import com.huatu.common.utils.collection.HashMapBuilder;
import com.huatu.springboot.web.version.mapping.annotation.ApiVersion;
import com.huatu.tiku.course.bean.NetSchoolResponse;
import com.huatu.tiku.course.netschool.api.v5.CourseServiceV5;
import com.huatu.tiku.course.util.ResponseUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;

/**
 * Created by lijun on 2018/7/2
 */
@Slf4j
@RestController
@RequestMapping("/ic/course")
@ApiVersion("v1")
public class CourseControllerV1 {


    @Autowired
    private CourseServiceV5 courseService;

    /**
     * 查询面库课程列表
     */
    @GetMapping("icClassList")
    public Object icClassList(
            @RequestHeader String cv,
            @RequestHeader int terminal,
            @RequestParam(defaultValue = "1") int isFree,
            @RequestParam(defaultValue = "0") int orderType,
            @RequestParam int categoryId,
            @RequestParam(defaultValue = "1000") int subjectId,
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "8") int pageSize
    ) {
        int provinceId = -1;
        HashMap<String, Object> map = HashMapBuilder.<String, Object>newBuilder()
                .put("categoryId", categoryId)
                .put("edition", cv)
                .put("isFree", isFree)
                .put("orderType", orderType)
                .put("page", page)
                .put("pageSize", pageSize)
                //.put("province", provinceId)
                .put("subject", subjectId)
                .put("terminal", terminal)
                .build();
        NetSchoolResponse netSchoolResponse = courseService.icClassList(map);
        return ResponseUtil.build(netSchoolResponse);
    }

    /**
     * 获取课程详情
     */
    @GetMapping("/{classId}/getCourseDetail")
    public Object getCourseDetail(
            @RequestHeader String token,
            @RequestHeader int terminal,
            @RequestHeader String cv,
            @PathVariable("classId") int classId,
            @RequestParam(defaultValue = "0") int isLive
    ) {
        String userName = "";

        HashMap<String, Object> map = HashMapBuilder.<String, Object>newBuilder()
                .put("classId", classId)
                .put("isLive", isLive)
                .put("terminal", terminal)
                .put("userName", userName)
                .build();
        return courseService.getClassDetail(map);
    }


    /**
     * 课程所有老师介绍
     */
    @GetMapping("/{classId}/getCourseTeacherInfo")
    public Object getCourseTeacherInfo(
            @PathVariable("classId") int classId
    ) {
        return ResponseUtil.build(courseService.getCourseTeacherInfo(classId));
    }
}
