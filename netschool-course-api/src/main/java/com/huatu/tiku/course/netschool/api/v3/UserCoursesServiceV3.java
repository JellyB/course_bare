package com.huatu.tiku.course.netschool.api.v3;

import com.huatu.tiku.course.netschool.bean.NetSchoolResponse;
import org.springframework.cloud.netflix.feign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.Map;

/**
 * @author hanchao
 * @date 2017/9/13 16:37
 */
@FeignClient(value = "course-service")
public interface UserCoursesServiceV3 {
    /**
     * 获取用户购买的所有课程id集合
     * @return
     */
    @PostMapping("/v3/classesIsBuy.php")
    NetSchoolResponse findProducts(@RequestParam Map<String,Object> params);
    /**
     * 我的课程列表
     * @param params
     * @return
     */
    @PostMapping("/v3/myCourse_new.php")
    NetSchoolResponse findUserCourses(@RequestParam Map<String,Object> params);

    /**
     * 我的隐藏的课表列表
     * @param params
     * @return
     */
    @PostMapping("/v3/myHideClasses.php")
    NetSchoolResponse findUserHideCourses(@RequestParam Map<String,Object> params);


    /**
     * 我的课程日历
     * @param username
     * @return
     */
    @GetMapping("/v3/liveCalendar.php?action=hasLive")
    NetSchoolResponse getLiveCalendar(@RequestParam("username") String username);


    /**
     * 日历直播详情
     * @param ids
     * @return
     */
    @GetMapping("/v3/liveCalendar.php?action=calendarDetail")
    NetSchoolResponse getCalendarDetail(@RequestParam("id") String ids);



}
