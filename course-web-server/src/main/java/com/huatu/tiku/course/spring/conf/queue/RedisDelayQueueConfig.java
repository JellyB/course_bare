package com.huatu.tiku.course.spring.conf.queue;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.huatu.tiku.course.service.v1.ProcessReportService;
import com.huatu.tiku.course.service.v1.impl.ProcessReportServiceImpl;
import com.huatu.tiku.course.util.CourseCacheKey;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.event.EventListener;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.scheduling.annotation.Async;
import redis.clients.jedis.JedisCluster;

/**
 * 描述：
 *
 * @author biguodong
 * Create time 2019-02-26 下午2:56
 **/
@Configuration
public class RedisDelayQueueConfig {

    @Autowired
    private ProcessReportService processReportService;


    @Autowired
    private RedisTemplate redisTemplate;

    @Bean(value = "redisDelayQueue")
    @Async
    @EventListener
    public RedisDelayQueue redisDelayQueue(){

        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        objectMapper.configure(DeserializationFeature.FAIL_ON_IGNORED_PROPERTIES, false);
        objectMapper.configure(DeserializationFeature.FAIL_ON_NULL_FOR_PRIMITIVES, false);
        objectMapper.setSerializationInclusion(JsonInclude.Include.NON_NULL);
        objectMapper.setSerializationInclusion(JsonInclude.Include.NON_EMPTY);
        objectMapper.disable(SerializationFeature.INDENT_OUTPUT);
        RedisDelayQueue redisDelayQueue = new RedisDelayQueue(CourseCacheKey.getProcessReportDelayQueue(), redisTemplate, 60 * 1000, objectMapper, new DelayQueueProcessListener() {
            @Override
            public void ackCallback(Message message) {

            }

            @Override
            public void peekCallback(Message message) {
                Object obj = message.getPayload();
                processReportService.ack(obj);
            }

            @Override
            public void pushCallback(Message message) {

            }
        });
        redisDelayQueue.listen();
        return redisDelayQueue;
    }
}
