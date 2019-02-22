package com.huatu.tiku.course.service.v1;

import java.util.Map;

/**
 * 描述：数据上报 service
 *
 * @author biguodong
 * Create time 2019-02-22 上午11:16
 **/
public interface ProcessReportService {
    /**
     * 数据上报接口
     * @param params
     * @return
     */
    Object report(Map<String,Object> params);
}
