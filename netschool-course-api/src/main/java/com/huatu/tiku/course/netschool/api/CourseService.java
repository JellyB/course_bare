package com.huatu.tiku.course.netschool.api;

import com.huatu.tiku.course.netschool.api.mock.CourseServiceMock;
import com.huatu.tiku.course.netschool.bean.NetSchoolResponse;
import org.springframework.cloud.netflix.feign.FeignClient;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

/**
 * @author hanchao
 * @date 2017/8/18 16:05
 */
@FeignClient(value = "course-service-provider",fallback = CourseServiceMock.class)
public interface CourseService {
    @RequestMapping(value = "/sydw/v2/ztkClassSearch.php",method = RequestMethod.GET)
    NetSchoolResponse sydwList();
}
