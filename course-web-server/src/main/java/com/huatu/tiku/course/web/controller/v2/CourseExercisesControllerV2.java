package com.huatu.tiku.course.web.controller.v2;

import com.huatu.springboot.web.version.mapping.annotation.ApiVersion;
import com.huatu.tiku.common.bean.user.UserSession;
import com.huatu.tiku.course.service.manager.CourseExercisesProcessLogManager;
import com.huatu.tiku.springboot.users.support.Token;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;


/**
 * 描述：录播创建课后作业答题卡逻辑
 *
 * @author biguodong
 * Create time 2019-03-08 5:14 PM
 **/


@RestController
@RequestMapping("exercises")
@ApiVersion("v2")
public class CourseExercisesControllerV2 {



    @Autowired
    private CourseExercisesProcessLogManager courseExercisesProcessLogManager;
    /**
     * 创建课后训练答题卡
     *
     * @return
     */
    @GetMapping(value = "/{courseType}/{coursewareId}/card")
    public Object card(
            @Token UserSession userSession,
            @RequestHeader("terminal") Integer terminal,
            @PathVariable(value = "courseType") Integer courseType,
            @PathVariable(value = "coursewareId") Long coursewareId,
            @RequestParam(value = "courseId") Long courseId,
            @RequestParam(value = "syllabusId") Long syllabusId

    ) {
        return courseExercisesProcessLogManager.createCourseWorkAnswerCardEntrance(courseId, syllabusId, courseType, coursewareId, userSession.getSubject(), terminal, userSession.getId());
    }
}

