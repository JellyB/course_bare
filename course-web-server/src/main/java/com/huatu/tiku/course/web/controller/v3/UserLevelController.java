package com.huatu.tiku.course.web.controller.v3;

import com.huatu.common.utils.collection.HashMapBuilder;
import com.huatu.tiku.course.netschool.api.v3.UserLevelServiceV3;
import com.huatu.tiku.course.service.RewardBizService;
import com.huatu.tiku.course.util.ResponseUtil;
import com.huatu.tiku.springboot.users.bean.UserSession;
import com.huatu.tiku.springboot.users.support.Token;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

/**
 * @author hanchao
 * @date 2017/10/14 14:49
 */
@RestController
@RequestMapping("/v3/my")
public class UserLevelController {
    @Autowired
    private RewardBizService rewardBizService;
    @Autowired
    private UserLevelServiceV3 userLevelServiceV3;


    /**
     *
     * @param userSession
     * @return
     */
    @RequestMapping("/reward/view")
    public Object getUserRewardProcess(@Token UserSession userSession){
        return rewardBizService.getRewardView(userSession.getId());
    }

    /**
     * 获取用户等级信息
     * @param userSession
     * @return
     */
    @RequestMapping("/level")
    public Object getUserLevel(@Token UserSession userSession){
        Map<String,Object> params = HashMapBuilder.newBuilder()
                .put("username",userSession.getUname())
                .put("action",1)
                .buildUnsafe();
        return ResponseUtil.build(userLevelServiceV3.getUserLevel(params));
    }
}
