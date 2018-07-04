package com.huatu.tiku.course.service.v5;

/**
 * 封装 courseService 接口
 * Created by lijun on 2018/6/25
 */
public interface CourseServiceV5Biz {

    /**
     * 获取课程说明
     */
    String getClassExt(int classId, int terminal);

    /**
     * 课程大纲-售前
     */
    Object findTimetable(int classId, int parentId, int page, int pageSize);
}
