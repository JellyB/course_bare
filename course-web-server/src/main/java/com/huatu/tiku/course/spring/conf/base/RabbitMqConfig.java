package com.huatu.tiku.course.spring.conf.base;

import com.ctrip.framework.apollo.spring.annotation.EnableApolloConfig;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.collect.Maps;
import com.huatu.tiku.course.consts.RabbitMqConstants;
import com.huatu.tiku.course.mq.listeners.RewardMessageListener;
import org.springframework.amqp.core.AcknowledgeMode;
import org.springframework.amqp.core.AmqpAdmin;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitAdmin;
import org.springframework.amqp.rabbit.listener.SimpleMessageListenerContainer;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import java.util.Map;

import static com.huatu.common.consts.ApolloConfigConsts.NAMESPACE_TIKU_RABBIT;
import static com.huatu.tiku.common.consts.RabbitConsts.*;

/**
 * @author hanchao
 * @date 2017/9/4 14:05
 */
@EnableApolloConfig(NAMESPACE_TIKU_RABBIT)
@Configuration
public class RabbitMqConfig {
    @Bean
    public Jackson2JsonMessageConverter jackson2JsonMessageConverter(@Autowired ObjectMapper objectMapper) {
        return new Jackson2JsonMessageConverter(objectMapper);
    }

    /**
     * queue声明
     *
     * @return
     */
    @Bean
    public Queue sendFreeCourseQueue() {
        return new Queue(QUEUE_SEND_FREE_COURSE);
    }

    @Bean
    public Queue userNickUpdateQueue() {
        return new Queue(QUEUE_USER_NICK_UPDATE);
    }


    @Bean
    public Queue rewardActionQueue() {
        Map<String, Object> arguments = Maps.newHashMap();
        arguments.put(ARG_DLX, DLX_DEFAULT);
        arguments.put(ARG_DLK, DLK_DEFAULT);
        Queue rewardActionQueue = new Queue(QUEUE_REWARD_ACTION, true, false, false, arguments);
        return rewardActionQueue;
    }


    @Bean
    public SimpleMessageListenerContainer rewardMessageListenerContainer(@Autowired ConnectionFactory connectionFactory,
                                                                         @Autowired(required = false) @Qualifier("coreThreadPool") ThreadPoolTaskExecutor threadPoolTaskExecutor,
                                                                         @Autowired RewardMessageListener rewardMessageListener,
                                                                         @Autowired AmqpAdmin amqpAdmin) {
        SimpleMessageListenerContainer manualRabbitContainer = new SimpleMessageListenerContainer();
        manualRabbitContainer.setQueueNames(QUEUE_REWARD_ACTION);
        manualRabbitContainer.setConnectionFactory(connectionFactory);
        if (amqpAdmin instanceof RabbitAdmin) {
            manualRabbitContainer.setRabbitAdmin((RabbitAdmin) amqpAdmin);
        }
        if (threadPoolTaskExecutor != null) {
            manualRabbitContainer.setTaskExecutor(threadPoolTaskExecutor);
        }
        manualRabbitContainer.setAcknowledgeMode(AcknowledgeMode.MANUAL);
        manualRabbitContainer.setConcurrentConsumers(threadPoolTaskExecutor.getCorePoolSize() / 4);
        manualRabbitContainer.setMaxConcurrentConsumers(threadPoolTaskExecutor.getCorePoolSize() / 4);
        manualRabbitContainer.setMessageListener(rewardMessageListener);
        return manualRabbitContainer;
    }

    @Bean
    public Queue courseWorkSubmitCardInfo(){
        return new Queue(RabbitMqConstants.COURSE_WORK_SUBMIT_CARD_INFO);
    }


}
