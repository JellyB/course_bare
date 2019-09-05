package com.huatu.tiku.course.service.v7.impl;

import com.alibaba.fastjson.JSONObject;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import com.google.common.collect.Table;
import com.huatu.common.ErrorResult;
import com.huatu.common.SuccessMessage;
import com.huatu.common.exception.BizException;
import com.huatu.tiku.course.bean.vo.*;
import com.huatu.tiku.course.common.StudyTypeEnum;
import com.huatu.tiku.course.common.SubjectEnum;
import com.huatu.tiku.course.consts.RabbitMqConstants;
import com.huatu.tiku.course.consts.SyllabusInfo;
import com.huatu.tiku.course.dao.essay.*;
import com.huatu.tiku.course.service.manager.CourseExercisesProcessLogManager;
import com.huatu.tiku.course.service.manager.EssayExercisesAnswerMetaManager;
import com.huatu.tiku.course.service.v7.UserCourseBizV7Service;
import com.huatu.tiku.course.util.CourseCacheKey;
import com.huatu.tiku.essay.constant.status.EssayAnswerConstant;
import com.huatu.tiku.essay.entity.correct.CorrectOrder;
import com.huatu.tiku.essay.entity.courseExercises.EssayCourseExercisesQuestion;
import com.huatu.tiku.essay.entity.courseExercises.EssayExercisesAnswerMeta;
import com.huatu.tiku.essay.essayEnum.CourseWareTypeEnum;
import com.huatu.tiku.essay.essayEnum.EssayAnswerCardEnum;
import com.huatu.tiku.essay.essayEnum.EssayStatusEnum;
import com.huatu.tiku.essay.essayEnum.YesNoEnum;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.MapUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.SetOperations;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.stereotype.Service;
import tk.mybatis.mapper.entity.Example;

import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;


/**
 * 描述：
 *
 * @author biguodong
 * Create time 2019-03-22 9:50 PM
 **/
@Service
@Slf4j
public class UserCourseBizV7ServiceImpl implements UserCourseBizV7Service {


    @Autowired
    private RabbitTemplate rabbitTemplate;

    @Autowired
    private RedisTemplate redisTemplate;

    @Autowired
    private CourseExercisesProcessLogManager courseExercisesProcessLogManager;

    @Autowired
    private EssayExercisesAnswerMetaManager essayExercisesAnswerMetaManager;

    @Autowired
    private EssayExercisesAnswerMetaMapper essayExercisesAnswerMetaMapper;

    @Autowired
    private EssayCourseExercisesQuestionMapper essayCourseExercisesQuestionMapper;

    @Autowired
    private EssaySimilarQuestionMapper essaySimilarQuestionMapper;

    @Autowired
    private EssayQuestionBaseMapper essayQuestionBaseMapper;

    @Autowired
    private EssayQuestionDetailMapper essayQuestionDetailMapper;

    @Autowired
    private EssayPaperBaseMapper essayPaperBaseMapper;

    @Autowired
    private CorrectOrderMapper correctOrderMapper;


    /**
     * 直播数据上报处理
     *
     * @param userId
     * @param userName
     * @param subject
     * @param terminal
     * @param liveRecordInfo
     * @param cv
     * @throws BizException
     */
    @Override
    public void dealLiveReport(int userId, String userName, int subject, int terminal, String cv, LiveRecordInfo liveRecordInfo) throws BizException {



        LiveRecordInfoWithUserInfo liveRecordInfoWithUserId = LiveRecordInfoWithUserInfo
                .builder()
                .subject(subject)
                .terminal(terminal)
                .userName(userName)
                .cv(cv)
                .userId(userId)
                .liveRecordInfo(liveRecordInfo).build();
        log.debug("学员直播上报数据v7:{}", JSONObject.toJSONString(liveRecordInfoWithUserId));
        rabbitTemplate.convertAndSend("", RabbitMqConstants.COURSE_LIVE_REPORT_LOG, JSONObject.toJSONString(liveRecordInfoWithUserId));
    }

