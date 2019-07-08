package com.huatu.tiku.course.service.manager;

import java.io.Serializable;
import java.sql.Timestamp;
import java.util.*;
import java.util.concurrent.*;
import java.util.stream.Collectors;

import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import com.google.common.collect.*;
import com.huatu.common.SuccessMessage;
import com.huatu.springboot.degrade.core.Degrade;
import com.huatu.tiku.course.bean.vo.*;
import com.huatu.tiku.course.common.VideoTypeEnum;
import com.huatu.tiku.course.consts.RabbitMqConstants;
import com.huatu.tiku.course.consts.SimpleUserInfo;
import com.huatu.tiku.course.consts.SyllabusInfo;
import com.huatu.tiku.course.dao.manual.CourseExercisesCardInfoMapper;
import com.huatu.tiku.course.netschool.api.UserAccountServiceV1;
import com.huatu.tiku.course.netschool.api.v6.CourseServiceV6;
import com.huatu.tiku.course.service.v1.practice.CourseLiveBackLogService;
import com.huatu.tiku.entity.CourseExercisesCardInfo;
import com.huatu.tiku.entity.CourseLiveBackLog;
import lombok.*;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.MapUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.SetOperations;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.data.redis.core.ZSetOperations;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import com.alibaba.fastjson.JSONObject;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.base.Joiner;
import com.huatu.common.exception.BizException;
import com.huatu.common.utils.collection.HashMapBuilder;
import com.huatu.tiku.course.bean.NetSchoolResponse;
import com.huatu.tiku.course.common.StudyTypeEnum;
import com.huatu.tiku.course.common.YesOrNoStatus;
import com.huatu.tiku.course.dao.manual.CourseExercisesProcessLogMapper;
import com.huatu.tiku.course.netschool.api.v6.UserCourseServiceV6;
import com.huatu.tiku.course.netschool.api.v7.SyllabusServiceV7;
import com.huatu.tiku.course.service.v1.CourseExercisesService;
import com.huatu.tiku.course.util.CourseCacheKey;
import com.huatu.tiku.course.util.ResponseUtil;
import com.huatu.tiku.course.util.ZTKResponseUtil;
import com.huatu.tiku.course.ztk.api.v1.paper.PracticeCardServiceV1;
import com.huatu.tiku.entity.CourseExercisesProcessLog;
import com.huatu.ztk.paper.bean.PracticeCard;
import com.huatu.ztk.paper.common.AnswerCardStatus;

import lombok.extern.slf4j.Slf4j;
import org.springframework.util.StopWatch;
import tk.mybatis.mapper.entity.Example;

import javax.annotation.Resource;


/**
 * 描述：
 *
 * @author biguodong
 * Create time 2019-03-04 5:25 PM
 **/

@Component
@Slf4j
public class CourseExercisesProcessLogManager {

    @Autowired
    private CourseExercisesProcessLogMapper courseExercisesProcessLogMapper;

    @Autowired
    private CourseExercisesCardInfoMapper courseExercisesCardInfoMapper;

    @Autowired
    private CourseExercisesStatisticsManager courseExercisesStatisticsManager;

    @Autowired
    private CourseExercisesService courseExercisesService;

    @Autowired
    private PracticeCardServiceV1 practiceCardService;

    @Autowired
    private SyllabusServiceV7 syllabusService;

    @Autowired
    private RedisTemplate redisTemplate;

    @Autowired
    private RabbitTemplate rabbitTemplate;

    @Autowired
    private UserCourseServiceV6 userCourseServiceV6;

    @Autowired
    private CourseLiveBackLogService courseLiveBackLogService;

    @Autowired
    private CourseServiceV6 courseServiceV6;

    @Autowired
    private UserAccountServiceV1 userAccountService;

    @Resource(name = "courseExecutorService")
    private ExecutorService executorService;

    private static final String LESSON_LABEL = "lesson";

    private static final String COURSE_LABEL = "course";

    private static final long PERIOD_TIME = 60 * 1000;

    private static final String CORRECT_DATA_KEY = "data_correct_2019";
    private static final String CORRECT_DATA_SWITCH = "data_correct_2019_switch";
    private static final String CORRECT_DATA_SWITCH_ON = "on";

    private final Cache<Integer, List<ClassInfo>> classInfo = CacheBuilder.newBuilder()
            .recordStats()
            .maximumSize(2048)
            .expireAfterWrite(5, TimeUnit.HOURS)
            .build();

    /**
     * 获取类型未读量
     * @param userId
     * @return
     */
    @Degrade(key = "unFinishNumV6", name = "学习面板 - 未读数")
    public Map<String, Integer> getCountByType(long userId,String userName) throws BizException{
        List<Integer> list = Lists.newArrayList(AnswerCardStatus.CREATE, AnswerCardStatus.UNDONE);
        StopWatch stopWatch = new StopWatch("学习面板-未读数");
        stopWatch.start("课后作业");
        Map<String, Integer> result = Maps.newHashMap();
        Map<String, String> param = Maps.newHashMap();
        Example example = new Example(CourseExercisesProcessLog.class);
        Example.Criteria criteria = example.and();
        criteria.andEqualTo("userId", userId);
        criteria.andEqualTo("status", YesOrNoStatus.YES.getCode());
        criteria.andEqualTo("isAlert", YesOrNoStatus.YES.getCode());
        criteria.andIn("bizStatus", list);
        criteria.andEqualTo("dataType", StudyTypeEnum.COURSE_WORK.getOrder());
        int countWork = courseExercisesProcessLogMapper.selectCountByExample(example);
        stopWatch.stop();
        stopWatch.start("阶段测试");
        result.put(StudyTypeEnum.COURSE_WORK.getKey(), countWork);
        //获取总数量
        param.put("userName",userName );
        NetSchoolResponse response  = userCourseServiceV6.unfinishStageExamCount(param);
        stopWatch.stop();
        log.info("学习面板-未读数 pretty:{}", stopWatch.prettyPrint());
		if (ResponseUtil.isSuccess(response)) {
			Map<String, Integer> retMap = (Map<String, Integer>) response.getData();
			Integer count = retMap.get("num");
			log.info("用户:{}需要提醒的阶段测试数为:{}", userName, count);
			result.put(StudyTypeEnum.PERIOD_TEST.getKey(), count);
		} else {
			result.put(StudyTypeEnum.PERIOD_TEST.getKey(), 0);
		}
        return result;
    }

    /**
     * 学习面板 - 降级处理
     * @param userId
     * @param userName
     * @return
     * @throws BizException
     */
    public Map<String, Integer> getCountByTypeDegrade(long userId,String userName) throws BizException{
        Map<String, Integer> result = Maps.newHashMap();
        result.put(StudyTypeEnum.COURSE_WORK.getKey(), 0);
        result.put(StudyTypeEnum.PERIOD_TEST.getKey(), 0);
        return result;
    }



    /**
     * 单种类型全部已读
     * @param userId
     * @param type
     * @return
     */
	public int allReadByType(long userId, String type, String uName) throws BizException {
		try {
			StudyTypeEnum studyTypeEnum = StudyTypeEnum.create(type);
			if (studyTypeEnum.equals(StudyTypeEnum.COURSE_WORK)) {
				CourseExercisesProcessLog courseExercisesProcessLog = new CourseExercisesProcessLog();
				courseExercisesProcessLog.setGmtModify(new Timestamp(System.currentTimeMillis()));
				courseExercisesProcessLog.setIsAlert(YesOrNoStatus.NO.getCode());

				Example example = new Example(CourseExercisesProcessLog.class);
				example.and().andEqualTo("status", YesOrNoStatus.YES.getCode())
						.andEqualTo("dataType", studyTypeEnum.getOrder()).andEqualTo("userId", userId)
						.andEqualTo("isAlert", YesOrNoStatus.YES.getCode());
				int count = courseExercisesProcessLogMapper.updateByExampleSelective(courseExercisesProcessLog,
						example);
				return count;
			} else {
				// 阶段测试全部已读
				Map<String, Object> param = Maps.newHashMap();
				param.put("userName", uName);
				param.put("type", YesOrNoStatus.YES.getCode());
				NetSchoolResponse response = userCourseServiceV6.readPeriod(param);
				log.info("用户{}全部已读阶段测试 返回结果:{}", uName, response.getData());
				return YesOrNoStatus.YES.getCode();
			}

		} catch (Exception e) {
			log.error("all read by type error!:{}", e);
			return -1;
		}
	}

