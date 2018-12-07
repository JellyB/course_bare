package com.huatu.tiku.course.netschool.api.v5;

import com.huatu.tiku.course.bean.NetSchoolResponse;
import org.springframework.cloud.netflix.feign.FeignClient;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

/**
 * 订单相关业务
 * Created by lijun on 2018/6/25
 */
@FeignClient(value = "o-course-service", path = "/lumenapi")
public interface OrderServiceV5 {

    /**
     * 获取订单物流信息
     */
    @GetMapping(value = "/v4/common/order/logistics")
    NetSchoolResponse getOrderLogistics(@RequestParam("orderId") int orderId);

    /**
     * 获取最后一条的物流信息
     */
    @GetMapping(value = "/v4/common/order/logistics?type=1")
    NetSchoolResponse getLastOrderLogistics(@RequestParam("orderId") int orderId);

    /**
     * 取消订单
     */
    @PutMapping(value = "/v4/common/order/cacel")
    NetSchoolResponse cancelOrder(@RequestParam("orderId") int orderId, @RequestParam("userName") String userName);

    /**
     * 删除订单
     */
    @DeleteMapping(value = "/v4/common/order/delete")
    NetSchoolResponse deleteOrder(@RequestParam Map<String, Object> params);

    /**
     * 查询订单详情
     */
    @GetMapping(value = "/v4/common/order/detail")
    NetSchoolResponse detail(@RequestParam Map<String, Object> params);

    /**
     * 查看小程序订单详情
     */
    @GetMapping(value = "/v4/wechat/collage/order_details")
    NetSchoolResponse detailWeChat(@RequestParam Map<String, Object> params);

    /**
     * 查询我的订单列表 - ZTK
     */
    @GetMapping(value = "/v4/common/order/list?isInterview=0")
    NetSchoolResponse userOrderListZTK(@RequestParam HashMap<String, Object> params);

    /**
     * 查询我的订单列表 - IC
     */
    @GetMapping(value = "/v4/common/order/list?isInterview=1")
    NetSchoolResponse userOrderListIC(@RequestParam HashMap<String, Object> params);

    /**
     * 估分送课
     */
    @PostMapping(value = "/v4/common/order/zero_order")
    NetSchoolResponse zeroOrder(@RequestParam Map<String, Object> params);

    /**
     * 用户是否已经有估分送课
     */
    @GetMapping(value = "/v4/common/order/has_free")
    NetSchoolResponse hasGetBigGiftOrder(@RequestParam Map<String, Object> params);

    /**
     * 拼团--正在拼团
     * @param params
     * @return
     */
    @GetMapping(value = "/v4/wechat/collage/collage_activity_all")
    NetSchoolResponse activityAll(@RequestParam Map<String, Object> params);
}
