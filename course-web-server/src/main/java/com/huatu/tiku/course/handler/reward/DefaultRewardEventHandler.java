package com.huatu.tiku.course.handler.reward;

import com.huatu.tiku.common.bean.reward.RewardMessage;
import com.huatu.tiku.course.common.RabbitConsts;
import com.huatu.tiku.springboot.basic.reward.RewardAction;
import com.huatu.tiku.springboot.basic.reward.event.AbstractRewardActionEventHandler;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.Ordered;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

/**
 * @author hanchao
 * @date 2017/10/13 19:38
 */
@Component
public class DefaultRewardEventHandler extends AbstractRewardActionEventHandler {
    @Autowired
    private RabbitTemplate rabbitTemplate;
    @Autowired
    private RedisTemplate redisTemplate;
    @Override
    public void dealMessage(RewardMessage rewardMessage) {
        rabbitTemplate.convertAndSend(RabbitConsts.QUEUE_REWARD_ACTION,rewardMessage);
    }

    /**
     * 不处理不限制次数的，不处理签到
     * @param rewardAction
     * @return
     */
    @Override
    protected boolean canHandle(RewardAction rewardAction) {
        return rewardAction.getStrategy() != RewardAction.Strategy.NONE &&
                rewardAction.getAction() != null &&
                rewardAction.getAction() != RewardAction.ActionType.ATTENDANCE;
    }

    @Override
    protected RedisTemplate getRedisTemplate() {
        return redisTemplate;
    }


    @Override
    public int getOrder() {
        return Ordered.LOWEST_PRECEDENCE;
    }
}
