package com.huatu.tiku.course.netschool.api.v5;

import com.huatu.tiku.course.bean.NetSchoolResponse;
import org.springframework.cloud.netflix.feign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.Map;


/**
 * @author zhouwei
 * @create 2018-07-19 下午8:00
 **/
@FeignClient(value = "o-course-service", path = "/lumenapi")
public interface BarrageServiceV5 {
    /**
     * 弹幕列表
     */
    @GetMapping(value = "/v4/common/barrage/get_list")
    NetSchoolResponse barrageList(@RequestParam Map<String, Object> params);

    /**
     * 弹幕获取
     */
    @PostMapping(value = "/v4/common/barrage/submit")
    NetSchoolResponse barrageAdd(@RequestParam Map<String, Object> params);

}
