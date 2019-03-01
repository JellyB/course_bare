package com.huatu.tiku.course.service.v1.practice;

import com.huatu.tiku.course.bean.practice.TeacherQuestionBo;

import java.util.List;
import java.util.concurrent.ExecutionException;

/**
 * Created by lijun on 2019/2/21
 */
public interface TeacherService {

    /**
     * 通过roomId 获取绑定的试题信息
     */
    List<TeacherQuestionBo> getQuestionInfoByRoomId(Long roomId) throws ExecutionException, InterruptedException;

    /**
     * 存储 试题练习信息
     */
    void saveQuestionPracticeInfo(Long roomId, Long questionId, Integer practiceTime);
}
