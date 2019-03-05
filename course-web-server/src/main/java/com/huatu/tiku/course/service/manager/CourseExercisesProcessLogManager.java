package com.huatu.tiku.course.service.manager;

import com.alibaba.fastjson.JSONObject;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.huatu.common.exception.BizException;
import com.huatu.tiku.course.bean.NetSchoolResponse;
import com.huatu.tiku.course.bean.vo.CourseWorkListVo;
import com.huatu.tiku.course.bean.vo.SyllabusWareInfo;
import com.huatu.tiku.course.common.StudyTypeEnum;
import com.huatu.tiku.course.common.YesOrNoStatus;
import com.huatu.tiku.course.dao.manual.CourseExercisesProcessLogMapper;
import com.huatu.tiku.course.netschool.api.v7.SyllabusServiceV7;
import com.huatu.tiku.course.util.CourseCacheKey;
import com.huatu.tiku.course.util.ResponseUtil;
import com.huatu.tiku.entity.CourseExercisesProcessLog;
import lombok.extern.slf4j.Slf4j;
import netscape.javascript.JSObject;
import org.apache.commons.collections.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.stereotype.Component;
import tk.mybatis.mapper.entity.Example;

import java.sql.Timestamp;
import java.util.*;
import java.util.concurrent.TimeUnit;

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
        criteria.andEqualTo("dataType", StudyTypeEnum.COURSE_WORK.getKey());
        int countWork = courseExercisesProcessLogMapper.selectCountByExample(example);
        result.put(StudyTypeEnum.COURSE_WORK.getKey(), countWork);
        criteria.andEqualTo("dataType", StudyTypeEnum.PERIOD_TEST.getKey());
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
     * 获取我的课后作业列表
     * @param userId
     * @param page
     * @param size
     * @return
     * @throws BizException
     */
    public Object courseWorkList(long userId, int page, int size) throws BizException{
        Example example = new Example(CourseExercisesProcessLog.class);
        example.and()
                .andEqualTo("userId", userId)
                .andEqualTo("dataType", StudyTypeEnum.COURSE_WORK.getKey())
                .andEqualTo("status", YesOrNoStatus.YES.getCode());
        PageInfo pageInfo = PageHelper.startPage(page, size).doSelectPageInfo(() -> courseExercisesProcessLogMapper.selectByExample(example));
        if(CollectionUtils.isEmpty(pageInfo.getList())){
            return pageInfo;
        }
        List<CourseWorkListVo> courseWorkListVoList = Lists.newArrayList();
        pageInfo.getList().forEach(item -> {
            CourseExercisesProcessLog courseExercisesProcessLog = (CourseExercisesProcessLog) item;
            JSONObject jsonObject = JSONObject.parseObject(courseExercisesProcessLog.getDataInfo());
            CourseWorkListVo courseWorkListVo = CourseWorkListVo.builder()
                    .courseWareId(courseExercisesProcessLog.getLessonId())
                    .courseWareTitle(courseExercisesProcessLog.getLessonTitle())
                    .videoLength(jsonObject.getString("length"))
                    .serialNumber(jsonObject.getInteger("serialNumber"))
                    .answerCardId(0L)
                    .questionIds("")
                    .answerCardInfo("")
                    .build();

            courseWorkListVoList.add(courseWorkListVo);
        });
        pageInfo.setList(courseWorkListVoList);
        return pageInfo;
    }

    /**
     * 根据大纲id获取课件信息，
     * @param syllabusId
     * @return
     * @throws BizException
     */
    public SyllabusWareInfo dealSyllabusInfo(String syllabusId) throws BizException{
        String key = CourseCacheKey.getProcessLogSyllabusInfo(syllabusId);
        ValueOperations<String,String> valueOperations = redisTemplate.opsForValue();
        SyllabusWareInfo syllabusWareInfo;
        if(redisTemplate.hasKey(key)){
            String value = valueOperations.get(key);
            syllabusWareInfo = JSONObject.parseObject(value, SyllabusWareInfo.class);
            return syllabusWareInfo;
        }else{
            NetSchoolResponse<LinkedHashMap<String, Object>> netSchoolResponse = syllabusService.courseWareInfo(syllabusId);
            if(ResponseUtil.isFailure(netSchoolResponse)){
                return null;
            }
            syllabusWareInfo = syllabusWareInfo(netSchoolResponse.getData());
            if(null != syllabusWareInfo){
                valueOperations.set(key, JSONObject.toJSONString(syllabusWareInfo), 2, TimeUnit.MINUTES);
            }
            return syllabusWareInfo;
        }
    }

    /**
     * 远程调用php返回 SyllabusWareInfo
     * @param data
     * @return
     */
    private SyllabusWareInfo syllabusWareInfo(LinkedHashMap<String, Object> data){
        try{
            String courseId = data.keySet().stream().toArray(String[]::new)[0];
            SyllabusWareInfo syllabusWareInfo = new SyllabusWareInfo();
            LinkedHashMap<String,Object> courseInfo = (LinkedHashMap<String,Object>)data.get(courseId);
            syllabusWareInfo.setClassName(String.valueOf(courseInfo.get("className")));
            syllabusWareInfo.setClassId(Long.valueOf(courseId));
            LinkedHashMap<String,Object> child = (LinkedHashMap<String,Object>) courseInfo.get("child");
            String wareId = child.keySet().stream().toArray(String[]::new)[0];
            LinkedHashMap<String,Object> wareInfo = (LinkedHashMap<String,Object>)child.get(wareId);
            syllabusWareInfo.setLength(String.valueOf(wareInfo.get("length")));
            syllabusWareInfo.setCoursewareName(String.valueOf(wareInfo.get("coursewareName")));
            syllabusWareInfo.setCoursewareId(Long.valueOf(String.valueOf(wareInfo.get("coursewareId"))));
            syllabusWareInfo.setSyllabusId(Long.valueOf(String.valueOf(wareInfo.get("syllabusId"))));
            syllabusWareInfo.setSerialNumber(Integer.valueOf(String.valueOf(wareInfo.get("serialNumber"))));
            syllabusWareInfo.setVideoType(Integer.valueOf(String.valueOf(wareInfo.get("videoType"))));
            log.info("syllabusWareInfo:{}", JSONObject.toJSONString(syllabusWareInfo));
            return syllabusWareInfo;
        }catch (Exception e){
            return null;
        }
    }

}
