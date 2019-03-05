package com.huatu.tiku.course.test;

import com.huatu.common.test.BaseWebTest;
import com.huatu.tiku.course.dao.manual.CourseExercisesProcessLogMapper;
import com.huatu.tiku.course.service.manager.CourseExercisesProcessLogManager;
import com.huatu.tiku.entity.CourseExercisesProcessLog;
import lombok.extern.slf4j.Slf4j;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.AutoConfigurationPackage;

/**
 * 描述：
 *
 * @author biguodong
 * Create time 2019-02-25 下午6:14
 **/
@Slf4j
public class CourseExercisesProcessLogMapperTest extends BaseWebTest {

    @Autowired
    private CourseExercisesProcessLogMapper courseExercisesProcessLogMapper;


    @Autowired
    private CourseExercisesProcessLogManager courseExercisesProcessLogManager;


    @Test
    public void insert(){
        CourseExercisesProcessLog courseExercisesProcessLog = new CourseExercisesProcessLog();
        courseExercisesProcessLogMapper.insert(courseExercisesProcessLog);
    }

    @Test
    public void testInfo(){
        String syllabusId = "8361565";
        courseExercisesProcessLogManager.dealSyllabusInfo(syllabusId);
    }
}
