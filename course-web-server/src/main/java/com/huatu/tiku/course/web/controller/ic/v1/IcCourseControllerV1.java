package com.huatu.tiku.course.web.controller.ic.v1;

import com.huatu.common.exception.BizException;
import com.huatu.common.utils.collection.HashMapBuilder;
import com.huatu.springboot.web.version.mapping.annotation.ApiVersion;
import com.huatu.tiku.course.bean.NetSchoolResponse;
import com.huatu.tiku.course.netschool.api.CourseServiceV1;
import com.huatu.tiku.course.netschool.api.v3.CourseServiceV3;
import com.huatu.tiku.course.netschool.api.v5.CourseServiceV5;
import com.huatu.tiku.course.service.CourseBizService;
import com.huatu.tiku.course.spring.conf.aspect.mapParam.LocalMapParam;
import com.huatu.tiku.course.spring.conf.aspect.mapParam.LocalMapParamHandler;
import com.huatu.tiku.course.util.ResponseUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.concurrent.ExecutionException;

/**
 * 课程接口
 * Created by lijun on 2018/7/2
 */
@Slf4j
@RestController
@RequestMapping("/ic/courses")
@ApiVersion("v1")
public class IcCourseControllerV1 {

    @Autowired
    private CourseServiceV1 courseServiceV1;

    @Autowired
    private CourseBizService courseBizService;

    @Autowired
    private CourseServiceV3 courseServiceV3;

    @Autowired
    private CourseServiceV5 courseService;

    /**
     * 查询面库课程列表 - 首页展示的课程列表信息
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
     * 获取课程详情
     */
    @LocalMapParam(checkToken = true)
    @GetMapping("/{courseId}")
    public Object getCourseDetail() throws ExecutionException, InterruptedException {
        HashMap<String, Object> map = LocalMapParamHandler.get();
        int courseId = Integer.valueOf(map.get("courseId").toString());
        String userName = map.get("userName").toString();
        return courseBizService.getCourseDetailV3(courseId, userName);
    }

    /**
     * 课程详情页(h5)
     */
    @GetMapping(value = "/{courseId}/getClassExt")
    public Object getCourseHtml(@PathVariable int courseId) throws BizException {
        return courseBizService.getCourseHtml(courseId);
    }

    /**
     * 课程表（课程大纲）
     */
    @GetMapping("/{courseId}/timetable")
    public Object findCourseTimetable(@PathVariable int courseId) {
        return ResponseUtil.build(courseServiceV3.findTimetable(courseId));
    }

    /**
     * 获取最近学习的课程
     */
    @LocalMapParam(checkToken = true)
    @GetMapping("/getLastStudyCourse")
    public Object getLastStudyCourse() {
        HashMap<String, Object> map = LocalMapParamHandler.get();
        return ResponseUtil.build(courseService.lastStudyCourse(map));
    }

    /**
     * 课程播放接口
     */
    @LocalMapParam(checkToken = true)
    @GetMapping("/{rid}/secrinfo")
    public Object getCourseSecrInfo(
            @RequestParam(required = false, defaultValue = "0") int isTrial,
            @RequestParam(required = false, defaultValue = "0") int fatherId

    ) {
        HashMap<String, Object> map = LocalMapParamHandler.get();
        map.put("username",String.valueOf(map.get("userName").toString()));

        NetSchoolResponse netSchoolResponse = courseServiceV3.getCourseSecrInfo(map);
        Object response = ResponseUtil.build(netSchoolResponse, true);
        //添加课程进度
        // courseUtil.addStudyProcessIntoSecrInfo(response, userSession.getToken(), cv, terminal);
        return response;
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
