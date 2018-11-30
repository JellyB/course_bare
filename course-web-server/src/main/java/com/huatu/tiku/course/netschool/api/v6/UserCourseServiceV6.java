package com.huatu.tiku.course.netschool.api.v6;

import com.huatu.tiku.course.bean.NetSchoolResponse;
import org.springframework.cloud.netflix.feign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.Map;

/**
 * 描述：
 *
 * @author biguodong
 * Create time 2018-11-26 下午5:28
 **/

@FeignClient(value = "o-course-service", path = "/lumenapi")
public interface UserCourseServiceV6 {

    /**
     * 我的学习-日历接口
     * @param params
     * @return
     */
    @GetMapping(value = "/v5/c/class/my_calendar")
    NetSchoolResponse obtainLearnCalender(@RequestParam Map<String, Object> params);


    /**
     * 我的-已过期课程
     * @param params
     * @return
     */
    @GetMapping(value = "/v5/c/class/expired_class")
    NetSchoolResponse obtainExpiredCourses(@RequestParam Map<String, Object> params);


    /**
     * 我的课程等筛选列表
     * @param params
     * @return
     */
    @GetMapping(value = "/v5/c/class/my_course_filter")
    NetSchoolResponse obtainCourseFilterList(@RequestParam Map<String, Object> params);

    /**
     * 我的课程
     * @param params
     * @return
     */
    @GetMapping(value = "/v5/c/class/my_new_course")
    NetSchoolResponse obtainMineCourses(@RequestParam Map<String, Object> params);


    /**
     * 一键清除我的已过期课程
     * @param params
     * @return
     */
    @GetMapping(value = "/v5/c/class/clear_expired")
    NetSchoolResponse clearExpiredCourses(@RequestParam Map<String, Object> params);
}
