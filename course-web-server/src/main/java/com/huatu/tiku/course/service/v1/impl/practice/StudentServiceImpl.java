package com.huatu.tiku.course.service.v1.impl.practice;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Lists;
import com.huatu.tiku.course.bean.practice.PracticeRoomRankUserBo;
import com.huatu.tiku.course.bean.practice.QuestionInfo;
import com.huatu.tiku.course.bean.practice.StudentQuestionMetaBo;
import com.huatu.tiku.course.service.cache.CoursePracticeCacheKey;
import com.huatu.tiku.course.service.v1.practice.CoursePracticeQuestionInfoService;
import com.huatu.tiku.course.service.v1.practice.QuestionInfoService;
import com.huatu.tiku.course.service.v1.practice.StudentService;
import lombok.RequiredArgsConstructor;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.SetOperations;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Created by lijun on 2019/2/27
 */
@Service
@RequiredArgsConstructor
public class StudentServiceImpl implements StudentService {

    private final QuestionInfoService questionInfoService;
    private final PracticeMetaComponent practiceMetaComponent;

    @Autowired
    private CoursePracticeQuestionInfoService coursePracticeQuestionInfoService;
    private final RedisTemplate redisTemplate;
    @Override
    public Map<String,Object> putAnswer(Long roomId, Long courseId, Integer userId, String userName, Long questionId, String answer, Integer time) {
        List<QuestionInfo> baseQuestionInfoList = questionInfoService.getBaseQuestionInfo(Lists.newArrayList(questionId));
        if (CollectionUtils.isEmpty(baseQuestionInfoList)) {
            return null;
        }
        if (StringUtils.isEmpty(answer)) {
            practiceMetaComponent.buildUserQuestionMeta(userId, courseId, questionId, answer, time, 0);
        }
        final QuestionInfo questionInfo = baseQuestionInfoList.get(0);
        boolean isCorrect = formatAnswer(answer).equals(questionInfo.getAnswer());
        practiceMetaComponent.buildMetaInfo(userId, userName, roomId, courseId, questionId, answer, time, isCorrect ? 1 : 2);
        //存储答题用户信息到set中
        practiceMetaComponent.setRoomInfoMeta(userId, roomId, courseId);
        //返回做题结果 TODO 送金币
        return ImmutableMap.of("isCorrect",isCorrect, "coin",3);
    }

    @Override
    public StudentQuestionMetaBo getStudentQuestionMetaBo(Integer userId, Long roomId, Long courseId, Long questionId) {
        StudentQuestionMetaBo studentQuestionMetaBo = practiceMetaComponent.getStudentQuestionMetaBo(userId, roomId, courseId, questionId);
        //构建用户的答题信息
        List<Long> roomPracticedQuestionList = practiceMetaComponent.getRoomPracticedQuestion(roomId);
        //设置已答总题量
        studentQuestionMetaBo.setTotalQuestionNum(roomPracticedQuestionList.size());
        return studentQuestionMetaBo;
    }

    @Override
    public List<PracticeRoomRankUserBo> listPracticeRoomRankUser(Long roomId, Integer start, Integer end) {
        return practiceMetaComponent.getRoomRankInfo(roomId, start, end);
    }

    @Override
    public PracticeRoomRankUserBo getUserRankInfo(Integer userId, Long courseId) {
        return practiceMetaComponent.getPracticeRoomRankUser(userId, courseId);
    }

    /**
     * 格式化答案
     */
    private static String formatAnswer(String answer) {
        if (answer.length() == 1) {
            return answer;
        }
        return Arrays.stream(answer.split("")).sorted().collect(Collectors.joining(""));
    }

    /**
     * 根据课件Id查询试题的统计信息
     * @param courseId 课件Id
     * @return
     */
    public Object getCourseQuestionInfo(Long courseId){
        return null;
    }

    /**
     * 根据课件Id查询课件的随堂练习正确率
     * @param courseId 课件Id
     * @return
     */
    public Integer getCourseRightRate(Long courseId,Long roomId){
        //获取课件下答对题的数目
        final String key = CoursePracticeCacheKey.roomRightQuestionSum(courseId);
        Integer rightNum=Integer.parseInt(redisTemplate.opsForValue().get(key,0,-1));

        //获取课件下作答总人数
        final SetOperations<String, Long> opsForSet = redisTemplate.opsForSet();
        final String allUserSumKey = CoursePracticeCacheKey.roomAllUserSum(courseId);
        Integer answerNum = opsForSet.members(allUserSumKey).size();

        //获取课件下试题的数量
        List<Integer> questionIds=coursePracticeQuestionInfoService.getQuestionsInfoByRoomId(roomId);
        Integer questionNum=questionIds.size();
        if (questionNum==0){
            questionNum=1;
        }

        //计算课件的正确率
        Integer rightRate=0;
        if (answerNum!=0){
            rightRate=rightNum/(answerNum * questionNum)  * 100;
        }
        return rightRate;
    }

}
