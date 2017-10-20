package com.huatu.tiku.course.netschool.api.v3;

import com.huatu.common.spring.web.MediaType;
import com.huatu.tiku.course.bean.NetSchoolResponse;
import org.springframework.cloud.netflix.feign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;

import java.util.Map;

/**
 * @author hanchao
 * @date 2017/10/14 11:08
 */
@FeignClient("o-course-service")
public interface GoldChargeService {
    @PostMapping( value = "/v3/order/actionremark.php",consumes = MediaType.APPLICATION_FORM_URLENCODED_UTF8_VALUE)
    NetSchoolResponse chargeGold(Map<String,Object> params);
}
