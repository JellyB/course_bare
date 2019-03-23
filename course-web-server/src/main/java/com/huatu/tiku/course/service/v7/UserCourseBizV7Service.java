package com.huatu.tiku.course.service.v7;

import com.huatu.common.exception.BizException;
import com.huatu.tiku.course.bean.vo.LiveRecordInfo;

/**
 * 描述：
 *
 * @author biguodong
 * Create time 2019-03-22 9:50 PM
 **/
public interface UserCourseBizV7Service {

    /**
     * 直播数据上报处理
     * @param userId
     * @param liveRecordInfo
     * @param terminal
     * @param subject
     * @throws BizException
     */
    void dealLiveReport(int userId, String userName, int subject, int terminal, String cv, LiveRecordInfo liveRecordInfo) throws BizException;

}
