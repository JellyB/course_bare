package com.huatu.tiku.course.netschool.api.v6;

import com.huatu.tiku.course.bean.NetSchoolResponse;
import org.springframework.cloud.netflix.feign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.Map;

/**
 * 描述：
 *
 * @author biguodong
 * Create time 2019-01-17 下午6:23
 **/

@FeignClient(value = "o-course-service", path = "/lumenapi")
public interface LessonServiceV6 {


    /**
     * 图书扫码听课详情
     * @param params
     * @return
     */
    @GetMapping(value = "/v5/c/lesson/play_lessions")
    NetSchoolResponse playLesson(@RequestParam Map<String, Object> params);

}
