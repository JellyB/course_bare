package com.huatu.tiku.course.consts;

/**
 * 描述：
 *
 * @author biguodong
 * Create time 2019-03-07 8:11 PM
 **/
public class RabbitMqConstants {

    /**
     * 课后保存答题卡队列
     */
    public static final String COURSE_WORK_SUBMIT_CARD_INFO = "course_work_submit_card_info";

    /**
     * 直播回放待处理队列
     */
    public static final String PLAY_BACK_DEAL_INFO = "play_back_deal_info";
    
    /**
     * 阶段测试提交答题卡队列
     */
    public static final String PERIOD_TEST_SUBMIT_CARD_INFO = "period_test_submit_card_info";

    /**
     * 学员直播上课队列
     */
    public static final String COURSE_LIVE_REPORT_LOG = "course_live_report_log";

    /**
     * 课后作业数据处理队列
     */
    public static final String COURSE_EXERCISES_PROCESS_LOG_CORRECT_QUEUE = "course_exercises_process_log_correct";
    
    /**
     * 直播随堂练信息持久化
     */
    public static final String COURSE_PRACTICE_SAVE_DB_QUEUE = "course_practice_save_db_queue";
    
    /**
     * 录播随堂练信息持久化
     */
    public static final String COURSE_BREAKPOINT_PRACTICE_SAVE_DB_QUEUE = "course_breakpoint_practice_save_db_queue";

}
