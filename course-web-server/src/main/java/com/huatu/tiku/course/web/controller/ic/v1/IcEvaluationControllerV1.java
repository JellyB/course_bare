package com.huatu.tiku.course.web.controller.ic.v1;

import com.huatu.springboot.web.version.mapping.annotation.ApiVersion;
import com.huatu.tiku.course.netschool.api.v5.EvaluationServiceV5;
import com.huatu.tiku.course.spring.conf.aspect.mapParam.LocalMapParam;
import com.huatu.tiku.course.spring.conf.aspect.mapParam.LocalMapParamHandler;
import com.huatu.tiku.course.util.ResponseUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;

/**
 * 课程-评价 接口
 * Created by lijun on 2018/7/10
 */
@RestController
@RequestMapping("ic/evaluation")
@ApiVersion("v1")
public class IcEvaluationControllerV1 {

    @Autowired
    private EvaluationServiceV5 evaluationService;

    /**
     * 获取老师介绍页评价列表
     */
    @LocalMapParam
    @GetMapping("getTeacherEvaluation")
    public Object getTeacherEvaluation(
            @RequestParam int teacherId,
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int pageSize
    ) {
        HashMap<String, Object> map = LocalMapParamHandler.get();
        return ResponseUtil.build(evaluationService.getTeacherEvaluation(map));
    }

    /**
     * 获取售前课程详情评价列表
     */
    @LocalMapParam
    @GetMapping("getClassEvaluation")
    public Object getClassEvaluation(
            @RequestParam int classId,
            @RequestParam(defaultValue = "0") int isLive,
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int pageSize
    ) {
        HashMap<String, Object> map = LocalMapParamHandler.get();
        return ResponseUtil.build(evaluationService.getClassEvaluation(map));
    }

    /**
     * 提交评价
     */
    @LocalMapParam
    @PostMapping("submit")
    public Object submit(
            @RequestParam int classId,
            @RequestParam String evaluation,
            @RequestParam int lessonId,
            @RequestParam String score
    ) {
        HashMap<String, Object> map = LocalMapParamHandler.get();
        return ResponseUtil.build(evaluationService.submit(map));
    }

}
