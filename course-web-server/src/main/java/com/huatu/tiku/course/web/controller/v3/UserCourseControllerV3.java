package com.huatu.tiku.course.web.controller.v3;

import com.google.common.collect.Maps;
import com.huatu.common.utils.collection.HashMapBuilder;
import com.huatu.common.utils.reflect.BeanUtil;
import com.huatu.tiku.course.bean.One2OneFormDTO;
import com.huatu.tiku.course.netschool.api.v3.UserCoursesServiceV3;
import com.huatu.tiku.course.util.RequestUtil;
import com.huatu.tiku.course.util.ResponseUtil;
import com.huatu.tiku.springboot.users.bean.UserSession;
import com.huatu.tiku.springboot.users.support.Token;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

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
     */
    @GetMapping("/courses")
    public Object findUserCourses(@Token UserSession userSession,
                                  @RequestParam(required = false)String keywords,
                                  @RequestParam int type,
                                  @RequestParam int page) {
        Map<String,Object> params = Maps.newHashMap();
        params.put("username",userSession.getUname());
        params.put("keywords",keywords);
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
     */
    @GetMapping( value = "/courses",params = {"_hide"})
    public Object findUserCoursesHiding(@RequestParam int type,
                                        @Token UserSession userSession,
                                        @RequestParam int page) {
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
    public Object getLiveCalendar(@Token UserSession userSession) {
        return ResponseUtil.build(userCoursesServiceV3.getLiveCalendar(userSession.getUname()));
    }

    /**
     * 日历直播详情
     * @param id 逗号隔开的
     * @param userSession
     * @return
     */
    @GetMapping("/calendar/show")
    public Object getCalendarDetail(@RequestParam String id,
                                    @Token UserSession userSession) {
        return ResponseUtil.build(userCoursesServiceV3.getCalendarDetail(id));
    }


    /**
     * 填写1对1报名表
     * @param dto
     * @param userSession
     * @param courseId
     * @return
     */
    @PostMapping(value = "/1v1/{courseId}",consumes = MediaType.APPLICATION_JSON_VALUE)
    public Object save1V1Table(@RequestBody One2OneFormDTO dto,
                               @Token UserSession userSession,
                               @PathVariable int courseId) {
        Map<String,Object> params = BeanUtil.toMap(dto);
        params.put("action","saveInfo");
        params.put("username",userSession.getUname());
        params.put("rid",courseId);
        return ResponseUtil.build(userCoursesServiceV3.save1V1Table(RequestUtil.encrypt(params)));
    }


    /**
     * 获取1v1报名表
     * @param courseId
     * @param OrderNum
     * @return
     */
    @GetMapping("/1v1/{courseId}")
    public Object get1V1Table(@PathVariable int courseId,
                              @RequestParam String OrderNum) {
        Map<String,Object> params = HashMapBuilder.newBuilder()
                .put("OrderNum", OrderNum)
                .put("action","getInfo")
                .put("rid",courseId)
                .buildUnsafe();
        return ResponseUtil.build(userCoursesServiceV3.get1V1Table(RequestUtil.encrypt(params)));
    }

}
