package com.huatu.tiku.course.service.v1;

import com.huatu.tiku.entity.CourseBreakpointQuestion;
import service.BaseServiceHelper;

import java.util.List;
import java.util.Map;

/**
 * Created by lijun on 2018/6/20
 */
public interface CourseBreakpointQuestionService extends BaseServiceHelper<CourseBreakpointQuestion> {

    /**
     * 根据断点ID 获取试题信息
     *
     * @param breakpointId
     * @return
     */
    List<Map<String, Object>> listQuestionIdByPointId(Long breakpointId);

    /**
     * 根据端点合集 获取 所有的试题集合
     * @param list
     * @return
     */
    List<CourseBreakpointQuestion> listQuestionIdByBreakpointIdList(List<Long> list);

}
