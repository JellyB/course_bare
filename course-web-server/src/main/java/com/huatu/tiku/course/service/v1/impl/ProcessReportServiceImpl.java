package com.huatu.tiku.course.service.v1.impl;

import com.huatu.common.SuccessMessage;
import com.huatu.tiku.course.service.v1.ProcessReportService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;


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


    @Override
    public Object playBackReport(Map<String, Object> params) {
        return SuccessMessage.create();
    }
}