    /**
     * 单条已读
     * @param userId
     * @param courseWareId
     * @param courseType
     * @return
     * @throws BizException
     */
	public int readyOneCourseWork(int userId, long courseWareId, int courseType) throws BizException {
        CourseExercisesProcessLog courseExercisesProcessLog = new CourseExercisesProcessLog();
        courseExercisesProcessLog.setGmtModify(new Timestamp(System.currentTimeMillis()));
        courseExercisesProcessLog.setIsAlert(YesOrNoStatus.NO.getCode());

        Example example = new Example(CourseExercisesProcessLog.class);
        example.and().andEqualTo("userId", userId)
                .andEqualTo("courseType", courseType)
                .andEqualTo("lessonId", courseWareId)
                .andEqualTo("dataType", StudyTypeEnum.COURSE_WORK.getOrder());

		return	courseExercisesProcessLogMapper.updateByExampleSelective(courseExercisesProcessLog, example);
    }

    /**
     * 录播 & 回放处理进度
     * @param recordProcess
     * @throws BizException
     */
    public void dealRecordProcess(RecordProcess recordProcess) throws BizException{
        if(recordProcess.getUserId() == 0 || recordProcess.getSyllabusId() == 0){
            return;
        }
        Table<String, Long, SyllabusWareInfo> syllabusWareInfoTable = dealSyllabusInfo(Sets.newHashSet(recordProcess.getSyllabusId()));
        SyllabusWareInfo syllabusWareInfo = syllabusWareInfoTable.get(LESSON_LABEL, recordProcess.getSyllabusId());
        if(null == syllabusWareInfo){
            return;
        }
        /**
         * 移动端数据上报，只处理录播的学习进度，回放不处理
         */
        if(syllabusWareInfo.getVideoType() == VideoTypeEnum.LIVE_PLAY_BACK.getVideoType()){
            return;
        }
        this.createCourseWorkAnswerCardEntrance(syllabusWareInfo.getClassId(),
                recordProcess.getSyllabusId(),
                syllabusWareInfo.getVideoType(),
                syllabusWareInfo.getCoursewareId(),
                recordProcess.getSubject(),
                recordProcess.getTerminal(),
                recordProcess.getCv(),
                recordProcess.getUserId());
    }


    /**
     * 创建课后作业答题卡前置逻辑入口
     * @param courseId
     * @param syllabusId
     * @param courseType
     * @param coursewareId
     * @param subject
     * @param terminal
     * @param cv
     * @param userId
     * @return
     * @throws BizException
     */
    public synchronized Object createCourseWorkAnswerCardEntrance(Long courseId, Long syllabusId, int courseType, Long coursewareId, int subject, int terminal, String cv, int userId) throws BizException{
        log.info("请求创建课后作业答题卡参数信息:courseType:{},courseId:{},syllabusId:{},coursewareId:{},terminal:{},cv:{},userId:{}",
                courseType, courseId, syllabusId, coursewareId, terminal, cv, userId);
        StopWatch stopwatch = new StopWatch("手动创建录播或直播回放课后作业答题卡");
        stopwatch.start();
        final ClassInfo classInfo = ClassInfo.builder()
                .classId(courseId.intValue())
                .syllabusId(syllabusId.intValue())
                .coursewareType(courseType)
                .courseWareId(coursewareId.intValue())
                .build();

        HashMap<String, Object> answerCardInfo;
        try{
            answerCardInfo = obtainOrCreateAnswerCardThroughPaperService(classInfo, subject, terminal, cv, userId, true);
            insertOrUpdateLogInfo(userId, classInfo, answerCardInfo, true);
        }catch (Exception e){
            log.error("Exception:{}, terminal:{}, cv:{}", syllabusId, terminal, cv);
            return null;
        }
        log.info("课后作业 - 创建课后答题卡请求参数:courseId:{},syllabusId:{},courseType:{},coursewareId:{},userId:{}", courseId, syllabusId, courseType, coursewareId, userId);
        stopwatch.stop();
        log.info("手动创建录播或直播回放课后作业答题卡:{}", stopwatch.prettyPrint());
        return answerCardInfo;
    }

    /**
     * 调用 paper 服务获取或者创建课后练习答题卡
     * @param classInfo
     * @param subject
     * @param terminal
     * @param cv
     * @param userId
     * @param needCreate 是否需要创建答题卡
     * @return
     */
    private HashMap<String, Object> obtainOrCreateAnswerCardThroughPaperService(final ClassInfo classInfo, int subject, int terminal, String cv, int userId, boolean needCreate){
        log.debug("课后作业数据修正--- obtainOrCreateAnswerCardThroughPaperService 1: userID:{}, syllabusId:{}", userId, classInfo.getSyllabusId().longValue());
        HashMap<String, Object> answerCardInfo = Maps.newHashMap();
        if(classInfo.getCoursewareType() == VideoTypeEnum.LIVE_PLAY_BACK.getVideoType()){
            SyllabusWareInfo syllabusWareInfo = requestSingleSyllabusInfoWithCache(classInfo.getSyllabusId().longValue());
            if(null == syllabusWareInfo || StringUtils.isEmpty(syllabusWareInfo.getRoomId())){
                log.error("课后作业数据修正---  直播回放创建课后作业答题卡失败，查询不到百家云信息:{}", classInfo.getSyllabusId().longValue());
                return answerCardInfo;
            }
            CourseLiveBackLog courseLiveBackLog = courseLiveBackLogService.findByRoomIdAndLiveCourseWareIdV2(Long.valueOf(syllabusWareInfo.getRoomId()), syllabusWareInfo.getCoursewareId());
            if(null == courseLiveBackLog){
                log.error("课后作业数据修正---  直播回放数据查询不到roomId:{},课件id:{},终端信息:terminal:{},cv:{}",syllabusWareInfo.getRoomId(), syllabusWareInfo.getCoursewareId(),terminal, cv);
                return answerCardInfo;
            }else{
                classInfo.setCourseWareId(courseLiveBackLog.getLiveCoursewareId().intValue());
                classInfo.setCoursewareType(VideoTypeEnum.LIVE.getVideoType());
            }
        }

        log.debug("课后作业数据修正--- obtainOrCreateAnswerCardThroughPaperService 2: userID:{}, syllabusId:{}", userId, classInfo.getSyllabusId().longValue());
        List<Map<String, Object>> list = courseExercisesService.listQuestionByCourseId(classInfo.getCoursewareType(), classInfo.getCourseWareId().longValue());
        if (CollectionUtils.isEmpty(list)) {
            log.error("课后作业数据修正---  没有配置课后作业试题信息:courseType:{}, courseWareId:{}", classInfo.getCoursewareType(), classInfo.getCourseWareId().longValue());
            return answerCardInfo;
        }

        log.debug("课后作业数据修正--- obtainOrCreateAnswerCardThroughPaperService 3: userID:{}, syllabusId:{}", userId, classInfo.getSyllabusId().longValue());
        String questionId = list.stream()
                .filter(map -> null != map && null != map.get("id"))
                .map(map -> String.valueOf(map.get("id")))
                .collect(Collectors.joining(","));

        log.debug("课后作业数据修正--- obtainOrCreateAnswerCardThroughPaperService 4: userID:{}, syllabusId:{}", userId, classInfo.getSyllabusId().longValue());
        Object practiceCard;
        if(needCreate){
            practiceCard = practiceCardService.createCourseExercisesPracticeCard(
                    terminal, subject, userId, "课后作业练习", classInfo.getCoursewareType(), classInfo.getCourseWareId().longValue(), questionId);
        }else{
            practiceCard = practiceCardService.obtainCourseExercisesPracticeCard(userId, classInfo.getCoursewareType(), classInfo.getCourseWareId().longValue());
        }


        answerCardInfo = (HashMap<String, Object>) ZTKResponseUtil.build(practiceCard);
        log.debug("课后作业数据修正--- obtainOrCreateAnswerCardThroughPaperService 5: userID:{}, syllabusId:{}, answerCardInfo:{}", userId, classInfo.getSyllabusId().longValue(), JSONObject.toJSONString(answerCardInfo));
        if (MapUtils.isNotEmpty(answerCardInfo)) {
            Long id = MapUtils.getLong(answerCardInfo, "id");
            if(null == id || id.intValue() == 0){
                return Maps.newHashMap();
            }else{
                answerCardInfo.computeIfPresent("id", (key, value) -> String.valueOf(value));
                log.debug("课后作业数据修正--- obtainOrCreateAnswerCardThroughPaperService 6: userID:{}, syllabusId:{}", userId, classInfo.getSyllabusId().longValue());
                return answerCardInfo;
            }
        }else{
            return Maps.newHashMap();
        }
    }

