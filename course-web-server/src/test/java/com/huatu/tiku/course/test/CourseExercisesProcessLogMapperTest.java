package com.huatu.tiku.course.test;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import com.google.common.collect.Table;
import com.huatu.common.test.BaseWebTest;
import com.huatu.tiku.course.bean.vo.LiveRecordInfo;
import com.huatu.tiku.course.bean.vo.LiveRecordInfoWithUserInfo;
import com.huatu.tiku.course.bean.vo.SyllabusWareInfo;
import com.huatu.tiku.course.common.YesOrNoStatus;
import com.huatu.tiku.course.consts.RabbitMqConstants;
import com.huatu.tiku.course.dao.manual.CourseExercisesProcessLogMapper;
import com.huatu.tiku.course.service.manager.CourseExercisesProcessLogManager;
import com.huatu.tiku.entity.CourseExercisesProcessLog;
import com.huatu.ztk.paper.common.AnswerCardStatus;
import lombok.extern.slf4j.Slf4j;
import org.junit.Test;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.AutoConfigurationPackage;
import tk.mybatis.mapper.entity.Example;

import javax.persistence.Temporal;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

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

    @Autowired
    private RabbitTemplate rabbitTemplate;



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
        Set<Long> syllabusId = Sets.newHashSet(8361563L,8361564L,8361562L,8361565L,4227750L);

        syllabusId.forEach(item ->{
            try{
                courseExercisesProcessLogManager.putIntoDealList(item);
                TimeUnit.SECONDS.sleep(1);
            }catch (InterruptedException e){
                log.error("and intterrupted error!");
            }
        });

    }


    @Test
    public void testCourseWorkList(){
        List<Integer> list = Lists.newArrayList(AnswerCardStatus.CREATE, AnswerCardStatus.UNDONE);
        Example example = new Example(CourseExercisesProcessLog.class);
        example.and().andEqualTo("status", YesOrNoStatus.YES.getCode())
                .andIn("bizStatus", list);

        List<CourseExercisesProcessLog> workList = courseExercisesProcessLogMapper.selectByExample(example);
        Set<Long> userIds = workList.stream().map(CourseExercisesProcessLog::getUserId).collect(Collectors.toSet());
        for (Long userId : userIds) {
            Object object = courseExercisesProcessLogManager.courseWorkList(userId, 1, 100);
            log.info(">>>>>>>>>>>>>>>>>>>>>>>>>>>>>>:{}", JSONObject.toJSONString(object));
        }
    }

    /**
     *
     */
    @Test
    public void saveLiveReport(){
        for(int i = 0; i < 1000; i ++){
            LiveRecordInfo liveRecordInfo = LiveRecordInfo.builder()
                    .courseWareId(1000123)
                    .classId(73987)
                    .syllabusId(5476947)
                    .bjyRoomId("19032545565293")
                    .build();

            LiveRecordInfoWithUserInfo liveRecordInfoWithUserId = LiveRecordInfoWithUserInfo
                    .builder()
                    .subject(1)
                    .terminal(1)
                    .userId(234934290)
                    .liveRecordInfo(liveRecordInfo).build();
            rabbitTemplate.convertAndSend("", RabbitMqConstants.COURSE_LIVE_REPORT_LOG, JSONObject.toJSONString(liveRecordInfoWithUserId));
        }
    }
}
