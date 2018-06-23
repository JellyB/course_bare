package com.huatu.tiku.course.ztk.api.v1.question;

import com.huatu.tiku.course.ztk.api.fail.question.QuestionServiceV1Fallback;
import org.springframework.cloud.netflix.feign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

/**
 * Created by lijun on 2018/6/20
 */
@FeignClient(value = "ztk-service",fallback = QuestionServiceV1Fallback.class,path = "/q")
public interface QuestionServiceV1 {

    @GetMapping(value = "/v3/questions/batch")
    Object listQuestionByIds(@RequestParam("ids") String questionIds);
}
