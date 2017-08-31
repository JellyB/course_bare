package com.huatu.tiku.course.netschool.api;

import org.springframework.cloud.netflix.feign.FeignClient;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import static com.huatu.tiku.course.netschool.consts.NetSchoolUrlConst.COURSE_H5;

/**
 * @author hanchao
 * @date 2017/8/30 11:59
 */
@FeignClient(value = "course-service")
public interface HtmlServiceV1 {
    @RequestMapping(value = COURSE_H5,method = RequestMethod.GET )
    String courseDetail(@RequestParam("rid")int rid);
}
