package com.huatu.tiku.course.spring.conf.base;

import com.ctrip.framework.apollo.spring.annotation.EnableApolloConfig;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.huatu.tiku.course.mq.listeners.RewardActionListener;
import org.springframework.amqp.core.AcknowledgeMode;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.listener.SimpleMessageListenerContainer;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import static com.huatu.tiku.course.common.RabbitQueueConsts.*;

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
    @Bean
    public Queue sendFreeCourseQueue(){
        return new Queue(QUEUE_SEND_FREE_COURSE);
    }

    @Bean
    public Queue userNickUpdateQueue(){
        return new Queue(QUEUE_USER_NICK_UPDATE);
    }


    @Bean
    public Queue rewardActionQueue(){
        return new Queue(QUEUE_REWARD_ACTION);
    }

    @Bean
    public SimpleMessageListenerContainer manualRabbitContainer(@Autowired ConnectionFactory connectionFactory,
                                                                @Autowired RewardActionListener rewardActionUpdateListener,
                                                                @Autowired(required = false) ThreadPoolTaskExecutor threadPoolTaskExecutor){
        SimpleMessageListenerContainer manualRabbitContainer = new SimpleMessageListenerContainer();
        manualRabbitContainer.setConnectionFactory(connectionFactory);
        if(threadPoolTaskExecutor != null){
            manualRabbitContainer.setTaskExecutor(threadPoolTaskExecutor);
        }
        manualRabbitContainer.setAcknowledgeMode(AcknowledgeMode.MANUAL);
        manualRabbitContainer.setConcurrentConsumers(threadPoolTaskExecutor.getCorePoolSize()/2);
        manualRabbitContainer.setMaxConcurrentConsumers(threadPoolTaskExecutor.getCorePoolSize()/2);
        manualRabbitContainer.setMessageListener(rewardActionUpdateListener);
        return manualRabbitContainer;
    }

}
