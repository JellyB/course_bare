package com.huatu.tiku.course.service.v1.impl;

import com.alibaba.fastjson.JSONObject;
import com.huatu.common.SuccessMessage;
import com.huatu.tiku.course.bean.vo.RecordProcess;
import com.huatu.tiku.course.consts.RabbitMqConstants;
import com.huatu.tiku.course.service.v1.ProcessReportService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections.MapUtils;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;


import java.util.Map;

/**
 * 描述：
 * 1. 录播回放处理
 * 2. 处理后查询是否配置有课后作业
 * 3. 如果有配置课后作业，查询答题卡状态；
 * 4. 如果做完或者没有做，处理数据
 * @author biguodong
 * Create time 2019-02-22 上午11:17
 **/

@Service
@Slf4j
public class ProcessReportServiceImpl implements ProcessReportService {


    @Autowired
    private RabbitTemplate rabbitTemplate;

    @Override
    public Object playBackReport(Map<String, Object> params) {

        long syllabusId = MapUtils.getLong(params, "syllabusId");
        String userName = MapUtils.getString(params, "userName");
        log.info("学员录播或回放学习进度上报数据:{}", params);
        RecordProcess playBack = RecordProcess.builder().syllabusId(syllabusId).userName(userName).build();
        rabbitTemplate.convertAndSend("", RabbitMqConstants.PLAY_BACK_DEAL_INFO, JSONObject.toJSONString(playBack));
        return SuccessMessage.create();
    }

}
