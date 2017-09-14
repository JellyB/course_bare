package com.huatu.tiku.course.netschool.api.v3;

import com.huatu.tiku.course.netschool.bean.NetSchoolResponse;
import org.springframework.cloud.netflix.feign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

/**
 * @author hanchao
 * @date 2017/9/14 16:52
 */
@FeignClient(value = "course-service")
public interface OrderServiceV3 {
    /**
     * 下单页面获取信息（action固定传placeOrder）
     * @param p (action  username rid )
     * @return
     */
    @PostMapping("/v3/order/order.php")
    NetSchoolResponse getPrevInfo(@RequestParam("p") String p);

}
