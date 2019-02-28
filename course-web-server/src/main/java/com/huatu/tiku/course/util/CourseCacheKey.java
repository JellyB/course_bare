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
    private static final String COURSE_ANALYSIS_V6 = "course_analysis_v6$%s";
    private static final String COURSE_MINE_V6 = "course_mine_v6$%s";
    /**
     * 直播列表
     */
    private static final String COURSE_LIST_V2 = "live_list$%s";
    private static final String COURSE_LIST_V3 = "live_list_v3$%s";

    private static final String REWARD_RECORD = "reward_record$%s$%s";
    /**
     * 日历详情
     */
    private static final String CALENDAR_DETAIL_V6 = "calendar_detail_v6$%s";
    private static final String CALENDAR_LEARN_V6 = "calendar_learn_v6$%s";


    /**
     * 直播录播数据上报key
     */
    private static final String PROCESS_REPORT_DELAY_QUEUE = "process_report_delay";


    /**
     * IOS 内侧版本信息
     */
    public static final String IOS_AUDIT_VERSION = "ios_audit_versions";

    public static String courseDetailV2(Integer courseId){
        return String.format(COURSE_DETAIL_V2,courseId);
    }

    public static String courseDetailV3(Integer courseId){
        return String.format(COURSE_DETAIL_V3,courseId);
    }

    public static String courseAnalysisV6(String classId){ return String.format(COURSE_ANALYSIS_V6, classId); }

    public static String courseListV2(String sign){
        return String.format(COURSE_LIST_V2,sign);
    }

    public static String courseListV3(String sign){
        return String.format(COURSE_LIST_V3,sign);
    }

    public static String calendarDetailV6(String sign){
        return String.format(CALENDAR_DETAIL_V6,sign);
    }

    public static String calendarLearnV6(String sign){
        return String.format(CALENDAR_LEARN_V6, sign);
    }

    public static String courseMineV6(String sign){
        return String.format(COURSE_MINE_V6, sign);
    }

    public static String rewardRecord(String action,int uid){
        return String.format(REWARD_RECORD,action,uid);
    }

    public static String getProcessReportDelayQueue(){
        return PROCESS_REPORT_DELAY_QUEUE;
    }

}
