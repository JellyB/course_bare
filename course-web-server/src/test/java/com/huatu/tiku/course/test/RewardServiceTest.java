package com.huatu.tiku.course.test;

import com.alibaba.fastjson.JSONObject;
import com.huatu.common.test.BaseWebTest;
import com.huatu.common.utils.encrypt.SignUtil;
import com.huatu.common.utils.reflect.BeanUtil;
import com.huatu.tiku.common.bean.reward.RewardMessage;
import com.huatu.tiku.common.consts.RabbitConsts;
import com.huatu.tiku.course.common.NetSchoolConfig;
import com.huatu.tiku.course.util.Crypt3Des;
import com.huatu.tiku.course.util.RequestUtil;
import lombok.extern.slf4j.Slf4j;
import org.junit.Test;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Map;

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
                .gold(20)
                .experience(0)
                .uid(233982024)
                .uname("app_ztk802796288")
                .timestamp(1553779862920L)
                .build();
        rabbitTemplate.convertAndSend("", RabbitConsts.QUEUE_REWARD_ACTION, rewardMessage);
    }

    @Test
    public void decryptMode() {
        RewardMessage rewardMessage  = RewardMessage.builder()
                .bizId("4001458,4001459,4001460")
                .action("ACTIVTY")
                .gold(5000)
                .experience(1000000)
                .uid(233982024)
                .uname("app_ztk802796288")
                .timestamp(1553779862920L)
                .build();


        Map<String,Object> params = BeanUtil.toMap(rewardMessage);
        params.remove("uid");
        params.put("origin",2);


        String sign = SignUtil.getPaySign(params, NetSchoolConfig.API_KEY);
        params.put("sign",sign);
        //添加金币
        log.info("用户:{} 添加金币, 请求参数:{}",rewardMessage.getUname(), RequestUtil.encryptParams(params));
        Map<String,Object> objectMap = RequestUtil.encryptParams(params);
        String p = String.valueOf(objectMap.get("p"));
        log.error("<<<<<<<<<<<<<<<<<<<<<<<<<<<<:{}", objectMap.get("p"));
        String pp =  Crypt3Des.decryptMode(p);
        log.error(">>>>>>>>>>>>>>>>>>>>>>>>>>>>>:{}", pp);
    }
}
