package com.huatu.tiku.course.service.v6.impl;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.stereotype.Service;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.TypeReference;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.huatu.tiku.course.bean.NetSchoolResponse;
import com.huatu.tiku.course.bean.vo.PeriodTestListVO;
import com.huatu.tiku.course.bean.vo.PeriodTestListVO.PeriodTestInfo;
import com.huatu.tiku.course.bean.vo.PeriodTestListVO.PeriodTestListVOBuilder;
import com.huatu.tiku.course.dao.manual.CourseExercisesProcessLogMapper;
import com.huatu.tiku.course.netschool.api.v6.CourseServiceV6;
import com.huatu.tiku.course.netschool.api.v6.UserCourseServiceV6;
import com.huatu.tiku.course.service.v6.CourseServiceV6Biz;
import com.huatu.tiku.course.util.CourseCacheKey;
import com.huatu.tiku.course.util.ResponseUtil;
import com.huatu.tiku.entity.CourseExercisesProcessLog;

import lombok.extern.slf4j.Slf4j;
import tk.mybatis.mapper.entity.Example;

/**
 * 描述：
 *
 * @author biguodong
 * Create time 2019-01-08 下午11:13
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


    @Autowired
    private RedisTemplate redisTemplate;

    @Autowired
    private CourseServiceV6 courseService;
    
    @Autowired
    private UserCourseServiceV6 userCourseServiceV6;
    
    @Autowired
    private CourseExercisesProcessLogMapper courseExercisesProcessLogMapper;

    /**
     * 模考大赛解析课信息,多个id使用逗号分隔
     * 模考大赛专用
     *
     * @param classIds
     * @return
     */
    @Override
    public HashMap<String, LinkedHashMap> getClassAnalysis(String classIds) {
        HashMap<String,LinkedHashMap> responseMap = Maps.newHashMap();
        SimpleDateFormat courseDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS");
        try{
            String[] classArray = classIds.split(",");
            if(ArrayUtils.isEmpty(classArray)){
                return responseMap;
            }
            for (String s : classArray) {
                LinkedHashMap<String,Object> linkedHashMap = Maps.newLinkedHashMap();
                int classId = Integer.valueOf(s);
                NetSchoolResponse netSchoolResponse = obtainNetNetSchoolResponseFromCache(classId);
                @SuppressWarnings("unchecked")
                LinkedHashMap<String,Object> result = (LinkedHashMap<String, Object>)netSchoolResponse.getData();
                if(!result.containsKey(ORIGIN_PRICE) || !result.containsKey(ORIGIN_LIVE_TIME)){
                    return Maps.newLinkedHashMap();
                }
                String liveDate = String.valueOf(result.get(ORIGIN_LIVE_TIME));
                if(StringUtils.isEmpty(liveDate)){
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
        }catch (Exception e){
            log.error("parse time info error, for classId:{}", classIds);
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
        params.put(RESPONSE_CLASS_ID, classId);
        String key = CourseCacheKey.courseAnalysisV6(String.valueOf(classId));
        ValueOperations<String, String> valueOperations = redisTemplate.opsForValue();

        NetSchoolResponse netSchoolResponse;
        if(null == valueOperations.get(key)){
            netSchoolResponse = courseService.analysis(params);
            if(null != netSchoolResponse.getData()){
                valueOperations.set(key, JSONObject.toJSONString(netSchoolResponse.getData()), 1, TimeUnit.DAYS);
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
    

	@Override
	public Object periodTestList(Map<String, Object> params) {
		NetSchoolResponse response = userCourseServiceV6.unfinishStageExamList(params);
		if (ResponseUtil.isSuccess(response)) {
			List<PeriodTestListVO> periodTestList = Lists.newArrayList();
			String responseStr = JSON.toJSONString(response.getData());
			JSONObject data = JSON.parseObject(responseStr);
			String listStr = JSON.toJSONString(data.get("list"));
			LinkedHashMap<String, Object> jsonMap = JSON.parseObject(listStr,
					new TypeReference<LinkedHashMap<String, Object>>() {
					});
			for (Map.Entry<String, Object> courseInfo : jsonMap.entrySet()) {
				// 课程集合
				PeriodTestListVOBuilder periodTestListVOBuilder = PeriodTestListVO.builder();
				// log.info(courseInfo.getKey() + ":" + courseInfo.getValue());
				// 获取每个课程相关信息
				String courseInfoStr = JSON.toJSONString(courseInfo.getValue());
				JSONObject courseJson = JSON.parseObject(courseInfoStr);
				String classId = courseJson.getString("classId");
				String className = courseJson.getString("className");
				periodTestListVOBuilder.courseId(Integer.parseInt(classId)).courseTitle(className);
				String periodTestStr = JSON.toJSONString(courseJson.get("child"));
				LinkedHashMap<String, Object> periodTestMap = JSON.parseObject(periodTestStr,
						new TypeReference<LinkedHashMap<String, Object>>() {
						});

				List<PeriodTestListVO.PeriodTestInfo> periodList = Lists.newArrayList();
				for (Map.Entry<String, Object> periodTestInfo : periodTestMap.entrySet()) {
					// 阶段测试集合
					String periodTestInfoStr = JSON.toJSONString(periodTestInfo.getValue());
					PeriodTestInfo periodTestVo = JSON.parseObject(periodTestInfoStr,
							PeriodTestListVO.PeriodTestInfo.class);
					// TODO 校验是否开启提醒
					long examId = periodTestVo.getExamId();
					Example example = new Example(CourseExercisesProcessLog.class);
					example.or().andEqualTo("courseId", classId).andEqualTo("userId", params.get("userId"));
					int count = courseExercisesProcessLogMapper.selectCountByExample(example);
					if (count > 0) {
						periodTestVo.setIsAlert(1);
					}
					periodList.add(periodTestVo);
				}
				periodTestList
						.add(periodTestListVOBuilder.periodTestList(periodList).undoCount(periodList.size()).build());
			}
			Integer total = 2;
			Integer currentPage = 2;
			Integer lastPage = 2;
			Integer perPage = 2;
			Map<String, Object> retMap = Maps.newHashMap();
			retMap.put("total", total);
			retMap.put("currentPage", currentPage);
			retMap.put("lastPage", lastPage);
			retMap.put("perPage", perPage);
			retMap.put("list", periodTestList);
			return retMap;
		}
		return null;
	}
}