    /**
     * 课后作业创建答题卡异步处理方法
     * @param classInfo
     * @param result
     * @param isAlert
     * @param userId
     */
    public synchronized void insertOrUpdateLogInfo(int userId, ClassInfo classInfo, HashMap<String,Object> result, boolean isAlert){
        Long cardId = MapUtils.getLongValue(result, "id");
        int status = MapUtils.getIntValue(result, "status");

        if(null == classInfo.getSyllabusId() || classInfo.getSyllabusId().intValue() == 0){
            throw new IllegalArgumentException("大纲id数据不对");
        }
        Example example = new Example(CourseExercisesProcessLog.class);
        example.and().andEqualTo("lessonId", classInfo.getCourseWareId())
                .andEqualTo("courseType", classInfo.getCoursewareType())
                .andEqualTo("userId", userId)
                .andEqualTo("dataType", StudyTypeEnum.COURSE_WORK.getOrder())
                .andEqualTo("status", YesOrNoStatus.YES.getCode())
                .andEqualTo("cardId", cardId);

        try{
            List<CourseExercisesProcessLog> courseExercisesProcessLogList = courseExercisesProcessLogMapper.selectByExample(example);
            log.info("创建课后作业答题卡信息:{}",JSONObject.toJSONString(courseExercisesProcessLogList));
            if(CollectionUtils.isEmpty(courseExercisesProcessLogList)){
                /**
                 * 新增数据
                 */
                buildCourseWorkLog4insert(userId, classInfo.getCoursewareType(), classInfo.getCourseWareId().longValue(), classInfo.getClassId().longValue(), classInfo.getSyllabusId().longValue(), isAlert, cardId, status);
                putIntoDealList(classInfo.getSyllabusId().longValue());
            }else{
                CourseExercisesProcessLog courseExercisesProcessLog = courseExercisesProcessLogList.get(0);
                if(courseExercisesProcessLogList.size() > 1){
                    for(int i = 1; i < courseExercisesProcessLogList.size(); i ++){
                        CourseExercisesProcessLog temp = courseExercisesProcessLogList.get(i);
                        courseExercisesProcessLogMapper.deleteByPrimaryKey(temp.getId());
                        log.error("课后作业重复数据,数据size:{}:数据内容{},", courseExercisesProcessLogList.size(), JSONObject.toJSONString(temp));
                    }
                }
                /**
                 * 更新答题卡字段
                 */
                CourseExercisesProcessLog update = new CourseExercisesProcessLog();
                BeanUtils.copyProperties(courseExercisesProcessLog, update);
                update.setGmtModify(new Timestamp(System.currentTimeMillis()));
                update.setBizStatus(status);
                courseExercisesProcessLogMapper.updateByExampleSelective(update, example);
            }
        }catch (Exception e){
            log.error("课后作业创建答题卡异步处理方法失败, userId:{},courseType:{},coursewareId:{},courseId:{},syllabusId:{},result:{},error:{}", userId, classInfo.getCoursewareType(), classInfo.getCourseWareId(), classInfo.getClassId(), classInfo.getSyllabusId(), result, e.getMessage());
        }
    }


    /**
     *
     * @param userId
     * @param classInfo
     * @param result
     * @param isAlert
     */
        public void insertCardInfo(int userId, ClassInfo classInfo, HashMap<String,Object> result, boolean isAlert){

        log.debug("课后作业数据修正---  insertCardInfo: userId:{}, syllabusId:{}", userId, classInfo.getSyllabusId());
        Long cardId = MapUtils.getLongValue(result, "id");
        int status = MapUtils.getIntValue(result, "status");

        if(null == classInfo.getSyllabusId()){
            throw new IllegalArgumentException("课后作业数据修正--- 大纲id数据不对");
        }
        try{
            buildCourseWorkCardInfo4insert(userId, classInfo.getCoursewareType(), classInfo.getCourseWareId().longValue(), classInfo.getClassId().longValue(), classInfo.getSyllabusId().longValue(), isAlert, cardId, status);
        }catch (Exception e){
            log.error("课后作业数据修正--- 课后作业创建答题卡异步处理方法失败--fix, userId:{},courseType:{}, courseWareId:{},courseId:{},syllabusId:{},result:{},error:{}", userId, classInfo.getCoursewareType(), classInfo.getCourseWareId(), classInfo.getClassId(), classInfo.getSyllabusId(), result, e.getMessage());
        }
    }

    /**
     * 构建数据 for insert
     * @param userId
     * @param courseType
     * @param coursewareId
     * @param courseId
     * @param syllabusId
     * @param isAlert
     * @param cardId
     * @param status
     */
    private void buildCourseWorkLog4insert(int userId, Integer courseType, Long coursewareId, Long courseId, Long syllabusId, boolean isAlert, Long cardId, int status) {
        CourseExercisesProcessLog newLog = newLog(userId, isAlert);
        newLog.setCourseType(courseType);
        newLog.setSyllabusId(syllabusId);
        newLog.setUserId(Long.valueOf(userId));
        newLog.setCourseId(courseId);
        newLog.setLessonId(coursewareId);
        newLog.setCardId(cardId);
        newLog.setBizStatus(status);
        courseExercisesProcessLogMapper.insertSelective(newLog);
    }

    /**
     * 保存学员课后作业答题信息
     * @param userId
     * @param courseType
     * @param courseWareId
     * @param courseId
     * @param syllabusId
     * @param isAlert
     * @param cardId
     * @param status
     */
    private void buildCourseWorkCardInfo4insert(int userId, Integer courseType, Long courseWareId, Long courseId, Long syllabusId, boolean isAlert, Long cardId, int status) {
        CourseExercisesCardInfo cardInfo = newCardInfo(userId, isAlert);
        cardInfo.setCourseType(courseType);
        cardInfo.setSyllabusId(syllabusId);
        cardInfo.setUserId(Long.valueOf(userId));
        cardInfo.setCourseId(courseId);
        cardInfo.setLessonId(courseWareId);
        cardInfo.setCardId(cardId);
        cardInfo.setBizStatus(status);
        log.info("课后作业数据修正--- >>>>>>>>>>>>>>>>>>>>>>>>>>:{}", JSONObject.toJSONString(cardInfo));
        courseExercisesCardInfoMapper.insertSelective(cardInfo);
    }


    /**
     * 待处理的大纲id
     * @param syllabusId
     */
    public void putIntoDealList(Long syllabusId){
        if(syllabusId.longValue() == 0){
            return;
        }
        String key = CourseCacheKey.getProcessLogSyllabusDealList();
        ZSetOperations<String, String> dealList = redisTemplate.opsForZSet();
        dealList.add(key, String.valueOf(syllabusId), System.currentTimeMillis());
    }

    /**
     * 每20秒处理一次
     * @throws BizException
     */
    @Scheduled(fixedRate = PERIOD_TIME)
    public synchronized void dealList() throws BizException{
        String key = CourseCacheKey.getProcessLogSyllabusDealList();
        ZSetOperations<String, String> dealList = redisTemplate.opsForZSet();
        long max = System.currentTimeMillis();
        Set<Long> syllabusIds = dealList.rangeByScore(key, 0, max).stream().map(Long::valueOf).collect(Collectors.toSet());
        if(CollectionUtils.isEmpty(syllabusIds)){
            return;
        }
        log.debug("deal syllabusInfo list:{}", syllabusIds);
        Set<Long> filter = Sets.newHashSet();
        for (Long syllabusId : syllabusIds) {
            if(syllabusId == 0){
                log.info("filter syllabus id is zero");
                filter.add(syllabusId);
            }
        }
        syllabusIds.removeAll(filter);
        if(CollectionUtils.isEmpty(syllabusIds)){
            return;
        }
        Table<String, Long, SyllabusWareInfo> table = dealSyllabusInfo(syllabusIds);
        syllabusIds.forEach(item -> {
            Map<Long, SyllabusWareInfo> maps = table.row(LESSON_LABEL);
            if(maps.containsKey(item)){
                dealList.remove(key, String.valueOf(item));
            }else{
                putIntoDealList(item);
            }
        });
    }

