package com.huatu.tiku.course.service.v6;

import java.util.HashMap;
import java.util.LinkedHashMap;

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
     * @param classIds
     * @return
     */
    HashMap<String, LinkedHashMap> getClassAnalysis(String classIds);
}
