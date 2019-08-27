package com.huatu.tiku.course.service.v7.impl;

import com.alibaba.fastjson.JSONObject;
import com.huatu.common.exception.BizException;
import com.huatu.tiku.course.bean.vo.LiveRecordInfo;
import com.huatu.tiku.course.bean.vo.LiveRecordInfoWithUserInfo;
import com.huatu.tiku.course.consts.RabbitMqConstants;
import com.huatu.tiku.course.service.manager.CourseExercisesProcessLogManager;
import com.huatu.tiku.course.service.v7.UserCourseBizV7Service;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;


/**
 * 描述：
 *
 * @author biguodong
 * Create time 2019-03-22 9:50 PM
 **/
@Service
@Slf4j
public class UserCourseBizServiceImpl implements UserCourseBizV7Service {


    @Autowired
    private RabbitTemplate rabbitTemplate;

    @Autowired
    private CourseExercisesProcessLogManager courseExercisesProcessLogManager;



    /**
     * 直播数据上报处理
     *
     * @param userId
     * @param userName
     * @param subject
     * @param terminal
     * @param liveRecordInfo
     * @param cv
     * @throws BizException
     */
    @Override
    public void dealLiveReport(int userId, String userName, int subject, int terminal, String cv, LiveRecordInfo liveRecordInfo) throws BizException {



        LiveRecordInfoWithUserInfo liveRecordInfoWithUserId = LiveRecordInfoWithUserInfo
                .builder()
                .subject(subject)
                .terminal(terminal)
                .userName(userName)
                .cv(cv)
                .userId(userId)
                .liveRecordInfo(liveRecordInfo).build();
        log.debug("学员直播上报数据v7:{}", JSONObject.toJSONString(liveRecordInfoWithUserId));
        rabbitTemplate.convertAndSend("", RabbitMqConstants.COURSE_LIVE_REPORT_LOG, JSONObject.toJSONString(liveRecordInfoWithUserId));
    }

    /**
     * 课后作业全部已读 行测、申论
     *
     * @param userId
     * @param type
     * @param uName
     * @throws BizException
     */
    @Override
    public void allReadByType(long userId, String type, String uName) throws BizException {
        return;
    }


    /**
     * 获取行测、申论课后作业列表
     *
     * @param userId
     * @param type
     * @param page
     * @param size
     * @return
     * @throws BizException
     */
    @Override
    public Object courseWorkList(long userId, String type, int page, int size) throws BizException {
        return null;
    }
}
