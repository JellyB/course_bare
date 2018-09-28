package com.huatu.tiku.course.netschool.api.v5;

import com.huatu.tiku.course.bean.NetSchoolResponse;
import org.springframework.cloud.netflix.feign.FeignClient;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

/**
 * 课程相关
 * Created by lijun on 2018/6/25
 */
@FeignClient(value = "o-course-service", path = "/lumenapi")
public interface CourseServiceV5 {

    /**
     * 获取录播课程合集
     */
    @GetMapping(value = "/v4/common/class/class_list?videoType=0")
    NetSchoolResponse recordClassList(@RequestParam Map<String, Object> params);

    /**
     * 获取直播课程合集
     */
    @GetMapping(value = "/v4/common/class/class_list?videoType=1")
    NetSchoolResponse liveClassList(@RequestParam Map<String, Object> params);

    /**
     * 获取面库课程合集
     */
    @GetMapping(value = "/v4/common/class/class_list?videoType=4")
    NetSchoolResponse icClassList(@RequestParam Map<String, Object> params);

    /**
     * 课程播放接口
     */
    @GetMapping(value = "/v4/common/user/play")
    NetSchoolResponse getCommonUserPlay(@RequestParam Map<String, Object> params);

    /**
     * 课程大纲-售前
     * TODO: v3 Service 中有降级代码，但未被启用。
     */
    @GetMapping(value = "/v4/common/class/class_syllabus")
    NetSchoolResponse findTimetable(@RequestParam Map<String, Object> params);

    /**
     * 课程大纲-售后
     */
    @GetMapping(value = "/v4/common/class/purchased_class_syllabus")
    NetSchoolResponse findPurchasesTimetable(@RequestParam Map<String, Object> params);

    /**
     * 查询课程详情 - 录播
     * 该接口在V5 之前有大量的业务处理。
     */
    @GetMapping(value = "/v4/common/class/class_detail?isLive=0")
    NetSchoolResponse getClassDetailNotLive(@RequestParam Map<String, Object> params);

    /**
     * 查询课程详情 - 直播
     * 该接口在V5 之前有大量的业务处理。
     */
    @GetMapping(value = "/v4/common/class/class_detail?isLive=1")
    NetSchoolResponse getClassDetailLive(@RequestParam Map<String, Object> params);

    /**
     * 获取课程介绍
     */
    @GetMapping(value = "/v4/common/class/course_introduction")
    NetSchoolResponse getCourseIntroduction(@RequestParam Map<String, Object> params);

    /**
     * 获取所有老师介绍
     */
    @GetMapping(value = "/v4/common/teacher/teacher_info")
    NetSchoolResponse getCourseTeacherInfo(@RequestParam("rid") int teacherId);

    /**
     * 获取课程说明
     * 获取 html 页面
     */
    @GetMapping(value = "/v4/common/class/class_ext")
    String getClassExt(@RequestParam("classId") int classId, @RequestParam("terminal") int terminal);

    /**
     * 删除课程
     */
    @DeleteMapping(value = "/v4/common/user/my_course")
    NetSchoolResponse deleteCourse(@RequestParam Map<String, Object> params);

    /**
     * 彻底删除回收站课程
     */
    @DeleteMapping(value = "/v4/common/user/my_course_deep")
    NetSchoolResponse deleteDeepCourse(@RequestParam Map<String, Object> params);

    /**
     * 取消删除
     */
    @PutMapping(value = "/v4/common/user/my_course")
    NetSchoolResponse cancelDeleteCourse(@RequestParam Map<String, Object> params);

    /**
     * 置顶课程
     */
    @PostMapping(value = "/v4/common/user/top_course")
    NetSchoolResponse postTopCourse(@RequestParam Map<String, Object> params);

    /**
     * 删除置顶课程
     */
    @DeleteMapping(value = "/v4/common/user/top_course")
    NetSchoolResponse deleteTopCourse(@RequestParam Map<String, Object> params);

    /**
     * qq群、课程学习总进度
     */
    @GetMapping(value = "/v4/common/class/qq_group_schedule")
    NetSchoolResponse qqGroupSchedule(@RequestParam Map<String, Object> params);

    /**
     * 获取最后一次学习课程 - IC
     */
    @GetMapping(value = "/v4/interview/user/last_study")
    NetSchoolResponse lastStudyCourse(@RequestParam Map<String, Object> params);

    /**
     * 已够课程列表 - IC
     */
    @GetMapping(value = "/v4/interview/class/list")
    NetSchoolResponse userCourseList(@RequestParam Map<String, Object> params);

    /**
     * 根据课程id 批量获取数据
     */
    @GetMapping(value = "/v4/interview/class/list_by_id")
    NetSchoolResponse courseInfoList(@RequestParam("classIds") String classIds);

    /**
     * 获取横屏课程信息
     */
    @GetMapping(value = "/v4/common/class/choose_courseware")
    NetSchoolResponse chooseCourseWare(@RequestParam Map<String, Object> params);

    /**
     * 继续学习
     */
    @GetMapping(value = "/v4/common/class/last_play_lesson")
    NetSchoolResponse lastPlayLesson(@RequestParam Map<String, Object> params);

    /**
     * 讲义
     */
    @GetMapping(value = "/v4/common/class/get_handouts")
    NetSchoolResponse handouts(@RequestParam Map<String, Object> params);

    /**
     * 课程详情活动促销
     */
    @GetMapping(value = "/v4/common/class/appClass_activity_details")
    NetSchoolResponse appClassActivityDetails(@RequestParam("classIds") int classIds);
}