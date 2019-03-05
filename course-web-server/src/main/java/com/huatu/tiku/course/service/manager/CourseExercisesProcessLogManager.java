package com.huatu.tiku.course.service.manager;

import com.google.common.collect.Maps;
import com.huatu.common.bean.BaseStatusEnum;
import com.huatu.common.exception.BizException;
import com.huatu.tiku.course.common.StudyTypeEnum;
import com.huatu.tiku.course.common.YesOrNoStatus;
import com.huatu.tiku.course.dao.manual.CourseExercisesProcessLogMapper;
import com.huatu.tiku.entity.CourseExercisesProcessLog;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import tk.mybatis.mapper.entity.Example;

import java.sql.Timestamp;
import java.util.Map;

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
    public int readyOnece(long id, String type) throws BizException{
        StudyTypeEnum studyTypeEnum = StudyTypeEnum.create(type);
        CourseExercisesProcessLog courseExercisesProcessLog = new CourseExercisesProcessLog();
        courseExercisesProcessLog.setGmtModify(new Timestamp(System.currentTimeMillis()));
        courseExercisesProcessLog.setIsAlert(YesOrNoStatus.NO.getCode());
        courseExercisesProcessLog.setId(id);
        return courseExercisesProcessLogMapper.updateByPrimaryKeySelective(courseExercisesProcessLog);
    }



}