    /**
     * 保存课后练习答题卡，更新状态
     * @param answerCard
     * @throws BizException
     */
    public void submitCourseWorkAnswerCard(final PracticeCard answerCard)throws BizException{
        ExecutorService executorService = Executors.newSingleThreadExecutor();
        //异步处理统计信息
        executorService.execute(() -> {
            courseExercisesStatisticsManager.dealCourseExercisesStatistics(answerCard);
        });
        executorService.shutdown();
        //更新 mysql 答题卡数据状态
        Example example = new Example(CourseExercisesProcessLog.class);
        example.and()
                .andEqualTo("status", YesOrNoStatus.YES.getCode())
                .andEqualTo("userId", answerCard.getUserId())
                .andEqualTo("cardId", answerCard.getId());

        CourseExercisesProcessLog updateLog = new CourseExercisesProcessLog();
        updateLog.setGmtModify(new Timestamp(System.currentTimeMillis()));
        updateLog.setBizStatus(answerCard.getStatus());
        courseExercisesProcessLogMapper.updateByExampleSelective(updateLog, example);
    }
    /**
     * new courseExercisesProcessLog
     * @return
     */
    private static CourseExercisesProcessLog newLog(int userId, boolean isAlert){
        CourseExercisesProcessLog courseExercisesProcessLog = new CourseExercisesProcessLog();
        courseExercisesProcessLog.setIsAlert(isAlert ? YesOrNoStatus.YES.getCode() : YesOrNoStatus.NO.getCode());
        courseExercisesProcessLog.setGmtModify(new Timestamp(System.currentTimeMillis()));
        courseExercisesProcessLog.setGmtCreate(new Timestamp(System.currentTimeMillis()));
        courseExercisesProcessLog.setDataType(StudyTypeEnum.COURSE_WORK.getOrder());
        courseExercisesProcessLog.setStatus(YesOrNoStatus.YES.getCode());
        courseExercisesProcessLog.setCreatorId(isAlert ? 0L : userId);
        courseExercisesProcessLog.setModifierId(isAlert ? 0L : userId);
        return courseExercisesProcessLog;
    }

    /**
     * new courseExercisesCardInfo
     * @return
     */
    private static CourseExercisesCardInfo newCardInfo(int userId, boolean isAlert){
        CourseExercisesCardInfo courseExercisesCardInfo = new CourseExercisesCardInfo();
        courseExercisesCardInfo.setIsAlert(isAlert ? YesOrNoStatus.YES.getCode() : YesOrNoStatus.NO.getCode());
        courseExercisesCardInfo.setGmtModify(new Timestamp(System.currentTimeMillis()));
        courseExercisesCardInfo.setGmtCreate(new Timestamp(System.currentTimeMillis()));
        courseExercisesCardInfo.setDataType(StudyTypeEnum.COURSE_WORK.getOrder());
        courseExercisesCardInfo.setStatus(YesOrNoStatus.YES.getCode());
        courseExercisesCardInfo.setCreatorId(isAlert ? 0L : userId);
        courseExercisesCardInfo.setModifierId(isAlert ? 0L : userId);
        return courseExercisesCardInfo;
    }

    /**
     * 获取我的课后作业列表
     * @param userId
     * @param page
     * @param size
     * @return
     * @throws BizException
     */
    public Object courseWorkList(long userId, int page, int size) throws BizException{

        List<CourseWorkCourseVo> courseWorkCourseVos = Lists.newArrayList();
        Map<Long, DataInfo> answerCardMaps = Maps.newHashMap();
        List<HashMap<String, Object>> dataList = courseExercisesProcessLogMapper.getCoursePageInfo(userId, page, size);
        log.info("查询数据库获取用户未完成课后练习数据列表: keySet:{}", dataList);
        Set<Long> allSyllabusIds = Sets.newHashSet();
        dataList.forEach(item -> {
            String ids = String.valueOf(item.get("syllabusIds"));
            Set<Long> temp = Arrays.stream(ids.split(",")).map(Long::valueOf).collect(Collectors.toSet());
            allSyllabusIds.addAll(temp);
        });
        Table<String, Long, SyllabusWareInfo> syllabusWareInfoTable = this.dealSyllabusInfo(allSyllabusIds);
        if(syllabusWareInfoTable.isEmpty()){
            return courseWorkCourseVos;
        }

        Example example = new Example(CourseExercisesProcessLog.class);
        example.and()
                .andEqualTo("userId", userId)
                .andEqualTo("dataType", StudyTypeEnum.COURSE_WORK.getOrder())
                .andEqualTo("status", YesOrNoStatus.YES.getCode())
                .andIn("syllabusId", allSyllabusIds);
        try{
            List<CourseExercisesProcessLog> logList = courseExercisesProcessLogMapper.selectByExample(example);
            String cardIds = logList.stream().map(CourseExercisesProcessLog::getCardId).distinct().map(String::valueOf).collect(Collectors.joining(","));
            if(StringUtils.isNotBlank(cardIds)){
                Object practiceCardInfos = practiceCardService.getCourseExercisesCardInfoBatch(cardIds);
                List<HashMap<String, Object>> answerCardInfo = (List<HashMap<String, Object>>) ZTKResponseUtil.build(practiceCardInfos);
                answerCardMaps.putAll(answerCardInfo.stream().collect(Collectors.toMap(item -> MapUtils.getLong(item,"id"), item -> {
                    DataInfo dataInfo = new DataInfo();
                    try{
                        org.apache.commons.beanutils.BeanUtils.populate(dataInfo, item);
                        return dataInfo;
                    }catch (Exception e) {
                        log.error("答题卡信息转换异常:{}", e);
                        return dataInfo;
                    }
                })));
            }

            Map<Long, CourseExercisesProcessLog> courseExercisesProcessLogMap = Maps.newHashMap();
            for (CourseExercisesProcessLog courseExercisesProcessLog : logList) {
                courseExercisesProcessLogMap.put(courseExercisesProcessLog.getSyllabusId(), courseExercisesProcessLog);
            }

            dataList.forEach(item->{
                CourseWorkCourseVo courseWorkCourseVo = new CourseWorkCourseVo();
                long courseId = MapUtils.getLong(item, "courseId");
                courseWorkCourseVo.setCourseId(courseId);
                SyllabusWareInfo courseInfo = syllabusWareInfoTable.get(COURSE_LABEL, courseWorkCourseVo.getCourseId());
                if(null == courseInfo){
                    courseWorkCourseVo.setCourseTitle(StringUtils.EMPTY);
                    log.error("根据大纲id获取大纲信息异常:课程id & 大纲 ids: {}", item);
                }else{
                    courseWorkCourseVo.setCourseTitle(courseInfo.getClassName());
                }
                String ids = String.valueOf(item.get("syllabusIds"));
                Set<Long> temp = Arrays.stream(ids.split(",")).map(Long::valueOf).collect(Collectors.toSet());
                if(CollectionUtils.isEmpty(temp)){
                    courseWorkCourseVo.setUndoCount(0);
                    courseWorkCourseVo.setWareInfoList(Lists.newArrayList());
                    courseWorkCourseVo.setCourseTitle(StringUtils.EMPTY);
                }

                List<CourseWorkWareVo> wareVos = Arrays.stream(ids.split(","))
                        .filter(ware -> {
                            boolean result = true;
                            result = result && (null != syllabusWareInfoTable.get(LESSON_LABEL, Long.valueOf(ware)));
                            result = result && (null !=  courseExercisesProcessLogMap.get(Long.valueOf(ware)));
                            return result;
                        }).map(ware -> {
                            SyllabusWareInfo syllabusWareInfo = syllabusWareInfoTable.get(LESSON_LABEL, Long.valueOf(ware));
                            CourseExercisesProcessLog courseExercisesProcessLog = courseExercisesProcessLogMap.get(Long.valueOf(ware));
                            CourseWorkWareVo courseWorkWareVo = new CourseWorkWareVo();

                            courseWorkWareVo.setCourseWareTitle(syllabusWareInfo.getCoursewareName());
                            courseWorkWareVo.setVideoLength(syllabusWareInfo.getLength());
                            courseWorkWareVo.setSerialNumber(syllabusWareInfo.getSerialNumber());
                            courseWorkWareVo.setAnswerCardId(courseExercisesProcessLog.getCardId());
                            if(syllabusWareInfo.getVideoType() == VideoTypeEnum.LIVE_PLAY_BACK.getVideoType()){
                                courseWorkWareVo.setCourseWareId(courseExercisesProcessLog.getLessonId());
                                courseWorkWareVo.setVideoType(courseExercisesProcessLog.getCourseType());
                            }else{
                                courseWorkWareVo.setCourseWareId(syllabusWareInfo.getCoursewareId());
                                courseWorkWareVo.setVideoType(syllabusWareInfo.getVideoType());
                            }
                            courseWorkWareVo.setQuestionIds("");
                            courseWorkWareVo.setIsAlert(courseExercisesProcessLog.getIsAlert());
                            if(answerCardMaps.containsKey(courseExercisesProcessLog.getCardId())){
                                courseWorkWareVo.setAnswerCardInfo(answerCardMaps.get(courseExercisesProcessLog.getCardId()));
                            }else{
                                courseWorkWareVo.setAnswerCardInfo(new DataInfo());
                            }
                            return courseWorkWareVo;
                        }).collect(Collectors.toList());


                courseWorkCourseVo.setUndoCount(temp.size());
                courseWorkCourseVo.setWareInfoList(wareVos);
                courseWorkCourseVos.add(courseWorkCourseVo);
            });
        }catch (Exception e){
            log.error("获取课后练习列表异常:{},{}",userId, e);
            return courseWorkCourseVos;
        }
        return courseWorkCourseVos;
    }

