package com.huatu.tiku.course.netschool.api.fall;

import com.google.common.collect.Lists;
import com.huatu.common.utils.web.RequestUtil;
import com.huatu.tiku.course.bean.NetSchoolResponse;
import com.huatu.tiku.course.bean.vo.PeriodTestListVO;
import com.huatu.tiku.course.netschool.api.v6.UserCourseServiceV6;
import com.huatu.tiku.course.util.ResponseUtil;
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


    private static final String CALENDAR_LEARN_PRE = "_mock_calendar_learn$";
    private static final String COURSE_MINE_PRE = "_course_mine_learn$";

    /**
     * 我的学习-日历接口
     *
     * @param params
     * @return
     */
    @Override
    public NetSchoolResponse obtainLearnCalender(Map<String, Object> params) {
        log.warn("response from call back obtainLearnCalender");
        String key = CALENDAR_LEARN_PRE + RequestUtil.getParamSign(params);
        NetSchoolResponse response = FallbackCacheHolder.get(key);
        if(null == response){
            log.warn("obtain learn calender not in fallbackHolder...");
            return NetSchoolResponse.newInstance(Lists.newArrayList());
        }
        return response;
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
        log.warn("response from call back obtainMineCourses");
        String key = COURSE_MINE_PRE + RequestUtil.getParamSign(params);
        NetSchoolResponse response = FallbackCacheHolder.get(key);
        if(null == response){
            log.warn("obtain mine courses not in fallbackHolder...");
            return NetSchoolResponse.newInstance(Lists.newArrayList());
        }
        return response;
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

    /**
     * 缓存我的学习日历接口
     * @param params
     * @param response
     */
    public void setCalendarLearnStaticData(Map<String,Object> params, NetSchoolResponse response){
        String key = CALENDAR_LEARN_PRE + RequestUtil.getParamSign(params);
        if(ResponseUtil.isSuccess(response)){
            FallbackCacheHolder.put(key, response);
        }
    }

    /**
     * 缓存我的课程数据接口
     * @param params
     * @param response
     */
    public void setCourseMineStaticData(Map<String,Object> params, NetSchoolResponse response){
        String key = COURSE_MINE_PRE + RequestUtil.getParamSign(params);
        if(ResponseUtil.isSuccess(response)){
            FallbackCacheHolder.put(key, response);
        }
    }

	@Override
	public NetSchoolResponse unfinishStageExamList(Map<String, Object> params) {
		return NetSchoolResponse.DEFAULT;
	}

	@Override
	public NetSchoolResponse<Integer> stageTestStudyRecord(Map<String, Object> params) {
		 return NetSchoolResponse.DEFAULT;
	}

	@Override
	public NetSchoolResponse<PeriodTestListVO> unfinishStageExamCount(Map<String, Object> params) {
		 return NetSchoolResponse.DEFAULT;
	}
}
