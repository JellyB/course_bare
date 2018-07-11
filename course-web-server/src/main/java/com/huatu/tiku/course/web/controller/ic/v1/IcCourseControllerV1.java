package com.huatu.tiku.course.web.controller.ic.v1;

import com.huatu.common.utils.collection.HashMapBuilder;
import com.huatu.springboot.web.version.mapping.annotation.ApiVersion;
import com.huatu.tiku.course.netschool.api.CourseServiceV1;
import com.huatu.tiku.course.netschool.api.v5.CourseServiceV5;
import com.huatu.tiku.course.spring.conf.aspect.mapParam.LocalMapParam;
import com.huatu.tiku.course.spring.conf.aspect.mapParam.LocalMapParamHandler;
import com.huatu.tiku.course.util.ResponseUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;

/**
 * 课程接口
 * Created by lijun on 2018/7/2
 */
@Slf4j
@RestController
@RequestMapping("/ic/course")
@ApiVersion("v1")
public class IcCourseControllerV1 {

    @Autowired
    private CourseServiceV1 courseServiceV1;

    @Autowired
    private CourseServiceV5 courseService;

    /**
     * 查询面库课程列表
     */
    @LocalMapParam
    @GetMapping("icClassList")
    public Object icClassList(
            @RequestParam(defaultValue = "1") int isFree,
            @RequestParam(defaultValue = "0") int orderType,
            @RequestParam int categoryId,
            @RequestParam(defaultValue = "1000") int subjectId,
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "8") int pageSize
    ) {
        HashMap<String, Object> map = LocalMapParamHandler.get();
        //hashMap.put("province", 1)
        return ResponseUtil.build(courseService.icClassList(map));
    }

    /**
     * 获取课程详情 - 直播
     */
    @LocalMapParam
    @GetMapping("/{classId}/getClassDetailLive")
    public Object getClassDetailLive() {
        HashMap<String, Object> map = LocalMapParamHandler.get();
        return ResponseUtil.build(courseService.getClassDetailLive(map));
    }

    /**
     * 获取课程详情-录播
     */
    @LocalMapParam
    @GetMapping("/{classId}/getClassDetailNotLive")
    public Object getClassDetailNotLive() {
        HashMap<String, Object> map = LocalMapParamHandler.get();
        return ResponseUtil.build(courseService.getClassDetailNotLive(map));
    }

    /**
     * 获取最近学习的课程
     */
    @LocalMapParam
    @GetMapping("/getLastStudyCourse")
    public Object getLastStudyCourse() {
        HashMap<String, Object> map = LocalMapParamHandler.get();
        return ResponseUtil.build(courseService.lastStudyCourse(map));
    }

    /**
     * 课程所有老师介绍
     */
    @GetMapping("/{classId}/getCourseTeacherInfo")
    public Object getCourseTeacherInfo(@PathVariable("classId") int classId) {
        return ResponseUtil.build(courseService.getCourseTeacherInfo(classId));
    }

    /**
     * 用户已购课程列表
     */
    @LocalMapParam
    @GetMapping("userCourseList")
    public Object getUserCourseList(
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int pageSize
    ) {
        HashMap<String, Object> map = LocalMapParamHandler.get();
        return ResponseUtil.build(courseService.userCourseList(map));
    }

    /**
     * 获取课程介绍
     */
    @LocalMapParam
    @GetMapping("/{classId}/getCourseIntroduction")
    public Object getCourseIntroduction() {
        HashMap<String, Object> map = LocalMapParamHandler.get();
        return courseService.getCourseIntroduction(map);
    }

    /**
     * 获取课程讲义
     */
    @GetMapping(value = "{courseId}/handouts")
    public Object handout(@PathVariable int courseId) {
        HashMap<String, Object> map = HashMapBuilder.<String, Object>newBuilder()
                .put("rid", courseId)
                .build();
        return ResponseUtil.build(courseServiceV1.getHandouts(map), true);
    }

}
