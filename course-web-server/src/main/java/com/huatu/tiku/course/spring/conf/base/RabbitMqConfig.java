package com.huatu.tiku.course.spring.conf.base;

import com.ctrip.framework.apollo.spring.annotation.EnableApolloConfig;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import static com.huatu.tiku.course.common.RabbitQueueConsts.QUEUE_SEND_FREE_COURSE;
import static com.huatu.tiku.course.common.RabbitQueueConsts.QUEUE_USER_NICK_UPDATE;

/**
 * @author hanchao
 * @date 2017/9/4 14:05
 */
@EnableApolloConfig("tiku.rabbitmq")
@Configuration
public class RabbitMqConfig {
    @Bean
    public Jackson2JsonMessageConverter jackson2JsonMessageConverter(@Autowired ObjectMapper objectMapper){
        return new Jackson2JsonMessageConverter(objectMapper);
    }

    /**
     * queue声明
     * @return
     */
    @Bean("send_free_course_queue")
    public Queue queue(){
        return new Queue(QUEUE_SEND_FREE_COURSE);
    }

    @Bean
    public Queue userNickUpdateQueue(){
        return new Queue(QUEUE_USER_NICK_UPDATE);
    }
}