    /**
     * 根据大纲id获取课件信息，
     * @param syllabusIds
     * @return
     * @throws BizException
     */
    public Table<String, Long, SyllabusWareInfo> dealSyllabusInfo(Set<Long> syllabusIds) throws BizException{
        log.debug("deal syllabusId info:{}", syllabusIds);
        Table<String, Long, SyllabusWareInfo> table = TreeBasedTable.create();
        Set<Long> copy = Sets.newHashSet();
        copy.addAll(syllabusIds);
        if(CollectionUtils.isEmpty(syllabusIds)){
            return table;
        }
        ValueOperations<String,String> valueOperations = redisTemplate.opsForValue();
        syllabusIds.forEach(item -> {
            if(item.longValue() == 0){
                return;
            }
            String key = CourseCacheKey.getProcessLogSyllabusInfo(item);
            if(redisTemplate.hasKey(key)){
                String value = valueOperations.get(key);
                SyllabusWareInfo syllabusWareInfo = JSONObject.parseObject(value, SyllabusWareInfo.class);
                table.put(LESSON_LABEL, item, syllabusWareInfo);
                table.put(COURSE_LABEL, syllabusWareInfo.getClassId(), syllabusWareInfo);
                copy.remove(item);
                if((syllabusWareInfo.getVideoType() == VideoTypeEnum.LIVE.getVideoType() || syllabusWareInfo.getVideoType() == VideoTypeEnum.LIVE_PLAY_BACK.getVideoType()) && StringUtils.isEmpty(syllabusWareInfo.getRoomId())){
                    redisTemplate.delete(key);
                }
            }
        });
        if(CollectionUtils.isNotEmpty(copy)){
            table.putAll(requestSyllabusWareInfoPut2Cache(copy));
        }
        return table;
    }

    /**
     * 使用单个大纲id获取大纲详细信息
     * @param syllabusId
     * @return
     * @throws BizException
     */
     public  SyllabusWareInfo requestSingleSyllabusInfoWithCache(long syllabusId) throws BizException{
        ValueOperations<String,String> valueOperations = redisTemplate.opsForValue();
        String key = CourseCacheKey.getProcessLogSyllabusInfo(syllabusId);
        if(redisTemplate.hasKey(key)){
            String value = valueOperations.get(key);
            try{
                SyllabusWareInfo syllabusWareInfo = JSONObject.parseObject(value, SyllabusWareInfo.class);
                return syllabusWareInfo;
            }catch (Exception e){
                redisTemplate.delete(key);
            }
        }
        HashMap<String, Object> params = HashMapBuilder.<String,Object>newBuilder().put("syllabusIds", syllabusId).build();
        NetSchoolResponse<LinkedHashMap<String, Object>> netSchoolResponse = syllabusService.courseWareInfo(params);
        if(ResponseUtil.isFailure(netSchoolResponse) || netSchoolResponse == NetSchoolResponse.DEFAULT){
            return null;
        }
        ObjectMapper objectMapper = new ObjectMapper();
        List<LinkedHashMap<String, Object>> data = (List<LinkedHashMap<String, Object>>)netSchoolResponse.getData();
        if(CollectionUtils.isEmpty(data)){
            return null;
        }
        SyllabusWareInfo syllabusWareInfo = objectMapper.convertValue(data.get(0), SyllabusWareInfo.class);
        valueOperations.set(key, JSONObject.toJSONString(syllabusWareInfo));
        redisTemplate.expire(key, 20, TimeUnit.MINUTES);
        return syllabusWareInfo;
    }
    /**
     * 请求大纲信息并缓存到 redis
     * @param syllabusIds
     * @return
     * @throws BizException
     */
    private Table<String, Long, SyllabusWareInfo> requestSyllabusWareInfoPut2Cache(Set<Long> syllabusIds)throws BizException{
        StopWatch stopwatch = new StopWatch("requestSyllabusWareInfoPut2Cache");
        stopwatch.start();
        Table<String, Long, SyllabusWareInfo> table = TreeBasedTable.create();
        try{
            String ids = Joiner.on(",").join(syllabusIds);
            HashMap<String, Object> params = HashMapBuilder.<String,Object>newBuilder().put("syllabusIds", ids).build();
            NetSchoolResponse<LinkedHashMap<String, Object>> netSchoolResponse = syllabusService.courseWareInfo(params);
            if(ResponseUtil.isFailure(netSchoolResponse) || netSchoolResponse == NetSchoolResponse.DEFAULT){
                return table;
            }
            ObjectMapper objectMapper = new ObjectMapper();
            List<LinkedHashMap<String, Object>> data = (List<LinkedHashMap<String, Object>>)netSchoolResponse.getData();

            List<SyllabusWareInfo> list = data.stream().map(item -> {
                LinkedHashMap<String, Object> map = item;
                try{
                    SyllabusWareInfo syllabusWareInfo = objectMapper.convertValue(map, SyllabusWareInfo.class);
                    return syllabusWareInfo;
                }catch (Exception e){
                    log.error("convert map 2 SyllabusWareInfo error! {}", e);
                    return null;
                }
            }).collect(Collectors.toList());

            if(CollectionUtils.isNotEmpty(list)){
                ValueOperations<String,String> valueOperations = redisTemplate.opsForValue();
                list.forEach(item -> {
                    table.put(LESSON_LABEL, item.getSyllabusId(), item);
                    table.put(COURSE_LABEL, item.getClassId(), item);
                    String key = CourseCacheKey.getProcessLogSyllabusInfo(item.getSyllabusId());
                    valueOperations.set(key, JSONObject.toJSONString(item));
                    redisTemplate.expire(key, 20, TimeUnit.MINUTES);
                });
            }
            stopwatch.stop();
            log.debug(">>>>>>>> 请求大纲ids耗费时间:{}", stopwatch.prettyPrint());
            return table;
        }catch (Exception e) {
            log.error("request syllabusInfo error!:{}", e);
            return table;
        }
    }


    /**
     * 直播数据上报处理，非直播不处理
     * @param subject
     * @param terminal
     * @param userId
     * @param syllabusId
     * @param cv
     */
    @Async
    public void saveLiveRecord(int userId, int subject, int terminal, long syllabusId, String cv) {
        Set<Long> syllabusIds = Sets.newHashSet();
        syllabusIds.add(syllabusId);
        Table<String, Long, SyllabusWareInfo> table = requestSyllabusWareInfoPut2Cache(syllabusIds);
        if (null == table.get(LESSON_LABEL, syllabusId)) {
            return;
        }
        /**
         * 创建答题卡
         */
        SyllabusWareInfo syllabusWareInfo = table.get(LESSON_LABEL, syllabusId);
        if(VideoTypeEnum.LIVE.getVideoType() != syllabusWareInfo.getVideoType()){
            log.error("直播上报数据与大纲数据不一致:{}", JSONObject.toJSONString(syllabusWareInfo));
            return;
        }
        log.info("直播创建或更新课后作业答题卡:大纲id{}", syllabusId);
        createCourseWorkAnswerCardEntrance(syllabusWareInfo.getClassId(), syllabusWareInfo.getSyllabusId(), syllabusWareInfo.getVideoType(), syllabusWareInfo.getCoursewareId(), subject, terminal, cv, userId);
    }

    /**
     *
     * @param userId
     * @param secret
     * @return
     */
    public void dataCorrect(int userId, String secret){
        List<Integer> list = Lists.newArrayList(AnswerCardStatus.CREATE, AnswerCardStatus.UNDONE);
        Example example = new Example(CourseExercisesProcessLog.class);
        example.and().andEqualTo("status", YesOrNoStatus.YES.getCode())
                .andNotEqualTo("modifierId", userId)
                .andIn("bizStatus", list);
        List<CourseExercisesProcessLog> courseExercisesProcessLogs = courseExercisesProcessLogMapper.selectByExample(example);
        log.info("dataCorrect:{}", courseExercisesProcessLogs.size());
        for (CourseExercisesProcessLog courseExercisesProcessLog : courseExercisesProcessLogs) {
            String message = userId + "_" + courseExercisesProcessLog.getId();
            rabbitTemplate.convertAndSend("", RabbitMqConstants.COURSE_EXERCISES_PROCESS_LOG_CORRECT_QUEUE, message);
        }
    }

