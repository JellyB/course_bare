package com.huatu.tiku.course.netschool.api;

import com.huatu.tiku.course.netschool.bean.NetSchoolResponse;
import org.springframework.cloud.netflix.feign.FeignClient;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.Map;

import static com.huatu.tiku.course.netschool.consts.NetSchoolSydwUrlConst.SYDW_TOTAL_LIST;

/**
 * @author hanchao
 * @date 2017/8/18 16:05
 */
@FeignClient(value = "course-service")
public interface SydwCourseService {
    @RequestMapping(value = SYDW_TOTAL_LIST,method = RequestMethod.GET,consumes = MediaType.APPLICATION_FORM_URLENCODED_VALUE)
    NetSchoolResponse sydwTotalList(@RequestParam Map<String,Object> params);


}
