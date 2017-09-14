package com.huatu.tiku.course.netschool.api;

import com.huatu.tiku.course.netschool.api.fall.CourseServiceV3FallbackFactory;
import com.huatu.tiku.course.netschool.bean.NetSchoolResponse;
import org.springframework.cloud.netflix.feign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.Map;

/**
 * @author hanchao
 * @date 2017/9/12 21:58
 */
@FeignClient(value = "course-service",fallbackFactory = CourseServiceV3FallbackFactory.class)
public interface CourseServiceV3 {
    @GetMapping(value="/v3/getBuyNum.php")
    NetSchoolResponse getCourseLimit(@RequestParam Map<String,Object> params);
}
