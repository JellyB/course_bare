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
import com.huatu.tiku.course.common.CourseWareTypeEnum;
import com.huatu.tiku.course.consts.RabbitMqConstants;
import com.huatu.tiku.course.dao.essay.*;
import com.huatu.tiku.course.service.manager.CourseExercisesProcessLogManager;
import com.huatu.tiku.course.service.v7.UserCourseBizV7Service;
import com.huatu.tiku.course.util.CourseCacheKey;
import com.huatu.tiku.essay.constant.status.EssayAnswerConstant;
import com.huatu.tiku.essay.entity.EssayPaperBase;
import com.huatu.tiku.essay.entity.EssayQuestionBase;
import com.huatu.tiku.essay.entity.EssayQuestionDetail;
import com.huatu.tiku.essay.entity.EssaySimilarQuestion;
import com.huatu.tiku.essay.entity.correct.CorrectOrder;
import com.huatu.tiku.essay.entity.courseExercises.EssayCourseExercisesQuestion;
import com.huatu.tiku.essay.entity.courseExercises.EssayExercisesAnswerMeta;
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
    public long allReadByType(long userId, String type, String uName) throws BizException {
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
    public Object courseWorkList(long userId, int type, int page, int size) throws BizException {
        SubjectEnum subjectEnum = SubjectEnum.create(type);
        Map<String,Object> result = Maps.newHashMap();
        List<CourseWorkCourseVo> list = Lists.newArrayList();
        if(subjectEnum == SubjectEnum.XC){
            list.addAll((List<CourseWorkCourseVo>) courseExercisesProcessLogManager.courseWorkList(userId, page, size));
        }
        if(subjectEnum == SubjectEnum.SL){
            List<HashMap<String, Object>> essayCoursePageInfo = essayExercisesAnswerMetaMapper.getEssayCoursePageInfo(userId, page, size);
            log.info("查询数据库获取用户未完成申论课后练习数据列表: essayCoursePageInfo.list:{}", essayCoursePageInfo);
            Set<Long> allSyllabusIds = Sets.newHashSet();
            essayCoursePageInfo.forEach(item -> {
                String ids = MapUtils.getString(item, "syllabusIds");
                Set<Long> temp = Arrays.stream(ids.split(",")).map(Long::valueOf).collect(Collectors.toSet());
                allSyllabusIds.addAll(temp);
            });

            Table<String, Long, SyllabusWareInfo> syllabusWareInfoTable = courseExercisesProcessLogManager.dealSyllabusInfo(allSyllabusIds);
            if(syllabusWareInfoTable.isEmpty()){
                return list;
            }

            Example example = new Example(EssayExercisesAnswerMeta.class);
            example.and()
                    .andEqualTo("userId", userId)
                    .andEqualTo("status", EssayStatusEnum.NORMAL.getCode())
                    .andIn("syllabusId", allSyllabusIds);

            try{
                // 处理答题卡 info
                List<EssayExercisesAnswerMeta> metaList = essayExercisesAnswerMetaMapper.selectByExample(example);
                Map<Long, EssayAnswerCardInfo> essayAnswerCardInfoMap = dealEssayCardInfo(metaList);

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
                                SyllabusWareInfo syllabusWareInfo = syllabusWareInfoTable.get(CourseExercisesProcessLogManager.LESSON_LABEL, syllabusId);
                                EssayExercisesAnswerMeta essayExercisesAnswerMeta = essayExercisesAnswerMetaHashMap.get(Long.valueOf(syllabusId_));
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
                                if(essayAnswerCardInfoMap.containsKey(essayExercisesAnswerMeta.getAnswerId())){
                                    courseWorkWareVo.setAnswerCardInfo(essayAnswerCardInfoMap.get(essayExercisesAnswerMeta.getAnswerId()));
                                }else{
                                    courseWorkWareVo.setAnswerCardInfo(new EssayAnswerCardInfo());
                                }
                                return courseWorkWareVo;
                            }).collect(Collectors.toList());

                    courseWorkCourseVo.setUndoCount(temp.size());
                    courseWorkCourseVo.setWareInfoList(wareVos);
                    list.add(courseWorkCourseVo);
                });
            }catch (Exception e){
                log.error("获取课后练习列表异常:{},{}",userId, e);
                return list;
            }

            list.addAll((List<CourseWorkCourseVo>) courseExercisesProcessLogManager.courseWorkList(userId, page, size));
            list.forEach(item -> {
                List<CourseWorkWareVo> courseWorkWareVos = item.getWareInfoList();
                for (CourseWorkWareVo courseWorkWareVo : courseWorkWareVos) {
                    courseWorkWareVo.setQuestionType(0);
                    courseWorkWareVo.setSyllabusId(141324L);
                    courseWorkWareVo.getAnswerCardInfo().setType(SubjectEnum.SL.getCode());
                }
            });
        }
        result.put("list", list);
        result.put("civilUnRead", courseExercisesProcessLogManager.obtainCivilCourseWorkUnReadCount(userId));
        result.put("essayUnRead", obtainEssayCourseWorkUnReadCount(userId));
        return result;
    }


    /**
     * cardId 对应 essayCardInfo
     * @param metaList
     * @return
     */
    private Map<Long, EssayAnswerCardInfo> dealEssayCardInfo(List<EssayExercisesAnswerMeta> metaList){
        Map<Long, EssayAnswerCardInfo> result = Maps.newHashMap();
        for (EssayExercisesAnswerMeta essayExercisesAnswerMeta : metaList) {
            EssayAnswerCardInfo essayAnswerCardInfo = new EssayAnswerCardInfo();
            essayAnswerCardInfo.setId(essayExercisesAnswerMeta.getAnswerId());
            essayAnswerCardInfo.setQcount(1);
            essayAnswerCardInfo.setUcount(0);
            essayAnswerCardInfo.setWcount(0);
            essayAnswerCardInfo.setRcount(1);
            essayAnswerCardInfo.setQuestionBaseId(essayExercisesAnswerMeta.getPQid());
            essayAnswerCardInfo.setType(SubjectEnum.SL.getCode());
            essayAnswerCardInfo.setCorrectNum(essayExercisesAnswerMeta.getCorrectNum());
            essayAnswerCardInfo.setPaperId(essayExercisesAnswerMeta.getPQid());
            essayAnswerCardInfo.setExamScore(essayExercisesAnswerMeta.getExamScore());
            essayAnswerCardInfo.setScore(100);
            essayAnswerCardInfo.setSimilarId(100l);
            essayAnswerCardInfo.setStatus(essayExercisesAnswerMeta.getBizStatus());
            if(essayExercisesAnswerMeta.getBizStatus() == EssayAnswerConstant.EssayAnswerBizStatusEnum.CORRECT_RETURN.getBizStatus()){
                Example example = new Example(CorrectOrder.class);
                example.and()
                .andEqualTo("status", EssayStatusEnum.NORMAL.getCode())
                .andEqualTo("answerCardType", essayExercisesAnswerMeta.getAnswerType())
                .andEqualTo("answerCardId", essayExercisesAnswerMeta.getAnswerId());
                CorrectOrder correctOrder = correctOrderMapper.selectOneByExample(example);
                essayAnswerCardInfo.setCorrectMemo(null != correctOrder ? correctOrder.getCorrectMemo() : StringUtils.EMPTY);
            }
            result.put(essayExercisesAnswerMeta.getAnswerId(), essayAnswerCardInfo);
        }
        return Maps.newHashMap();
    }

    /**
     * 获取申论课后作业未读数
     * @param userId
     * @return
     */
    private long obtainEssayCourseWorkUnReadCount (long userId){
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
    public Map<String, Integer> getCountByType(long userId, String userName) throws BizException {
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
    public EssayCourseWorkSyllabusInfo essayCourseWorkSyllabusInfo(Integer courseType, Long courseWareId) throws BizException {
        EssayCourseWorkSyllabusInfo essayCourseWorkSyllabusInfo = null;
        String key = CourseCacheKey.getEssayCourseWorkSyllabusInfo(courseType, courseWareId);
        ValueOperations<String, String> valueOperations = redisTemplate.opsForValue();
        if(redisTemplate.hasKey(key)){
            String value = valueOperations.get(key);
            essayCourseWorkSyllabusInfo = JSONObject.parseObject(value, EssayCourseWorkSyllabusInfo.class);
            return essayCourseWorkSyllabusInfo;
        }else{
            essayCourseWorkSyllabusInfo = new EssayCourseWorkSyllabusInfo();
            Example example = new Example(EssayCourseExercisesQuestion.class);
            example.and()
                    .andEqualTo("courseType", courseType)
                    .andEqualTo("courseWareId", courseWareId)
                    .andEqualTo("status", EssayStatusEnum.NORMAL.getCode());
            EssayCourseExercisesQuestion essayCourseExercisesQuestion = essayCourseExercisesQuestionMapper.selectOneByExample(example);
            if(null == essayCourseExercisesQuestion){
                throw new BizException(ErrorResult.create(100010, "数据错误"));
            }
            /**
             * 如果为单题
             */
            if(essayCourseExercisesQuestion.getType() == EssayAnswerCardEnum.TypeEnum.QUESTION.getType()){
                Example exampleQuestion = new Example(EssaySimilarQuestion.class);
                exampleQuestion.and()
                        .andEqualTo("questionBaseId", essayCourseExercisesQuestion.getPQid());
                EssaySimilarQuestion essaySimilarQuestion = essaySimilarQuestionMapper.selectOneByExample(exampleQuestion);
                EssayQuestionBase essayQuestionBase = essayQuestionBaseMapper.selectByPrimaryKey(essayCourseExercisesQuestion.getPQid());
                EssayQuestionDetail essayQuestionDetail = essayQuestionDetailMapper.selectByPrimaryKey(essayQuestionBase.getDetailId());
                essayCourseWorkSyllabusInfo.setSimilarId(essaySimilarQuestion.getSimilarId());
                essayCourseWorkSyllabusInfo.setQuestionId(essayCourseExercisesQuestion.getPQid());
                essayCourseWorkSyllabusInfo.setAreaName(essayQuestionBase.getAreaName());
                essayCourseWorkSyllabusInfo.setQuestionType(essayQuestionDetail.getType());
                essayCourseWorkSyllabusInfo.setPaperName(StringUtils.EMPTY);
                essayCourseWorkSyllabusInfo.setPaperId(0l);
            }

            /**
             * 如果为套题
             */
            if(essayCourseExercisesQuestion.getType() == EssayAnswerCardEnum.TypeEnum.PAPER.getType()){
                EssayPaperBase essayPaperBase = essayPaperBaseMapper.selectByPrimaryKey(essayCourseExercisesQuestion.getPQid());
                essayCourseWorkSyllabusInfo.setSimilarId(0l);
                essayCourseWorkSyllabusInfo.setQuestionId(0l);
                essayCourseWorkSyllabusInfo.setAreaName(StringUtils.EMPTY);
                essayCourseWorkSyllabusInfo.setQuestionType(0);
                essayCourseWorkSyllabusInfo.setPaperName(essayPaperBase.getName());
                essayCourseWorkSyllabusInfo.setPaperId(essayPaperBase.getId());
            }
            valueOperations.set(key, JSONObject.toJSONString(essayCourseWorkSyllabusInfo),  30, TimeUnit.DAYS);
        }
        return essayCourseWorkSyllabusInfo;
    }

    /**
     * 根据 syllabus id 批量查询 EssayExercisesAnswerMeta 信息
     *
     * @param userId
     * @param syllabusIds
     * @return
     * @throws BizException
     */
    @Override
    public List<EssayExercisesAnswerMeta> metas(int userId, Set<Long> syllabusIds) throws BizException {

        Example example = new Example(EssayExercisesAnswerMeta.class);
        example.and()
                .andEqualTo("userId", userId)
                .andEqualTo("status", EssayStatusEnum.NORMAL.getCode())
                .andIn("syllabusId", syllabusIds);
        return essayExercisesAnswerMetaMapper.selectByExample(example);
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
