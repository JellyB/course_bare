package com.huatu.tiku.course.web.controller.v6;

import com.huatu.springboot.web.version.mapping.annotation.ApiVersion;
import com.huatu.tiku.common.bean.user.UserSession;
import com.huatu.tiku.course.bean.NetSchoolResponse;
import com.huatu.tiku.course.netschool.api.v6.SyllabusServiceV6;
import com.huatu.tiku.course.spring.conf.aspect.mapParam.LocalMapParam;
import com.huatu.tiku.course.spring.conf.aspect.mapParam.LocalMapParamHandler;
import com.huatu.tiku.course.util.ResponseUtil;
import com.huatu.tiku.course.web.controller.util.CourseUtil;
import com.huatu.tiku.springboot.users.support.Token;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * 描述：
 *
 * @author biguodong
 * Create time 2019-01-03 下午4:11
 **/

@Slf4j
@RestController
@RequestMapping("/syllabus")
@ApiVersion("v6")
public class SyllabusControllerV6 {


    @Autowired
    private SyllabusServiceV6 syllabusService;

    @Autowired
    private CourseUtil courseUtil;

    /**
     * 课程大纲课程列表 7.1.1
     * @param cv
     * @param terminal
     * @param netClassId
     * @param stageNodeId
     * @return
     */
    @LocalMapParam
    @GetMapping("/{netClassId}/syllabusClasses")
    public Object syllabusClasses(@RequestHeader(value = "cv") String cv,
                                  @RequestHeader(value = "terminal") int terminal,
                                  @PathVariable(value = "netClassId") int netClassId,
                                  @RequestParam(defaultValue = "0") int stageNodeId) {


        Map<String,Object> params = LocalMapParamHandler.get();
        NetSchoolResponse netSchoolResponse =  syllabusService.syllabusClasses(params);
        return ResponseUtil.build(netSchoolResponse);
    }


    /**
     * 售后大纲老师列表（7.1.1）
     */
    @LocalMapParam
    @GetMapping("{netClassId}/syllabusTeachers")
    public Object syllabusTeachers(@RequestHeader(value = "terminal") int terminal,
                                   @PathVariable(value = "netClassId") int netClassId,
                                   @RequestParam(defaultValue = "0") int classNodeId,
                                   @RequestParam(defaultValue = "0") int stageNodeId) {
        HashMap<String, Object> map = LocalMapParamHandler.get();
        return ResponseUtil.build(syllabusService.syllabusTeachers(map));
    }


    /**
     * 大纲 售后
     */
    @LocalMapParam(checkToken = true)
    @GetMapping("{netClassId}/buyAfterSyllabus")
    public Object buyAfterSyllabus(
            @Token UserSession userSession,
            @RequestHeader(value = "terminal") int terminal,
            @RequestHeader(value = "cv") String cv,
            @RequestParam(defaultValue = "") String teacherId,
            @RequestParam(defaultValue = "0") int stageNodeId,
            @RequestParam(defaultValue = "1") int position,
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int pageSize,
            @RequestParam(defaultValue = "0") int nextClassNodeId,
            @RequestParam(defaultValue = "0") int nextCoursewareNodeId,
            @PathVariable int netClassId,
            @RequestParam(defaultValue = "0") int coursewareNodeId,
            @RequestParam(defaultValue = "") String classNodeId,
            @RequestParam(defaultValue = "0") int afterNodeId,
            @RequestParam(defaultValue = "0") int beforeNodeId
    ) {
        HashMap<String, Object> map = LocalMapParamHandler.get();
        Object response = ResponseUtil.build(syllabusService.buyAfterSyllabus(map));
        //添加答题信息
        courseUtil.addExercisesCardInfo((LinkedHashMap) response, userSession.getId());
        return response;
    }

    /**
     * 获取课程大纲-售前
     */
    @LocalMapParam
    @GetMapping("/{classId}/classSyllabus")
    public Object classSyllabus(
            @Token UserSession userSession,
            @RequestHeader(value = "terminal") int terminal,
            @PathVariable("classId") int classId,
            @RequestParam int parentId,
            @RequestParam(defaultValue = "0") int onlyTrial,
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "20") int pageSize
    ) {
        HashMap<String, Object> map = LocalMapParamHandler.get();
        return ResponseUtil.build(syllabusService.classSyllabus(map));
    }



    /**
     * 获取课程大纲-售前-不用登录
     */
    @LocalMapParam
    @GetMapping("/{classId}/classSyllabusWithoutSession")
    public Object classSyllabusWithoutSession(
            @RequestHeader(value = "terminal") int terminal,
            @PathVariable("classId") int classId,
            @RequestParam int parentId,
            @RequestParam(defaultValue = "0") int onlyTrial,
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "20") int pageSize
    ) {
        HashMap<String, Object> map = LocalMapParamHandler.get();
        return ResponseUtil.build(syllabusService.classSyllabus(map));
    }
}
