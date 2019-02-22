package com.huatu.tiku.course.web.controller.v6.practice;

import com.google.common.collect.Lists;
import com.huatu.common.SuccessMessage;
import com.huatu.springboot.web.version.mapping.annotation.ApiVersion;
import com.huatu.tiku.common.bean.user.UserSession;
import com.huatu.tiku.course.bean.practice.StudentQuestionMetaBo;
import com.huatu.tiku.course.bean.practice.StudentRankBo;
import com.huatu.tiku.springboot.users.support.Token;
import org.springframework.web.bind.annotation.*;

/**
 * Created by lijun on 2019/2/18
 */
@RestController
@RequestMapping("practice/student")
@ApiVersion("v6")
public class StudentController {

    /**
     * 用户提交答案
     */
    @PostMapping("{courseId}/putAnswer")
    public Object putAnswer(@Token UserSession userSession, @PathVariable Integer courseId,
                            @RequestParam String answer, @RequestParam Integer time) {
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
    public Object getQuestionRankInfo(@PathVariable Long roomId, @PathVariable Integer questionId) {
        return Lists.newArrayList(StudentRankBo.builder().build());
    }

    /**
     * 获取用户个人排名信息
     */
    @GetMapping("{roomId}/userRankInfo")
    public Object getUserRankInfo(@Token UserSession userSession) {
        return StudentRankBo.builder().build();
    }
}
