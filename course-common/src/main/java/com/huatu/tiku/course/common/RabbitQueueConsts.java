package com.huatu.tiku.course.common;

/**
 * @author hanchao
 * @date 2017/9/4 17:27
 */
public class RabbitQueueConsts {
    /**
     * 免费课赠送
     */
    @Deprecated
    public static final String QUEUE_SEND_FREE_COURSE = "send_free_course_queue";
    /**
     * 用户昵称更新
     */
    public static final String QUEUE_USER_NICK_UPDATE = "user_nick_update_queue";
    /**
     * 每日任务
     */
    public static final String QUEUE_REWARD_ACTION = "reward_action_queue";
}
