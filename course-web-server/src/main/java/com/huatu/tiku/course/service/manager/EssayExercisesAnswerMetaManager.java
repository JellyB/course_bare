package com.huatu.tiku.course.service.manager;

import com.huatu.common.ErrorResult;
import com.huatu.common.exception.BizException;
import com.huatu.tiku.course.bean.vo.EssayAnswerCardInfo;
import com.huatu.tiku.course.common.SubjectEnum;
import com.huatu.tiku.course.consts.SyllabusInfo;
import com.huatu.tiku.course.dao.essay.*;
import com.huatu.tiku.essay.constant.status.EssayAnswerConstant;
import com.huatu.tiku.essay.entity.EssayPaperAnswer;
import com.huatu.tiku.essay.entity.EssayQuestionAnswer;
import com.huatu.tiku.essay.entity.EssayQuestionDetail;
import com.huatu.tiku.essay.entity.EssaySimilarQuestion;
import com.huatu.tiku.essay.entity.correct.CorrectOrder;
import com.huatu.tiku.essay.entity.courseExercises.EssayCourseExercisesQuestion;
import com.huatu.tiku.essay.entity.courseExercises.EssayExercisesAnswerMeta;
import com.huatu.tiku.essay.essayEnum.EssayAnswerCardEnum;
import com.huatu.tiku.essay.essayEnum.EssayStatusEnum;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.MapUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import tk.mybatis.mapper.entity.Example;

