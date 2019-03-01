package com.huatu.tiku.course.web.controller.v6.practice;

import com.huatu.common.SuccessMessage;
import com.huatu.springboot.web.version.mapping.annotation.ApiVersion;
import com.huatu.tiku.common.bean.user.UserSession;
import com.huatu.tiku.course.bean.practice.StudentQuestionMetaBo;
import com.huatu.tiku.course.service.v1.practice.StudentService;
import com.huatu.tiku.springboot.users.support.Token;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

/**
 * Created by lijun on 2019/2/18
 */
@RestController
@RequestMapping("practice/student")
@RequiredArgsConstructor
@ApiVersion("v6")
public class StudentController {

    final StudentService studentService;

    /**
     * 用户提交答案
     */
    @PutMapping("{roomId}/{questionId}/putAnswer")
    public Object putAnswer(@Token UserSession userSession,
                            @PathVariable Long roomId, @PathVariable Long questionId,
                            @RequestParam Long courseId, @RequestParam String answer, @RequestParam Integer time) {
        studentService.putAnswer(roomId, courseId, userSession.getId(), userSession.getNick(), questionId, answer, time);
        return SuccessMessage.create();
    }

    /**
     * 获取答题情况
     */
    @GetMapping("{roomId}/{questionId}/questionStatistics")
    public Object getQuestionStatistics(@PathVariable Long roomId, @PathVariable Integer questionId) {
        return StudentQuestionMetaBo.builder().build();
    }

    /**
     * 获取排名信息
     */
    @GetMapping("{roomId}/questionRankInfo")
    public Object getQuestionRankInfo(@PathVariable Long roomId) {
        return studentService.listPracticeRoomRankUser(roomId, 0, 10);
    }

    /**
     * 获取用户个人排名信息
     */
    @GetMapping("{roomId}/userRankInfo")
    public Object getUserRankInfo(@Token UserSession userSession) {

        return null;
    }
}
