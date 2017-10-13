package com.huatu.tiku.course.mq.listeners;

import com.alibaba.fastjson.JSON;
import com.rabbitmq.client.Channel;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.core.ChannelAwareMessageListener;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * 处理金币任务消息
 * @author hanchao
 * @date 2017/10/13 13:02
 */
@Component
@Slf4j
public class RewardActionListener implements ChannelAwareMessageListener{
    @Autowired
    private MessageConverter messageConverter;

    @Override
    public void onMessage(Message message, Channel channel) throws Exception {
        try {
            Object o = messageConverter.fromMessage(message);
            log.info(JSON.toJSONString(o));
            channel.basicAck(message.getMessageProperties().getDeliveryTag(),false);
        } catch(Exception e){
            e.printStackTrace();
        }
    }
}
