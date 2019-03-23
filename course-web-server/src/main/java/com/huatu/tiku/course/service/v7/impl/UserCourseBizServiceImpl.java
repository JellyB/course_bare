package com.huatu.tiku.course.service.v7.impl;

import com.alibaba.fastjson.JSONObject;
import com.huatu.common.exception.BizException;
import com.huatu.tiku.course.bean.vo.LiveRecordInfo;
import com.huatu.tiku.course.bean.vo.LiveRecordInfoWithUserInfo;
import com.huatu.tiku.course.consts.RabbitMqConstants;
import com.huatu.tiku.course.netschool.api.v6.UserCourseServiceV6;
import com.huatu.tiku.course.service.v7.UserCourseBizV7Service;
import com.huatu.tiku.course.spring.conf.aspect.mapParam.LocalMapParamHandler;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Map;

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
    private UserCourseServiceV6 userCourseService;


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

        Map<String,Object> params = LocalMapParamHandler.get();
        params.put("userName", userName);
        params.put("syllabusId", liveRecordInfo.getSyllabusId());
        params.put("terminal", terminal);
        params.put("cv", cv);

        LiveRecordInfoWithUserInfo liveRecordInfoWithUserId = LiveRecordInfoWithUserInfo
                .builder()
                .subject(subject)
                .terminal(terminal)
                .userId(userId)
                .liveRecordInfo(liveRecordInfo).build();
        log.debug("学员直播上报数据v7:{}", JSONObject.toJSONString(liveRecordInfoWithUserId));
        userCourseService.saveLiveRecord(params);
        rabbitTemplate.convertAndSend("", RabbitMqConstants.COURSE_LIVE_REPORT_LOG, JSONObject.toJSONString(liveRecordInfoWithUserId));
    }


}
