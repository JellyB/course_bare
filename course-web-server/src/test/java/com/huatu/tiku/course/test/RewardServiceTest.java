package com.huatu.tiku.course.test;

import com.alibaba.fastjson.JSONObject;
import com.huatu.common.test.BaseWebTest;
import com.huatu.tiku.common.bean.reward.RewardMessage;
import com.huatu.tiku.common.consts.RabbitConsts;
import lombok.extern.slf4j.Slf4j;
import org.junit.Test;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * 描述：
 *
 * @author biguodong
 * Create time 2019-04-19 1:30 PM
 **/

@Slf4j
public class RewardServiceTest extends BaseWebTest {

    @Autowired
    private RabbitTemplate rabbitTemplate;


    @Test
    public void testReward(){

        //bizId=, action=, gold=500, experience=0, uid=0, uname=httk_58ccec42d3ab0, timestamp=1553779862910
        RewardMessage rewardMessage  = RewardMessage.builder()
                .bizId("4001458,4001459,4001460")
                .action("ACTIVTY")
                .gold(500)
                .experience(0)
                .uid(233982024)
                .uname("app_ztk802796288")
                .timestamp(1553779862910L)
                .build();
        rabbitTemplate.convertAndSend("", RabbitConsts.QUEUE_REWARD_ACTION, rewardMessage);
    }
}
