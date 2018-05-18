package com.huatu.tiku.course.netschool.api.v4;

import com.huatu.tiku.course.bean.NetSchoolResponse;
import org.springframework.cloud.netflix.feign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

/**
 * Created by lijun on 2018/5/18
 */
@FeignClient("o-course-service")
public interface EvaluateServiceV4 {

    @GetMapping("/lumenapi/v4/app/lession/evaluate")
    NetSchoolResponse lessionEvaluate(@RequestParam("lessionId") int lessionId, @RequestParam("userName") String userName);
}