    /**
     * 数据纠正开关
     * @param userId
     * @param str
     */
    public void dataCorrectSwitch(int userId, String str){
        ValueOperations<String, String> valueOperations = redisTemplate.opsForValue();
        if(str.equals(CORRECT_DATA_SWITCH_ON)){
            valueOperations.set(CORRECT_DATA_SWITCH, CORRECT_DATA_SWITCH_ON);
        }else{
            valueOperations.set(CORRECT_DATA_SWITCH, String.valueOf(userId).concat(CORRECT_DATA_SWITCH_ON));
        }

    }

    /**
     * 数据处理
     * @param message
     */
    public void correct(String message){
        log.info(">>>>>>>>> current deal message:{}", message);
        ValueOperations<String, String> keySwitch = redisTemplate.opsForValue();
        SetOperations<String, String> keyExist = redisTemplate.opsForSet();
        String [] data = message.split("_");
        long id = Long.valueOf(data[1]);
        long userId = Long.valueOf(data[0]);
        try{
            int courseType;
            long lessonId;
            CourseExercisesProcessLog courseExercisesProcessLog = courseExercisesProcessLogMapper.selectByPrimaryKey(id);
            SyllabusWareInfo syllabusWareInfo = requestSingleSyllabusInfoWithCache(courseExercisesProcessLog.getSyllabusId());
            if(null == syllabusWareInfo){
                return;
            }

            /**
             * 如果为直播回放，获取直播信息，处理
             */
            if(syllabusWareInfo.getVideoType() == VideoTypeEnum.LIVE_PLAY_BACK.getVideoType()){
                String roomId = syllabusWareInfo.getRoomId();
                CourseLiveBackLog courseLiveBackLog = courseLiveBackLogService.findByRoomIdAndLiveCoursewareId(Long.valueOf(roomId), syllabusWareInfo.getCoursewareId());
                if(null == courseLiveBackLog){
                    return;
                }
                courseType = VideoTypeEnum.LIVE.getVideoType();
                lessonId = courseLiveBackLog.getLiveCoursewareId();
            }else{
                courseType = syllabusWareInfo.getVideoType();
                lessonId = syllabusWareInfo.getCoursewareId();
            }
            StringBuffer stringBuffer = new StringBuffer(String.valueOf(userId)).append(String.valueOf(courseExercisesProcessLog.getSyllabusId()));
            if(lessonId != courseExercisesProcessLog.getLessonId() || courseType != courseExercisesProcessLog.getCourseType()){
                if(redisTemplate.hasKey(CORRECT_DATA_SWITCH) && keySwitch.get(CORRECT_DATA_SWITCH).equals(CORRECT_DATA_SWITCH_ON)){
                    keyExist.remove(CORRECT_DATA_KEY, stringBuffer.toString());
                    courseExercisesProcessLog.setCourseType(courseType);
                    courseExercisesProcessLog.setLessonId(lessonId);
                    courseExercisesProcessLog.setModifierId(userId);
                    int execute = courseExercisesProcessLogMapper.updateByPrimaryKeySelective(courseExercisesProcessLog);
                    if(execute > 0){
                        log.info("成功更新数据:,课件:{},类型:{}, 大纲数据:课件:{}, 类型:{}", courseExercisesProcessLog.getLessonId(), courseExercisesProcessLog.getCourseType(), lessonId, courseType);
                    }
                }else{
                    keyExist.add(CORRECT_DATA_KEY, stringBuffer.toString());
                    log.info("暂存缓存数据:,课件:{},类型:{}, 大纲数据:课件:{}, 类型:{}", courseExercisesProcessLog.getLessonId(), courseExercisesProcessLog.getCourseType(),lessonId,courseType);
                }
            }
        }catch (Exception e){
            e.printStackTrace();
            log.error("修正课后作业数据失败:数据id:{},{}",id, e);
        }
    }


    /**
     * 阶段测试单条已读
     * @param syllabusId
     * @param courseId
     * @param uname
     * @return
     */
	public void readyOnePeriod(Long syllabusId, Long courseId, String uname) {
		Map<String, Object> params = Maps.newHashMap();
		params.put("syllabusId", syllabusId);
		params.put("netClassId", courseId);
		params.put("userName", uname);
		params.put("type", 0);
		NetSchoolResponse response = userCourseServiceV6.readPeriod(params);
		log.info("用户{}已读阶段测试大纲id为{} 返回结果:{}", uname, syllabusId, response.getData());
	}

    /**
     * 数据迁移处理逻辑
     * @param message
     */
	public void dealCourseWorkReportUsers(String message){
	    int userId = Integer.parseInt(message);
	    String userIdStr = String.valueOf(userId);
	    String alreadyProcessed = CourseCacheKey.COURSE_WORK_REPORT_USERS_ALREADY_PROCESSED;
	    SetOperations<String, String> alreadyProcessedOperations = redisTemplate.opsForSet();
        //如果处理完队列中存在 userId -> 不做处理返回
        Object courseExercisesCardInfo = practiceCardService.getCourseExercisesAllCardInfo(userId);
        if(courseExercisesCardInfo == ResponseUtil.DEFAULT_PAGE_EMPTY){
            log.error("obtain current user's course works failed! userId:{}", userId);
        }else{
            try{
                Object build = ZTKResponseUtil.build(courseExercisesCardInfo);
                List<Map> courseExercisesCards = (List<Map>) build;
                if(CollectionUtils.isEmpty(courseExercisesCards)){
                    log.info("current user's course work size is 0 :userId:{}", userIdStr);
                }else{
                    synchronized (this){
                        dealCourseExercisesCards(userId, courseExercisesCards);
                        //处理完毕加入已经处理 set 中
                        alreadyProcessedOperations.add(alreadyProcessed, userIdStr);
                    }
                }
            }catch (Exception e){
                alreadyProcessedOperations.remove(alreadyProcessed, userIdStr);
                log.error("deal course exercises caught an exception:{}", e);
            }
        }
    }



    /**
     * 数据迁移处理逻辑
     * @param userInfo
     */
	public void dealCourseWorkUsersDataFix(String userInfo){
	    log.debug("课后作业数据修正--- dealCourseWorkUsersDataFix.start:{}", userInfo);
        SimpleUserInfo simpleUserInfo = JSONObject.parseObject(userInfo, SimpleUserInfo.class);
        NetSchoolResponse myCourseIdList = userCourseServiceV6.obtainMyCourseIdList(simpleUserInfo.getUserName());
        if(myCourseIdList == NetSchoolResponse.DEFAULT){
            log.error("课后作业数据修正--- obtain current user's course id list failed! user info :{}", userInfo);
        }else{
            try{
                List<String> courseList = (List<String>)(myCourseIdList.getData());
                if(CollectionUtils.isEmpty(courseList)){
                    log.debug("课后作业数据修正--- current user's course id list is 0 : user info :{}", userInfo);
                }else{
                    dealCourseWorkUsersDataFixStep2(simpleUserInfo, courseList);
                }
            }catch (Exception e){
                log.error("课后作业数据修正--- deal course user id list caught an exception, user info :{}, error: {}", userInfo, e);
            }
        }
    }


