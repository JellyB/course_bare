package com.huatu.tiku.course.netschool.api.fall;

import com.huatu.tiku.course.bean.NetSchoolResponse;
import com.huatu.tiku.course.netschool.api.v6.UserCourseServiceV6;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.Map;

/**
 * 描述：
 *
 * @author biguodong
 * Create time 2019-01-23 下午1:32
 **/
@Slf4j
@Component
public class UserCourseServiceV6FallBack implements UserCourseServiceV6 {

    /**
     * 我的学习-日历接口
     *
     * @param params
     * @return
     */
    @Override
    public NetSchoolResponse obtainLearnCalender(Map<String, Object> params) {
        log.error("response from call back obtainLearnCalender");
        return NetSchoolResponse.DEFAULT;
    }

    /**
     * 我的-已过期课程
     *
     * @param params
     * @return
     */
    @Override
    public NetSchoolResponse obtainExpiredCourses(Map<String, Object> params) {
        return NetSchoolResponse.DEFAULT;
    }

    /**
     * 我的课程等筛选列表
     *
     * @param params
     * @return
     */
    @Override
    public NetSchoolResponse obtainCourseFilterList(Map<String, Object> params) {
        return NetSchoolResponse.DEFAULT;
    }

    /**
     * 我的课程
     *
     * @param params
     * @return
     */
    @Override
    public NetSchoolResponse obtainMineCourses(Map<String, Object> params) {
        log.error("response from call back obtainMineCourses");
        return NetSchoolResponse.DEFAULT;
    }

    /**
     * 一键清除我的已过期课程
     *
     * @param params
     * @return
     */
    @Override
    public NetSchoolResponse clearExpiredCourses(Map<String, Object> params) {
        return NetSchoolResponse.DEFAULT;
    }

    /**
     * 课程所属考试接口
     *
     * @param params
     * @return
     */
    @Override
    public NetSchoolResponse cateList(Map<String, Object> params) {
        return NetSchoolResponse.DEFAULT;
    }

    /**
     * 列表设置考试类型
     *
     * @param params
     * @return
     */
    @Override
    public NetSchoolResponse setCategory(Map<String, Object> params) {
        return NetSchoolResponse.DEFAULT;
    }

    /**
     * 直播学习记录上报
     *
     * @param params
     * @return
     */
    @Override
    public NetSchoolResponse saveLiveRecord(Map<String, Object> params) {
        return NetSchoolResponse.DEFAULT;
    }
}
