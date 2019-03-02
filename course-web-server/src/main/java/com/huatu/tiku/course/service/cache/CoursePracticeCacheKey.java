package com.huatu.tiku.course.service.cache;

import java.util.concurrent.TimeUnit;

/**
 * key 前缀 course-web-server.
 * Created by lijun on 2019/2/27
 */
public final class CoursePracticeCacheKey {

    /**
     * 获取用户个人信息统计
     *
     * @param userId   用户ID
     * @param courseId 课程ID
     * @return 用户个人信息统计 key
     */
    public static String userMetaKey(Integer userId, Long courseId) {
        return "course:practice:userMeta:" + userId + ":" + courseId;
    }

    /**
     * 获取房间答题信息统计
     *
     * @param roomId 获取用户答题信息统计
     * @return 房间答题信息统计
     */
    public static String roomRankKey(Long roomId) {
        return "course:practice:roomRank:" + roomId;
    }

    /**
     * 房间中已经练习试题
     */
    public static String roomPractedQuestionNumKey(Long roomId) {
        return "course:practice:roomPracticedQuestionNumKey:" + roomId;
    }

    /**
     * 获取试题统计信息key
     *
     * @param roomId     房间ID
     * @param questionId 试题信息
     * @return 获取试题统计信息key
     */
    public static String questionMetaKey(Long roomId, Long questionId) {
        return "course:practice:questionMeta:" + questionId + ":" + roomId;
    }

    /**
     * 获取一次听课答题中默认key 失效时间
     */
    public static Integer getDefaultKeyTTL() {
        return 7;
    }

    public static TimeUnit getDefaultTimeUnit() {
        return TimeUnit.DAYS;
    }
}
