package com.huatu.tiku.course.web.controller.v5;

import com.huatu.springboot.web.version.mapping.annotation.ApiVersion;
import com.huatu.tiku.common.bean.user.UserSession;
import com.huatu.tiku.course.netschool.api.v5.OrderServiceV5;
import com.huatu.tiku.course.util.ResponseUtil;
import com.huatu.tiku.springboot.users.support.Token;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

/**
 * Created by lijun on 2018/6/25
 */
@Slf4j
@RestController
@RequestMapping("/order")
@ApiVersion("v5")
public class OrderControllerV5 {

    @Autowired
    private OrderServiceV5 orderService;

    /**
     * 获取订单物流信息
     */
    @GetMapping("/{orderId}/orderLogistics")
    public Object getOrderLogistics(@PathVariable("orderId") int orderId) {
        return ResponseUtil.build(orderService.getOrderLogistics(orderId));
    }

    /**
     * 获取最后一条的物流信息
     */
    @GetMapping("/{orderId}/lastOrderLogistics")
    public Object getLastOrderLogistics(@PathVariable("orderId") int orderId) {
        return ResponseUtil.build(orderService.getLastOrderLogistics(orderId));
    }

    /**
     * 取消订单
     */
    @PutMapping("{orderId}/cancelOrder")
    public Object cancelOrder(
            @RequestHeader(required = false) int terminal,
            @RequestHeader(required = false) String cv,
            @Token UserSession userSession,
            @PathVariable("orderId") int orderId
    ) {
        log.warn("12$${}$${}$${}$${}$${}$${}", orderId, userSession.getId(), userSession.getUname(), String.valueOf(System.currentTimeMillis()), cv, terminal);
        return ResponseUtil.build(orderService.cancelOrder(orderId, userSession.getUname()));
    }

    /**
     * 删除订单
     */
    @DeleteMapping("{orderId}")
    public Object deleteOrder(
            @Token UserSession userSession,
            @PathVariable("orderId") int orderId
    ) {
        return ResponseUtil.build(orderService.deleteOrder(orderId, userSession.getUname()));
    }

    /**
     * 获取订单详情
     */
    @GetMapping("{orderId}")
    public Object detail(
            @PathVariable("orderId") int orderId
    ) {
        return ResponseUtil.build(orderService.detail(orderId));
    }
}
