package com.huatu.tiku.course.service.v6;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 * 描述：
 *
 * @author biguodong
 * Create time 2019-01-08 下午11:12
 **/
public interface CourseServiceV6Biz {

    /**
     * 获取次课程classId的解析课信息
     * 模考大赛专用
     * @param classId
     * @return
     */
    LinkedHashMap<String, Object> getClassAnalysis(int classId);
}
