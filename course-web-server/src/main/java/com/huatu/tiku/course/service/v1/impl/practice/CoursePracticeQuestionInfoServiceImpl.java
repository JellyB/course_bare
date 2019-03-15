package com.huatu.tiku.course.service.v1.impl.practice;

import com.huatu.tiku.course.service.v1.practice.CoursePracticeQuestionInfoService;
import com.huatu.tiku.entity.CoursePracticeQuestionInfo;
import org.springframework.stereotype.Service;
import service.impl.BaseServiceHelperImpl;
import tk.mybatis.mapper.entity.Example;
import tk.mybatis.mapper.weekend.WeekendSqls;

import java.util.List;

/**
 * Created by lijun on 2019/2/21
 */
@Service
public class CoursePracticeQuestionInfoServiceImpl extends BaseServiceHelperImpl<CoursePracticeQuestionInfo> implements CoursePracticeQuestionInfoService {

    public CoursePracticeQuestionInfoServiceImpl() {
        super(CoursePracticeQuestionInfo.class);
    }

    @Override
    public List<CoursePracticeQuestionInfo> listByRoomIdAndQuestionId(Long roomId, List<Long> questionIdList) {
        final WeekendSqls<CoursePracticeQuestionInfo> weekendSqls = WeekendSqls.<CoursePracticeQuestionInfo>custom()
                .andEqualTo(CoursePracticeQuestionInfo::getRoomId, roomId)
                .andIn(CoursePracticeQuestionInfo::getQuestionId, questionIdList);
        final Example example = Example.builder(CoursePracticeQuestionInfo.class)
                .where(weekendSqls)
                .build();
        return selectByExample(example);
    }

	@Override
	public List<Long> getQuestionsInfoByRoomId(Long roomId) {
		// TODO Auto-generated method stub
		return null;
	}
}
