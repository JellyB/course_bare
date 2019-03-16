package com.huatu.tiku.course.service.v1.impl.practice;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

import com.huatu.tiku.course.common.CoursePracticeQuestionInfoEnum;
import com.google.common.collect.Lists;
import com.huatu.tiku.course.bean.practice.PracticeUserQuestionMetaInfoBo;
import com.huatu.tiku.course.service.v1.practice.CoursePracticeQuestionInfoService;
import com.huatu.tiku.entity.CoursePracticeQuestionInfo;

import io.jsonwebtoken.lang.Collections;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.HashOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import service.impl.BaseServiceHelperImpl;
import tk.mybatis.mapper.entity.Example;
import tk.mybatis.mapper.weekend.WeekendSqls;

import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Created by lijun on 2019/2/21
 */
@Service
public class CoursePracticeQuestionInfoServiceImpl extends BaseServiceHelperImpl<CoursePracticeQuestionInfo>
		implements CoursePracticeQuestionInfoService {

	public CoursePracticeQuestionInfoServiceImpl() {
		super(CoursePracticeQuestionInfo.class);
	}
    @Autowired
    private RedisTemplate redisTemplate;

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
    @Override
    public void generateAnswerCardInfo(Set<Long> questionIds, Set <String> courseUserStrs){
        //course_userId_key,questionId,questionAnswer
        //qustionIds
        HashOperations<String, String, PracticeUserQuestionMetaInfoBo> opsForHash = redisTemplate.opsForHash();

        //遍历所有的key
        for (String courseUserKey:courseUserStrs){
            //根据key查出对应的答题信息
            Map<String, PracticeUserQuestionMetaInfoBo> map = opsForHash.entries(courseUserKey);
            //组装数据
            List<String> answersList= Lists.newArrayList();
            List<Integer> timesList= Lists.newArrayList();
            List<Integer> correctsList= Lists.newArrayList();
                //遍历每一道试题的作答信息
                for(Map.Entry<String,PracticeUserQuestionMetaInfoBo> entry:map.entrySet()){

                    PracticeUserQuestionMetaInfoBo question= entry.getValue();
                    for (Long quesiontId:questionIds){
                        if (Long.parseLong(entry.getKey())==quesiontId){
                            answersList.add(question.getAnswer());
                            timesList.add(question.getTime());
                            correctsList.add(question.getCorrect());
                        }
                        String [] answers= (String[]) answersList.toArray();
                        Integer [] corrects=(Integer[]) timesList.toArray();
                        Integer [] times=(Integer[]) correctsList.toArray();

                    }
                }
        }

    }
}
