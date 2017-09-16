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
    // 同样地址的，是因为action不一样，这里写多次，是为了将不同职责的区分开，有调整或者需要做fallback的话方便


    /**
     * 下单页面获取信息（action固定传placeOrder）
     * @param p (action  username rid )
     * @return
     */
    @PostMapping("/v3/order/order.php")
    NetSchoolResponse getPrevInfo(@RequestParam("p") String p);

    /**
     * 下单接口
     * @param p
     * @return
     */
    @PostMapping("/v3/order/order.php")
    NetSchoolResponse getOrderDetail(@RequestParam("p") String p);

    /**
     * 下单接口
     * @param p
     * @return
     */
    @PostMapping("/v3/order/order.php")
    NetSchoolResponse createOrder(@RequestParam("p") String p);

    /**
     * 取消   接口
     * @param p
     * @return
     */
    @PostMapping("/v3/order/order.php")
    NetSchoolResponse cancelOrder(@RequestParam("p") String p);


}