    /**
     * 课后作业全部已读 行测、申论
     *
     * @param userId
     * @param type
     * @param uName
     * @throws BizException
     */
    @Override
    public long allReadByType(int userId, String type, String uName) throws BizException {
        long updateCount = 0;
        try{
            updateCount = courseExercisesProcessLogManager.allReadByType(userId, type, uName);
            StudyTypeEnum studyTypeEnum = StudyTypeEnum.create(type);
            //更新申论课后作业小红点
            if(studyTypeEnum.equals(StudyTypeEnum.COURSE_WORK)){
                String key = CourseCacheKey.getCourseWorkEssayIsAlert(userId);
                SetOperations<String, Long> setOperations = redisTemplate.opsForSet();
                updateCount = updateCount + setOperations.size(key);
                Set<Long> members = setOperations.members(key);
                setOperations.remove(key, members);
            }
        }catch (Exception e){
            e.printStackTrace();
            log.error("UserCourseBizV7Service.allReadByType error: userId:{}, type:{}", userId, type);
            updateCount = -1;
        }
        return updateCount;
    }

    /**
     * 单条已读
     *
     * @param userId
     * @param syllabusId
     * @return
     * @throws BizException
     */
    @Override
    public Object readyOneCourseWork(int userId, int type, long syllabusId) throws BizException {
        SubjectEnum subjectEnum = SubjectEnum.create(type);
        if(subjectEnum == SubjectEnum.XC){
            courseExercisesProcessLogManager.readyOneCourseWorkBySyllabusId(userId, syllabusId);
        }
        if(subjectEnum == SubjectEnum.SL){
            String key = CourseCacheKey.getCourseWorkEssayIsAlert(userId);
            SetOperations<String, Long> setOperations = redisTemplate.opsForSet();
            if(setOperations.isMember(key, syllabusId)){
                setOperations.remove(key, syllabusId);
            }
        }
        return SuccessMessage.create("操作成功");
    }


