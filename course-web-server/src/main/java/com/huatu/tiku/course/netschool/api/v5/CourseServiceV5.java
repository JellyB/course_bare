package com.huatu.tiku.course.netschool.api.v5;

import com.huatu.tiku.course.bean.NetSchoolResponse;
import org.springframework.cloud.netflix.feign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

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
     * 课程大纲
     * TODO: v3 Service 中有降级代码，但未被启用。
     *
     * @param classId
     * @return
     */
    @GetMapping(value = "/v4/common/class/class_syllabus")
    NetSchoolResponse findTimetable(@RequestParam("classId") int classId);

    /**
     * 查询课程详情
     * 该接口在V5 之前有大量的业务处理。
     */
    @GetMapping(value = "/v4/common/class/class_detail")
    NetSchoolResponse getClassDetail(@RequestParam Map<String, Object> params);

    /**
     * 获取课程介绍
     */
    @GetMapping(value = "/v4/common/class/course_introduction")
    NetSchoolResponse getCourseIntroduction(@RequestParam("classId") int classId);

    /**
     * 获取所有老师介绍
     */
    @GetMapping(value = "/v4/common/teacher/teacher_info")
    NetSchoolResponse getCourseTeacherInfo(@RequestParam("classId") int teacherId);

}