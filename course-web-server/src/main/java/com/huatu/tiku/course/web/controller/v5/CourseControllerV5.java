package com.huatu.tiku.course.web.controller.v5;

import com.huatu.common.utils.collection.HashMapBuilder;
import com.huatu.springboot.web.version.mapping.annotation.ApiVersion;
import com.huatu.tiku.common.bean.AreaConstants;
import com.huatu.tiku.common.bean.user.UserSession;
import com.huatu.tiku.course.bean.NetSchoolResponse;
import com.huatu.tiku.course.netschool.api.v5.CourseServiceV5;
import com.huatu.tiku.course.service.v5.CourseServiceV5Biz;
import com.huatu.tiku.course.util.ResponseUtil;
import com.huatu.tiku.course.web.controller.util.CourseUtil;
import com.huatu.tiku.springboot.users.support.Token;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;

/**
 * Created by lijun on 2018/6/25
 */
@Slf4j
@RestController
@RequestMapping("/courses")
@ApiVersion("v5")
public class CourseControllerV5 {

    @Autowired
    private CourseServiceV5 courseService;

    @Autowired
    private CourseServiceV5Biz courseServiceBiz;

    @Autowired
    private CourseUtil courseUtil;

    /**
     * 查询直播列表
     */
    @GetMapping("recordClassList")
    public Object recordClassList(
            @Token UserSession userSession,
            @RequestHeader String cv,
            @RequestHeader int terminal,
            @RequestParam(defaultValue = "1") int isFree,
            @RequestParam(defaultValue = "0") int orderType,
            @RequestParam int categoryId,
            @RequestParam(defaultValue = "1000") int subjectId,
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "8") int pageSize
    ) {
        int provinceId = AreaConstants.getNetSchoolProvinceId(userSession.getArea());
        HashMap map = buildParams(cv, terminal, isFree, orderType, categoryId, provinceId, subjectId, page, pageSize);
        NetSchoolResponse netSchoolResponse = courseService.recordClassList(map);
        log.warn("1$${}$${}$${}$${}$${}$${}$${}", categoryId, userSession.getId(), userSession.getUname(), "", String.valueOf(System.currentTimeMillis()), cv, terminal);
        return ResponseUtil.build(netSchoolResponse);
    }

    /**
     * 查询录播列表
     */
    @GetMapping("liveClassList")
    public Object liveClassList(
            @Token UserSession userSession,
            @RequestHeader String cv,
            @RequestHeader int terminal,
            @RequestParam(defaultValue = "1") int isFree,
            @RequestParam(defaultValue = "0") int orderType,
            @RequestParam int categoryId,
            @RequestParam(defaultValue = "1000") int subjectId,
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "8") int pageSize
    ) {
        int provinceId = AreaConstants.getNetSchoolProvinceId(userSession.getArea());
        HashMap map = buildParams(cv, terminal, isFree, orderType, categoryId, provinceId, subjectId, page, pageSize);
        NetSchoolResponse netSchoolResponse = courseService.liveClassList(map);
        log.warn("2$${}$${}$${}$${}$${}$${}$${}$${}", categoryId, subjectId, userSession.getId(), userSession.getUname(), "", String.valueOf(System.currentTimeMillis()), cv, terminal);
        return ResponseUtil.build(netSchoolResponse);
    }

    /**
     * 查询面库课程列表
     */
    @GetMapping("icClassList")
    public Object icClassList(
            @RequestHeader String cv,
            @RequestHeader int terminal,
            @RequestParam(defaultValue = "1") int isFree,
            @RequestParam(defaultValue = "0") int orderType,
            @RequestParam int categoryId,
            @RequestParam(defaultValue = "1000") int subjectId,
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "8") int pageSize
    ) {
        int provinceId = -1;
        HashMap map = buildParams(cv, terminal, isFree, orderType, categoryId, provinceId, subjectId, page, pageSize);
        NetSchoolResponse netSchoolResponse = courseService.icClassList(map);
        return ResponseUtil.build(netSchoolResponse);
    }

    /**
     * 课程播放
     */
    @GetMapping("/{classId}/getClassPlayInfo")
    public Object getClassPlayInfo(
            @Token UserSession userSession,
            @RequestHeader String cv,
            @RequestHeader int terminal,
            @PathVariable("classId") int classId,
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int pageSize
    ) {
        String userName = userSession.getUname();
        HashMap<String, Object> map = HashMapBuilder.<String, Object>newBuilder()
                .put("userName", userName)
                .put("classId", classId)
                .put("page", page)
                .put("pageSize", pageSize)
                .build();
        NetSchoolResponse netSchoolResponse = courseService.getCommonUserPlay(map);
        Object response = ResponseUtil.build(netSchoolResponse, true);
        //发布课程播放事件
        courseUtil.pushPlayEvent(userSession, netSchoolResponse, response);
        //添加课程进度
        courseUtil.addStudyProcessIntoSecrInfo(response, userSession.getToken(), cv, terminal);
        return response;
    }

    /**
     * 获取课程大纲
     */
    @GetMapping("/{classId}/classSyllabus")
    public Object classSyllabus(
            @PathVariable("classId") int classId
    ) {
        NetSchoolResponse netSchoolResponse = courseService.findTimetable(classId);
        return ResponseUtil.build(netSchoolResponse);
    }

    /**
     * 获取课程详情
     */
    @GetMapping("/{classId}/getCourseDetail")
    public Object getCourseDetail(
            @Token UserSession userSession,
            @RequestHeader int terminal,
            @RequestHeader String cv,
            @PathVariable("classId") int classId,
            @RequestParam(defaultValue = "0") int isLive
    ) {
        HashMap<String, Object> map = HashMapBuilder.<String, Object>newBuilder()
                .put("classId", classId)
                .put("isLive", isLive)
                .put("terminal", terminal)
                .put("userName", userSession.getUname())
                .build();
        log.warn("4$${}$${}$${}$${}$${}$${}", classId, userSession.getId(), userSession.getUname(), String.valueOf(System.currentTimeMillis()), cv, terminal);
        return courseServiceBiz.getClassDetail(map);
    }

    /**
     * 获取课程介绍
     */
    @GetMapping("/{classId}/getCourseIntroduction")
    public Object getCourseIntroduction(
            @PathVariable("classId") int classId
    ) {
        return courseServiceBiz.getCourseIntroduction(classId);
    }

    /**
     * 课程所有老师介绍
     */
    @GetMapping("/{classId}/getCourseTeacherInfo")
    public Object getCourseTeacherInfo(
            @PathVariable("classId") int classId
    ) {
        return ResponseUtil.build(courseService.getCourseTeacherInfo(classId));
    }

    /**
     * 获取课程说明
     */
    @GetMapping(value = "/{classId}/getClassExt", produces = MediaType.TEXT_HTML_VALUE + ";charset=utf-8")
    public Object getClassExt(
            @PathVariable("classId") int classId,
            @RequestHeader(defaultValue = "1") int terminal
    ) {
        return courseService.getClassExt(classId, terminal);
    }

    /**
     * 构造课程列表查询参数
     *
     * @param cv         版本号
     * @param terminal   设备类型
     * @param isFree     1 非免费课 2免费课 (PC端参数)
     * @param orderType  排序方式 0:默认 1:价格升序 2:价格降序 3:最新
     * @param categoryId 所属考试
     * @param provinceId 地区ID
     * @param subjectId  1:笔试 2:面试 (PC端参数)
     * @param page       页码
     * @param pageSize   页面大小
     * @return
     */
    private HashMap buildParams(String cv, int terminal, int isFree, int orderType, int categoryId, int provinceId, int subjectId, int page, int pageSize) {
        /**
         * TODO:区域ID 在V4以前有传递，V5接口确认不需要。
         */
        HashMap<String, Object> map = HashMapBuilder.<String, Object>newBuilder()
                .put("categoryId", categoryId)
                .put("edition", cv)
                .put("isFree", isFree)
                .put("orderType", orderType)
                .put("page", page)
                .put("pageSize", pageSize)
                //.put("province", provinceId)
                .put("subject", subjectId)
                .put("terminal", terminal)
                .build();
        return map;
    }
}
