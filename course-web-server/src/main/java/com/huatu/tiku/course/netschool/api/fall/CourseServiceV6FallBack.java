package com.huatu.tiku.course.netschool.api.fall;

import com.huatu.tiku.course.bean.NetSchoolResponse;
import com.huatu.tiku.course.netschool.api.v6.CourseServiceV6;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.Map;

/**
 * 描述：
 *
 * @author biguodong
 * Create time 2019-01-23 下午1:28
 **/
@Slf4j
@Component
public class CourseServiceV6FallBack implements CourseServiceV6 {


    /**
     * app课程列表
     *
     * @param params
     * @return
     */
    @Override
    public NetSchoolResponse obtainCourseList(Map<String, Object> params) {
        return NetSchoolResponse.DEFAULT;
    }

    /**
     * 日历详情接口
     *
     * @param params
     * @return
     */
    @Override
    public NetSchoolResponse calendarDetail(Map<String, Object> params) {
        log.error("response from call back calendarDetail");
        return NetSchoolResponse.DEFAULT;
    }

    /**
     * 课程分类详情
     *
     * @param params
     * @return
     */
    @Override
    public NetSchoolResponse courseTypeDetail(Map<String, Object> params) {
        return NetSchoolResponse.DEFAULT;
    }

    /**
     * 课程搜索接口
     *
     * @param params
     * @return
     */
    @Override
    public NetSchoolResponse searchCourses(Map<String, Object> params) {
        return NetSchoolResponse.DEFAULT;
    }

    /**
     * 合集课程列表
     *
     * @param params
     * @return
     */
    @Override
    public NetSchoolResponse collectDetail(Map<String, Object> params) {
        return NetSchoolResponse.DEFAULT;
    }

    /**
     * 获取解析课课程信息 pc 端模考大赛专用
     *
     * @param params
     * @return
     */
    @Override
    public NetSchoolResponse analysis(Map<String, Object> params) {
        return NetSchoolResponse.DEFAULT;
    }
}
