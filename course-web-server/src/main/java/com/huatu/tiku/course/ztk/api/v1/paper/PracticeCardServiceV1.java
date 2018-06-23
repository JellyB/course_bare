package com.huatu.tiku.course.ztk.api.v1.paper;

import com.huatu.tiku.course.ztk.api.fail.paper.PracticeCardServiceV1Fallback;
import org.springframework.cloud.netflix.feign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

/**
 * Created by lijun on 2018/6/22
 */
@FeignClient(value = "ztk-service",fallback = PracticeCardServiceV1Fallback.class,path = "/p")
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
            @RequestParam("questionId") String questionId
    );
}
