package com.huatu.tiku.course.web.controller.v3;

import com.huatu.common.exception.BizException;
import com.huatu.common.utils.collection.HashMapBuilder;
import com.huatu.tiku.course.netschool.api.v3.LogisticsServiceV3;
import com.huatu.tiku.course.util.RequestUtil;
import com.huatu.tiku.course.util.ResponseUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

/**
 * @author hanchao
 * @date 2017/9/20 11:25
 */
@RestController
@RequestMapping(value = "/v3/logistics")
public class LogisticsControllerV3 {
    @Autowired
    private LogisticsServiceV3 logisticsServiceV3;

    /**
     * 物流详情
     * @param number
     * @return
     */
    @GetMapping("/{number}")
    public Object getLogisticsDetail(@PathVariable String number) {
        Map<String,Object> params = HashMapBuilder.<String,Object>newBuilder()
                .put("action","getLogistics")
                .put("number",number)
                .build();
        return ResponseUtil.build(logisticsServiceV3.getLogisticsDetail(RequestUtil.encrypt(params)));
    }
}
