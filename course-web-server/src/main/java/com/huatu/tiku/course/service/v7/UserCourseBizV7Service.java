package com.huatu.tiku.course.service.v7;

import com.huatu.common.exception.BizException;

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
     * @param syllabusId
     * @param bjyRoomId
     * @param courseWareId
     * @param classId
     * @param terminal
     * @param subject
     * @throws BizException
     */
    void dealLiveReport(int userId, long syllabusId, String bjyRoomId, long classId, long courseWareId, int subject, int terminal) throws BizException;

}
