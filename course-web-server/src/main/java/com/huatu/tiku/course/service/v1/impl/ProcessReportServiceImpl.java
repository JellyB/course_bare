package com.huatu.tiku.course.service.v1.impl;

import com.alibaba.fastjson.JSONObject;
import com.huatu.common.SuccessMessage;
import com.huatu.tiku.common.consts.RabbitConsts;
import com.huatu.tiku.course.consts.RabbitMqConstants;
import com.huatu.tiku.course.service.v1.ProcessReportService;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections.MapUtils;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.SetOperations;
import org.springframework.stereotype.Service;


import java.io.Serializable;
import java.util.Map;

/**
 * 描述：
 *
 * TODO 1. 直播数据上报延时时间处理
 * 2. 处理后插叙是否配置有课后作业
 * 3. 如果有配置课后作业，查询答题卡状态；
 * 4. 如果做完或者没有做，处理数据
 * @author biguodong
 * Create time 2019-02-22 上午11:17
 **/

@Service
@Slf4j
public class ProcessReportServiceImpl<T> implements ProcessReportService<T> {


    @Autowired
    private RabbitTemplate rabbitTemplate;

    @Override
    public Object playBackReport(Map<String, Object> params) {

        long syllabusId = MapUtils.getLong(params, "syllabusId");
        String userName = MapUtils.getString(params, "userName");

        PlayBack playBack = PlayBack.builder().syllabusId(syllabusId).userName(userName).build();
        rabbitTemplate.convertAndSend("", RabbitMqConstants.PLAY_BACK_DEAL_INFO, JSONObject.toJSONString(playBack));
        return SuccessMessage.create();
    }



    @NoArgsConstructor
    @Getter
    @Setter
    public static class PlayBack implements Serializable{
        private long syllabusId;
        private String userName;

        @Builder
        public PlayBack(long syllabusId, String userName) {
            this.syllabusId = syllabusId;
            this.userName = userName;
        }
    }
}
