package com.huatu.tiku.course.service.v1;

import com.huatu.tiku.course.consts.ReportMsg;

import java.util.Map;

/**
 * 描述：数据上报 service
 *
 * @author biguodong
 * Create time 2019-02-22 上午11:16
 **/
public interface ProcessReportService {
    String SYLLABUS_ID = "syllabusId";
    String EXPIRATION = "expiration";
    int TYPE_LIVE = 1;
    int TYPE_PLAYBACK = 2;
    /**
     * 数据上报接口
     * @param params
     * @return
     */
    Object liveReport(Map<String,Object> params);


    /**
     * 录播数据回放
     * @param params
     * @return
     */
    Object playBackReport(Map<String,Object> params);

    /**
     * 处理数据上报信息
     * @param reportMsg
     * @return
     */
    Object dealReportMsg(ReportMsg reportMsg);
}