    /**
     * 数据迁移处理逻辑 - step 2
     * @param courseList
     */
    private void dealCourseWorkUsersDataFixStep2(SimpleUserInfo simpleUserInfo, List<String> courseList) throws Exception{
        log.debug("课后作业数据修正---  dealCourseWorkUsersDataFixStep2, userId:{}, courseList:{}", simpleUserInfo.getUserId(), courseList);
        final CopyOnWriteArrayList<ClassInfo> classInfoList_ = new CopyOnWriteArrayList<>();
        for (String classId : courseList) {
            Future<List<ClassInfo>> future = executorService.submit(new Callable<List<ClassInfo>>() {
                @Override
                public List<ClassInfo> call() throws Exception {
                    final Integer classId_ = Integer.valueOf(classId);
                    List<ClassInfo> tmp = getClassInfoWithGuava(classId_ );
                    return tmp;
                }

                /**
                 * 通过缓存获取数据
                 * @return
                 */
                private List<ClassInfo> getClassInfoWithGuava(final Integer classId_ ) {
                    try{
                         return classInfo.get(classId_, new Callable<List<ClassInfo>>() {
                            @Override
                            public List<ClassInfo> call() throws Exception {
                                return getCourseInfoClient(classId_);
                            }
                        });
                    }catch (Exception e){
                        log.error("课后作业数据修正--- 通过 guava 获取课程信息异常:{}", e.getMessage());
                        return Lists.newArrayList();
                    }
                }

                /**
                 * php 接口获取课程信息
                 * @param classId_
                 * @return
                 */
                private List<ClassInfo> getCourseInfoClient(Integer classId_) {
                    List<ClassInfo> tmp = Lists.newArrayList();
                    NetSchoolResponse nodeInfo = courseServiceV6.nodeIdByClassId(classId_);
                    if(nodeInfo == NetSchoolResponse.DEFAULT){
                        log.error("课后作业数据修正--- obtain current course info failed! class id :{}", classId);
                        return tmp;
                    }
                    List<Map> nodeInfo_ = (List<Map>) nodeInfo.getData();
                    if(CollectionUtils.isEmpty(nodeInfo_)){
                        log.debug("课后作业数据修正--- empty result:{}", nodeInfo_.size());
                        return tmp;
                    }
                    for (Map map : nodeInfo_){
                        Integer courseWareId = MapUtils.getInteger(map, "coursewareId", 0);
                        Integer courseWareType = MapUtils.getInteger(map, "coursewareType", 0);
                        Integer syllabusId = MapUtils.getInteger(map, "syllabusId", 0);
                        String bjyRoomId = MapUtils.getString(map, "bjyRoomId", "");
                        Integer hasAfterExercises = MapUtils.getInteger(map, "hasAfterExercises", 0);
                        if(courseWareId.intValue() <= 0 || courseWareType.intValue() <= 0 || syllabusId.intValue() <= 0 || hasAfterExercises.intValue() <= 0){
                            log.error("课后作业数据修正--- 课后作业数据 fix, 非法的数据:courseWareId {}, courseWareType:{}, syllabusId:{}, bjyRoomId:{}, hasAfterExercises:{}", courseWareId, courseWareType, syllabusId, bjyRoomId, hasAfterExercises);
                        }else{
                            ClassInfo classInfo = ClassInfo.builder().classId(classId_).hasAfterExercises(hasAfterExercises).courseWareId(courseWareId).coursewareType(courseWareType).syllabusId(syllabusId).bjyRoomId(bjyRoomId).build();
                            tmp.add(classInfo);
                        }
                    }
                    return tmp;
                }
            });

            List<ClassInfo> classInfoList = future.get();
            if(CollectionUtils.isNotEmpty(classInfoList)){
                classInfoList_.addAll(classInfoList);
            }
        }
        log.debug("课后作业数据修正--- guava 缓存命中率:, userId:{}, status:{}", simpleUserInfo.getUserId(), classInfo.stats());
        if(CollectionUtils.isNotEmpty(classInfoList_)){
            dealCourseWorkUsersDataFixStep3(simpleUserInfo, classInfoList_);
        }
    }

    /**
     * 处理直播或录播的课后作业
     * @param classInfoList
     */
    private void dealCourseWorkUsersDataFixStep3(final SimpleUserInfo simpleUserInfo, CopyOnWriteArrayList<ClassInfo> classInfoList){
        if(CollectionUtils.isEmpty(classInfoList)){
            return;
        }
        log.debug("课后作业数据修正--- dealCourseWorkUsersDataFixStep3 start. userId:{}, classInfo.size:{}", simpleUserInfo.getUserId(), classInfoList.size());
        Set<ClassInfo> classInfoSet = classInfoList.stream().collect(Collectors.toSet());
        for (ClassInfo classInfo : classInfoSet) {
            try{
                log.debug("课后作业数据修正---  新的任务执行了 -----> :{}", JSONObject.toJSONString(classInfo));
                HashMap<String, Object> answerCardInfo = obtainOrCreateAnswerCardThroughPaperService(classInfo, simpleUserInfo.getSubject(), simpleUserInfo.getTerminal(), simpleUserInfo.getCv(), simpleUserInfo.getUserId(), false);
                log.debug("课后作业数据修正--- obtainOrCreateAnswerCardThroughPaperService <=> insertCardInfo");
                if(CollectionUtils.isNotEmpty(answerCardInfo.keySet())){
                    insertCardInfo(simpleUserInfo.getUserId(), classInfo, answerCardInfo, true);
                }
            }catch (Exception e){
                log.error("课后作业数据修正--- dealCourseWorkUsersDataFixStep3 exception, error:{}", e.getMessage());
            }
        }
    }



    /**
     * 查询数据库校验答题卡是否存在
     * @param userId
     * @param hashMapImmutableList
     * @param longImmutableList
     */
    public void analyzeCardIdExist(int userId, ImmutableList<HashMap<String, Object>> hashMapImmutableList, ImmutableList<Long> longImmutableList){
        List<HashMap<String,Object>> fixList = Lists.newArrayList();
        for(HashMap<String, Object> map : hashMapImmutableList){

            int courseType = MapUtils.getInteger(map, SyllabusInfo.VideoType, 0);
            long lessonId = MapUtils.getInteger(map, SyllabusInfo.CourseId, 0);

            try{
                Example example = new Example(CourseExercisesProcessLog.class);
                example.and()
                        .andEqualTo("userId", userId)
                        .andEqualTo("lessonId", lessonId)
                        .andEqualTo("courseType", courseType)
                        .andEqualTo("status", YesOrNoStatus.YES.getCode());
                CourseExercisesProcessLog log = courseExercisesProcessLogMapper.selectOneByExample(example);
                if(null == log || !(longImmutableList.contains(log.getCardId()))){
                    fixList.add(map);
                }
            }catch (Exception e){
                log.error("courseExercisesProcessLogMapper selectOneByExample error,userId = {}, courseType = {}, lessonId = {}, error = {}", userId, courseType, lessonId, e.getMessage());
            }
        }
        if(CollectionUtils.isNotEmpty(fixList)){
            requestCourseWorkInfo4Mysql(userId, fixList);
        }
    }

    /**
     * 请求 paper 服务刷新数据进入 mysql
     * @param userId
     * @param fixList
     */
    private void requestCourseWorkInfo4Mysql(int userId, List<HashMap<String,Object>> fixList){
        log.error("课后作业数据修正 - 需要请求paper并放入mysql的数据  userId = {}, paramList = {}", userId, fixList);
        Object practiceCardInfos = practiceCardService.getCourseExercisesCardInfo(userId, fixList);
        List<Map> answerCardInfo = (List<Map>) ZTKResponseUtil.build(practiceCardInfos);
        if(CollectionUtils.isNotEmpty(answerCardInfo)){
            log.info("requestCourseWorkInfo4Mysql ===》 mysql, userId = {}, answerCardInfos = {}", userId, JSONObject.toJSONString(answerCardInfo));
            dealCourseExercisesCards(userId, answerCardInfo);
        }
    }


    /**
     * 遍历获取到的数据
     * @param courseExercisesCards
     */
    private void dealCourseExercisesCards(int userId, List<Map> courseExercisesCards){
	    for(Map<String,Object> current : courseExercisesCards){
	        try{
                int courseType = MapUtils.getInteger(current, "courseType");
                Long courseWareId = MapUtils.getLong(current, "courseId");
                Long cardId = MapUtils.getLong(current, "id");
                int status = MapUtils.getInteger(current, "status");
                Map<String,Object> params = Maps.newHashMap();
                params.put("coursewareId", courseWareId);
                params.put("coursewareType", courseType);
                NetSchoolResponse netSchoolResponse = syllabusService.obtainSyllabusIdByCourseWareId(params);
                if(netSchoolResponse != NetSchoolResponse.DEFAULT){
                    List<Map<String,Object>> syllabusDataInfo = (List<Map<String,Object>>)netSchoolResponse.getData();
                    if(CollectionUtils.isNotEmpty(syllabusDataInfo)){
                        //处理入库 mysql
                        dealCourseWorkDataIntoMySQL(userId, courseType, cardId, status, courseWareId, syllabusDataInfo);
                    }
                }
            }catch (Exception e){
	            log.error("dealCourseExercisesCards caught an error:{}", e.getMessage());
	            throw new IllegalArgumentException("遍历数据请求 php 并入库异常");
            }
        }
    }

