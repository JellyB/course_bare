package com.huatu.tiku.course.web.controller.v3;

import com.google.common.collect.Maps;
import com.huatu.common.exception.BizException;
import com.huatu.tiku.course.netschool.api.v3.UserCoursesServiceV3;
import com.huatu.tiku.course.util.ResponseUtil;
import com.huatu.tiku.springboot.users.bean.UserSession;
import com.huatu.tiku.springboot.users.support.Token;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

/**
 * @author hanchao
 * @date 2017/9/14 9:23
 */
@RestController
@RequestMapping("/v3/my")
public class UserCourseControllerV3 {

    @Autowired
    private UserCoursesServiceV3 userCoursesServiceV3;

    /**
     * 我的课程列表
     * @param userSession
     * @param type
     * @param page
     * @return
     * @throws BizException
     */
    @GetMapping("/courses")
    public Object findUserCourses(@Token UserSession userSession,
                                  @RequestParam int type,
                                  @RequestParam int page) throws BizException {
        Map<String,Object> params = Maps.newHashMap();
        params.put("username",userSession.getUname());
        params.put("page",page);
        params.put("type",type);
        return ResponseUtil.build(userCoursesServiceV3.findUserCourses(params));
    }

     /**
     * 我的课程列表(隐藏课程列表)
     * @param userSession
     * @param type
     * @param page
     * @return
     * @throws BizException
     */
    @GetMapping( value = "/courses",params = {"_hide=true"})
    public Object findUserCoursesHiding(@RequestParam int type,
                                        @Token UserSession userSession,
                                        @RequestParam int page) throws BizException {
        Map<String,Object> params = Maps.newHashMap();
        params.put("username",userSession.getUname());
        params.put("page",page);
        params.put("type",type);

        return ResponseUtil.build(userCoursesServiceV3.findUserHideCourses(params));
    }

    /**
     * 我的直播课程日历
     * @param userSession
     * @return
     */
    @GetMapping("/calendar")
    public Object getLiveCalendar(@Token UserSession userSession) throws BizException {
        return ResponseUtil.build(userCoursesServiceV3.getLiveCalendar(userSession.getUname()));
    }

    /**
     * 日历直播详情
     * @param id 逗号隔开的
     * @param userSession
     * @return
     * @throws BizException
     */
    @GetMapping("/calendar/show")
    public Object getCalendarDetail(@RequestParam String id,
                                    @Token UserSession userSession) throws BizException {
        return ResponseUtil.build(userCoursesServiceV3.getCalendarDetail(id));
    }

}
