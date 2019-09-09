package com.huatu.tiku.course.service.manager;

import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.MapUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.SetOperations;
import org.springframework.stereotype.Component;

import com.huatu.common.ErrorResult;
import com.huatu.common.exception.BizException;
import com.huatu.tiku.course.bean.vo.EssayAnswerCardInfo;
import com.huatu.tiku.course.bean.vo.EssayCourseWorkAnswerCardInfo;
import com.huatu.tiku.course.consts.SyllabusInfo;
import com.huatu.tiku.course.dao.essay.CorrectOrderMapper;
import com.huatu.tiku.course.dao.essay.EssayCourseExercisesQuestionMapper;
import com.huatu.tiku.course.dao.essay.EssayExercisesAnswerMetaMapper;
import com.huatu.tiku.course.dao.essay.EssayPaperAnswerMapper;
import com.huatu.tiku.course.dao.essay.EssayQuestionAnswerMapper;
import com.huatu.tiku.course.dao.essay.EssayQuestionDetailMapper;
import com.huatu.tiku.course.util.CourseCacheKey;
import com.huatu.tiku.essay.constant.status.EssayAnswerConstant;
import com.huatu.tiku.essay.constant.status.QuestionTypeConstant;
import com.huatu.tiku.essay.entity.courseExercises.EssayCourseExercisesQuestion;
import com.huatu.tiku.essay.entity.courseExercises.EssayExercisesAnswerMeta;
import com.huatu.tiku.essay.essayEnum.CourseWareTypeEnum;
import com.huatu.tiku.essay.essayEnum.EssayAnswerCardEnum;
import com.huatu.tiku.essay.essayEnum.EssayStatusEnum;

