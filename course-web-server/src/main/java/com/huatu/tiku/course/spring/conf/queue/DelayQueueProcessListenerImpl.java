package com.huatu.tiku.course.spring.conf.queue;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

/**
 * 描述：
 *
 * @author biguodong
 * Create time 2019-02-28 6:33 PM
 **/
@Service
@Slf4j
public class DelayQueueProcessListenerImpl implements DelayQueueProcessListener {

    @Override
    public void ackCallback(Message message) {
        log.info("ackCallback:{}", message);
    }

    @Override
    public void peekCallback(Message message) {
        log.info("peekCallback:{}", message);
    }

    @Override
    public void pushCallback(Message message) {
        log.info("pushCallback:{}", message);
    }
}
