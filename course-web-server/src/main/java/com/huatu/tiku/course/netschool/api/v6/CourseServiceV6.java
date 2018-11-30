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
 * Create time 2018-11-26 下午3:22
 **/

@FeignClient(value = "o-course-service", path = "/lumenapi")
public interface CourseServiceV6 {


    /**
     * app课程列表
     * @param params
     * @return
     */
    @GetMapping(value = "/v5/c/class/app_list")
    NetSchoolResponse obtainCourseList(@RequestParam Map<String, Object> params);


    /**
     * 日历详情接口
     * @param params
     * @return
     */
    @GetMapping(value = "/v5/c/class/live_detail")
    NetSchoolResponse calendarDetail(@RequestParam Map<String, Object> params);
}