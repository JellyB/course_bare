package com.huatu.tiku.course.web.controller.v4;

import com.huatu.tiku.common.bean.user.UserSession;
import com.huatu.tiku.course.bean.NetSchoolResponse;
import com.huatu.tiku.course.netschool.api.v4.EvaluateServiceV4;
import com.huatu.tiku.course.util.ResponseUtil;
import com.huatu.tiku.springboot.users.support.Token;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * Created by lijun on 2018/5/18
 */
@RestController
@RequestMapping("/v4/evaluates")
public class EvaluateControllerV4 {

    @Autowired
    private EvaluateServiceV4 evaluateService;

    /**
     * 课件是否被用户评价
     * @return
     */
    @GetMapping("/lession")
    public Object lessionEvaluate(
            @Token UserSession userSession,
            @RequestParam("lessionId") int lessionId
    ) {
        NetSchoolResponse netSchoolResponse = evaluateService.lessionEvaluate(lessionId, userSession.getUname());
        return ResponseUtil.build(netSchoolResponse);
    }
}
