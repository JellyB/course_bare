package com.huatu.tiku.course.service.v1.impl;

import com.alibaba.fastjson.JSONObject;
import com.google.common.util.concurrent.ThreadFactoryBuilder;
import com.huatu.common.SuccessMessage;
import com.huatu.tiku.course.consts.RabbitConsts;
import com.huatu.tiku.course.consts.ReportMsg;
import com.huatu.tiku.course.service.v1.ProcessReportService;
import lombok.extern.slf4j.Slf4j;
import org.bouncycastle.cms.PasswordRecipientId;
import org.springframework.amqp.AmqpException;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.core.MessagePostProcessor;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import static com.huatu.tiku.course.consts.RabbitConsts.*;

import java.util.Map;
import java.util.concurrent.*;

/**
 * 描述：
 *
 * @author biguodong
 * Create time 2019-02-22 上午11:17
 **/

@Service
@Slf4j
public class ProcessReportServiceImpl implements ProcessReportService {

    @Autowired
    private RabbitTemplate rabbitTemplate;

    /**
     * 数据上报接口
     *
     * @param params
     * @return
     */
    @Override
    public Object liveReport(Map<String, Object> params) {
        ReportMsg reportMsg = ReportMsg
                .builder()
                .syllabusId(Integer.valueOf(String.valueOf(params.getOrDefault(SYLLABUS_ID, "0"))))
                .expiration(String.valueOf(params.getOrDefault(EXPIRATION, "1000")))
                .type(TYPE_LIVE)
                .build();

        /*ScheduledExecutorService timer = Executors.newSingleThreadScheduledExecutor();
        //ThreadFactory threadFactory = new ThreadFactoryBuilder().setNameFormat("con-%s").build();
        //ExecutorService threadPoolExecutor = new ThreadPoolExecutor(5, 20, 30, TimeUnit.SECONDS, new ArrayBlockingQueue<>(30), threadFactory);

        timer.schedule(()->{


        }, reportMsg.getExpiration(), TimeUni   t.SECONDS);*/
        log.warn("liveReport:{}", params);

        rabbitTemplate.convertAndSend(REPORT_DELAY_EXCHANGE_NAME, REPORT_ROUTING_KEY, JSONObject.toJSONString(reportMsg), message -> {
            message.getMessageProperties().setExpiration(reportMsg.getExpiration());
            return message;
        });
        return SuccessMessage.create();
    }


    @Override
    public Object playBackReport(Map<String, Object> params) {
        log.warn("liveReport:{}", params);
        ReportMsg reportMsg = ReportMsg
                .builder()
                .syllabusId(Integer.valueOf(String.valueOf(params.getOrDefault(SYLLABUS_ID, "0"))))
                .expiration("")
                .type(TYPE_PLAYBACK)
                .build();
        rabbitTemplate.convertAndSend(REPORT_PROCESS_EXCHANGE_NAME, REPORT_ROUTING_KEY, JSONObject.toJSONString(reportMsg));
        return SuccessMessage.create();
    }

    /**
     * 处理数据上报信息
     *
     * @param reportMsg
     * @return
     */
    @Override
    public Object dealReportMsg(ReportMsg reportMsg) {
        return null;
    }

    private static class DelayTask implements Runnable{
        private ReportMsg reportMsg;

        public DelayTask(ReportMsg reportMsg) {
            this.reportMsg = reportMsg;
        }
        @Override
        public void run() {
            reportMsg.getSyllabusId();
            log.error("execute on delay time:");
        }
    }
}
