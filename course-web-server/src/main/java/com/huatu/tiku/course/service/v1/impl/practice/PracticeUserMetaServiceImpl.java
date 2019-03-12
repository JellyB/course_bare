package com.huatu.tiku.course.service.v1.impl.practice;

import java.util.List;

import org.apache.commons.collections.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.huatu.tiku.course.dao.manual.CoursePracticeUserMataMapper;
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
}