import lombok.extern.slf4j.Slf4j;
import tk.mybatis.mapper.entity.Example;

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
    private RedisTemplate redisTemplate;

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
    public void createEssayInitUserMeta(int userId, long syllabusId, int courseType, long courseWareId, long courseId) throws BizException {
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
                .andEqualTo("courseType", courseType)
                .andEqualTo("courseWareId", courseWareId)
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
                    .courseType(essayCourseExercisesQuestion.getCourseType())
                    .courseId(courseId)
                    .syllabusId(syllabusId)
                    .correctNum(1)
                    .submitTime(new Date())
                    .pQid(essayCourseExercisesQuestion.getPQid())
                    .userId(userId)
                    .build();

            exercisesAnswerMeta.setGmtCreate(new Date());
            exercisesAnswerMeta.setGmtModify(new Date());
            exercisesAnswerMeta.setStatus(EssayStatusEnum.NORMAL.getCode());
            exercisesAnswerMeta.setBizStatus(EssayAnswerConstant.EssayAnswerBizStatusEnum.INIT.getBizStatus());
            essayExercisesAnswerMetaMapper.insertSelective(exercisesAnswerMeta);
        }

        String key = CourseCacheKey.getCourseWorkEssayIsAlert(userId);
        SetOperations<String, Long> setOperations = redisTemplate.opsForSet();
        setOperations.add(key, syllabusId);
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
    public void dealSingleQuestionOrPaperOrMultiQuestions(int userId, EssayAnswerCardInfo defaultCardInfo, Map map){
        int videoType = MapUtils.getIntValue(map, SyllabusInfo.VideoType, 0);
        int courseType = CourseWareTypeEnum.changeVideoType2TableCourseType(videoType);
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
        defaultCardInfo.setQcount(questions.size());
        // 单题或套题处理
        if(questions.size() == CORRECT_COUNT_ONE){
            EssayCourseExercisesQuestion essayCourseExercisesQuestion = questions.get(0);
            if(essayCourseExercisesQuestion.getType() == EssayAnswerCardEnum.TypeEnum.QUESTION.getType()){
                dealSingleQuestion(userId, essayCourseExercisesQuestion.getPQid(), defaultCardInfo, map);
            }
            if(essayCourseExercisesQuestion.getType() == EssayAnswerCardEnum.TypeEnum.PAPER.getType()){
                dealSinglePaper(userId, essayCourseExercisesQuestion.getPQid(), defaultCardInfo, map);
            }
        }else{
            long syllabusId = MapUtils.getIntValue(map, SyllabusInfo.SyllabusId, 0);
            EssayAnswerCardInfo essayAnswerCardInfo = dealMultiQuestionAnswerCardInfo(userId, syllabusId);
            defaultCardInfo.setFcount(essayAnswerCardInfo.getFcount());
            defaultCardInfo.setStatus(essayAnswerCardInfo.getStatus());
        }
    }

    /**
     * 查询没道单题、套题、多道单题的答题卡 meta 信息
     * @param userId
     * @param answerType
     * @param pQid
     * @param map
     * @return
     */
    private EssayExercisesAnswerMeta obtainUserCurrentUsefulAnswerMetas(int userId, int answerType, long pQid, Map map) throws BizException{
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
        }else if(metas.size() == CORRECT_COUNT_TWO){
            return metas.get(0);
        }{
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
        EssayExercisesAnswerMeta essayExercisesAnswerMeta = obtainUserCurrentUsefulAnswerMetas(userId, EssayAnswerCardEnum.TypeEnum.QUESTION.getType(), questionId, map);
        if(null == essayExercisesAnswerMeta){
            return;
        }
        Map<String, Object> questionAnswer = essayQuestionAnswerMapper.selectQuestionAnswerById(essayExercisesAnswerMeta.getAnswerId());
        if(null == questionAnswer || questionAnswer.isEmpty()){
            log.error("buildEssayAnswerCardInfo.essayQuestionAnswer is null:{}", essayExercisesAnswerMeta.getAnswerId());
            return;
        }
        defaultCardInfo.setId(MapUtils.getLongValue(questionAnswer, "id", 0));
        defaultCardInfo.setStatus(MapUtils.getIntValue(questionAnswer, "biz_status"));
        if(MapUtils.getIntValue(questionAnswer, "biz_status") == EssayAnswerConstant.EssayAnswerBizStatusEnum.CORRECT_RETURN.getBizStatus()){
            ((EssayCourseWorkAnswerCardInfo) defaultCardInfo).setCorrectMemo(dealCorrectReturnMemo(defaultCardInfo.getId(), EssayAnswerCardEnum.TypeEnum.QUESTION.getType()));

        }
        if(defaultCardInfo instanceof EssayCourseWorkAnswerCardInfo){
            ((EssayCourseWorkAnswerCardInfo) defaultCardInfo).setAreaId(MapUtils.getIntValue(questionAnswer, "area_id"));
            ((EssayCourseWorkAnswerCardInfo) defaultCardInfo).setAreaName(MapUtils.getString(questionAnswer, "area_name"));
            ((EssayCourseWorkAnswerCardInfo) defaultCardInfo).setQuestionBaseId(MapUtils.getLongValue(questionAnswer, "question_base_id", 0));
            ((EssayCourseWorkAnswerCardInfo) defaultCardInfo).setCorrectNum(essayExercisesAnswerMeta.getCorrectNum());


            // 单题组处理为 0
            /*Map<String, Object> essaySimilarQuestionMap = essaySimilarQuestionMapper.selectByQuestionBaseId(essayExercisesAnswerMeta.getPQid());
            if(null == essaySimilarQuestionMap || essaySimilarQuestionMap.isEmpty()){
                throw new BizException(ErrorResult.create(100010, "试题不存在"));
            }*/
            //((EssayCourseWorkAnswerCardInfo) defaultCardInfo).setSimilarId(MapUtils.getLongValue(essaySimilarQuestionMap, "similar_id"));
            ((EssayCourseWorkAnswerCardInfo) defaultCardInfo).setSimilarId(0L);
            Map<String, Object> detailMap = essayQuestionDetailMapper.selectQuestionDetailById(MapUtils.getLongValue(questionAnswer, "question_detail_id"));
            if(null == detailMap || detailMap.isEmpty()){
                throw new BizException(ErrorResult.create(100010, "试题不存在"));
            }
            int type = MapUtils.getIntValue(detailMap, "type");
            //判断单题是否为议论文
            if(type == 5){
                ((EssayCourseWorkAnswerCardInfo) defaultCardInfo).setQuestionType(QuestionTypeConstant.ARGUMENTATION);
            }else{
                ((EssayCourseWorkAnswerCardInfo) defaultCardInfo).setQuestionType(QuestionTypeConstant.SINGLE_QUESTION);
            }
        }else{
            defaultCardInfo.setExamScore(MapUtils.getDoubleValue(questionAnswer, "exam_score"));
            defaultCardInfo.setScore(MapUtils.getDoubleValue(questionAnswer, "score"));
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
        EssayExercisesAnswerMeta essayExercisesAnswerMeta = obtainUserCurrentUsefulAnswerMetas(userId, EssayAnswerCardEnum.TypeEnum.PAPER.getType(), paperId, map);
        if(null == essayExercisesAnswerMeta){
            return;
        }
        Map<String, Object> paperMap = essayPaperAnswerMapper.selectPaperAnswerById(essayExercisesAnswerMeta.getAnswerId());
        if(null == paperMap || paperMap.isEmpty()){
            log.error("buildEssayAnswerCardInfo.essayQuestionAnswer is null:{}", essayExercisesAnswerMeta.getAnswerId());
            return;
        }
        defaultCardInfo.setId(MapUtils.getLongValue(paperMap, "id"));
        defaultCardInfo.setStatus(MapUtils.getIntValue(paperMap, "biz_status"));
        if(MapUtils.getIntValue(paperMap, "biz_status") == EssayAnswerConstant.EssayAnswerBizStatusEnum.CORRECT_RETURN.getBizStatus()){
            ((EssayCourseWorkAnswerCardInfo) defaultCardInfo).setCorrectMemo(dealCorrectReturnMemo(defaultCardInfo.getId(), EssayAnswerCardEnum.TypeEnum.PAPER.getType()));
        }
        if(defaultCardInfo instanceof EssayCourseWorkAnswerCardInfo){
            ((EssayCourseWorkAnswerCardInfo) defaultCardInfo).setAreaId(MapUtils.getIntValue(paperMap, "area_id"));
            ((EssayCourseWorkAnswerCardInfo) defaultCardInfo).setAreaName(MapUtils.getString(paperMap, "area_name"));
            ((EssayCourseWorkAnswerCardInfo) defaultCardInfo).setPaperId(MapUtils.getLongValue(paperMap, "paper_base_id"));
            ((EssayCourseWorkAnswerCardInfo) defaultCardInfo).setCorrectNum(essayExercisesAnswerMeta.getCorrectNum());
            ((EssayCourseWorkAnswerCardInfo) defaultCardInfo).setQuestionType(QuestionTypeConstant.PAPER);
        }else{
            defaultCardInfo.setScore(MapUtils.getDoubleValue(paperMap, "score"));
            defaultCardInfo.setExamScore(MapUtils.getDoubleValue(paperMap, "exam_score"));
        }
    }


    /**
     * 多个单题处理
     * @param userId
     * @param syllabusId
     */
	public EssayAnswerCardInfo dealMultiQuestionAnswerCardInfo(int userId, long syllabusId) {
		EssayAnswerCardInfo answerCardInfo = new EssayAnswerCardInfo();
		answerCardInfo.setFcount(0);
		answerCardInfo.setStatus(EssayAnswerConstant.EssayAnswerBizStatusEnum.INIT.getBizStatus());
		List<Map<String, Object>> metaListMap = essayExercisesAnswerMetaMapper.selectMultiBizStatusCount(userId,
				syllabusId);
		if (CollectionUtils.isEmpty(metaListMap)) {
			log.error("处理多题做题统计状态异常: userId:{}, syllabusId:{}, correctNum:{}", userId, syllabusId);
			return answerCardInfo;
		}
		Map<Long, Object> answerMetaListMap = metaListMap.stream()
				.collect(Collectors.toMap(answerMeta -> (Long) answerMeta.get("p_qid"),
						answerMeta -> answerMeta, (answerMeta1, answerMeta2) -> {
							if (MapUtils.getInteger((Map) answerMeta1, "correct_num", 0) > MapUtils
									.getInteger((Map) answerMeta2, "correct_num", 0)) {
								return answerMeta1;
							}
							return answerMeta2;

						}));
		List<Integer> statusList = answerMetaListMap.entrySet().stream()
				.map(meta -> MapUtils.getInteger((Map) meta.getValue(), "biz_status")).collect(Collectors.toList());
		Map<Integer, Long> bizStatusMap = statusList.stream()
				.collect(Collectors.groupingBy(Function.identity(), Collectors.counting()));
		Integer unDoCount = MapUtils.getInteger(bizStatusMap,
				EssayAnswerConstant.EssayAnswerBizStatusEnum.INIT.getBizStatus());
		Integer commitCount = MapUtils.getInteger(bizStatusMap,
				EssayAnswerConstant.EssayAnswerBizStatusEnum.COMMIT.getBizStatus());
		Integer correctCount = MapUtils.getInteger(bizStatusMap,
				EssayAnswerConstant.EssayAnswerBizStatusEnum.CORRECT.getBizStatus());
		Integer returnCount = MapUtils.getInteger(bizStatusMap,
				EssayAnswerConstant.EssayAnswerBizStatusEnum.CORRECT_RETURN.getBizStatus());
		if (null != returnCount) {
			answerCardInfo.setStatus(EssayAnswerConstant.EssayAnswerBizStatusEnum.CORRECT_RETURN.getBizStatus());
			return answerCardInfo;
		}
		if (null != unDoCount && unDoCount == answerCardInfo.getQcount()) {
			answerCardInfo.setStatus(EssayAnswerConstant.EssayAnswerBizStatusEnum.INIT.getBizStatus());
			answerCardInfo.setFcount(0);
			return answerCardInfo;
		}
		if (null != correctCount) {
			answerCardInfo.setStatus(EssayAnswerConstant.EssayAnswerBizStatusEnum.UNFINISHED.getBizStatus());
			answerCardInfo.setFcount(correctCount);
		}
		if (null != commitCount) {
			answerCardInfo.setStatus(EssayAnswerConstant.EssayAnswerBizStatusEnum.UNFINISHED.getBizStatus());
			return answerCardInfo;
		}
		return answerCardInfo;
	}




    /**
     * 处理被退回原因
     * @param answerCardId
     * @param answerCardType
     */
    public String dealCorrectReturnMemo(long answerCardId, int answerCardType){
        Map<String, Object> result = correctOrderMapper.selectByAnswerCardIdAndType(answerCardType, answerCardId);
        return MapUtils.getString(result, "correct_memo", StringUtils.EMPTY);
    }
}
