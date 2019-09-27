package com.huatu.tiku.course.web.controller.v5;

import com.google.common.collect.Maps;
import com.huatu.springboot.web.version.mapping.annotation.ApiVersion;
import com.huatu.tiku.common.bean.user.UserSession;
import com.huatu.tiku.course.consts.SyllabusInfo;
import com.huatu.tiku.course.netschool.api.v4.CommonServiceV4;
import com.huatu.tiku.course.service.v5.CommonServiceV5Biz;
import com.huatu.tiku.course.util.ResponseUtil;
import com.huatu.tiku.springboot.users.support.Token;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;

/**
 * 描述：
 *
 * @author biguodong
 * Create time 2019-09-27 4:13 PM
 **/
@RestController
@RequestMapping(value = "/common")
@ApiVersion("v5")
public class CommonControllerV5 {


    @Autowired
    private CommonServiceV5Biz commonServiceV5Biz;

    /**
     * 上课token
     * @param terminal
     * @param userSession
     * @param coursewareId
     * @param netClassId
     * @param videoType
     * @return
     */
    @GetMapping(value = "/class/token")
    public Object classToken(
            @RequestHeader(value = "terminal") int terminal,
            @Token UserSession userSession,
            @RequestParam(value = "coursewareId") long coursewareId,
            @RequestParam(value = "netClassId") long netClassId,
            @RequestParam(value = "videoType") int videoType,
            @RequestParam(value = "syllabusNodeId") long syllabsusId,
            @RequestParam(value = "subjectType") int subjectType,
            @RequestParam(value = "buildType") int buildType,
            @RequestParam(value = "afterCoreseNum") int afterCoreseNum){

        HashMap<String, Object> params = Maps.newHashMap();
        params.put("terminal", terminal);
        params.put(SyllabusInfo.CourseWareId, coursewareId);
        params.put(SyllabusInfo.NetClassId, netClassId);
        params.put(SyllabusInfo.VideoType, videoType);
        params.put("userName", userSession.getUname());
        return commonServiceV5Biz.classToken(params, userSession.getId(), syllabsusId, subjectType, buildType, afterCoreseNum);
    }
}
