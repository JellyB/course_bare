package com.huatu.tiku.course.web.controller.v2;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.huatu.springboot.web.version.mapping.annotation.ApiVersion;
import com.huatu.tiku.common.bean.user.UserSession;
import com.huatu.tiku.course.service.manager.CourseExercisesProcessLogManager;
import com.huatu.tiku.course.service.v1.CourseExercisesService;
import com.huatu.tiku.course.util.ZTKResponseUtil;
import com.huatu.tiku.course.ztk.api.v1.paper.PracticeCardServiceV1;
import com.huatu.tiku.springboot.users.support.Token;
import com.huatu.ztk.paper.bean.AnswerCard;
import com.huatu.ztk.paper.common.AnswerCardStatus;
import org.apache.commons.collections.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 描述：
 *
 * @author biguodong
 * Create time 2019-03-08 5:14 PM
 **/


@RestController
@RequestMapping("exercises")
@ApiVersion("v2")
public class CourseExercisesControllerV2 {

    @Autowired
    private CourseExercisesService service;

    @Autowired
    private PracticeCardServiceV1 practiceCardService;

    @Autowired
    private ObjectMapper objectMapper;

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
        List<Map<String, Object>> list = service.listQuestionByCourseId(courseType, coursewareId);
        if (CollectionUtils.isEmpty(list)) {
            return null;
        }
        int subjectId = userSession.getSubject();
        String questionId = list.stream()
                .filter(map -> null != map && null != map.get("id"))
                .map(map -> String.valueOf(map.get("id")))
                .collect(Collectors.joining(","));
        Object practiceCard = practiceCardService.createCourseExercisesPracticeCard(
                terminal, subjectId, userSession.getId(), "课后作业练习",
                courseType, courseId, questionId
        );
        HashMap<String, Object> result = (HashMap<String, Object>) ZTKResponseUtil.build(practiceCard);
        if (null == result) {
            //return ErrorResult.create(5000000, "暂无答题卡信息");
            return null;
        }
        result.computeIfPresent("id", (key, value) -> String.valueOf(value));

        courseExercisesProcessLogManager.createCourseWorkAnswerCard(userSession.getId(), courseType, coursewareId, courseId, syllabusId, result);
        return result;
    }
}

