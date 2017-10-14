package com.huatu.tiku.course.web.controller.v3;

import com.huatu.tiku.course.service.RewardBizService;
import com.huatu.tiku.springboot.users.bean.UserSession;
import com.huatu.tiku.springboot.users.support.Token;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author hanchao
 * @date 2017/10/14 14:49
 */
@RestController
@RequestMapping("/v3/my")
public class UserRewardController {
    @Autowired
    private RewardBizService rewardBizService;


    @RequestMapping("/reward/view")
    public Object getUserRewardProcess(@Token UserSession userSession){
        return rewardBizService.getRewardView(userSession.getId());
    }
}
