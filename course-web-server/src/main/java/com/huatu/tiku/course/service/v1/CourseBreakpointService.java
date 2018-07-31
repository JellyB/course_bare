package com.huatu.tiku.course.service.v1;

import com.huatu.tiku.entity.CourseBreakpoint;
import service.BaseServiceHelper;

import java.util.List;

/**
 * Created by lijun on 2018/6/19
 */
public interface CourseBreakpointService extends BaseServiceHelper<CourseBreakpoint> {

    /**
     * 根据课程类型、ID 查询知识点数据
     *
     * @param courseType 课程类型
     * @param courseId   课程ID
     * @return
     */
    List<CourseBreakpoint> listByCourseTypeAndId(Integer courseType, Long courseId);

    /**
     * 获取某个课程下的所有试题信息
     *
     * @param courseType 课程类型
     * @param courseId   课程ID
     * @return
     */
    List<Long> listAllQuestionId(Integer courseType, Long courseId);
}
