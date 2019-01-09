package com.huatu.tiku.course.service.v6.impl;

import com.alibaba.fastjson.JSONObject;
import com.google.common.collect.Maps;
import com.huatu.tiku.course.bean.NetSchoolResponse;
import com.huatu.tiku.course.netschool.api.v6.CourseServiceV6;
import com.huatu.tiku.course.service.v6.CourseServiceV6Biz;
import com.huatu.tiku.course.util.CourseCacheKey;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.stereotype.Service;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

/**
 * 描述：
 *
 * @author biguodong
 * Create time 2019-01-08 下午11:13
 **/

@Service
@Slf4j
public class CourseServiceV6BizImpl implements CourseServiceV6Biz {

    private static final String COURSE_TITLE_FORMAT = "￥%s元领取解析课";
    private static final String COURSE_TIME_INFO_FORMAT = "%s月%s日（%s）%s开始直播";
    private static final String RESPONSE_TITLE = "courseTitle";
    private static final String RESPONSE_LIVE_INFO = "liveInfo";
    private static final String RESPONSE_CLASS_ID = "classId";

    @Autowired
    private RedisTemplate redisTemplate;

    @Autowired
    private CourseServiceV6 courseService;

    /**
     * 获取次课程classId的解析课信息
     * 模考大赛专用
     *
     * @param classId
     * @return
     */
    @Override
    public LinkedHashMap<String, Object> getClassAnalysis(int classId) {
        LinkedHashMap<String,Object> linkedHashMap = Maps.newLinkedHashMap();
        SimpleDateFormat courseDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS");
        SimpleDateFormat hourMinuteFormat = new SimpleDateFormat("HH:mm");
        try{
            NetSchoolResponse netSchoolResponse = obtainNetNetSchoolResponseFromCache(classId);
            @SuppressWarnings("unchecked")
            LinkedHashMap<String,Object> result = (LinkedHashMap<String, Object>)netSchoolResponse.getData();
            if(!result.containsKey(COURSE_PRICE) || !result.containsKey(COURSE_LIVE)){
                return Maps.newLinkedHashMap();
            }
            String price = String.valueOf(result.get(COURSE_PRICE));
            String liveDate = String.valueOf(result.get(COURSE_LIVE));
            if(StringUtils.isEmpty(liveDate)){
                liveDate = courseDateFormat.format(new Date());
            }
            Date date = courseDateFormat.parse(liveDate);
            Calendar instance = Calendar.getInstance();
            instance.setTime(date);
            int month  = instance.get(Calendar.MONTH) + 1;
            int day = instance.get(Calendar.DAY_OF_MONTH);
            String dayOfWeek = getDayInfo(instance);
            String hourMinute = hourMinuteFormat.format(date);
            String courseTitle = String.format(COURSE_TITLE_FORMAT, price);
            String courseTimeInfo = String.format(COURSE_TIME_INFO_FORMAT, month, day, dayOfWeek, hourMinute);
            linkedHashMap.put(RESPONSE_TITLE, courseTitle);
            linkedHashMap.put(RESPONSE_LIVE_INFO, courseTimeInfo);
            linkedHashMap.put(RESPONSE_CLASS_ID, classId);
            return linkedHashMap;
        }catch (Exception e){
            log.error("parse time info error, for classId:{}", classId);
            return Maps.newLinkedHashMap();
        }
    }

    /**
     * 缓存课程信息
     * @param classId
     * @return
     */
    private NetSchoolResponse obtainNetNetSchoolResponseFromCache(int classId){
        Map<String, Object> params = Maps.newHashMap();
        params.put(CourseServiceV6Biz.CLASS_ID, classId);
        String key = CourseCacheKey.courseAnalysisV6(String.valueOf(classId));
        ValueOperations<String, String> valueOperations = redisTemplate.opsForValue();

        NetSchoolResponse netSchoolResponse;
        if(null == valueOperations.get(key)){
            netSchoolResponse = courseService.analysis(params);
            if(null != netSchoolResponse.getData()){
                valueOperations.set(key, JSONObject.toJSONString(netSchoolResponse.getData()), 30, TimeUnit.DAYS);
            }else{
                log.info("obtain course info from php client error, classId:{}", classId);
                return NetSchoolResponse.newInstance(Maps.newLinkedHashMap());
            }
        }else{
            String valueStr = String.valueOf(valueOperations.get(key));
            LinkedHashMap valueData = JSONObject.parseObject(valueStr, LinkedHashMap.class);
            netSchoolResponse = NetSchoolResponse.newInstance(valueData);
        }
        return netSchoolResponse;
    }
    /**
     * 返回每周 day 对应的中文
     * @param calendar
     * @return
     */
    private static String getDayInfo(Calendar calendar){
        String dayString = StringUtils.EMPTY;
        if(null == calendar){
            return dayString;
        }

        int day = calendar.get(Calendar.DAY_OF_WEEK);
        switch (day) {
            case Calendar.SUNDAY:
                dayString = "周日";
                break;

            case Calendar.MONDAY:
                dayString = "周一";
                break;

            case Calendar.TUESDAY:
                dayString = "周二";
                break;
            case Calendar.WEDNESDAY:
                dayString = "周三";
                break;
            case Calendar.THURSDAY:
                dayString = "周四";
                break;
            case Calendar.FRIDAY:
                dayString = "周五";
                break;

            case Calendar.SATURDAY:
                dayString = "周六";
                break;
            default:
                break;
        }
        return dayString;
    }
}
