package com.huatu.tiku.course.service.v7.impl;

import com.huatu.common.exception.BizException;
import com.huatu.tiku.course.service.manager.CourseExercisesProcessLogManager;
import com.huatu.tiku.course.service.v7.UserCourseBizV7Service;
import com.huatu.tiku.entity.CourseExercisesProcessLog;
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

    /**
     * 直播数据上报处理
     *
     * @param userId
     * @param syllabusId
     * @param bjyRoomId
     * @param classId
     * @param courseWareId
     * @param subject
     * @param terminal
     * @throws BizException
     */
    @Override
    public void dealLiveReport(int userId, long syllabusId, String bjyRoomId, long classId, long courseWareId, int subject, int terminal) throws BizException {


        // todo 队列消费
    }
}