    /**
     * 获取行测、申论课后作业列表
     *
     * @param userId
     * @param type
     * @param page
     * @param size
     * @return
     * @throws BizException
     */
    @Override
    public Object courseWorkList(int userId, int type, int page, int size) throws BizException {
        SubjectEnum subjectEnum = SubjectEnum.create(type);
        Map<String,Object> result = Maps.newHashMap();
        List<CourseWorkCourseVo> list = Lists.newArrayList();
        result.put("list", list);
        result.put("civilUnRead", courseExercisesProcessLogManager.obtainCivilCourseWorkUnReadCount(userId));
        result.put("essayUnRead", obtainEssayCourseWorkUnReadCount(userId));
        if(subjectEnum == SubjectEnum.XC){
            list.addAll((List<CourseWorkCourseVo>) courseExercisesProcessLogManager.courseWorkList(userId, page, size));
        }
        if(subjectEnum == SubjectEnum.SL){
            List<HashMap<String, Object>> essayCoursePageInfo = essayExercisesAnswerMetaMapper.getEssayCoursePageInfo(userId, page, size);
            if(null == essayCoursePageInfo || CollectionUtils.isEmpty(essayCoursePageInfo)){
                return result;
            }
            log.info("查询数据库获取用户未完成申论课后练习数据列表: essayCoursePageInfo.list:{}", essayCoursePageInfo);
            Set<Long> allSyllabusIds = Sets.newHashSet();
            essayCoursePageInfo.forEach(item -> {
                String ids = MapUtils.getString(item, "syllabusIds");
                Set<Long> temp = Arrays.stream(ids.split(",")).map(Long::valueOf).collect(Collectors.toSet());
                allSyllabusIds.addAll(temp);
            });

            Table<String, Long, SyllabusWareInfo> syllabusWareInfoTable = courseExercisesProcessLogManager.dealSyllabusInfo(allSyllabusIds);
            if(syllabusWareInfoTable.isEmpty()){
                return result;
            }

            Example example = new Example(EssayExercisesAnswerMeta.class);
            example.and()
                    .andEqualTo("userId", userId)
                    .andEqualTo("status", EssayStatusEnum.NORMAL.getCode())
                    .andIn("syllabusId", allSyllabusIds);

            try{
                // 处理答题卡 info
                List<EssayExercisesAnswerMeta> metaList = essayExercisesAnswerMetaMapper.selectByExample(example);

                Map<Long, EssayExercisesAnswerMeta> essayExercisesAnswerMetaHashMap = Maps.newHashMap();
                for (EssayExercisesAnswerMeta essayExercisesAnswerMeta : metaList) {
                    essayExercisesAnswerMetaHashMap.put(essayExercisesAnswerMeta.getSyllabusId(), essayExercisesAnswerMeta);
                }

                String key = CourseCacheKey.getCourseWorkEssayIsAlert(userId);
                SetOperations<String, Long> setOperations = redisTemplate.opsForSet();

                essayCoursePageInfo.forEach(item->{
                    CourseWorkCourseVo courseWorkCourseVo = new CourseWorkCourseVo();
                    long courseId = MapUtils.getLong(item, "courseId");
                    courseWorkCourseVo.setCourseId(courseId);
                    SyllabusWareInfo courseInfo = syllabusWareInfoTable.get(CourseExercisesProcessLogManager.COURSE_LABEL, courseWorkCourseVo.getCourseId());
                    if(null == courseInfo){
                        courseWorkCourseVo.setCourseTitle(StringUtils.EMPTY);
                        log.error("根据大纲id获取大纲信息异常:课程id & 大纲 ids: {}", item);
                    }else{
                        courseWorkCourseVo.setCourseTitle(courseInfo.getClassName());
                    }
                    String syllabusIds = String.valueOf(item.get("syllabusIds"));
                    Set<Long> temp = Arrays.stream(syllabusIds.split(",")).map(Long::valueOf).collect(Collectors.toSet());
                    if(CollectionUtils.isEmpty(temp)){
                        courseWorkCourseVo.setUndoCount(0);
                        courseWorkCourseVo.setWareInfoList(Lists.newArrayList());
                        courseWorkCourseVo.setCourseTitle(StringUtils.EMPTY);
                    }

                    List<CourseWorkWareVo> wareVos = Arrays.stream(syllabusIds.split(","))
                            .filter(syllabusId_ ->
                                null != syllabusWareInfoTable.get(CourseExercisesProcessLogManager.LESSON_LABEL, Long.valueOf(syllabusId_)) &&
                                null !=  essayExercisesAnswerMetaHashMap.get(Long.valueOf(syllabusId_)))
                            .map(syllabusId_ -> {
                                Long syllabusId = Long.valueOf(syllabusId_);
                                EssayCourseWorkAnswerCardInfo essayAnswerCardInfo = new EssayCourseWorkAnswerCardInfo();

                                essayAnswerCardInfo.setStatus(EssayAnswerConstant.EssayAnswerBizStatusEnum.INIT.getBizStatus());
                                SyllabusWareInfo syllabusWareInfo = syllabusWareInfoTable.get(CourseExercisesProcessLogManager.LESSON_LABEL, syllabusId);
                                EssayExercisesAnswerMeta essayExercisesAnswerMeta = essayExercisesAnswerMetaHashMap.get(Long.valueOf(syllabusId_));
                                Map map = new HashMap();
                                map.put(SyllabusInfo.VideoType, essayExercisesAnswerMeta.getCourseType());
                                map.put(SyllabusInfo.CourseWareId, essayExercisesAnswerMeta.getCourseWareId());
                                map.put(SyllabusInfo.SyllabusId, essayExercisesAnswerMeta.getSyllabusId());

                                essayExercisesAnswerMetaManager.dealSingleQuestionOrPaperOrMultiQuestions(userId, essayAnswerCardInfo, map);
                                CourseWorkWareVo courseWorkWareVo = new CourseWorkWareVo();

                                courseWorkWareVo.setSyllabusId(syllabusId);
                                courseWorkWareVo.setCourseWareTitle(syllabusWareInfo.getCoursewareName());
                                courseWorkWareVo.setVideoLength(syllabusWareInfo.getLength());
                                courseWorkWareVo.setSerialNumber(syllabusWareInfo.getSerialNumber());
                                courseWorkWareVo.setAnswerCardId(essayExercisesAnswerMeta.getAnswerId());
                                if(syllabusWareInfo.getVideoType() == CourseWareTypeEnum.LIVE_PLAY_BACK.getVideoType()){
                                    courseWorkWareVo.setCourseWareId(essayExercisesAnswerMeta.getCourseWareId());
                                    courseWorkWareVo.setVideoType(essayExercisesAnswerMeta.getCourseType());
                                }else{
                                    courseWorkWareVo.setCourseWareId(syllabusWareInfo.getCoursewareId());
                                    courseWorkWareVo.setVideoType(syllabusWareInfo.getVideoType());
                                }
                                courseWorkWareVo.setQuestionIds("");
                                courseWorkWareVo.setIsAlert(setOperations.isMember(key, syllabusId) ? YesNoEnum.YES.getValue() : YesNoEnum.NO.getValue());
                                // 设置申论的questionType
                                courseWorkWareVo.setQuestionType(essayAnswerCardInfo.getQuestionType());
                                courseWorkWareVo.setAnswerCardInfo(essayAnswerCardInfo);
                                return courseWorkWareVo;
                            }).collect(Collectors.toList());

                    courseWorkCourseVo.setUndoCount(temp.size());
                    courseWorkCourseVo.setWareInfoList(wareVos);
                    list.add(courseWorkCourseVo);
                });
            }catch (Exception e){
                log.error("获取课后练习列表异常:{},{}",userId, e);
                return result;
            }
        }
        return result;
    }


