package com.huatu.tiku.course.mq.listeners;

import com.alibaba.fastjson.JSONObject;
import com.huatu.tiku.course.bean.vo.LiveRecordInfo;
import com.huatu.tiku.course.bean.vo.LiveRecordInfoWithUserInfo;
import com.huatu.tiku.course.consts.RabbitMqConstants;
import com.huatu.tiku.course.service.manager.CourseExercisesProcessLogManager;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

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

    @RabbitListener(queues = RabbitMqConstants.COURSE_LIVE_REPORT_LOG)
    public void onMessage(String message){
        LiveRecordInfoWithUserInfo liveRecordInfoWithUserId = JSONObject.parseObject(message, LiveRecordInfoWithUserInfo.class);
        LiveRecordInfo liveRecordInfo = liveRecordInfoWithUserId.getLiveRecordInfo();
        //创建课后作业
        courseExercisesProcessLogManager.saveLiveRecord(liveRecordInfoWithUserId.getUserId(), liveRecordInfoWithUserId.getSubject(), liveRecordInfoWithUserId.getTerminal(),liveRecordInfo.getSyllabusId());
    }
}
