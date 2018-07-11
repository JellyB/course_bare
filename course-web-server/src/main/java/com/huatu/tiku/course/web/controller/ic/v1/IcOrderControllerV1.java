package com.huatu.tiku.course.web.controller.ic.v1;

import com.huatu.common.utils.collection.HashMapBuilder;
import com.huatu.springboot.web.version.mapping.annotation.ApiVersion;
import com.huatu.tiku.common.bean.user.UserSession;
import com.huatu.tiku.course.netschool.api.v3.AddressServiceV3;
import com.huatu.tiku.course.netschool.api.v3.PromoteCoreServiceV3;
import com.huatu.tiku.course.spring.conf.aspect.mapParam.LocalMapParam;
import com.huatu.tiku.course.spring.conf.aspect.mapParam.LocalMapParamHandler;
import com.huatu.tiku.course.util.RequestUtil;
import com.huatu.tiku.course.util.ResponseUtil;
import com.huatu.tiku.springboot.users.support.Token;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

/**
 * 课程 - 订单接口
 * Created by lijun on 2018/7/10
 */
@RestController
@RequestMapping("ic/order")
@ApiVersion("v1")
public class IcOrderControllerV1 {

    @Autowired
    private AddressServiceV3 addressServiceV3;

    @Autowired
    private PromoteCoreServiceV3 promoteCoreServiceV3;

    /**
     * 获取某个用户的 地址列表
     */
    @GetMapping("addressList")
    public Object listByUserInfo(
            @Token UserSession userSession
    ) {
        Map<String, Object> params = HashMapBuilder.<String, Object>newBuilder()
                .put("action", "getAddress")
                .put("username", userSession.getUname())
                .build();
        return ResponseUtil.build(addressServiceV3.findAddressList(RequestUtil.encrypt(params)), true);
    }

    /**
     * 创建订单
     */
    @LocalMapParam
    @PostMapping("/create")
    public Object createOrder(
            @RequestParam String addressid,
            @RequestParam int rid,
            @RequestParam(required = false, defaultValue = "0") String fromuser,
            @RequestParam String tjCode,
            @RequestParam(required = false) String FreeCardID
    ) {
        HashMap<String, Object> map = LocalMapParamHandler.get();
        int terminal = (int) map.getOrDefault("terminal", 0);
        map.put("source", (terminal == 2 || terminal == 5) ? 'I' : 'A');//不是ios，就传android
        return ResponseUtil.build(promoteCoreServiceV3.createOrder(RequestUtil.encrypt(map)), true);
    }
}
