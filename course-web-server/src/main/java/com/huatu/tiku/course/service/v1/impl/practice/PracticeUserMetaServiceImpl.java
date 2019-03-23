package com.huatu.tiku.course.service.v1.impl.practice;

import java.util.List;
import java.util.Map;

import org.apache.commons.collections.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.HashOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.SetOperations;
import org.springframework.stereotype.Service;

import com.google.common.collect.Maps;
import com.huatu.tiku.course.dao.manual.CoursePracticeUserMataMapper;
import com.huatu.tiku.course.service.cache.CoursePracticeCacheKey;
import com.huatu.tiku.course.service.v1.practice.PracticeUserMetaService;
import com.huatu.tiku.entity.CoursePracticeUserMeta;

import service.impl.BaseServiceHelperImpl;
import tk.mybatis.mapper.entity.Example;
import tk.mybatis.mapper.weekend.WeekendSqls;

/**
 * Created by lijun on 2019/3/7
 */
@Service
public class PracticeUserMetaServiceImpl extends BaseServiceHelperImpl<CoursePracticeUserMeta>
		implements PracticeUserMetaService {

	public PracticeUserMetaServiceImpl() {
		super(CoursePracticeUserMeta.class);
	}

	@Autowired
	private RedisTemplate redisTemplate;
	
	@Autowired
	private CoursePracticeUserMataMapper coursePracticeUserMataMapper;

	@Override
	public Long getLiveCourseIdListByRoomId(Long roomId, Long userId, Integer courseId) {

		List<CoursePracticeUserMeta> userMetaList = coursePracticeUserMataMapper
				.selectByExample(new Example.Builder(CoursePracticeUserMeta.class).where(WeekendSqls
						.<CoursePracticeUserMeta>custom().andEqualTo(CoursePracticeUserMeta::getUserId, userId)
						.andEqualTo(CoursePracticeUserMeta::getRoomId, roomId)
						.andEqualTo(CoursePracticeUserMeta::getCourseId, courseId)).build());
		if (CollectionUtils.isNotEmpty(userMetaList)) {
			return userMetaList.get(0).getAnswerCardId();
		}
		return null;
	}

	@Override
	public Map<String, Integer> getCountDateByRIdAndCId(Long roomId, Long coursewareId) {
		HashOperations<String, String, Integer> metaOpsForHash = redisTemplate.opsForHash();
		final SetOperations<String, Integer> setOperations = redisTemplate.opsForSet();
		Long count = setOperations.size(CoursePracticeCacheKey.roomIdUserMetaKey(roomId,coursewareId));
		if (count == null || count == 0) {
			count = 1L;
		}
		Map<String, Integer> metaEntries = metaOpsForHash
				.entries(CoursePracticeCacheKey.roomIdCourseIdTypeMetaKey(roomId, coursewareId, 2));
		Integer rCount = metaEntries.get(CoursePracticeCacheKey.RCOUNT);
		Integer totalTime = metaEntries.get(CoursePracticeCacheKey.TOTALTIME);
		Map<String, Integer> retMap = Maps.newHashMap();
		retMap.put("classAverageTime", totalTime / count.intValue());
		retMap.put("classAverageRcount", rCount / count.intValue());
		return retMap;
	}
}
