package com.huatu.tiku.course.web.controller;

import com.huatu.tiku.common.bean.reward.RewardMessage;
import com.huatu.tiku.common.consts.RabbitConsts;
import com.huatu.tiku.course.service.cache.OrderCacheKey;
import com.huatu.tiku.springboot.basic.reward.RewardAction;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.SetOperations;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

import java.util.HashSet;
import java.util.Set;

/**
 * @创建人 lizhenjuan
 * @创建时间 2019/3/28
 * @描述
 */
@Slf4j
@RestController
@RequestMapping(value = "estimateSendCoins")
public class EstimateSendCoinsController {

    @Autowired
    RabbitTemplate rabbitTemplate;

    @Autowired
    RedisTemplate redisTemplate;

    //线上用户名称（app_ztk1656238565,app_ztk338350685）
    //测试代码
        /*userNames.add("app_ztk1656238565");
        userNames.add("app_ztk338350685");*/

    @RequestMapping(value = "{courseId}", produces = MediaType.APPLICATION_JSON_UTF8_VALUE, method = RequestMethod.GET)
    public void estimateSendCoins(@PathVariable int courseId) {
        log.info("课程ID是:{}", courseId);
        Set<String> userNames = new HashSet();

        SetOperations setOperations = redisTemplate.opsForSet();
        String zeroOrderKey = OrderCacheKey.zeroOrder(courseId);
        log.info("课程key是:{}", zeroOrderKey);
        userNames = setOperations.members(zeroOrderKey);

        log.info("赠送用户数量是:{},赠送的用户name是:{}", userNames.size(), userNames.toString());

        userNames.stream().forEach(userName -> {
            RewardMessage msg = RewardMessage.builder().gold(330).action(RewardAction.ActionType.ACTIVTY.name())
                    .bizId("4001458" + System.currentTimeMillis()).uname(userName)
                    .timestamp(System.currentTimeMillis()).build();
            rabbitTemplate.convertAndSend("", RabbitConsts.QUEUE_REWARD_ACTION, msg);
        });
    }

}
