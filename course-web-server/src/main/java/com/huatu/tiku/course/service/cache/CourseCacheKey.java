package com.huatu.tiku.course.service.cache;


import com.huatu.common.utils.web.RequestUtil;

import java.util.HashMap;

/**
 * 课程相关 key
 * Created by lijun on 2018/6/25
 */
public final class CourseCacheKey {

    /**
     * 课程详情
     */
    public static String courseDetailKey(HashMap<String, Object> map) {
        String paramSign = RequestUtil.getParamSign(map);
        return "course:detail:" + paramSign;
    }

    /**
     * 课程介绍
     */
    public static String courseIntroductionKey(String userName, int classId) {
        return new StringBuilder("course:introduction:").append(userName).append(":").append(classId).toString();
    }

    /**
     * 课程详情 - 网页
     */
    public static String classExtKey(int classId, int terminal) {
        return new StringBuilder("course:classExt:").append(classId).append(":").append(terminal).toString();
    }

    /**
     * 课程大纲
     */
    public static String timeTableKey(int classId, int parentId, int page, int pageSize) {
        return new StringBuilder("course:timeTable")
                .append(":").append(classId)
                .append(":").append(parentId)
                .append(":").append(page)
                .append(":").append(pageSize)
                .toString();
    }
}
