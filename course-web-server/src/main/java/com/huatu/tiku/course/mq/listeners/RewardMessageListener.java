package com.huatu.tiku.course.mq.listeners;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.huatu.common.utils.encrypt.SignUtil;
import com.huatu.common.utils.reflect.BeanUtil;
import com.huatu.tiku.common.bean.RewardMessage;
import com.huatu.tiku.course.bean.NetSchoolResponse;
import com.huatu.tiku.course.common.NetSchoolConfig;
import com.huatu.tiku.course.netschool.api.v3.GoldChargeService;
import com.huatu.tiku.course.service.RewardBizService;
import com.huatu.tiku.course.util.RequestUtil;
import com.huatu.tiku.course.util.ResponseUtil;
import com.huatu.tiku.springboot.basic.reward.RewardAction;
import com.huatu.tiku.springboot.basic.reward.RewardActionService;
import com.rabbitmq.client.Channel;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.core.ChannelAwareMessageListener;
import org.springframework.beans.factory.annotation.Autowired;

import java.io.IOException;
import java.util.Map;

/**
 * 处理金币任务消息
 * @author hanchao
 * @date 2017/10/13 13:02
 */
@Slf4j
public class RewardMessageListener implements ChannelAwareMessageListener{

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private RewardActionService rewardActionService;

    @Autowired
    private RewardBizService rewardBizService;

    @Autowired
    private GoldChargeService goldChargeService;

    @Override
    public void onMessage(Message message, Channel channel) throws Exception{
        try {
            RewardMessage rewardMessage = objectMapper.readValue(message.getBody(), RewardMessage.class);

            //消息合法性检查
            if(rewardMessage == null || StringUtils.isBlank(rewardMessage.getAction())){
                throw new IllegalArgumentException("get a reward message with action empty," + rewardMessage);
            }

            //如果客户端未设置成长值或者金币，根据配置自动设置
            if(rewardMessage.getGold() == 0 || rewardMessage.getExperience() == 0){
                RewardAction rewardAction = rewardActionService.get(rewardMessage.getAction());
                if(rewardAction == null){
                    throw new IllegalArgumentException("get a reward message with a unknown action," + rewardMessage);
                }
                rewardMessage.setGold(rewardAction.getGold());
                rewardMessage.setExperience(rewardAction.getExperience());
            }

            //处理逻辑
            Map<String,Object> params = BeanUtil.toMap(rewardMessage);
            params.remove("uid");

            String sign = SignUtil.getPaySign(params, NetSchoolConfig.API_KEY);
            params.put("sign",sign);

            NetSchoolResponse response = goldChargeService.chargeGold(RequestUtil.encryptParams(params));

            if(ResponseUtil.isSuccess(response)){
                rewardBizService.addRewardAction(rewardActionService.get(rewardMessage.getAction()),rewardMessage.getUid(),rewardMessage.getBizId());
            }

            channel.basicAck(message.getMessageProperties().getDeliveryTag(),false);
        } catch(IOException | ClassCastException | IllegalArgumentException e){
            //转换异常丢弃消息
            log.error("convert error,message is wrong ? {},{}",message.getMessageProperties(),String.valueOf(message.getBody()),e);
            channel.basicNack(message.getMessageProperties().getDeliveryTag(),false,false);
        } catch(Exception e){
            //处理异常重新入队
            log.error("consume message caused an error...",e);
            channel.basicNack(message.getMessageProperties().getDeliveryTag(),false,true);
        }
    }
}
