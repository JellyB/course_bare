package com.huatu.tiku.course.service.v1.practice;

import com.huatu.tiku.course.bean.practice.PracticeRoomRankUserBo;

import java.util.List;

/**
 * Created by lijun on 2019/2/27
 */
public interface StudentService {

    /**
     * 用户保存答案
     *
     * @param userId     用户ID
     * @param userName   用户名称
     * @param courseId   课程ID
     * @param questionId 试题ID
     * @param answer     用户答案
     * @param time       耗时
     */
    void putAnswer(Long roomId, Long courseId, Integer userId, String userName, Long questionId, String answer, Integer time);

    /**
     * 获取排名信息
     *
     * @param roomId 房间ID
     * @param start  开始下标
     * @param end    结束下标
     */
    List<PracticeRoomRankUserBo> listPracticeRoomRankUser(Long roomId, Integer start, Integer end);
}
