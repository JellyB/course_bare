package com.huatu.tiku.course.netschool.api.v4;

import com.huatu.tiku.course.bean.NetSchoolResponse;
import org.springframework.cloud.netflix.feign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.HashMap;

/**
 * Created by lijun on 2018/5/18
 */
@FeignClient(value = "o-course-service", path = "/lumenapi/v4/app", fallback = AppServiceV4.class)
public interface AppServiceV4 {

    @GetMapping("/lession/evaluate")
    NetSchoolResponse lessionEvaluate(
            @RequestParam("lessionId") int lessionId,
            @RequestParam("userName") String userName);

    @GetMapping("/lession/token")
    NetSchoolResponse lessionToken(
            @RequestParam("bjyRoomId") String bjyRoomId,
            @RequestParam("bjySessionId") String bjySessionId,
            @RequestParam("videoId") String videoId);

    /**
     * 合集查询
     */
    @GetMapping("/collectionclasses/collection_classes")
    NetSchoolResponse collectionClasses(@RequestParam HashMap map);

    /**
     * 专栏
     */
    @GetMapping(value = "/netclasses/specialcolumn")
    NetSchoolResponse specialColumn();
}
