package com.huatu.tiku.course.service.v6.impl;

import com.huatu.common.exception.BizException;
import com.huatu.tiku.course.service.v6.CourseWorkService;
import com.huatu.tiku.course.spring.conf.queue.Payload;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

/**
 * 描述：
 *
 * @author biguodong
 * Create time 2019-02-28 5:56 PM
 **/

@Slf4j
@Service
public class CourseWorkServiceImpl implements CourseWorkService {


    @Override
    public void ack(Object object) throws BizException {
        if(object instanceof Payload){
            Payload payload = (Payload) object;
            log.info(">>>>>>>>>>>:{}", payload.getSyllabusId());
        }
    }
}
