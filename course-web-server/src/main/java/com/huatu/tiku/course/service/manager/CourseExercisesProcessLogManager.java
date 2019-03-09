package com.huatu.tiku.course.service.manager;

import com.alibaba.fastjson.JSONObject;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.base.Joiner;
import com.google.common.base.Stopwatch;
import com.google.common.collect.*;
import com.huatu.common.exception.BizException;
import com.huatu.tiku.course.bean.NetSchoolResponse;
import com.huatu.tiku.course.bean.vo.CourseWorkCourseVo;
import com.huatu.tiku.course.bean.vo.CourseWorkWareVo;
import com.huatu.tiku.course.bean.vo.SyllabusWareInfo;
import com.huatu.tiku.course.common.StudyTypeEnum;
import com.huatu.tiku.course.common.YesOrNoStatus;
import com.huatu.tiku.course.dao.manual.CourseExercisesProcessLogMapper;
import com.huatu.tiku.course.netschool.api.v7.SyllabusServiceV7;
import com.huatu.tiku.course.util.CourseCacheKey;
import com.huatu.tiku.course.util.ResponseUtil;
import com.huatu.tiku.entity.CourseExercisesProcessLog;
import com.huatu.ztk.paper.bean.PracticeCard;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.*;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import tk.mybatis.mapper.entity.Example;