    /**
     * 根据 courseType && courseId 获取课后作业信息
     * @param userId
     * @param courseType
     * @param courseWareId
     * @return
     */
    public Optional<CourseExercisesProcessLog> getCourseExercisesProcessLogByTypeAndWareId(long userId, int courseType, long courseWareId) {
        Example example = new Example(CourseExercisesProcessLog.class);
        example.and()
                .andEqualTo("userId", userId)
                .andEqualTo("lessonId", courseWareId)
                .andEqualTo("courseType", courseType)
                .andEqualTo("status", YesOrNoStatus.YES.getCode());

        example.orderBy("gmtCreate").desc();
        List<CourseExercisesProcessLog> list = courseExercisesProcessLogMapper.selectByExample(example);
        if(CollectionUtils.isNotEmpty(list)){
            return Optional.of(list.get(0));
        }else{
            return Optional.ofNullable(null);
        }
    }

    /**
     * 处理数据入库 mySql
     * @param courseType
     * @param courseWareId
     * @param syllabusDataInfo
     */
    private void dealCourseWorkDataIntoMySQL(int userId, int courseType, Long cardId, int status, Long courseWareId, List<Map<String,Object>> syllabusDataInfo){
        HashMap<String, Object> params = Maps.newHashMap();
        params.put("id", cardId);
        params.put("status", status);
        for(Map<String, Object> current : syllabusDataInfo){
            try{
                Long courseId = MapUtils.getLong(current, "classId");
                Long syllabusId = MapUtils.getLong(current, "syllabusId");
                ClassInfo classInfo = ClassInfo.builder().coursewareType(courseType)
                        .courseWareId(courseWareId.intValue())
                        .classId(courseId.intValue())
                        .syllabusId(syllabusId.intValue())
                        .build();

                insertOrUpdateLogInfo(userId, classInfo, params,false);
                log.debug("处理课后作业入库----> userId:{},courseType:{},courseWareId:{},courseId:{},syllabusId:{},params:{}", userId, courseType, courseWareId, courseId, syllabusId, params);
            }catch (Exception e){
                log.error("处理课后作业入库 mysql 异常 userId:{}, courseType:{}, cardId:{}, status:{}, courseWareId:{},syllabusDataInfo:{}, error:{}", userId, courseType, cardId, status, courseWareId, syllabusDataInfo,e.getMessage());
                throw new IllegalArgumentException("处理课后作业入库 mysql 异常" + userId);
            }
        }
    }

    /**
     * 通过courseType或者lessonId获取答题卡id
     * @param userId
     * @param paramsList
     * @return
     */
    public List<Long> obtainCardIdsByCourseTypeAndLessonId(long userId, List<HashMap<String, Object>> paramsList){

        List<Long> cardIds = Lists.newArrayList();
        for(Map item : paramsList){
            int courseType = MapUtils.getIntValue(item, SyllabusInfo.VideoType);
            int lessonId = MapUtils.getIntValue(item, SyllabusInfo.CourseId);
            try{
                Example example = new Example(CourseExercisesProcessLog.class);
                example.and()
                        .andEqualTo("userId", userId)
                        .andEqualTo("status", YesOrNoStatus.YES.getCode())
                        .andEqualTo("courseType", courseType)
                        .andEqualTo("lessonId", lessonId);

                CourseExercisesProcessLog log = courseExercisesProcessLogMapper.selectOneByExample(example);
                cardIds.add(log.getCardId());
            }catch (Exception e){
                log.error("obtainCardIdsByCourseTypeAndLessonId error!: userId = {}, paramsList = {}, error = {}", userId, paramsList, e.getMessage());
            }
        }
        log.info("obtainCardIdsByCourseTypeAndLessonId: cardId = {}", cardIds);
        return cardIds;
    }

    /**
     * 课后作业数据修正接口
     * @param operator
     * @return
     */
    public Object courseWorkDataFix(int operator) throws Exception{
        StopWatch stopWatch = new StopWatch();
        stopWatch.start("课后作业数据修正--- put into mq 开始, 操作人" + operator);
        String alreadyProcessed = CourseCacheKey.COURSE_WORK_REPORT_USERS_ALREADY_PROCESSED;
        SetOperations<String, String> datOperations = redisTemplate.opsForSet();
        List<String> userIds = datOperations.members(alreadyProcessed).stream().collect(Collectors.toList());
        ExecutorService executorService = Executors.newFixedThreadPool(15);
        CountDownLatch countDownLatch = new CountDownLatch(userIds.size());
        Semaphore semaphore = new Semaphore(10);
        List<SimpleUserInfo> simpleUserInfoList = Lists.newArrayList();

        for(int i = 0; i < userIds.size(); i ++){
            final int userId = Integer.valueOf(userIds.get(i));
            log.debug("课后作业数据修正--- executorService deal userId start:{}", userId);
            Future<Optional<SimpleUserInfo>> future = executorService.submit(() -> {
                try{
                    semaphore.acquire();
                    NetSchoolResponse netSchoolResponse = userAccountService.getUserNameById(userId);
                    Object build = ResponseUtil.build(netSchoolResponse);
                    Map map = (Map)build;
                    String userName = MapUtils.getString(map, "userName");

                    semaphore.release();
                    countDownLatch.countDown();
                    SimpleUserInfo simpleUserInfo = SimpleUserInfo.builder().userName(userName).userId(userId).cv("7.1.151")
                            .terminal(1).subject(1).build();
                    return Optional.of(simpleUserInfo);
                }catch (Exception e){
                    log.error("multi thread get userName through userId error, userId:{}, error:{}", userId, e.getMessage());
                    return Optional.ofNullable(null);
                }
            });
            Optional<SimpleUserInfo> optionalSimpleUserInfo = future.get();
            log.debug("课后作业数据修正--- executorService deal userId finish:{}", userId);
            if(optionalSimpleUserInfo.isPresent()){
                simpleUserInfoList.add(optionalSimpleUserInfo.get());
            }

        }
        countDownLatch.await();
        executorService.shutdown();
        log.debug("课后作业数据修正--- convertAndSend data size:{}", simpleUserInfoList.size());
        for(SimpleUserInfo userInfo : simpleUserInfoList){
            String userInfo_ = JSONObject.toJSONString(userInfo);
            log.debug("课后作业数据修正--- deal user info :{} into rabbit mq", userInfo_);
            rabbitTemplate.convertAndSend("", RabbitMqConstants.COURSE_WORK_REPORT_USERS_DEAL_QUEUE_USER_INFO, userInfo_);
        }
        stopWatch.stop();

        log.info("课后作业数据修正--- put into mq 结束:{}", stopWatch.prettyPrint());
        return SuccessMessage.create("待处理:" + userIds.size() + "如队列:" + simpleUserInfoList.size());
    }

    @NoArgsConstructor
    @Getter
    @Setter
    public static class ClassInfo implements Serializable{
        private Integer classId;
        private Integer courseWareId;
        private Integer coursewareType;
        private Integer syllabusId;
        private String bjyRoomId;
        private Integer hasAfterExercises;

        @Builder
        public ClassInfo(Integer classId, Integer courseWareId, Integer coursewareType, Integer syllabusId, String bjyRoomId, Integer hasAfterExercises) {
            this.classId = classId;
            this.courseWareId = courseWareId;
            this.coursewareType = coursewareType;
            this.syllabusId = syllabusId;
            this.bjyRoomId = bjyRoomId;
            this.hasAfterExercises = hasAfterExercises;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (!(o instanceof ClassInfo)) return false;
            ClassInfo classInfo = (ClassInfo) o;
            return Objects.equals(getSyllabusId(), classInfo.getSyllabusId());
        }

        @Override
        public int hashCode() {

            return Objects.hash(getSyllabusId());
        }
    }

	@NoArgsConstructor
    @Getter
    @Setter
	public static class DataInfo{
	    private int status;
	    private int wcount;
	    private int ucount;
	    private int rcount;
	    private int qcount;
	    private long  id;

	    @Builder
        public DataInfo(int status, int wcount, int ucount, int rcount, int qcount, long id) {
            this.status = status;
            this.wcount = wcount;
            this.ucount = ucount;
            this.rcount = rcount;
            this.qcount = qcount;
            this.id = id;
        }
    }

    @NoArgsConstructor
    @Getter
    @Setter
    public static class UserInfo{
        private Integer userId;
        private Long lessonId;
        private Integer courseType;

        @Builder
        public UserInfo(Integer userId, Long lessonId, Integer courseType) {
            this.userId = userId;
            this.lessonId = lessonId;
            this.courseType = courseType;
        }
    }
}
