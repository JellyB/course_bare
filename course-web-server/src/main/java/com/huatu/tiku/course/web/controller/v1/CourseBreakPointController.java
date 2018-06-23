package com.huatu.tiku.course.web.controller.v1;

import com.huatu.springboot.web.version.mapping.annotation.ApiVersion;
import com.huatu.tiku.common.bean.user.UserSession;
import com.huatu.tiku.course.service.v1.CourseBreakpointQuestionService;
import com.huatu.tiku.course.service.v1.CourseBreakpointService;
import com.huatu.tiku.course.util.ZTKResponseUtil;
import com.huatu.tiku.course.ztk.api.v1.paper.PracticeCardServiceV1;
import com.huatu.tiku.entity.CourseBreakpoint;
import com.huatu.tiku.springboot.users.support.Token;
import org.apache.commons.collections.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Created by lijun on 2018/6/19
 */
@RestController
@RequestMapping("breakPoint")
@ApiVersion("v1")
public class CourseBreakPointController {

    @Autowired
    private CourseBreakpointService service;

    @Autowired
    private CourseBreakpointQuestionService breakpointQuestionService;

    @Autowired
    private PracticeCardServiceV1 practiceCardService;

    /**
     * 根据课程ID、课程类型获取端点集合并分组
     *
     * @return
     */
    @GetMapping(value = "/{courseType}/{courseId}")
    public Object list(
            @PathVariable(value = "courseType") Integer courseType,
            @PathVariable(value = "courseId") Long courseId
    ) {
        Map<Integer, List<CourseBreakpoint>> listMap = service.listByCourseTypeAndId(courseType, courseId);
        return listMap;
    }

    /**
     * 根据断点ID 查询数据
     *
     * @return
     */
    @GetMapping(value = "/{breakQuestionId}")
    public Object listQuestion(
            @PathVariable("breakQuestionId") long breakQuestionId
    ) {
        List<Map<String, Object>> mapList = breakpointQuestionService.listQuestionIdByPointId(breakQuestionId);
        return mapList;
    }

    /**
     * 创建课中训练答题卡
     *
     * @return
     */
    @GetMapping(value = "/{courseType}/{courseId}/card")
    public Object card(
            @Token UserSession userSession,
            @RequestHeader("terminal") Integer terminal,
            @PathVariable(value = "courseType") Integer courseType,
            @PathVariable(value = "courseId") Long courseId
    ) {
        List<Long> list = service.listAllQuestionId(courseType, courseId);
        if (CollectionUtils.isEmpty(list)) {
            return null;
        }
        int subjectId = userSession.getSubject();
        String questionId = list.stream().map(String::valueOf).collect(Collectors.joining(","));
        Object practiceCard = practiceCardService.createCourseBreakPointPracticeCard(
                terminal, subjectId, userSession.getId(), "课中练习",
                courseType, courseId, questionId
        );
        return ZTKResponseUtil.build(practiceCard);
    }
}
