package com.huatu.tiku.course.service.cache;

/**
 * Created by lijun on 2018/7/5
 */
public final class OrderCacheKey {

    /**
     * 立即购买 按钮
     */
    public static String orderPrevInfo(){
        return "_order:prevInfo:qps";
    }

}
