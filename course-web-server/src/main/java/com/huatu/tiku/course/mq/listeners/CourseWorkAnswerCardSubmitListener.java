package com.huatu.tiku.course.mq.listeners;

import com.alibaba.fastjson.JSONObject;
import com.huatu.tiku.course.bean.CourseWorkDto;
import com.huatu.tiku.course.consts.RabbitMqConstants;
import com.huatu.tiku.course.service.manager.CourseExercisesProcessLogManager;
import com.huatu.ztk.paper.bean.AnswerCard;
import com.huatu.ztk.paper.bean.PracticeCard;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * 描述：课后作业保存答题卡队列监听
 *
 * @author biguodong
 * Create time 2019-03-07 5:24 PM
 **/
@Slf4j
@Component
public class CourseWorkAnswerCardSubmitListener {


    @Autowired
    private CourseExercisesProcessLogManager courseExercisesProcessLogManager;


    @RabbitListener(queues = RabbitMqConstants.COURSE_WORK_SUBMIT_CARD_INFO)
    public void onMessage(String message){

        PracticeCard answerCard = JSONObject.parseObject(message, PracticeCard.class);
        log.info("course_work_submit_card_info:{}", answerCard.getId());
        courseExercisesProcessLogManager.submitCourseWorkAnswerCard(answerCard);
    }
}
