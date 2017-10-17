package com.huatu.tiku.course.util;

/**
 * 直接使用的统一用Public,方法中使用的再private定义后使用，方便日后统一建立后台缓存更新系统
 * @author hanchao
 * @date 2017/9/13 10:50
 */
public class CourseCacheKey {
    /**
     * 缓存数据增加标记使用等
     */
    public static final String CACHE_FLAG = "_cache";
    public static final String TIMESTAMP_FLAG = "_timestamp";


    /**
     * 课程搜索配置条件
     */
    public static final String RECORDING_SETTINGS = "recording_settings";
    public static final String LIVE_SETTINGS = "live_settings";
    public static final String LEVEL_SETTINGS = "level_settings";

    /**
     * 下单失败的队列
     */
    public static final String ORDERS_QUEUE = "orders_queue";


    /**
     * 课程详情
     */
    private static final String COURSE_DETAIL_V2 = "course_detail$%s";
    private static final String COURSE_DETAIL_V3 = "course_detail_v3$%s";

    /**
     * 直播列表
     */
    private static final String COURSE_LIST_V2 = "live_list$%s";
    private static final String COURSE_LIST_V3 = "live_list_v3$%s";

    private static final String REWARD_RECORD = "reward_record$%s$%s";

    public static String courseDetailV2(Integer courseId){
        return String.format(COURSE_DETAIL_V2,courseId);
    }

    public static String courseDetailV3(Integer courseId){
        return String.format(COURSE_DETAIL_V3,courseId);
    }

    public static String courseListV2(String sign){
        return String.format(COURSE_LIST_V2,sign);
    }

    public static String courseListV3(String sign){
        return String.format(COURSE_LIST_V3,sign);
    }

    public static String rewardRecord(String action,int uid){
        return String.format(REWARD_RECORD,action,uid);
    }


}