import java.util.List;
import java.util.Map;

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

    @Autowired
    private EssaySimilarQuestionMapper essaySimilarQuestionMapper;

    @Autowired
    private EssayQuestionDetailMapper essayQuestionDetailMapper;

    @Autowired
    private EssayPaperAnswerMapper essayPaperAnswerMapper;

    @Autowired
    private EssayQuestionAnswerMapper essayQuestionAnswerMapper;

    @Autowired
    private CorrectOrderMapper correctOrderMapper;

    private static Integer CORRECT_COUNT_ONE = 1;
    private static Integer CORRECT_COUNT_TWO = 2;

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


    /**
     * 构架申论 answerCardInfo
     * @param userId
     * @param
     * @return
     * @throws BizException
     */
    public EssayAnswerCardInfo buildEssayAnswerCardInfo(int userId, final Map map) throws BizException {

        int afterCourseNum = MapUtils.getIntValue(map, SyllabusInfo.AfterCourseNum, 0);

        EssayAnswerCardInfo defaultCardInfo = new EssayAnswerCardInfo();
        defaultCardInfo.setType(SubjectEnum.SL.getCode());
        defaultCardInfo.setStatus(EssayAnswerConstant.EssayAnswerBizStatusEnum.INIT.getBizStatus());
        defaultCardInfo.setQcount(afterCourseNum);
        dealSingleQuestionOrPaperOrMultiQuestions(userId, defaultCardInfo, map);
        return defaultCardInfo;
    }

    /**
     * 单题 & 套题 & 多个单题处理
     * @param defaultCardInfo
     * @param map
     */
    private void dealSingleQuestionOrPaperOrMultiQuestions(int userId, EssayAnswerCardInfo defaultCardInfo, Map map){
        int courseType = MapUtils.getIntValue(map, SyllabusInfo.VideoType, 0);
        long courseWareId = MapUtils.getLongValue(map, SyllabusInfo.CourseWareId, 0);

        Example questionExample = new Example(EssayCourseExercisesQuestion.class);
        questionExample.and()
                .andEqualTo("courseWareId", courseWareId)
                .andEqualTo("courseType", courseType)
                .andEqualTo("status", EssayStatusEnum.NORMAL.getCode());

        questionExample.orderBy("sort").asc();

        List<EssayCourseExercisesQuestion> questions = essayCourseExercisesQuestionMapper.selectByExample(questionExample);

        //单题处理
        if(CollectionUtils.isEmpty(questions)){
            log.error("dealSingleQuestionOrPaperOrMultiQuestions.questions is empty:{},{}", courseType, courseWareId);
            return;
        }
        if(questions.size() == CORRECT_COUNT_ONE){
            EssayCourseExercisesQuestion essayCourseExercisesQuestion = questions.get(0);
            if(essayCourseExercisesQuestion.getType() == EssayAnswerCardEnum.TypeEnum.QUESTION.getType()){
                dealSingleQuestion(userId, essayCourseExercisesQuestion.getPQid(), defaultCardInfo, map);
            }
            if(essayCourseExercisesQuestion.getType() == EssayAnswerCardEnum.TypeEnum.PAPER.getType()){
                dealSinglePaper(userId, essayCourseExercisesQuestion.getPQid(), defaultCardInfo, map);
            }
        }
        /*else{
            List<Long> questionIds = questions.stream().map(EssayCourseExercisesQuestion::getPQid).collect(Collectors.toList());
            dealMultiQuestion(userId, questionIds, defaultCardInfo, map);
        }*/
    }

    /**
     * 查询没道单题、套题、多道单题的答题卡 meta 信息
     * @param userId
     * @param answerType
     * @param pQid
     * @param map
     * @return
     */
    private EssayExercisesAnswerMeta obtainUserAnswerMetas(int userId, int answerType, long pQid, Map map) throws BizException{
        int courseType = MapUtils.getIntValue(map, SyllabusInfo.VideoType, 0);
        long courseWareId = MapUtils.getLongValue(map, SyllabusInfo.CourseWareId, 0);
        long syllabusId = MapUtils.getLongValue(map, SyllabusInfo.SyllabusId, 0);

        Example example = new Example(EssayExercisesAnswerMeta.class);
        example.and()
                .andEqualTo("userId", userId)
                .andEqualTo("syllabusId", syllabusId)
                .andEqualTo("answerType", answerType)
                .andEqualTo("pQid", pQid)
                .andEqualTo("courseType", courseType)
                .andEqualTo("courseWareId", courseWareId)
                .andEqualTo("status", EssayStatusEnum.NORMAL.getCode());

        example.orderBy("correctNum").desc();
        List<EssayExercisesAnswerMeta> metas = essayExercisesAnswerMetaMapper.selectByExample(example);
        if(CollectionUtils.isEmpty(metas)){
            return null;
        }else if(metas.size() == CORRECT_COUNT_ONE){
            return metas.get(0);
        }else{
            throw new BizException(ErrorResult.create(100010, "数据错误"));
        }
    }

    /**
     * 单题处理逻辑
     * @param userId
     * @param defaultCardInfo
     * @param map
     */
    private void dealSingleQuestion(int userId, long questionId, EssayAnswerCardInfo defaultCardInfo, Map map) throws BizException{
        EssayExercisesAnswerMeta essayExercisesAnswerMeta = obtainUserAnswerMetas(userId, EssayAnswerCardEnum.TypeEnum.QUESTION.getType(), questionId, map);
        if(null == essayExercisesAnswerMeta){
            return;
        }
        EssayQuestionAnswer essayQuestionAnswer = essayQuestionAnswerMapper.selectByPrimaryKey(essayExercisesAnswerMeta.getAnswerId());
        if(null == essayQuestionAnswer){
            log.error("buildEssayAnswerCardInfo.essayQuestionAnswer is null:{}", essayExercisesAnswerMeta.getAnswerId());
            return;
        }
        defaultCardInfo.setId(essayQuestionAnswer.getId());
        defaultCardInfo.setQuestionBaseId(essayQuestionAnswer.getQuestionBaseId());
        defaultCardInfo.setExamScore(essayQuestionAnswer.getExamScore());
        defaultCardInfo.setScore(essayQuestionAnswer.getScore());
        defaultCardInfo.setStatus(essayQuestionAnswer.getBizStatus());
        defaultCardInfo.setCorrectNum(essayExercisesAnswerMeta.getCorrectNum());

        Example exampleQuestion = new Example(EssaySimilarQuestion.class);
        exampleQuestion.and()
                .andEqualTo("questionBaseId", essayExercisesAnswerMeta.getPQid());
        EssaySimilarQuestion essaySimilarQuestion = essaySimilarQuestionMapper.selectOneByExample(exampleQuestion);
        if(null == essaySimilarQuestion){
            throw new BizException(ErrorResult.create(100010, "试题不存在"));
        }
        defaultCardInfo.setSimilarId(essaySimilarQuestion.getSimilarId());
        EssayQuestionDetail essayQuestionDetail = essayQuestionDetailMapper.selectByPrimaryKey(essayQuestionAnswer.getQuestionDetailId());
        if(null == essayQuestionDetail){
            throw new BizException(ErrorResult.create(100010, "试题不存在"));
        }
        defaultCardInfo.setQuestionType(essayQuestionDetail.getType());
        if(essayQuestionAnswer.getBizStatus() == EssayAnswerConstant.EssayAnswerBizStatusEnum.CORRECT_RETURN.getBizStatus()){
            dealCorrectReturnMemo(defaultCardInfo, essayExercisesAnswerMeta);
        }
    }


    /**
     * 套题处理逻辑
     * @param userId
     * @param paperId
     * @param defaultCardInfo
     * @param map
     */
    private void dealSinglePaper(int userId, long paperId, EssayAnswerCardInfo defaultCardInfo, Map map){
        EssayExercisesAnswerMeta essayExercisesAnswerMeta = obtainUserAnswerMetas(userId, EssayAnswerCardEnum.TypeEnum.PAPER.getType(), paperId, map);
        if(null == essayExercisesAnswerMeta){
            return;
        }
        EssayPaperAnswer essayPaperAnswer = essayPaperAnswerMapper.selectByPrimaryKey(essayExercisesAnswerMeta.getAnswerId());
        if(null == essayPaperAnswer){
            log.error("buildEssayAnswerCardInfo.essayQuestionAnswer is null:{}", essayExercisesAnswerMeta.getAnswerId());
            return;
        }
        defaultCardInfo.setId(essayPaperAnswer.getId());
        defaultCardInfo.setPaperId(essayPaperAnswer.getPaperBaseId());
        defaultCardInfo.setExamScore(essayPaperAnswer.getExamScore());
        defaultCardInfo.setScore(essayPaperAnswer.getScore());
        defaultCardInfo.setStatus(essayPaperAnswer.getBizStatus());
        defaultCardInfo.setCorrectNum(essayExercisesAnswerMeta.getCorrectNum());

        if(essayPaperAnswer.getBizStatus() == EssayAnswerConstant.EssayAnswerBizStatusEnum.CORRECT_RETURN.getBizStatus()){
            dealCorrectReturnMemo(defaultCardInfo, essayExercisesAnswerMeta);
        }
    }

    /**
     * 多道单题处理
     * @param userId
     * @param questionIds
     * @param defaultCardInfo
     * @param map
     */
    /*private void dealMultiQuestion(int userId, List<Long> questionIds, EssayAnswerCardInfo defaultCardInfo, Map map){
        for (Long questionId : questionIds) {
            EssayExercisesAnswerMeta essayExercisesAnswerMeta = obtainUserAnswerMetas(userId, EssayAnswerCardEnum.TypeEnum.QUESTION.getType(), questionId, map);
            if(null == essayExercisesAnswerMeta){
                continue;
            }
            EssayQuestionAnswer essayQuestionAnswer = essayQuestionAnswerMapper.selectByPrimaryKey(essayExercisesAnswerMeta.getAnswerId());
            if(null == essayQuestionAnswer){
                log.error("buildEssayAnswerCardInfo.essayQuestionAnswer is null:{}", essayExercisesAnswerMeta.getAnswerId());
                return;
            }
        }
    }*/


    /**
     * 处理被退回原因
     * @param defaultCardInfo
     * @param essayExercisesAnswerMeta
     */
    private void dealCorrectReturnMemo(EssayAnswerCardInfo defaultCardInfo, EssayExercisesAnswerMeta essayExercisesAnswerMeta){
        Example example = new Example(CorrectOrder.class);
        example.and()
                .andEqualTo("status", EssayStatusEnum.NORMAL.getCode())
                .andEqualTo("answerCardType", essayExercisesAnswerMeta.getAnswerType())
                .andEqualTo("answerCardId", essayExercisesAnswerMeta.getAnswerId());
        CorrectOrder correctOrder = correctOrderMapper.selectOneByExample(example);
        defaultCardInfo.setCorrectMemo(null != correctOrder ? correctOrder.getCorrectMemo() : StringUtils.EMPTY);
    }
}
