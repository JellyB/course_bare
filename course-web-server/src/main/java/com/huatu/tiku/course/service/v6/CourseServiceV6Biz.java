package com.huatu.tiku.course.service.v6;

import com.huatu.tiku.course.bean.NetSchoolResponse;

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
     * 模考大赛解析课信息,多个id使用逗号分隔
     * 模考大赛专用
     * @param classIds
     * @return
     */
    HashMap<String, LinkedHashMap> getClassAnalysis(String classIds);

    /**
     * 小模考历史解析课分页查询
     * @param subject
     * @param page
     * @param size
     * @param startTime
     *@param endTime @return
     */
    NetSchoolResponse analysisClassList(int subject, int page, int size, long startTime, long endTime);
}
