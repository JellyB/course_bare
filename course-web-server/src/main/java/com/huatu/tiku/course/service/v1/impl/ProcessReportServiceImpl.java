package com.huatu.tiku.course.service.v1.impl;

import com.huatu.common.SuccessMessage;
import com.huatu.tiku.course.service.v1.ProcessReportService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Map;

/**
 * 描述：
 *
 * @author biguodong
 * Create time 2019-02-22 上午11:17
 **/

@Service
@Slf4j
public class ProcessReportServiceImpl implements ProcessReportService {

    @Autowired
    private RabbitTemplate rabbitTemplate;


    /**
     * 数据上报接口
     *
     * @param params
     * @return
     */
    @Override
    public Object report(Map<String, Object> params) {
        log.error("params:{}", params);
        return SuccessMessage.create();
    }
}
