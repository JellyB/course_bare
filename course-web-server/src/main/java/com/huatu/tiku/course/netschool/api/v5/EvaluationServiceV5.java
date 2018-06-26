package com.huatu.tiku.course.netschool.api.v5;

import com.huatu.tiku.course.bean.NetSchoolResponse;
import org.springframework.cloud.netflix.feign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.Map;

/**
 * Created by lijun on 2018/6/25
 */
@FeignClient(value = "o-course-service", path = "/lumenapi")
public interface EvaluationServiceV5 {

    /**
     * 获取售前课程详情评价列表
     */
    @GetMapping(value = "/v4/common/class/evaluation")
    NetSchoolResponse getClassEvaluation(@RequestParam Map<String, Object> params);

    /**
     * 获取老师介绍页评价列表
     */
    @GetMapping(value = "/v4/common/teacher/evaluation")
    NetSchoolResponse getTeacherEvaluation(@RequestParam Map<String, Object> params);

    /**
     * 获取用户当前课件评价信息接口
     */
    @GetMapping(value = "/v4/common/evaluation/click")
    NetSchoolResponse getClickEvaluation(@RequestParam Map<String, Object> params);

    /**
     * 提交评价
     */
    @PostMapping(value = "/v4/common/evaluation/submit")
    NetSchoolResponse submit(@RequestParam Map<String, Object> params);

}
