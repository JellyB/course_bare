package com.huatu.tiku.course.web.controller.v3;

import com.google.common.collect.Maps;
import com.huatu.common.exception.BizException;
import com.huatu.tiku.course.netschool.api.v3.UserAccountServiceV3;
import com.huatu.tiku.course.util.RequestUtil;
import com.huatu.tiku.course.util.ResponseUtil;
import com.huatu.tiku.springboot.users.bean.UserSession;
import com.huatu.tiku.springboot.users.support.Token;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

/**
 * @author hanchao
 * @date 2017/9/13 16:59
 */
@RestController
@RequestMapping("/v3/account")
public class UserAccountControllerV3 {
    @Autowired
    private UserAccountServiceV3 userAccountServiceV3;

    /**
     * 我的代金券列表
     * @param type 1，可用 2，不可用
     * @param userSession
     * @return
     * @throws BizException
     */
    @GetMapping("/coupons")
    public Object findUserCoupons(@RequestParam int type,
                                  @Token UserSession userSession) throws BizException {
        Map<String,Object> params = Maps.newHashMap();
        params.put("action",type);
        params.put("username",userSession.getUname());

        return ResponseUtil.build(userAccountServiceV3.findUserCouponList(RequestUtil.encrypt(params)),true);
    }


    /**
     * 我的积分余额
     * @param userSession
     * @return
     */
    @GetMapping("/point")
    public Object getUserPointLeft(@Token UserSession userSession) throws BizException {
        Map<String,Object> params = Maps.newHashMap();
        params.put("action",1);//积分余额默认
        params.put("username",userSession.getUname());

        return ResponseUtil.build(userAccountServiceV3.getUserPointLeft(RequestUtil.encrypt(params)),true);
    }

    /**
     * 我的积分记录
     * @param userSession
     * @return
     */
    @GetMapping("/point/records")
    public Object findUserPointRecords(@Token UserSession userSession,
                                       @RequestParam int page) throws BizException {
        Map<String,Object> params = Maps.newHashMap();
        params.put("action",2);//积分余额默认
        params.put("username",userSession.getUname());
        params.put("page",page);

        return ResponseUtil.build(userAccountServiceV3.findUserPointRecords(RequestUtil.encrypt(params)),true);
    }


    /**
     * 积分兑换代金券
     * @param userSession
     * @param integrat
     * @param voucherid
     * @return
     */
    @PostMapping("/point/exchangeCoupon")
    public Object exchangeCoupon(@Token UserSession userSession,
                                 @RequestParam String integrat,
                                 @RequestParam String voucherid) throws BizException {
        Map<String,Object> params = Maps.newHashMap();
        params.put("integrat",integrat);//积分余额默认
        params.put("username",userSession.getUname());
        params.put("voucherid",voucherid);

        return ResponseUtil.build(userAccountServiceV3.exchangeCoupon(RequestUtil.encrypt(params)),true);
    }


    /**
     * 账户充值记录，消费记录(不用单独做路由了)
     * @param type
     * @param page
     * @return
     */
    @GetMapping("/records")
    public Object findAccountRecords(@RequestParam int type,
                                     @RequestParam int page,
                                     @Token UserSession userSession) throws BizException {
        Map<String,Object> params = Maps.newHashMap();
        params.put("action",type == 1 ? 2:3);//
        params.put("username",userSession.getUname());
        params.put("page",page);

        return ResponseUtil.build(userAccountServiceV3.findAccountRecortds(RequestUtil.encrypt(params)),true);
    }


    /**
     * 获取用户账户余额
     * @param userSession
     * @return
     */
    @GetMapping("/balance")
    public Object getBalance(@Token UserSession userSession) throws BizException {
        Map<String,Object> params = Maps.newHashMap();
        params.put("action",1);//
        params.put("username",userSession.getUname());

        return ResponseUtil.build(userAccountServiceV3.getAccountBalance(RequestUtil.encrypt(params)),true);
    }

}