import java.sql.Timestamp;
import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

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
    private SyllabusServiceV7 syllabusService;

    @Autowired
    private RedisTemplate redisTemplate;

    @Autowired
    private ObjectMapper objectMapper;

    private static final String LESSON_LABEL = "lesson";

    private static final String COURSE_LABEL = "course";

    private static final long PERIOD_TIME = 30 * 1000;

    /**
     * 获取类型未读量
     * @param userId
     * @return
     */
    public Map<String, Integer> getCountByType(long userId) throws BizException{
        Map<String, Integer> result = Maps.newHashMap();
        Example example = new Example(CourseExercisesProcessLog.class);
        Example.Criteria criteria = example.and();
        criteria.andEqualTo("userId", userId);
        criteria.andEqualTo("isAlert", YesOrNoStatus.YES.getCode());
        criteria.andEqualTo("status", YesOrNoStatus.YES.getCode());
        criteria.andEqualTo("dataType", StudyTypeEnum.COURSE_WORK.getOrder());
        int countWork = courseExercisesProcessLogMapper.selectCountByExample(example);
        result.put(StudyTypeEnum.COURSE_WORK.getKey(), countWork);
        criteria.andEqualTo("dataType", StudyTypeEnum.PERIOD_TEST.getOrder());
        int countTest = courseExercisesProcessLogMapper.selectCountByExample(example);
        result.put(StudyTypeEnum.PERIOD_TEST.getKey(), countTest);
        return result;
    }



    /**
     * 单种类型全部已读
     * @param userId
     * @param type
     * @return
     */
    public int allReadByType(long userId, String type) throws BizException{
        try{
            StudyTypeEnum studyTypeEnum = StudyTypeEnum.create(type);
            CourseExercisesProcessLog courseExercisesProcessLog = new CourseExercisesProcessLog();
            courseExercisesProcessLog.setGmtModify(new Timestamp(System.currentTimeMillis()));
            courseExercisesProcessLog.setIsAlert(YesOrNoStatus.NO.getCode());

            Example example = new Example(CourseExercisesProcessLog.class);
            example.and().andEqualTo("status", YesOrNoStatus.YES.getCode())
                    .andEqualTo("dataType", studyTypeEnum.getKey())
                    .andEqualTo("userId", userId);
            int count = courseExercisesProcessLogMapper.updateByExampleSelective(courseExercisesProcessLog, example);
            return count;
        }catch (Exception e){
            log.error("all read by type error!:{}", e);
            return -1;
        }
    }

    /**
     * 单条已读
     * @param id
     * @param type
     * @return
     * @throws BizException
     */
    public int readyOne(long id, String type) throws BizException{
        StudyTypeEnum studyTypeEnum = StudyTypeEnum.create(type);
        CourseExercisesProcessLog courseExercisesProcessLog = new CourseExercisesProcessLog();
        courseExercisesProcessLog.setGmtModify(new Timestamp(System.currentTimeMillis()));
        courseExercisesProcessLog.setIsAlert(YesOrNoStatus.NO.getCode());
        courseExercisesProcessLog.setId(id);
        return courseExercisesProcessLogMapper.updateByPrimaryKeySelective(courseExercisesProcessLog);
    }


    /**
     * 课后作业创建答题卡异步处理方法
     * @param courseType
     * @param coursewareId
     * @param courseId
     * @param syllabusId
     * @param result
     */
    @Async
    public void createCourseWorkAnswerCard(int userId, Integer courseType, Long coursewareId, Long courseId, Long syllabusId, HashMap<String,Object> result){
        Long cardId = Long.valueOf(String.valueOf(result.get("id")));
        int status = Integer.valueOf(String.valueOf(result.get("status")));

        Example example = new Example(CourseExercisesProcessLog.class);
        example.and().andEqualTo("lessonId", coursewareId)
                .andEqualTo("courseType", courseType)
                .andEqualTo("userId", userId)
                .andEqualTo("dataType", StudyTypeEnum.COURSE_WORK.getOrder())
                .andEqualTo("status", YesOrNoStatus.YES.getCode());
        CourseExercisesProcessLog courseExercisesProcessLog = courseExercisesProcessLogMapper.selectOneByExample(example);
        if(null == courseExercisesProcessLog){
            /**
             * 新增数据
             */
            CourseExercisesProcessLog newLog = newLog();
            newLog.setCourseType(courseType);
            newLog.setSyllabusId(syllabusId);
            newLog.setUserId(Long.valueOf(userId));
            newLog.setCourseId(courseId);
            newLog.setLessonId(coursewareId);
            newLog.setCardId(cardId);
            newLog.setBizStatus(status);
            courseExercisesProcessLogMapper.insertSelective(newLog);
            putIntoDealList(syllabusId);
        }else{
            /**
             * 更新答题卡字段
             */
            CourseExercisesProcessLog update = new CourseExercisesProcessLog();
            BeanUtils.copyProperties(courseExercisesProcessLog, update);
            update.setGmtModify(new Timestamp(System.currentTimeMillis()));
            update.setBizStatus(status);
            courseExercisesProcessLogMapper.updateByExampleSelective(update, example);
        }
    }


    /**
     * 待处理的大纲id
     * @param syllabusId
     */
    public void putIntoDealList(Long syllabusId){
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
    public void submitCourseWorkAnswerCard(PracticeCard answerCard)throws BizException{
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
    private static CourseExercisesProcessLog newLog(){
        CourseExercisesProcessLog courseExercisesProcessLog = new CourseExercisesProcessLog();
        courseExercisesProcessLog.setIsAlert(YesOrNoStatus.YES.getCode());
        courseExercisesProcessLog.setGmtModify(new Timestamp(System.currentTimeMillis()));
        courseExercisesProcessLog.setGmtCreate(new Timestamp(System.currentTimeMillis()));
        courseExercisesProcessLog.setDataType(StudyTypeEnum.COURSE_WORK.getOrder());
        courseExercisesProcessLog.setStatus(YesOrNoStatus.YES.getCode());
        courseExercisesProcessLog.setCreatorId(0L);
        courseExercisesProcessLog.setModifierId(0L);
        return courseExercisesProcessLog;
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
        List<HashMap<String, Object>> dataList = courseExercisesProcessLogMapper.getCoursePageInfo(userId, page, size);

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

        Map<Long, CourseExercisesProcessLog> courseExercisesProcessLogMap = courseExercisesProcessLogMapper
                .selectByExample(example).stream().collect(Collectors.toMap(wareLog -> wareLog.getSyllabusId(), wareLog -> wareLog));

        dataList.forEach(item->{
            CourseWorkCourseVo courseWorkCourseVo = new CourseWorkCourseVo();
            courseWorkCourseVo.setCourseId(Long.valueOf(String.valueOf(item.get("courseId"))));
            courseWorkCourseVo.setCourseTitle(syllabusWareInfoTable.get(COURSE_LABEL, courseWorkCourseVo.getCourseId()).getClassName());
            String ids = String.valueOf(item.get("syllabusIds"));
            Set<Long> temp = Arrays.stream(ids.split(",")).map(Long::valueOf).collect(Collectors.toSet());
            if(CollectionUtils.isEmpty(temp)){
                courseWorkCourseVo.setUndoCount(0);
                courseWorkCourseVo.setWareInfoList(Lists.newArrayList());
                courseWorkCourseVo.setCourseTitle(StringUtils.EMPTY);
            }

            List<CourseWorkWareVo> wareVos = Arrays.stream(ids.split(","))
                    .filter(ware ->
                            null !=  syllabusWareInfoTable.get(LESSON_LABEL, Long.valueOf(ware)) &&
                            null !=  courseExercisesProcessLogMap.get(Long.valueOf(ware)) &&
                            null != courseExercisesProcessLogMap.get(Long.valueOf(ware)).getDataInfo())
                    .map(ware -> {
                    SyllabusWareInfo syllabusWareInfo = syllabusWareInfoTable.get(LESSON_LABEL, Long.valueOf(ware));
                    CourseExercisesProcessLog courseExercisesProcessLog = courseExercisesProcessLogMap.get(Long.valueOf(ware));
                    JSONObject jsonObject = JSONObject.parseObject(courseExercisesProcessLog.getDataInfo());
                    CourseWorkWareVo courseWorkWareVo = CourseWorkWareVo
                        .builder()
                        .courseWareId(syllabusWareInfo.getCoursewareId())
                        .courseWareTitle(syllabusWareInfo.getCoursewareName())
                        .videoLength(syllabusWareInfo.getLength())
                        .serialNumber(syllabusWareInfo.getSerialNumber())
                        .answerCardId(jsonObject.getLongValue("id"))
                        .answerCardInfo(jsonObject.getString("info"))
                        .questionIds("")
                        .isAlert(courseExercisesProcessLog.getIsAlert())
                        .build();
                return courseWorkWareVo;
            }).collect(Collectors.toList());


            courseWorkCourseVo.setUndoCount(temp.size());
            courseWorkCourseVo.setWareInfoList(wareVos);
            courseWorkCourseVos.add(courseWorkCourseVo);
        });
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
            String key = CourseCacheKey.getProcessLogSyllabusInfo(item);
            if(redisTemplate.hasKey(key)){
                String value = valueOperations.get(key);
                SyllabusWareInfo syllabusWareInfo = JSONObject.parseObject(value, SyllabusWareInfo.class);
                table.put(LESSON_LABEL, item, syllabusWareInfo);
                table.put(COURSE_LABEL, syllabusWareInfo.getClassId(), syllabusWareInfo);
                copy.remove(item);
            }
        });
        if(CollectionUtils.isNotEmpty(copy)){
            table.putAll(requestSyllabusWareInfoPut2Cache(copy));
        }
        return table;
    }

    /**
     * 请求大纲信息并缓存到 redis
     * @param syllabusIds
     * @return
     * @throws BizException
     */
    private Table<String, Long, SyllabusWareInfo> requestSyllabusWareInfoPut2Cache(Set<Long> syllabusIds)throws BizException{
        Stopwatch stopwatch = Stopwatch.createStarted();
        Table<String, Long, SyllabusWareInfo> table = TreeBasedTable.create();
        try{
            String ids = Joiner.on(",").join(syllabusIds);
            NetSchoolResponse<LinkedHashMap<String, Object>> netSchoolResponse = syllabusService.courseWareInfo(ids);
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
            log.debug(">>>>>>>> 请求大纲ids耗费时间:{}", stopwatch.elapsed(TimeUnit.MILLISECONDS));
            return table;
        }catch (Exception e) {
            log.error("request syllabusInfo error!:{}", e);
            return table;
        }
    }

}
