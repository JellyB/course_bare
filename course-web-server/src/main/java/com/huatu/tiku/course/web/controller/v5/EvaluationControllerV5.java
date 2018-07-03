package com.huatu.tiku.course.web.controller.v5;

import com.huatu.common.utils.collection.HashMapBuilder;
import com.huatu.springboot.web.version.mapping.annotation.ApiVersion;
import com.huatu.tiku.common.bean.user.UserSession;
import com.huatu.tiku.course.netschool.api.v5.EvaluationServiceV5;
import com.huatu.tiku.course.util.ResponseUtil;
import com.huatu.tiku.springboot.users.support.Token;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;

/**
 * Created by lijun on 2018/6/25
 */
@Slf4j
@RestController
@RequestMapping("evaluation")
@ApiVersion("v5")
public class EvaluationControllerV5 {

    @Autowired
    private EvaluationServiceV5 evaluationService;

    /**
     * 获取售前课程详情评价列表
     */
    @GetMapping("getClassEvaluation")
    public Object getClassEvaluation(
            @RequestHeader int terminal,
            @RequestParam int classId,
            @RequestParam(defaultValue = "0") int isLive,
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int pageSize
    ) {
        HashMap<String, Object> map = HashMapBuilder.<String, Object>newBuilder()
                .put("classId", classId)
                .put("terminal", terminal)
                .put("isLive", isLive)
                .put("page", page)
                .put("pageNum", pageSize)
                .build();
        return ResponseUtil.build(evaluationService.getClassEvaluation(map));
    }

    /**
     * 获取老师介绍页评价列表
     */
    @GetMapping("getTeacherEvaluation")
    public Object getTeacherEvaluation(
            @RequestHeader int terminal,
            @RequestParam int teacherId,
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int pageNum
    ) {
        HashMap<String, Object> map = HashMapBuilder.<String, Object>newBuilder()
                .put("terminal", terminal)
                .put("teacherId", teacherId)
                .put("page", page)
                .put("pageNum", pageNum)
                .build();
        return ResponseUtil.build(evaluationService.getTeacherEvaluation(map));
    }

    /**
     * 获取用户当前课件评价信息接口
     */
    @GetMapping("getClickEvaluation")
    public Object getClickEvaluation(
            @Token UserSession userSession,
            @RequestParam int classId,
            @RequestParam int lessonId
    ) {
        HashMap<String, Object> map = HashMapBuilder.<String, Object>newBuilder()
                .put("userName", userSession.getUname())
                .put("classId", classId)
                .put("lessonId", lessonId)
                .build();
        return ResponseUtil.build(evaluationService.getClickEvaluation(map));
    }

    /**
     * 提交评价
     */
    @PostMapping("submit")
    public Object submit(
            @Token UserSession userSession,
            @RequestHeader int terminal,
            @RequestParam int classId,
            @RequestParam String evaluation,
            @RequestParam int lessonId,
            @RequestParam String score
    ) {
        HashMap<String, Object> map = HashMapBuilder.<String, Object>newBuilder()
                .put("classId", classId)
                .put("evaluation", evaluation)
                .put("lessonId", lessonId)
                .put("score", score)
                .put("terminal", terminal)
                .put("userName", userSession.getUname())
                .build();
        return ResponseUtil.build(evaluationService.submit(map));
    }
}
