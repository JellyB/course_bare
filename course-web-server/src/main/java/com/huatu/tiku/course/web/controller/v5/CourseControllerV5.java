package com.huatu.tiku.course.web.controller.v5;

import com.huatu.common.utils.collection.HashMapBuilder;
import com.huatu.springboot.web.version.mapping.annotation.ApiVersion;
import com.huatu.tiku.common.bean.AreaConstants;
import com.huatu.tiku.common.bean.user.UserSession;
import com.huatu.tiku.course.bean.NetSchoolResponse;
import com.huatu.tiku.course.netschool.api.v5.CourseServiceV5;
import com.huatu.tiku.course.service.v5.CourseServiceV5Biz;
import com.huatu.tiku.course.spring.conf.aspect.mapParam.LocalMapParam;
import com.huatu.tiku.course.spring.conf.aspect.mapParam.LocalMapParamHandler;
import com.huatu.tiku.course.util.ResponseUtil;
import com.huatu.tiku.course.web.controller.util.CourseUtil;
import com.huatu.tiku.springboot.users.support.Token;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.LinkedHashMap;

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
        log.warn("1$${}$${}$${}$${}$${}$${}$${}$${}", categoryId, subjectId, userSession.getId(), userSession.getUname(), "", String.valueOf(System.currentTimeMillis()), cv, terminal);
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
     * 获取课程大纲-售前
     */
    @LocalMapParam(checkToken = true)
    @GetMapping("/{classId}/classSyllabus")
    public Object classSyllabus(
            @Token UserSession userSession,
            @PathVariable("classId") int classId,
            @RequestParam int parentId,
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "20") int pageSize
    ) {
        //return courseServiceBiz.findTimetable(classId, parentId, page, pageSize);
        HashMap<String, Object> map = LocalMapParamHandler.get();
        //添加答题信息
        Object timeTable = ResponseUtil.build(courseService.findTimetable(map));
        //添加答题信息
        courseUtil.addExercisesCardInfo((LinkedHashMap) timeTable, userSession.getId());
        return timeTable;
    }

    /**
     * 获取课程大纲-售后
     */
    @LocalMapParam(checkToken = true)
    @GetMapping("/{classId}/purchasedClassSyllabus")
    public Object purchasedClassSyllabus(
            @Token UserSession userSession,
            @RequestParam int parentId,
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "20") int pageSize
    ) {
        HashMap<String, Object> map = LocalMapParamHandler.get();
        Object purchasesTimetable = courseServiceBiz.findPurchasesTimetable(userSession.getId(), map);
        //添加答题信息
        courseUtil.addExercisesCardInfo((LinkedHashMap) purchasesTimetable, userSession.getId());
        return purchasesTimetable;
    }

    /**
     * 获取课程详情-录播
     */
    @GetMapping("/{classId}/getClassDetailNotLive")
    public Object getClassDetailNotLive(
            @Token UserSession userSession,
            @RequestHeader int terminal,
            @RequestHeader String cv,
            @PathVariable("classId") int classId
    ) {
        HashMap<String, Object> map = HashMapBuilder.<String, Object>newBuilder()
                .put("classId", classId)
                .put("terminal", terminal)
                .put("userName", userSession.getUname())
                .build();
        log.warn("4$${}$${}$${}$${}$${}$${}", classId, userSession.getId(), userSession.getUname(), String.valueOf(System.currentTimeMillis()), cv, terminal);
        return ResponseUtil.build(courseService.getClassDetailNotLive(map));
    }

    /**
     * 获取课程详情-直播
     */
    @GetMapping("/{classId}/getClassDetailLive")
    public Object getClassDetailLive(
            @Token UserSession userSession,
            @RequestHeader int terminal,
            @RequestHeader String cv,
            @PathVariable("classId") int classId
    ) {
        HashMap<String, Object> map = HashMapBuilder.<String, Object>newBuilder()
                .put("classId", classId)
                .put("terminal", terminal)
                .put("userName", "uname")
                .build();
        log.warn("4$${}$${}$${}$${}$${}$${}", classId, userSession.getId(), userSession.getUname(), String.valueOf(System.currentTimeMillis()), cv, terminal);
        return ResponseUtil.build(courseService.getClassDetailLive(map));
    }

    /**
     * 获取课程介绍
     */
    @LocalMapParam(checkToken = true)
    @GetMapping("/{classId}/getCourseIntroduction")
    public Object getCourseIntroduction() {
        HashMap<String, Object> map = LocalMapParamHandler.get();
        return ResponseUtil.build(courseService.getCourseIntroduction(map));
    }

    /**
     * 课程所有老师介绍
     */
    @GetMapping("/{classId}/getCourseTeacherInfo")
    public Object getCourseTeacherInfo(@PathVariable("classId") int classId) {
        return ResponseUtil.build(courseService.getCourseTeacherInfo(classId));
    }

    /**
     * 获取课程说明-返回H5 页面
     */
    @GetMapping(value = "/{classId}/getClassExt", produces = MediaType.TEXT_HTML_VALUE + ";charset=utf-8")
    public Object getClassExt(
            @PathVariable("classId") int classId,
            @RequestHeader(defaultValue = "1") int terminal
    ) {
        return courseServiceBiz.getClassExt(classId, terminal);
    }

    /**
     * 删除课程
     */
    @LocalMapParam(checkToken = true)
    @DeleteMapping(value = "/{classId}")
    public Object deleteMyCourse(@RequestParam int orderId) {
        HashMap<String, Object> map = LocalMapParamHandler.get();
        return ResponseUtil.build(courseService.deleteCourse(map));
    }

    /**
     * 彻底删除回收站中用户客户课程数据
     */
    @LocalMapParam(checkToken = true)
    @DeleteMapping(value = "/{orderId}/deepDeleteCourse")
    public Object deepDeleteMyCourse() {
        HashMap<String, Object> map = LocalMapParamHandler.get();
        return ResponseUtil.build(courseService.deleteDeepCourse(map));
    }


    /**
     * 取消删除
     */
    @LocalMapParam(checkToken = true)
    @PutMapping(value = "/{classId}")
    public Object cancelDeleteMyCourse(@RequestParam int orderId) {
        HashMap<String, Object> map = LocalMapParamHandler.get();
        return ResponseUtil.build(courseService.cancelDeleteCourse(map));
    }

    /**
     * 置顶课程
     */
    @LocalMapParam(checkToken = true)
    @PostMapping(value = "{classId}/topCourse")
    public Object topCourse(@RequestParam int orderId) {
        HashMap<String, Object> map = LocalMapParamHandler.get();
        return ResponseUtil.build(courseService.postTopCourse(map));
    }

    /**
     * 删除置顶信息
     */
    @LocalMapParam(checkToken = true)
    @DeleteMapping(value = "{classId}/deleteTopCourse")
    public Object deleteTopCourse(@RequestParam int orderId) {
        HashMap<String, Object> map = LocalMapParamHandler.get();
        return ResponseUtil.build(courseService.deleteTopCourse(map));
    }

    /**
     * 获取 qq群、课程学习总进度
     */
    @LocalMapParam(checkToken = true)
    @GetMapping(value = "{classId}/getQqGroupSchedule")
    public Object getQqGroupSchedule() {
        HashMap<String, Object> map = LocalMapParamHandler.get();
        return ResponseUtil.build(courseService.qqGroupSchedule(map));
    }

    /**
     * 获取横屏课件列表
     */
    @LocalMapParam(checkToken = false)
    @GetMapping(value = "/{netClassId}/getChooseCourseWare")
    public Object getChooseCourseWare(
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int pageSize
    ) {
        HashMap<String, Object> map = LocalMapParamHandler.get();
        return ResponseUtil.build(courseService.chooseCourseWare(map));
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
