package com.huatu.tiku.course.service.cache;

import org.springframework.data.redis.core.ValueOperations;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;

/**
 * Created by lijun on 2018/7/5
 */
@Component
public class OrderCacheQPS {

    private static final int DEFAULT_QPS = 10;

    @Resource(name = "redisTemplate")
    private ValueOperations<String, Integer> valueOperations;

    /**
     * 立即购买按钮
     */
    public int orderPreInfoQPS(){
        String key = OrderCacheKey.orderPrevInfo();
        return orderQPS(key);
    }


    /**
     * 订单 qps
     */
    private int orderQPS(String key) {
        Integer num = valueOperations.get(key);
        if (null != num) { //避免第一次 获取到 空值
            return num;
        } else {
            valueOperations.set(key, DEFAULT_QPS);
            return DEFAULT_QPS;
        }
    }
}
