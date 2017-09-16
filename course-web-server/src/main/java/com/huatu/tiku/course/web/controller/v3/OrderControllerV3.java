package com.huatu.tiku.course.web.controller.v3;

import com.google.common.collect.Maps;
import com.huatu.common.exception.BizException;
import com.huatu.common.utils.collection.HashMapBuilder;
import com.huatu.tiku.course.netschool.api.v3.OrderServiceV3;
import com.huatu.tiku.course.util.RequestUtil;
import com.huatu.tiku.course.util.ResponseUtil;
import com.huatu.tiku.springboot.users.bean.UserSession;
import com.huatu.tiku.springboot.users.support.Token;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

/**
 * @author hanchao
 * @date 2017/9/14 16:31
 */
@RestController
@RequestMapping("/v3/orders")
public class OrderControllerV3 {
    @Autowired
    private OrderServiceV3 orderServiceV3;
    /**
     * 下单页面相关信息（结算信息，收货地址）
     * @param rid
     * @param userSession
     * @return
     */
    @GetMapping("/previnfo")
    public Object getPrevInfo(@RequestParam int rid,
                              @Token UserSession userSession) throws BizException {
        Map<String,Object> params = Maps.newHashMap();
        params.put("rid",rid);
        params.put("action","placeOrder");
        params.put("username",userSession.getUname());
        return ResponseUtil.build(orderServiceV3.getPrevInfo(RequestUtil.encrypt(params)),true);
    }

    /**
     * 创建订单
     * @param addressid
     * @param rid
     * @param fromuser
     * @param tjCode
     * @param userSession
     * @return
     */
    @PostMapping("/create")
    public Object createOrder(@RequestParam String addressid,
                              @RequestParam int rid,
                              @RequestParam String fromuser,
                              @RequestParam String tjCode,
                              @RequestHeader int terminal,
                              @Token UserSession userSession) throws BizException {
        Map<String,Object> params = Maps.newHashMap();
        params.put("action","createOrder");
        params.put("addressid",addressid);
        params.put("fromuser",fromuser);
        params.put("rid",rid);
        params.put("source",(terminal == 4 || terminal == 5)?'I':'A');//不是ios，就传android
        params.put("tjCode",tjCode);
        params.put("username",userSession.getUname());

        return ResponseUtil.build(orderServiceV3.createOrder(RequestUtil.encrypt(params)),true);
    }

    /**
     * 订单详情
     * @param orderNo
     * @return
     * @throws BizException
     */
    @PostMapping("/{orderNo}")
    public Object getOrderDetail(@PathVariable String orderNo,
                                 @RequestParam String type) throws BizException {
        Map<String,Object> params = HashMapBuilder.<String,Object>newBuilder()
                .put("action","orderDetail")
                .put("ordernum",orderNo)
                .put("type",type)
                .build();
        return ResponseUtil.build(orderServiceV3.getOrderDetail(RequestUtil.encrypt(params)),true);
    }


    /**
     * 取消订单
     * @param orderNo
     * @return
     * @throws BizException
     */
    @PostMapping("/{orderNo}/cancel")
    public Object cancelOrder(@PathVariable String orderNo) throws BizException {
        Map<String,Object> params = HashMapBuilder.<String,Object>newBuilder()
                .put("action","cancel")
                .put("ordernum",orderNo)
                .build();
        return ResponseUtil.build(orderServiceV3.createOrder(RequestUtil.encrypt(params)),true);
    }



}
