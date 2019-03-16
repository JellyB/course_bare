package com.huatu.tiku.course.service;

import com.google.common.util.concurrent.RateLimiter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.concurrent.TimeUnit;

/**
 * 描述：
 *
 * @author biguodong
 * Create time 2019-03-16 6:18 PM
 **/

@Service
@Slf4j
public class AccessLimitService {

    RateLimiter rateLimiter = RateLimiter.create(20);


    public boolean tryAccess(){
        return rateLimiter.tryAcquire(1500, TimeUnit.MILLISECONDS);
    }
}
