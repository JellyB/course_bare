package com.huatu.tiku.course.mq.listeners;

import com.alibaba.fastjson.JSONObject;
import com.google.common.collect.Maps;
import com.huatu.tiku.course.bean.vo.LiveRecordInfo;
import com.huatu.tiku.course.bean.vo.LiveRecordInfoWithUserInfo;
import com.huatu.tiku.course.consts.RabbitMqConstants;
import com.huatu.tiku.course.netschool.api.v6.UserCourseServiceV6;
import com.huatu.tiku.course.service.manager.CourseExercisesProcessLogManager;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Map;

/**
 * 描述：
 *
 * @author biguodong
 * Create time 2019-03-22 10:31 PM
 **/

@Slf4j
@Component
public class CourseLiveReportLogListener {

    @Autowired
    private CourseExercisesProcessLogManager courseExercisesProcessLogManager;

    @Autowired
    private UserCourseServiceV6 userCourseService;

    @RabbitListener(queues = RabbitMqConstants.COURSE_LIVE_REPORT_LOG)
    public void onMessage(String message){
        log.info("save_record_v7, message content:{}", message);
        LiveRecordInfoWithUserInfo liveRecordInfoWithUserId = JSONObject.parseObject(message, LiveRecordInfoWithUserInfo.class);
        LiveRecordInfo liveRecordInfo = liveRecordInfoWithUserId.getLiveRecordInfo();

        Map<String,Object> params = Maps.newHashMap();
        params.put("userName", liveRecordInfoWithUserId.getUserName());
        params.put("syllabusId", liveRecordInfo.getSyllabusId());
        params.put("terminal", liveRecordInfoWithUserId.getTerminal());
        params.put("cv", liveRecordInfoWithUserId.getCv());
        userCourseService.saveLiveRecord(params);
        //创建课后作业
        courseExercisesProcessLogManager.saveLiveRecord(liveRecordInfoWithUserId.getUserId(),
                liveRecordInfoWithUserId.getSubject(),
                liveRecordInfoWithUserId.getTerminal(),
                liveRecordInfo.getSyllabusId(),
                liveRecordInfoWithUserId.getCv());
    }
}
