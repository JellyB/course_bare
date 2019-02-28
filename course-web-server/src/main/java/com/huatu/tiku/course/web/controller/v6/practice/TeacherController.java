package com.huatu.tiku.course.web.controller.v6.practice;

import com.huatu.common.SuccessMessage;
import com.huatu.springboot.web.version.mapping.annotation.ApiVersion;
import com.huatu.tiku.course.bean.practice.QuestionMetaBo;
import com.huatu.tiku.course.service.v1.practice.TeacherService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.concurrent.ExecutionException;

/**
 * Created by lijun on 2019/2/18
 */
@RestController
@RequestMapping("practice/teacher")
@ApiVersion("v6")
@RequiredArgsConstructor
public class TeacherController {

    final TeacherService teacherService;

    /**
     * 根据 roomId 获取试题详情
     */
    @GetMapping("{roomId}/getQuestionInfo")
    public Object getQuestionInfoByRoomId(@PathVariable Long roomId) throws ExecutionException, InterruptedException {
        return teacherService.getQuestionInfoByRoomId(roomId);
    }

    /**
     * 点击练一下
     * 1.更新各个试题绑定开始考试时间
     */
    @PutMapping("{roomId}/{questionId}/practice")
    public Object putQuestionPractice(@PathVariable Long roomId, @PathVariable Long questionId, @RequestParam Integer practiceTime) {
        teacherService.saveQuestionPracticeInfo(roomId, questionId, practiceTime);
        return SuccessMessage.create();
    }

    /**
     * 获取答题情况
     */
    @GetMapping("{roomId}/{questionId}/questionStatistics")
    public Object getQuestionStatistics(@PathVariable Long roomId, @PathVariable Integer questionId) {
        return new QuestionMetaBo();
    }

    /**
     * 获取试题排名信息
     */
    @GetMapping("{roomId}/questionRankInfo")
    public Object getQuestionRankInfo(
            @PathVariable Long roomId,
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int pageSize) {

        return null;
    }
}
