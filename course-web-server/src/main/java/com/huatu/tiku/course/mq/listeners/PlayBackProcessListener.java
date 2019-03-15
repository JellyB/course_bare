package com.huatu.tiku.course.mq.listeners;

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
 * Create time 2019-03-15 10:21 AM
 **/

@Component
@Slf4j
public class PlayBackProcessListener {

    @Autowired
    private CourseExercisesProcessLogManager courseExercisesProcessLogManager;



    @RabbitListener(queues = RabbitMqConstants.PLAY_BACK_DEAL_INFO)
    public void onMessage(String message){

    }
}