    /**
     * 获取申论课后作业未读数
     * @param userId
     * @return
     */
    private long obtainEssayCourseWorkUnReadCount (int userId){
        String key = CourseCacheKey.getCourseWorkEssayIsAlert(userId);
        SetOperations<String, Long> setOperations = redisTemplate.opsForSet();
        return setOperations.size(key);
    }



    /**
     * 课后作业未读数
     *
     * @param userId
     * @param userName
     * @return
     * @throws BizException
     */
    @Override
    public Map<String, Integer> getCountByType(int userId, String userName) throws BizException {
        Map<String, Integer> result = courseExercisesProcessLogManager.getCountByType(userId, userName);
        //todo 通过 redis 查询申论课后作业数目
        int essay = 0;
        int origin = MapUtils.getIntValue(result, StudyTypeEnum.COURSE_WORK.getKey(), 0);
        result.put(StudyTypeEnum.COURSE_WORK.getKey(), essay + origin);
        return result;
    }


    /**
     * 申论课后作业处理队列
     *
     * @param type
     * @param message
     * @throws BizException
     */
    @Override
    public void dealEssayCourseWork(String type, String message) throws BizException {

    }

    /**
     * 获取申论课后作业大纲信息
     *
     * @param courseType
     * @param courseWareId
     * @return
     * @throws BizException
     */
    @Override
    public EssayCourseWorkSyllabusInfo essayCourseWorkSyllabusInfo(Integer courseType, Long courseWareId, Long cardId) throws BizException {
        EssayCourseWorkSyllabusInfo essayCourseWorkSyllabusInfo = null;
        String key = CourseCacheKey.getEssayCourseWorkSyllabusInfo(courseType, courseWareId, cardId.longValue());
        ValueOperations<String, String> valueOperations = redisTemplate.opsForValue();
        if (redisTemplate.hasKey(key)) {
            String value = valueOperations.get(key);
            essayCourseWorkSyllabusInfo = JSONObject.parseObject(value, EssayCourseWorkSyllabusInfo.class);
            return essayCourseWorkSyllabusInfo;
        } else {
            essayCourseWorkSyllabusInfo = new EssayCourseWorkSyllabusInfo();
            essayCourseWorkSyllabusInfo.setBizStatus(EssayAnswerConstant.EssayAnswerBizStatusEnum.INIT.getBizStatus());
            if (cardId.longValue() > 0) {
                Map<String, Object> metaMap = essayExercisesAnswerMetaMapper.getBizStatusByCardId(cardId);
                if (null != metaMap) {
                    essayCourseWorkSyllabusInfo.setBizStatus(MapUtils.getIntValue(metaMap, "biz_status", 0));
                }

                Example example = new Example(EssayCourseExercisesQuestion.class);
                example.and()
                        .andEqualTo("courseType", courseType)
                        .andEqualTo("courseWareId", courseWareId)
                        .andEqualTo("status", EssayStatusEnum.NORMAL.getCode());


                List<EssayCourseExercisesQuestion> essayCourseExercisesQuestions = essayCourseExercisesQuestionMapper.selectByExample(example);
                if (CollectionUtils.isEmpty(essayCourseExercisesQuestions) || essayCourseExercisesQuestions.size() > 1) {
                    throw new BizException(ErrorResult.create(100010, "数据错误"));
                }
                EssayCourseExercisesQuestion essayCourseExercisesQuestion = essayCourseExercisesQuestions.get(0);

                //处理被退回原因
                if (essayCourseWorkSyllabusInfo.getBizStatus() == EssayAnswerConstant.EssayAnswerBizStatusEnum.CORRECT_RETURN.getBizStatus()) {
                    dealCorrectReturnMemo(essayCourseWorkSyllabusInfo, cardId, essayCourseExercisesQuestion.getType());
                }

                /**
                 * 如果为单题
                 */
                if (essayCourseExercisesQuestion.getType() == EssayAnswerCardEnum.TypeEnum.QUESTION.getType()) {

                    Map<String, Object> similarQuestionMap = essaySimilarQuestionMapper.selectByQuestionBaseId(essayCourseExercisesQuestion.getPQid());
                    if (null == similarQuestionMap || similarQuestionMap.isEmpty()) {
                        throw new BizException(ErrorResult.create(100010, "试题不存在"));
                    }

                    Map<String, Object> questionBaseMap = essayQuestionBaseMapper.selectQuestionBaseById(essayCourseExercisesQuestion.getPQid());
                    if (null == questionBaseMap || questionBaseMap.isEmpty()) {
                        throw new BizException(ErrorResult.create(100010, "试题不存在"));
                    }
                    Map<String, Object> detailMap = essayQuestionDetailMapper.selectQuestionDetailById(MapUtils.getLongValue(questionBaseMap, "detail_id", 0));
                    if (null == detailMap || detailMap.isEmpty()) {
                        throw new BizException(ErrorResult.create(100010, "试题不存在"));
                    }
                    essayCourseWorkSyllabusInfo.setSimilarId(MapUtils.getLongValue(similarQuestionMap, "similar_id"));
                    essayCourseWorkSyllabusInfo.setQuestionId(essayCourseExercisesQuestion.getPQid());
                    essayCourseWorkSyllabusInfo.setAreaName(MapUtils.getString(questionBaseMap, "area_name", ""));
                    essayCourseWorkSyllabusInfo.setQuestionType(MapUtils.getIntValue(detailMap, "type", 0));
                    essayCourseWorkSyllabusInfo.setPaperName(StringUtils.EMPTY);
                    essayCourseWorkSyllabusInfo.setPaperId(0l);
                }

                /**
                 * 如果为套题
                 */
                if (essayCourseExercisesQuestion.getType() == EssayAnswerCardEnum.TypeEnum.PAPER.getType()) {
                    Map<String, Object> paperBaseMap = essayPaperBaseMapper.selectPaperBaseById(essayCourseExercisesQuestion.getPQid().longValue());
                    if (null == paperBaseMap || paperBaseMap.isEmpty()) {
                        throw new BizException(ErrorResult.create(100010, "套卷不存在"));
                    }
                    essayCourseWorkSyllabusInfo.setSimilarId(0l);
                    essayCourseWorkSyllabusInfo.setQuestionId(0l);
                    essayCourseWorkSyllabusInfo.setAreaName(MapUtils.getString(paperBaseMap, "area_name"));
                    essayCourseWorkSyllabusInfo.setQuestionType(0);
                    essayCourseWorkSyllabusInfo.setPaperName(MapUtils.getString(paperBaseMap, "name", ""));
                    essayCourseWorkSyllabusInfo.setPaperId(essayCourseExercisesQuestion.getPQid());
                }

                valueOperations.set(key, JSONObject.toJSONString(essayCourseWorkSyllabusInfo), 1, TimeUnit.MINUTES);
            }
            return essayCourseWorkSyllabusInfo;
        }
    }

    /**
     * 处理被退回原因
     * @param essayCourseWorkSyllabusInfo
     * @param answerCardId
     * @param answerCardType
     */
    private void dealCorrectReturnMemo(EssayCourseWorkSyllabusInfo essayCourseWorkSyllabusInfo, long answerCardId, int answerCardType){
        Example example = new Example(CorrectOrder.class);
        example.and()
                .andEqualTo("status", EssayStatusEnum.NORMAL.getCode())
                .andEqualTo("answerCardType", answerCardType)
                .andEqualTo("answerCardId", answerCardId);
        CorrectOrder correctOrder = correctOrderMapper.selectOneByExample(example);
        essayCourseWorkSyllabusInfo.setCorrectMemo(null != correctOrder ? correctOrder.getCorrectMemo() : StringUtils.EMPTY);
    }

    /**
     * 使用 syllabusId 构建申论课后作业答题卡信息
     *
     * @param userId
     * @param syllabusId
     * @return
     * @throws BizException
     */
    @Override
    public EssayAnswerCardInfo buildEssayAnswerCardInfo(int userId, long syllabusId) throws BizException {
        return null;
    }
}
