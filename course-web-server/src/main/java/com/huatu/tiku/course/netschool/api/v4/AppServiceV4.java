package com.huatu.tiku.course.netschool.api.v4;

import com.huatu.tiku.course.bean.NetSchoolResponse;
import org.springframework.cloud.netflix.feign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

/**
 * Created by lijun on 2018/5/18
 */
@FeignClient(value = "o-course-service",path = "/lumenapi/v4/app")
public interface AppServiceV4 {

    @GetMapping("/lession/evaluate")
    NetSchoolResponse lessionEvaluate(
            @RequestParam("lessionId") int lessionId,
            @RequestParam("userName") String userName);

    @GetMapping("/lession/token")
    NetSchoolResponse lessionToken(
            @RequestParam int bjyRoomId,
            @RequestParam int bjySessionId,
            @RequestParam int videoId);

    @PostMapping("/collectionclasses/collection_classes")
    NetSchoolResponse collectionClasses(
            @RequestParam int page,
            @RequestParam int pageSize,
            @RequestParam int collectionId
    );
}
