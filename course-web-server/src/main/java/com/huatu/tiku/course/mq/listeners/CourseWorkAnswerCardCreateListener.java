package com.huatu.tiku.course.mq.listeners;

import com.alibaba.fastjson.JSONObject;
import com.huatu.tiku.course.consts.RabbitMqConstants;
import com.huatu.tiku.course.service.manager.CourseExercisesProcessLogManager;
import com.huatu.ztk.paper.bean.PracticeCard;
import com.huatu.ztk.paper.bo.CourseWorkAnswerCardBo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * 描述：课后作业创建答题卡队列监听
 *
 * @author biguodong
 * Create time 2019-03-07 8:50 PM
 **/

@Slf4j
@Component
public class CourseWorkAnswerCardCreateListener {

    @Autowired
    private CourseExercisesProcessLogManager courseExercisesProcessLogManager;


    @RabbitListener(queues = RabbitMqConstants.COURSE_WORK_CREATE_CARD_INFO)
    public void onMessage(String message){
        CourseWorkAnswerCardBo courseWorkAnswerCardBo = JSONObject.parseObject(message, CourseWorkAnswerCardBo.class);
        log.info("course_work_create_card_info:{}", courseWorkAnswerCardBo.getPracticeCard().getId());
        courseExercisesProcessLogManager.createCourseWorkAnswerCard(courseWorkAnswerCardBo);
    }
}
