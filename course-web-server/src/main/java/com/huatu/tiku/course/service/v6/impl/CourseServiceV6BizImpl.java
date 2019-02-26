package com.huatu.tiku.course.service.v6.impl;

import com.alibaba.fastjson.JSONObject;
import com.google.common.collect.Maps;
import com.huatu.tiku.course.bean.NetSchoolResponse;
import com.huatu.tiku.course.common.EstimateCourseRedisKey;
import com.huatu.tiku.course.netschool.api.v6.CourseServiceV6;
import com.huatu.tiku.course.service.v6.CourseServiceV6Biz;
import com.huatu.tiku.course.util.CourseCacheKey;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.connection.RedisConnection;
import org.springframework.data.redis.connection.RedisZSetCommands;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.stereotype.Service;

import java.text.SimpleDateFormat;
import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

/**
 * 描述：
 *
 * @author biguodong
 *         Create time 2019-01-08 下午11:13
 **/

@Service
@Slf4j
public class CourseServiceV6BizImpl implements CourseServiceV6Biz {

    /**
     * response from php
     */
    private static final String ORIGIN_TITLE = "title";
    private static final String ORIGIN_PRICE = "price";
    private static final String ORIGIN_LIVE_TIME = "liveDate";
    /**
     * response
     */
    private static final String RESPONSE_TITLE = "courseTitle";
    private static final String RESPONSE_LIVE_TIME = "liveDate";
    private static final String RESPONSE_PRICE = "price";
    private static final String RESPONSE_CLASS_ID = "classId";

    private static final String RESPONSE_CLASS_IDS = "classIds";

    @Autowired
    private RedisTemplate redisTemplate;

    @Autowired
    private CourseServiceV6 courseService;

    /**
     * 模考大赛解析课信息,多个id使用逗号分隔
     * 模考大赛专用
     *
     * @param classIds
     * @return
     */
    @Override
    public HashMap<String, LinkedHashMap> getClassAnalysis(String classIds) {
        HashMap<String, LinkedHashMap> responseMap = Maps.newHashMap();
        SimpleDateFormat courseDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS");
        try {
            String[] classArray = classIds.split(",");
            if (ArrayUtils.isEmpty(classArray)) {
                return responseMap;
            }
            for (String s : classArray) {
                LinkedHashMap<String, Object> linkedHashMap = Maps.newLinkedHashMap();
                int classId = Integer.valueOf(s);
                NetSchoolResponse netSchoolResponse = obtainNetNetSchoolResponseFromCache(classId);
                @SuppressWarnings("unchecked")
                LinkedHashMap<String, Object> result = (LinkedHashMap<String, Object>) netSchoolResponse.getData();
                if (!result.containsKey(ORIGIN_PRICE) || !result.containsKey(ORIGIN_LIVE_TIME)) {
                    return Maps.newLinkedHashMap();
                }
                String liveDate = String.valueOf(result.get(ORIGIN_LIVE_TIME));
                if (StringUtils.isEmpty(liveDate)) {
                    liveDate = courseDateFormat.format(new Date());
                }
                Date date = courseDateFormat.parse(liveDate);
                linkedHashMap.put(RESPONSE_TITLE, result.get(ORIGIN_TITLE));
                linkedHashMap.put(RESPONSE_LIVE_TIME, date.getTime());
                linkedHashMap.put(RESPONSE_PRICE, result.get(ORIGIN_PRICE));
                linkedHashMap.put(RESPONSE_CLASS_ID, classId);
                responseMap.put(s, linkedHashMap);
            }

            return responseMap;
        } catch (Exception e) {
            log.error("parse time info error, for classId:{}", classIds);
            return Maps.newLinkedHashMap();
        }
    }

    /**
     * 分页查询小模考历史解析课列表
     *
     * @param subject
     * @param page
     * @param size
     * @param startTime
     * @param endTime   @return
     */
    @Override
    public NetSchoolResponse analysisClassList(int subject, int page, int size, long startTime, long endTime) {
        RedisConnection connection = redisTemplate.getConnectionFactory().getConnection();
        String smallEstimateCourseIdKey = EstimateCourseRedisKey.getSmallEstimateCourseIdKey(subject);
        String courseIds = "";
        try {
            Set<RedisZSetCommands.Tuple> tuples = connection.zRangeWithScores(smallEstimateCourseIdKey.getBytes(), 0, -1);
            List<RedisZSetCommands.Tuple> all = tuples.stream().filter(i -> i.getScore().longValue() >= startTime)
                    .filter(i -> i.getScore().longValue() <= endTime)
                    .collect(Collectors.toList());
            //降序排序
            Collections.sort(all, (o1, o2) -> o2.getScore() - o1.getScore() > 0 ? 1 : -1);
            int startIndex = (page - 1) * size;
            int endIndex = page * size > all.size() ? all.size() : page * size;
            if (startIndex < 0 || startIndex >= endIndex) {
                return NetSchoolResponse.DEFAULT;
            }
            courseIds = all.subList(startIndex, endIndex)
                    .stream()
                    .map(RedisZSetCommands.Tuple::getValue)
                    .map(String::new)
                    .collect(Collectors.joining(","));
            Map<String, Object> params = Maps.newHashMap();
            params.put(RESPONSE_CLASS_IDS, courseIds);
            NetSchoolResponse netSchoolResponse = courseService.analysisClassList(params);
            Object data = netSchoolResponse.getData();
            if(data instanceof LinkedHashMap){
                ((LinkedHashMap) data).put("current_page",page);
                ((LinkedHashMap) data).put("last_page",page*size>=all.size()?page:page+1);
                ((LinkedHashMap) data).put("per_page",size);
                ((LinkedHashMap) data).put("from",startIndex);
                ((LinkedHashMap) data).put("to",endIndex);
                ((LinkedHashMap) data).put("total",all.size());

            }
            return netSchoolResponse;
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            connection.close();
        }

        return NetSchoolResponse.DEFAULT;
    }

    /**
     * 缓存课程信息
     *
     * @param classId
     * @return
     */
    private NetSchoolResponse obtainNetNetSchoolResponseFromCache(int classId) {
        Map<String, Object> params = Maps.newHashMap();
        params.put(RESPONSE_CLASS_ID, classId);
        String key = CourseCacheKey.courseAnalysisV6(String.valueOf(classId));
        ValueOperations<String, String> valueOperations = redisTemplate.opsForValue();

        NetSchoolResponse netSchoolResponse;
        if (null == valueOperations.get(key)) {
            netSchoolResponse = courseService.analysis(params);
            if (null != netSchoolResponse.getData()) {
                valueOperations.set(key, JSONObject.toJSONString(netSchoolResponse.getData()), 1, TimeUnit.DAYS);
            } else {
                log.info("obtain course info from php client error, classId:{}", classId);
                return NetSchoolResponse.newInstance(Maps.newLinkedHashMap());
            }
        } else {
            String valueStr = String.valueOf(valueOperations.get(key));
            LinkedHashMap valueData = JSONObject.parseObject(valueStr, LinkedHashMap.class);
            netSchoolResponse = NetSchoolResponse.newInstance(valueData);
        }
        return netSchoolResponse;
    }
}
