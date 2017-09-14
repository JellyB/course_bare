package com.huatu.tiku.course.netschool.api;

/**
 * @author hanchao
 * @date 2017/9/13 16:29
 */

import com.huatu.tiku.course.netschool.bean.NetSchoolResponse;
import org.springframework.cloud.netflix.feign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

@FeignClient(value = "course-service")
public interface CouponServiceV3 {
    /**
     *
     * @param div 设备类型 0，安卓  1，IOS
     * @return
     */
    @GetMapping("/v3/mycount/GetVoucher.php")
    NetSchoolResponse findCouponList(@RequestParam int div);

}
