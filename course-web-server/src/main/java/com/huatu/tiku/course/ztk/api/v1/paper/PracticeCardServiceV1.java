package com.huatu.tiku.course.ztk.api.v1.paper;

import com.huatu.tiku.course.bean.NetSchoolResponse;
import com.huatu.tiku.course.ztk.api.fail.paper.PracticeCardServiceV1Fallback;
import com.huatu.ztk.commons.exception.BizException;
import org.springframework.cloud.netflix.feign.FeignClient;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;

/**
 * Created by lijun on 2018/6/22
 */
@FeignClient(value = "ztk-service", fallback = PracticeCardServiceV1Fallback.class, path = "/p")
public interface PracticeCardServiceV1 {

    /**
     * 创建课程 课后练习
     */
    @PostMapping(value = "/v2/practices/createCourseExercisesPracticeCard")
    Object createCourseExercisesPracticeCard(
            @RequestParam("terminal") Integer terminal,
            @RequestParam("subject") Integer subject,
            @RequestParam("userId") Integer uid,
            @RequestParam("name") String name,
            @RequestParam("courseType") Integer courseType,
            @RequestParam("courseId") Long courseId,
            @RequestParam("questionId") String questionId
    );

    /**
     * 创建课程 课中练习
     */
    @PostMapping(value = "/v2/practices/createCourseBreakPointPracticeCard")
    Object createCourseBreakPointPracticeCard(
            @RequestParam("terminal") Integer terminal,
            @RequestParam("subject") Integer subject,
            @RequestParam("userId") Integer uid,
            @RequestParam("name") String name,
            @RequestParam("courseType") Integer courseType,
            @RequestParam("courseId") Long courseId,
            @RequestParam("questionId") String questionId,
            @RequestBody List<Object> questionInfoList
    );

    /**
     * 查询课后练习答题卡信息
     */
    @PostMapping(value = "/v2/practices/{userId}/getCourseExercisesCardInfo")
    Object getCourseExercisesCardInfo(
            @RequestHeader("userId") long userId,
            @RequestBody List<HashMap<String, Object>> paramsList
    );

    /**
     * 查询课中练习答题卡信息
     */
    @PostMapping(value = "/v2/practices/{userId}/getCourseBreakPointCardInfo")
    Object getCourseBreakPointCardInfo(
            @RequestHeader("userId") long userId,
            @RequestBody List<HashMap<String, Object>> paramsList
    );

    /**
     * 根据id获取答题卡信息
     * @param token
     * @param terminal
     * @param id
     * @return
     */
    @GetMapping(value = "/v1/practices/{id}")
    NetSchoolResponse getAnswerCard(@RequestHeader(value = "token") String token, @RequestHeader(value = "terminal") int terminal, @PathVariable(value = "id") long id);
}
