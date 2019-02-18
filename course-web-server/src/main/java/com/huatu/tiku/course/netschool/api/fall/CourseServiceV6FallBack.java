package com.huatu.tiku.course.netschool.api.fall;

import com.google.common.collect.Lists;
import com.huatu.common.utils.web.RequestUtil;
import com.huatu.tiku.course.bean.CourseListV3DTO;
import com.huatu.tiku.course.bean.NetSchoolResponse;
import com.huatu.tiku.course.netschool.api.v6.CourseServiceV6;
import com.huatu.tiku.course.util.ResponseUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.Map;

import static com.huatu.tiku.course.bean.NetSchoolResponse.DEFAULT_ERROR;

/**
 * 描述：
 *
 * @author biguodong
 * Create time 2019-01-23 下午1:28
 **/
@Slf4j
@Component
public class CourseServiceV6FallBack implements CourseServiceV6 {


    private static final String CALENDAR_DETAIL_PRE = "_mock_calendar_detail$";


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
        log.warn("response from call back calendarDetail");
        String key = CALENDAR_DETAIL_PRE + RequestUtil.getParamSign(params);
        NetSchoolResponse response = FallbackCacheHolder.get(key);
        if(response == null){
            log.warn("obtain calendar detail not in fallbackHolder");
            return NetSchoolResponse.newInstance(Lists.newArrayList());
        }
        return response;
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

    /**
     * 缓存日历详情接口数据
     * @param params
     * @param response
     */
    public void setCalendarDetailStaticData(Map<String,Object> params, NetSchoolResponse response){
        String key = CALENDAR_DETAIL_PRE + RequestUtil.getParamSign(params);
        if(ResponseUtil.isSuccess(response)){
            FallbackCacheHolder.put(key, response);
        }
    }
}
