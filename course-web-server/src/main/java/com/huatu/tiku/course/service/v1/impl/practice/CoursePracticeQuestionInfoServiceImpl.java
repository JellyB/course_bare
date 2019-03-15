package com.huatu.tiku.course.service.v1.impl.practice;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

import com.huatu.tiku.course.common.CoursePracticeQuestionInfoEnum;
import com.huatu.tiku.course.service.v1.practice.CoursePracticeQuestionInfoService;
import com.huatu.tiku.entity.CoursePracticeQuestionInfo;

import io.jsonwebtoken.lang.Collections;
import service.impl.BaseServiceHelperImpl;
import tk.mybatis.mapper.entity.Example;
import tk.mybatis.mapper.weekend.WeekendSqls;

/**
 * Created by lijun on 2019/2/21
 */
@Service
public class CoursePracticeQuestionInfoServiceImpl extends BaseServiceHelperImpl<CoursePracticeQuestionInfo>
		implements CoursePracticeQuestionInfoService {

	public CoursePracticeQuestionInfoServiceImpl() {
		super(CoursePracticeQuestionInfo.class);
	}

	@Override
	public List<CoursePracticeQuestionInfo> listByRoomIdAndQuestionId(Long roomId, List<Long> questionIdList) {
		final WeekendSqls<CoursePracticeQuestionInfo> weekendSqls = WeekendSqls.<CoursePracticeQuestionInfo>custom()
				.andEqualTo(CoursePracticeQuestionInfo::getRoomId, roomId)
				.andIn(CoursePracticeQuestionInfo::getQuestionId, questionIdList);
		final Example example = Example.builder(CoursePracticeQuestionInfo.class).where(weekendSqls).build();
		return selectByExample(example);
	}

	/**
	 * 查询已经练习的试题id集合按照答题时间排序
	 */
	@Override
	public List<Integer> getQuestionsInfoByRoomId(Long roomId) {
		List<CoursePracticeQuestionInfo> coursePracticeQuestionList = selectByExample(
				Example.builder(CoursePracticeQuestionInfo.class)
						.where(WeekendSqls.<CoursePracticeQuestionInfo>custom()
								.andEqualTo(CoursePracticeQuestionInfo::getRoomId, roomId)
								.andNotEqualTo(CoursePracticeQuestionInfo::getBizStatus,
										CoursePracticeQuestionInfoEnum.INIT.getStatus())).orderByAsc("startPracticeTime")
						.build());
		if (!Collections.isEmpty(coursePracticeQuestionList)) {
			List<Integer> questionIds = coursePracticeQuestionList.stream()
					.map(CoursePracticeQuestionInfo::getQuestionId).collect(Collectors.toList());
			return questionIds;
		}
		return null;
	}
}
