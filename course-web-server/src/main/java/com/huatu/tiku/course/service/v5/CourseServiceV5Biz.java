package com.huatu.tiku.course.service.v5;

import java.util.HashMap;

/**
 * 封装 courseService 接口
 * Created by lijun on 2018/6/25
 */
public interface CourseServiceV5Biz {

    /**
     * 获取课程详情
     */
    Object getClassDetail(HashMap<String, Object> params);

    /**
     * 获取课程介绍
     */
    Object getCourseIntroduction(String userName, int classId);

    /**
     *
     */
    String getClassExt(int classId, int terminal);
}
