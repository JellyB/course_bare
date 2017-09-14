package com.huatu.tiku.course.netschool.api.v3;

/**
 * @author hanchao
 * @date 2017/9/13 16:59
 */

import com.huatu.tiku.course.netschool.bean.NetSchoolResponse;
import org.springframework.cloud.netflix.feign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

@FeignClient(value = "course-service")
public interface UserAccountServiceV3 {
    /**
     * 我的兑换券列表
     * @param p (action 1，正常 2，过期  username)
     * @return
     */
    @PostMapping("/v3/mycount/myVoucher.php")
    NetSchoolResponse findUserCouponList(@RequestParam("p") String p);


    /**
     * 获取用户积分余额（action固定传1）
     * @param p (action 1，余额 2，列表  username)
     * @return
     */
    @PostMapping("/v3/mycount/myPoint.php")
    NetSchoolResponse getUserPointLeft(@RequestParam("p") String p);

    /**
     * 获取用户积分记录（action固定传2）
     * @param p (action 1，余额 2，列表  username  page)
     * @return
     */
    @PostMapping("/v3/mycount/myPoint.php")
    NetSchoolResponse findUserPointRecords(@RequestParam("p") String p);

    /**
     * 积分兑换代金券
     * @param p
     * @return
     */
    @PostMapping("/v3/mycount/VocherByIntegrate.php")
    NetSchoolResponse exchangeCoupon(@RequestParam("p") String p);

    /**
     * 账户 消费/充值 记录
     * @param p (action 2，充值 3，消费 username page)
     * @return
     */
    @PostMapping("/v3/mycount/myCount.php")
    NetSchoolResponse findAccountRecortds(@RequestParam("p") String p);

    /**
     * 账户 消费/充值 记录
     * @param p (action 1，余额 username page)
     * @return
     */
    @PostMapping("/v3/mycount/myCount.php")
    NetSchoolResponse getAccountBalance(@RequestParam("p") String p);
}
