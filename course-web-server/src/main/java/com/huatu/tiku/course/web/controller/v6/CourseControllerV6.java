package com.huatu.tiku.course.web.controller.v6;

import com.huatu.common.SuccessMessage;
import com.huatu.springboot.web.version.mapping.annotation.ApiVersion;
import com.huatu.tiku.common.bean.user.UserSession;
import com.huatu.tiku.course.bean.NetSchoolResponse;
import com.huatu.tiku.course.netschool.api.v6.CourseServiceV6;
import com.huatu.tiku.course.service.v6.CourseBizV6Service;
import com.huatu.tiku.course.service.v6.CourseServiceV6Biz;
import com.huatu.tiku.course.spring.conf.aspect.mapParam.LocalMapParam;
import com.huatu.tiku.course.spring.conf.aspect.mapParam.LocalMapParamHandler;
import com.huatu.tiku.course.util.ResponseUtil;
import com.huatu.tiku.springboot.users.support.Token;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * 描述：课程接口v6
 *
 * @author biguodong
 *         Create time 2018-11-26 下午4:22
 **/

@Slf4j
@RestController
@RequestMapping("/courses")
@ApiVersion("v6")
public class CourseControllerV6 {

    @Autowired
    private CourseServiceV6 courseService;

    @Autowired
    private CourseBizV6Service courseBizV6Service;

    @Autowired
    private CourseServiceV6Biz courseServiceV6Biz;

    /**
     * App课程列表
     *
     * @param cateId
     * @return
     */
    @LocalMapParam
    @GetMapping(value = "list")
    public Object obtainCourseList(@RequestParam(value = "cateId") String cateId) {
        Map<String, Object> params = LocalMapParamHandler.get();
        return courseServiceV6Biz.obtainCourseList(params);
    }


    /**
     * 日历详情
     *
     * @param userSession
     * @param cv
     * @param date
     * @param id
     * @param page
     * @param pageSize
     * @return
     */
    @LocalMapParam
    @GetMapping(value = "calendarDetail")
    public Object obtainCalendarDetail(
            @Token UserSession userSession,
            @RequestHeader(value = "cv") String cv,
            @RequestHeader(value = "terminal") int terminal,
            @RequestParam(value = "date") String date,
            @RequestParam(value = "id", required = false) String id,
            @RequestParam(value = "page", defaultValue = "1") int page,
            @RequestParam(value = "pageSize", defaultValue = "20") int pageSize) {
        Map<String, Object> params = LocalMapParamHandler.get();
        return courseBizV6Service.calendarDetail(params);
    }


    /**
     * 课程分类详情
     *
     * @param cateId
     * @param cv
     * @param terminal
     * @param page
     * @param typeId
     * @return
     */
    @LocalMapParam
    @GetMapping(value = "typeDetail")
    public Object courseTypeDetail(@RequestHeader(value = "cv") String cv,
                                   @RequestHeader(value = "terminal") int terminal,
                                   @RequestParam(value = "cateId") int cateId,
                                   @RequestParam(value = "page", defaultValue = "1") int page,
                                   @RequestParam(value = "typeId") int typeId) {
        Map<String, Object> params = LocalMapParamHandler.get();
        NetSchoolResponse netSchoolResponse = courseService.courseTypeDetail(params);
        return ResponseUtil.build(netSchoolResponse);
    }

    /**
     * 课程搜索接口
     *
     * @param keyWord
     * @param page
     * @return
     */
    @LocalMapParam
    @GetMapping(value = "search")
    public Object searchCourses(@Token(check = false, defaultValue = "") UserSession userSession,
                                @RequestParam(value = "keyWord") String keyWord,
                                @RequestParam(value = "cateId") int cateId,
                                @RequestParam(value = "page", defaultValue = "1") int page,
                                @RequestParam(value = "isHistory", defaultValue = "-1") int isHistory,
                                @RequestParam(value = "isRecommend", defaultValue = "-1") int isRecommend) {
        Map<String, Object> params = LocalMapParamHandler.get();
        NetSchoolResponse netSchoolResponse = courseService.searchCourses(params);
        if(isHistory > 0){
            courseServiceV6Biz.upSetSearchKeyWord(userSession.getToken(), keyWord);
        }
        return ResponseUtil.build(netSchoolResponse);
    }


    /**
     * 合集课程列表
     *
     * @param collectId
     * @return
     */
    @LocalMapParam
    @GetMapping(value = "collectDetail")
    public Object collectDetail(@RequestParam(value = "collectId") long collectId,
                                @RequestParam(value = "page", defaultValue = "1") int page) {
        Map<String, Object> params = LocalMapParamHandler.get();
        NetSchoolResponse netSchoolResponse = courseService.collectDetail(params);
        return ResponseUtil.build(netSchoolResponse);
    }

    /**
     * 模考大赛解析课信息,多个id使用逗号分隔
     *
     * @param classIds
     * @return
     */
    @GetMapping(value = "courseAnalysis")
    public Object courseAnalysis(@RequestParam(value = "classIds") String classIds) {
        HashMap<String, LinkedHashMap> result = courseServiceV6Biz.getClassAnalysis(classIds);
        return result;
    }


    /**
     * 查询用户是否报名课程，课程是否免费，课程是否已结束
     * @param userSession
     * @param terminal
     * @param cv
     * @return
     */
    @GetMapping(value = "status/{classId}")
    public Object getUserCourseInfo(@Token UserSession userSession,
                                    @RequestHeader(value = "terminal") int terminal,
                                    @RequestHeader(value = "cv") String cv,
                                    @PathVariable int classId,
                                    @RequestParam(defaultValue = "-1") int collageActivityId ){
        return courseServiceV6Biz.getUserCourseStatus(userSession.getUname(),classId,collageActivityId);
    }
    /**
     * 小模考历史解析课信息列表
     *
     * @param
     * @return
     */
    @LocalMapParam
    @GetMapping(value = "analysisClassList")
    public Object courseList(@RequestHeader int subject,
                             @RequestParam(value = "page", defaultValue = "1") int page,
                             @RequestParam(value = "size", defaultValue = "30") int size,
                             @RequestParam(value = "startTime",defaultValue = "-1") long startTime,
                             @RequestParam(value = "endTime",defaultValue = Long.MAX_VALUE+"") long endTime) {
        NetSchoolResponse netSchoolResponse = courseServiceV6Biz.analysisClassList(subject,page,size,startTime,endTime);
        return ResponseUtil.build(netSchoolResponse);
    }

    /**
     * 添加秒杀课
     * @param classId
     * @param limit
     * @return
     */
    @PostMapping(value = "/addSecKill")
    public Object addSecKillInfo(@RequestParam(value = "classId") String  classId,
                                 @RequestParam(value = "limit") int limit){
        courseServiceV6Biz.addSecKillInfo(classId, limit);
        return SuccessMessage.create("ok");
    }
}
