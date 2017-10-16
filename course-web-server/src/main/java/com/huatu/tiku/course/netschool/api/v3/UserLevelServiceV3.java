package com.huatu.tiku.course.netschool.api.v3;

import com.huatu.tiku.course.bean.NetSchoolResponse;
import org.springframework.cloud.netflix.feign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.Map;

/**
 * @author hanchao
 * @date 2017/10/16 13:44
 */
@FeignClient(value = "course-service")
public interface UserLevelServiceV3 {
    @GetMapping("/v3/mycount/myLevel.php")
    NetSchoolResponse getUserLevel(@RequestParam Map<String,Object> params);

    @GetMapping("/v3/mycount/levelDetail.php")
    NetSchoolResponse getLevelSettings();
}
