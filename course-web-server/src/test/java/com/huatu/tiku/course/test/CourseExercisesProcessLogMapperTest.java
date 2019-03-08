package com.huatu.tiku.course.test;

import com.alibaba.fastjson.JSONObject;
import com.google.common.collect.Sets;
import com.google.common.collect.Table;
import com.huatu.common.test.BaseWebTest;
import com.huatu.tiku.course.bean.vo.SyllabusWareInfo;
import com.huatu.tiku.course.dao.manual.CourseExercisesProcessLogMapper;
import com.huatu.tiku.course.service.manager.CourseExercisesProcessLogManager;
import com.huatu.tiku.entity.CourseExercisesProcessLog;
import lombok.extern.slf4j.Slf4j;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.AutoConfigurationPackage;

import java.util.Map;
import java.util.Set;
import java.util.concurrent.TimeUnit;

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
        Set<Long> syllabusId = Sets.newHashSet();
        syllabusId.add(8361563L);
        syllabusId.add(8361564L);
        syllabusId.add(8361562L);
        syllabusId.add(8361565L);
        Table<String, Long, SyllabusWareInfo> table =  courseExercisesProcessLogManager.dealSyllabusInfo(syllabusId);

        table.row("lesson").values().forEach(item -> {
            log.info("SyllabusWareInfo:{}", JSONObject.toJSONString(item));
        });
    }

    @Test
    public void dealList(){
        Set<Long> syllabusId = Sets.newHashSet(8351423L,8361641L,8361871L,8361872L,8361874L, 8361907L, 8361908L,
                8361909L,8361911L,4227738L,8353913L,8353914L,8353915L,8353916L,4227745L,4227746L);

        syllabusId.forEach(item ->{
            try{
                courseExercisesProcessLogManager.putIntoDealList(item);
                TimeUnit.SECONDS.sleep(1);
            }catch (InterruptedException e){
                log.error("and intterrupted error!");
            }
        });

    }
}
