package com.huatu.tiku.course.netschool.api.v5;

import com.huatu.tiku.course.bean.NetSchoolResponse;
import org.springframework.cloud.netflix.feign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.Map;

/**
 * Created by lijun on 2018/6/26
 */
@FeignClient(value = "o-course-service", path = "/lumenapi")
public interface TeacherServiceV5 {

    /**
     * 老师在售课程更多
     */
    @GetMapping(value = "/v4/common/teacher/pay_classes_all")
    NetSchoolResponse getPayClassesAll(@RequestParam Map<String, Object> map);

    /**
     * 获取老师详情
     */
    @GetMapping(value = "/v4/common/teacher/teacher_details")
    NetSchoolResponse getTeacherDetail(@RequestParam("teacherId") int teacherId);

}
