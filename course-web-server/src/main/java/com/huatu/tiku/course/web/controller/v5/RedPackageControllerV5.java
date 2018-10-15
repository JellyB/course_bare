package com.huatu.tiku.course.web.controller.v5;

import com.huatu.springboot.web.version.mapping.annotation.ApiVersion;
import com.huatu.tiku.course.netschool.api.v5.MessageServiceV5;
import com.huatu.tiku.course.netschool.api.v5.RedPackageServiceV5;
import com.huatu.tiku.course.spring.conf.aspect.mapParam.LocalMapParam;
import com.huatu.tiku.course.spring.conf.aspect.mapParam.LocalMapParamHandler;
import com.huatu.tiku.course.util.ResponseUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;

/**
 * Created by lijun on 2018/9/28
 */
@RestController
@RequestMapping("/redPackage")
@ApiVersion("v5")
public class RedPackageControllerV5 {

    @Autowired
    private RedPackageServiceV5 redPackageService;

    @Autowired
    private MessageServiceV5 messageService;

    /**
     * 获取用户的红包中心
     */
    @LocalMapParam(checkToken = true)
    @GetMapping("redEnvelopeCore")
    public Object redEnvelopeCore() {
        HashMap<String, Object> map = LocalMapParamHandler.get();
        return ResponseUtil.build(redPackageService.redEnvelopeCore(map));
    }

    /**
     * 获取红包领取详情 -APP
     */
    @LocalMapParam(needUserName = false)
    @GetMapping("/receiveDetails")
    public Object receiveDetails(@RequestParam int redEnvelopeId) {
        HashMap<String, Object> map = LocalMapParamHandler.get();
        return ResponseUtil.build(redPackageService.receiveDetails(map));
    }

    /**
     * 获取红包领取详情 - H5
     */
    @LocalMapParam
    @GetMapping("/receiveDetailsForH5")
    public Object receiveDetailsForH5(
            @RequestParam String phone,
            @RequestParam int redEnvelopeId,
            @RequestParam String userName
    ) {
        HashMap<String, Object> map = LocalMapParamHandler.get();
        return ResponseUtil.build(redPackageService.receiveDetails(map));
    }

    /**
     * 获取可发红包详情
     */
    @LocalMapParam(needUserName = false)
    @GetMapping("/{id}/detail")
    public Object detail(@PathVariable int id) {
        HashMap<String, Object> map = LocalMapParamHandler.get();
        return ResponseUtil.build(redPackageService.detail(map));
    }

    /**
     * 判断用户是否有发起红包或领取红包
     */
    @LocalMapParam(checkToken = true)
    @GetMapping("checkRedEnv")
    public Object checkRedEnv() {
        HashMap<String, Object> map = LocalMapParamHandler.get();
        return ResponseUtil.build(redPackageService.checkRedEnv(map));
    }

    /**
     * 微信体现
     */
    @LocalMapParam(checkToken = true)
    @PostMapping("withdrawWechat")
    public Object withdrawWechat(
            @RequestParam String openid,
            @RequestParam String phone,
            @RequestParam String verify
    ){
        HashMap<String, Object> map = LocalMapParamHandler.get();
        return ResponseUtil.build(redPackageService.withdrawWechat(map));
    }

    /**
     * 支付宝提现
     */
    @LocalMapParam(checkToken = true)
    @PostMapping("withdrawAli")
    public Object withdrawAli(
            @RequestParam String payeeAccount,
            @RequestParam String phone,
            @RequestParam String verify
    ){
        HashMap<String, Object> map = LocalMapParamHandler.get();
        return ResponseUtil.build(redPackageService.withdrawAli(map));
    }

    /**
     * 发送短信
     */
    @LocalMapParam(needUserName = false)
    @GetMapping("/sendVerify/{phone}")
    public Object sendVerify(@PathVariable String phone){
        HashMap<String, Object> map = LocalMapParamHandler.get();
        return ResponseUtil.build(messageService.sendVerify(map));
    }

    /**
     * 判断红包是否显示
     */
    @GetMapping("/showRedEvn")
    public Object showRedEvn(){
        return ResponseUtil.build(redPackageService.showRedEvn());
    }
}
