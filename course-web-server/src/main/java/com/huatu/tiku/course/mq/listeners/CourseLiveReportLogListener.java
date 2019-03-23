package com.huatu.tiku.course.mq.listeners;

import com.huatu.tiku.course.consts.RabbitMqConstants;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
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



    @RabbitListener(queues = RabbitMqConstants.COURSE_LIVE_REPORT_LOG)
    public void onMessage(String message){
        //todo 入库

    }
}
