package com.huatu.tiku.course.web.controller.v6;

import com.huatu.springboot.web.version.mapping.annotation.ApiVersion;
import com.huatu.tiku.common.bean.user.UserSession;
import com.huatu.tiku.course.netschool.api.v6.OrderServiceV6;
import com.huatu.tiku.course.spring.conf.aspect.mapParam.LocalMapParam;
import com.huatu.tiku.course.spring.conf.aspect.mapParam.LocalMapParamHandler;
import com.huatu.tiku.course.util.ResponseUtil;
import com.huatu.tiku.springboot.users.support.Token;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

/**
 * 描述：
 *
 * @author biguodong
 * Create time 2019-10-28 10:17 AM
 **/

@RestController
@RequestMapping(value = "/order")
@ApiVersion("/v6")
@Slf4j
public class OrderControllerV6 {

    @Autowired
    private OrderServiceV6 orderService;


    /**
     * 0 元下单
     * @param userSession
     * @param terminal
     * @param cv
     * @param classId
     * @param pageSource
     * @param source
     * @return
     */
    @LocalMapParam
    @PostMapping(value = "zero")
    public Object zeroOrder(@Token UserSession userSession,
                            @RequestHeader int terminal,
                            @RequestHeader String cv,
                            @RequestParam(value = "classId") int classId,
                            @RequestParam(value = "pageSource") String pageSource,
                            @RequestParam(value = "source") int source){
        Map<String,Object> params = LocalMapParamHandler.get();
        return ResponseUtil.build(orderService.zeroOrder(params));
    }


    /**
     * 订单--下单支付
     * @param userSession
     * @param terminal
     * @param cv
     * @param classId
     * @param source
     * @param actualPrice
     * @param addressId
     * @param code
     * @param p
     * @param pageSource
     * @param payment
     * @param tjCode
     * @param unionData
     * @param wxH5
     * @return
     */
    @LocalMapParam
    @PostMapping(value = "create")
    public Object createOrder(@Token UserSession userSession,
                            @RequestHeader int terminal,
                            @RequestHeader String cv,
                            @RequestParam(value = "classId") int classId,
                            @RequestParam(value = "source", defaultValue = "0") int source,
                            @RequestParam(value = "actualPrice") String actualPrice,
                            @RequestParam(value = "addressId", defaultValue = "0") int addressId,
                            @RequestParam(value = "code", defaultValue = "0") int code,
                            @RequestParam(value = "p") String p,
                            @RequestParam(value = "pageSource", defaultValue = "") String pageSource,
                            @RequestParam(value = "payment", defaultValue = "") String payment,
                            @RequestParam(value = "tjCode", defaultValue = "0") int tjCode,
                            @RequestParam(value = "unionData", defaultValue = "") String unionData,
                            @RequestParam(value = "wxH5", defaultValue = "0") int wxH5){
        Map<String,Object> params = LocalMapParamHandler.get();
        return ResponseUtil.build(orderService.createOrder(params));
    }


    /**
     * 订单--是否支付成功
     * @param userSession
     * @param terminal
     * @param cv
     * @param orderNum
     * @return
     */
    @LocalMapParam
    @GetMapping(value = "status")
    public Object orderStatus(@Token UserSession userSession,
                            @RequestHeader int terminal,
                            @RequestHeader String cv,
                            @RequestParam(value = "orderNum") int orderNum){
        Map<String,Object> params = LocalMapParamHandler.get();
        return ResponseUtil.build(orderService.orderStatus(params));
    }


    /**
     * 订单--继续支付
     * @param userSession
     * @param terminal
     * @param cv
     * @param code
     * @param orderId
     * @param payment
     * @param source
     * @param wxH5
     * @return
     */
    @LocalMapParam
    @PostMapping(value = "continue")
    public Object continuePay(@Token UserSession userSession,
                            @RequestHeader int terminal,
                            @RequestHeader String cv,
                            @RequestParam(value = "code") String code,
                            @RequestParam(value = "orderId") int orderId,
                            @RequestParam(value = "payment") int payment,
                            @RequestParam(value = "source") int source,
                            @RequestParam(value = "wxH5") int wxH5){
        Map<String,Object> params = LocalMapParamHandler.get();
        return ResponseUtil.build(orderService.continuePay(params));
    }


    /**
     * 订单--预下单接口
     * @param userSession
     * @param terminal
     * @param cv
     * @param classId
     * @param pageSource
     * @return
     */
    @LocalMapParam
    @GetMapping(value = "place")
    public Object placeOrder(@Token UserSession userSession,
                            @RequestHeader int terminal,
                            @RequestHeader String cv,
                            @RequestParam(value = "classId") int classId,
                            @RequestParam(value = "pageSource") String pageSource){
        Map<String,Object> params = LocalMapParamHandler.get();
        return ResponseUtil.build(orderService.placeOrder(params));
    }
}
