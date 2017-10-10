package com.huatu.tiku.course.netschool.api.fall;

import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;

import java.util.concurrent.TimeUnit;

/**
 * @author hanchao
 * @date 2017/10/2 15:59
 */
public class FallbackCache {
    private static final Cache<Object,Object> FALLBACK_CACHE  = CacheBuilder.newBuilder()
            .expireAfterAccess(3, TimeUnit.DAYS)
            .initialCapacity(100)
            .maximumSize(2000)
            .concurrencyLevel(10)
            .build();

    /**
     * 不能返回基本类型,否则npe
     * @param key
     * @param <T>
     * @return
     */
    public static <T> T get(Object key){
        return (T)FALLBACK_CACHE.getIfPresent(key);
    }

    public static void put(Object key,Object value){
        FALLBACK_CACHE.put(key,value);
    }
}
