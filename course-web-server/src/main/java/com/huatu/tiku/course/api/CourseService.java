package com.huatu.tiku.course.api;

import org.springframework.cloud.netflix.feign.FeignClient;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

/**
 * @author hanchao
 * @date 2017/8/18 16:05
 */
@FeignClient(value = "course-service")
public interface CourseService {
    @RequestMapping(value = "/sydw/v2/ztkClassSearch.php",method = RequestMethod.GET)
    NetSchoolResponse sydwList();
}
