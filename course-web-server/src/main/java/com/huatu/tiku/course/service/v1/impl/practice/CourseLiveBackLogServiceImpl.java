package com.huatu.tiku.course.service.v1.impl.practice;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import com.alibaba.fastjson.JSONObject;
import com.google.common.collect.Maps;
import com.huatu.tiku.course.bean.NetSchoolResponse;
import com.huatu.tiku.course.netschool.api.v6.LessonServiceV6;
import com.huatu.tiku.course.util.CourseCacheKey;
import com.huatu.tiku.course.util.ResponseUtil;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.MapUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.stereotype.Service;

import com.huatu.tiku.course.service.v1.practice.CourseLiveBackLogService;
import com.huatu.tiku.entity.CourseLiveBackLog;

import lombok.extern.slf4j.Slf4j;
import service.impl.BaseServiceHelperImpl;
import tk.mybatis.mapper.entity.Example;
import tk.mybatis.mapper.weekend.WeekendSqls;

/**
 * 
 * @author zhangchong
 *
 */
@Service
@Slf4j
public class CourseLiveBackLogServiceImpl extends BaseServiceHelperImpl<CourseLiveBackLog>
		implements CourseLiveBackLogService {

	@Autowired
	private LessonServiceV6 lessonService;

	@Autowired
	private RedisTemplate redisTemplate;


	public CourseLiveBackLogServiceImpl() {
		super(CourseLiveBackLog.class);
	}

	@Override
	public CourseLiveBackLog findByRoomIdAndLiveCoursewareId(Long roomId, Long coursewareId) {

		String key = CourseCacheKey.findByRoomIdAndLiveCourseWareId(roomId, coursewareId);
		ValueOperations<String,String> operations = redisTemplate.opsForValue();
		if(redisTemplate.hasKey(key)){
			String value = operations.get(key);
			log.info("get live courseWareId from redis:roomId:{}, coursewareId:{}", roomId, coursewareId);
			return JSONObject.parseObject(value,CourseLiveBackLog.class);
		}
		final WeekendSqls<CourseLiveBackLog> weekendSqls = WeekendSqls.<CourseLiveBackLog>custom()
				.andEqualTo(CourseLiveBackLog::getRoomId, roomId)
				.andEqualTo(CourseLiveBackLog::getLiveBackCoursewareId, coursewareId);
		final Example example = Example.builder(CourseLiveBackLog.class).where(weekendSqls).build();
		List<CourseLiveBackLog> courseLiveBackLogList = selectByExample(example);
		if (CollectionUtils.isNotEmpty(courseLiveBackLogList)) {
			CourseLiveBackLog courseLiveBackLog = courseLiveBackLogList.get(0);
			operations.set(key, JSONObject.toJSONString(courseLiveBackLog), 30, TimeUnit.MINUTES);
			log.info("get live courseWareId from mysql:roomId:{}, coursewareId:{}", roomId, coursewareId);
			return courseLiveBackLog;
		}else{
			log.info("调用php获取直播回放对应的直播课件id:直播回放id:{},房间id:{}", coursewareId, roomId);
			Map<String,Object> params = Maps.newHashMap();
			params.put("liveBackCoursewareId",coursewareId);
			params.put("roomId", roomId);
			NetSchoolResponse netSchoolResponse = lessonService.obtainLiveWareId(params);
			if(ResponseUtil.isSuccess(netSchoolResponse)){
				LinkedHashMap<String,Object> result = (LinkedHashMap<String,Object>) netSchoolResponse.getData();
				long liveCourseWareId = MapUtils.getLong(result, "liveCoursewareId");
				if(liveCourseWareId == 0){
					return null;
				}
				CourseLiveBackLog courseLiveBackLog = new CourseLiveBackLog();
				courseLiveBackLog.setLiveBackCoursewareId(coursewareId);
				courseLiveBackLog.setRoomId(roomId);
				courseLiveBackLog.setLiveCoursewareId(liveCourseWareId);
				courseLiveBackLog.setLiveBackCoursewareId(coursewareId);
				courseLiveBackLog.setCreatorId(10L);
				insert(courseLiveBackLog);
				operations.set(key, JSONObject.toJSONString(courseLiveBackLog), 30, TimeUnit.MINUTES);
				log.info("get live courseWareId from remote by rest:roomId:{}, coursewareId:{}", roomId, coursewareId);
				return courseLiveBackLog;
			}
		}
		return null;
	}

}
