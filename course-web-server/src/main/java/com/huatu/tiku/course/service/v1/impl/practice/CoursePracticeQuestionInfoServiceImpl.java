package com.huatu.tiku.course.service.v1.impl.practice;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.apache.commons.lang3.StringUtils;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.HashOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import com.huatu.tiku.common.CourseQuestionTypeEnum.CourseType;
import com.huatu.tiku.common.bean.reward.RewardMessage;
import com.huatu.tiku.common.consts.RabbitConsts;
import com.huatu.tiku.course.bean.NetSchoolResponse;
import com.huatu.tiku.course.bean.practice.PracticeUserQuestionMetaInfoBo;
import com.huatu.tiku.course.bean.practice.UserCourseBo;
import com.huatu.tiku.course.common.CoinType;
import com.huatu.tiku.course.common.CoursePracticeQuestionInfoEnum;
import com.huatu.tiku.course.service.cache.CoursePracticeCacheKey;
import com.huatu.tiku.course.service.v1.practice.CoursePracticeQuestionInfoService;
import com.huatu.tiku.course.util.ResponseUtil;
import com.huatu.tiku.course.ztk.api.v1.paper.PracticeCardServiceV1;
import com.huatu.tiku.course.ztk.api.v4.user.UserServiceV4;
import com.huatu.tiku.entity.CoursePracticeQuestionInfo;

import io.jsonwebtoken.lang.Collections;
import lombok.extern.slf4j.Slf4j;
import service.impl.BaseServiceHelperImpl;
import tk.mybatis.mapper.entity.Example;
import tk.mybatis.mapper.weekend.WeekendSqls;

/**
 * Created by lijun on 2019/2/21
 */
@Service
@Slf4j
public class CoursePracticeQuestionInfoServiceImpl extends BaseServiceHelperImpl<CoursePracticeQuestionInfo>
		implements CoursePracticeQuestionInfoService {

	public CoursePracticeQuestionInfoServiceImpl() {
		super(CoursePracticeQuestionInfo.class);
	}

	@Autowired
	private RedisTemplate redisTemplate;

	@Autowired
	private PracticeCardServiceV1 practiceCardServiceV1;

	@Autowired
	private RabbitTemplate rabbitTemplate;

	@Autowired
	private UserServiceV4 userServiceV4;

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
										CoursePracticeQuestionInfoEnum.INIT.getStatus()))
						.orderByAsc("startPracticeTime").build());
		if (!Collections.isEmpty(coursePracticeQuestionList)) {
			List<Integer> questionIds = coursePracticeQuestionList.stream()
					.map(CoursePracticeQuestionInfo::getQuestionId).collect(Collectors.toList());
			return questionIds;
		}
		return null;
	}

	@Override
	public void generateAnswerCardInfo(List<Integer> questionIds, List<String> courseUserStrs,Long roomId) {
		HashOperations<String, String, PracticeUserQuestionMetaInfoBo> opsForHash = redisTemplate.opsForHash();
		// 遍历所有的key
		for (String courseUserKey : courseUserStrs) {
			// 根据key查出对应的答题信息
			Map<String, PracticeUserQuestionMetaInfoBo> map = opsForHash.entries(courseUserKey);
			Integer rcount = 0;
			String[] answers = new String[questionIds.size()];
			int[] corrects = new int[questionIds.size()];
			int[] times = new int[questionIds.size()];
			for (int i = 0; i < questionIds.size(); i++) {
				boolean isAnswer = false;
				// 遍历每一道试题的作答信息
				for (Map.Entry<String, PracticeUserQuestionMetaInfoBo> entry : map.entrySet()) {

					PracticeUserQuestionMetaInfoBo question = entry.getValue();
					String questionId = entry.getKey();
					if (Long.parseLong(questionId) == questionIds.get(i)) {
						answers[i] = question.getAnswer();
						corrects[i] = question.getCorrect();
						times[i] = question.getTime();
						isAnswer = true;
						if (question.getCorrect() == 1) {
							rcount++;
						}
					}
				}
				if (!isAnswer) {
					// 该题未作答
					answers[i] = "0";
					corrects[i] = 0;
					times[i] = 0;
				}
			}
			UserCourseBo userCourse = CoursePracticeCacheKey.getUserAndCourseByUserMetaKey(courseUserKey);
			String qids = StringUtils.join(questionIds, ",");
			// 直播课type为2
			practiceCardServiceV1.createAndSaveAnswerCoursePracticeCard(1, 1, userCourse.getUserId(), "随堂练习-直播课",
					CourseType.LIVE.getCode(), userCourse.getCourseId(), qids, answers, corrects, times,
					new ArrayList<>());
			log.info("随堂练用户id:{} courseId:{}生成答题卡", userCourse.getUserId(), userCourse.getCourseId());
			// 赠送图币
//			NetSchoolResponse response = userServiceV4.getUserLevelBatch(new ArrayList<>(userCourse.getUserId()));
//			if (ResponseUtil.isSuccess(response)) {
//				List<Map<String, String>> userInfoList = (List<Map<String, String>>) response.getData();
//				String userName = userInfoList.get(0).get("name");
//				RewardMessage msg = RewardMessage.builder().gold(rcount * 2).uid(userCourse.getUserId())
//						.action(CoinType.COURSE_PRACTICE_RIGHT).experience(rcount * 2).bizId(roomId + userName)
//						.uname(userName).timestamp(System.currentTimeMillis()).build();
//				rabbitTemplate.convertAndSend("", RabbitConsts.QUEUE_REWARD_ACTION, msg);
//			}

		}

	}
}
