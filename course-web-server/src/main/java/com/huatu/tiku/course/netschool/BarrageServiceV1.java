package com.huatu.tiku.course.netschool;

import com.huatu.tiku.course.bean.NetSchoolResponse;
import org.springframework.cloud.netflix.feign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.Map;

import static com.huatu.tiku.course.consts.NetSchoolUrlConst.BARRAGE_ADD;
import static com.huatu.tiku.course.consts.NetSchoolUrlConst.BARRAGE_LIST;


/**
 * @author zhouwei
 * @Description: TODO
 * @create 2018-07-19 下午8:00
 **/
@FeignClient(value = "o-course-service", path = "/lumenapi")
public interface BarrageServiceV1 {
    /**
     * 弹幕列表
     * @param params
     * @return
     */
    @GetMapping(value = BARRAGE_LIST)
    NetSchoolResponse barrageList(@RequestParam Map<String,Object> params);

    /**
     * 弹幕获取
     * @param params
     * @return
     */
    @PostMapping(value = BARRAGE_ADD)
    NetSchoolResponse barrageAdd(@RequestParam Map<String,Object> params);

}
