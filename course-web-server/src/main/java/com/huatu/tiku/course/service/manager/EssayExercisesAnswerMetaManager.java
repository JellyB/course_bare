package com.huatu.tiku.course.service.manager;

import com.huatu.common.exception.BizException;
import com.huatu.tiku.course.dao.essay.EssayCourseExercisesQuestionMapper;
import com.huatu.tiku.course.dao.essay.EssayExercisesAnswerMetaMapper;
import com.huatu.tiku.essay.constant.status.EssayAnswerConstant;
import com.huatu.tiku.essay.entity.courseExercises.EssayCourseExercisesQuestion;
import com.huatu.tiku.essay.entity.courseExercises.EssayExercisesAnswerMeta;
import com.huatu.tiku.essay.essayEnum.EssayStatusEnum;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import tk.mybatis.mapper.entity.Example;

import java.util.List;

/**
 * 描述：
 *
 * @author biguodong
 * Create time 2019-09-03 10:18 AM
 **/

@Slf4j
@Component
public class EssayExercisesAnswerMetaManager {


    @Autowired
    private EssayExercisesAnswerMetaMapper essayExercisesAnswerMetaMapper;

    @Autowired
    private EssayCourseExercisesQuestionMapper essayCourseExercisesQuestionMapper;

    /**
     * 创建申论课后作业空白答题卡
     * @param userId
     * @param syllabusId
     * @throws BizException
     */
    /**
     * 创建空白答题卡
     * @param userId
     * @param syllabusId
     * @throws BizException
     */
    public void createEssayInitUserMeta(int userId, long syllabusId) throws BizException {
        Example example = new Example(EssayExercisesAnswerMeta.class);
        example.and().andEqualTo("syllabusId", syllabusId)
                .andEqualTo("userId", userId)
                .andEqualTo("status", EssayStatusEnum.NORMAL.getCode());

        List<EssayExercisesAnswerMeta> list = essayExercisesAnswerMetaMapper.selectByExample(example);
        if(CollectionUtils.isNotEmpty(list)){
            return;
        }
        Example example_ = new Example(EssayCourseExercisesQuestion.class);
        example_.and()
                .andEqualTo("syllabusId", syllabusId)
                .andEqualTo("status", EssayStatusEnum.NORMAL.getCode());

        List<EssayCourseExercisesQuestion> essayCourseExercisesQuestions = essayCourseExercisesQuestionMapper.selectByExample(example_);
        if(CollectionUtils.isEmpty(essayCourseExercisesQuestions)){
            return;
        }
        //创建空白答题卡
        for (EssayCourseExercisesQuestion essayCourseExercisesQuestion : essayCourseExercisesQuestions) {
            EssayExercisesAnswerMeta exercisesAnswerMeta = EssayExercisesAnswerMeta.builder()
                    .answerType(essayCourseExercisesQuestion.getType())
                    .courseWareId(essayCourseExercisesQuestion.getCourseWareId())
                    .pQid(essayCourseExercisesQuestion.getPQid())
                    .userId(userId)
                    .build();

            exercisesAnswerMeta.setBizStatus(EssayAnswerConstant.EssayAnswerBizStatusEnum.INIT.getBizStatus());
            essayExercisesAnswerMetaMapper.insertSelective(exercisesAnswerMeta);
        }
    }




}
