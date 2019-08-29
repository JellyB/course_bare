package com.huatu.tiku.course.service.v7;

import com.huatu.common.exception.BizException;
import com.huatu.springboot.degrade.core.Degrade;
import com.huatu.tiku.course.bean.vo.LiveRecordInfo;

import java.util.Map;

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


    /**
     * 课后作业全部已读 行测、申论
     * @param userId
     * @param type
     * @param uName
     * @throws BizException
     */
    int allReadByType(long userId, String type, String uName) throws BizException;

    /**
     * 单条已读
     * @param userId
     * @param type
     * @param syllabusId
     * @return
     * @throws BizException
     */
     Object readyOneCourseWork(int userId, int type, long syllabusId) throws BizException;


    /**
     * 获取行测、申论课后作业列表
     * @param userId
     * @param type
     * @param page
     * @param size
     * @return
     * @throws BizException
     */
    Object courseWorkList(long userId, int type, int page, int size) throws BizException;


    /**
     * 课后作业未读数
     * @param userId
     * @param userName
     * @return
     * @throws BizException
     */
    Map<String, Integer> getCountByType(long userId, String userName) throws BizException;
}
