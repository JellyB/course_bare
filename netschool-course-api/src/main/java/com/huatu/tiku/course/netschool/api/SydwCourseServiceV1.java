package com.huatu.tiku.course.netschool.api;

import com.huatu.tiku.course.netschool.bean.NetSchoolResponse;
import org.springframework.cloud.netflix.feign.FeignClient;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.Map;

import static com.huatu.tiku.course.netschool.consts.NetSchoolSydwUrlConst.SYDW_COURSE_DATAIL;
import static com.huatu.tiku.course.netschool.consts.NetSchoolSydwUrlConst.SYDW_TOTAL_LIST;

/**
 * @author hanchao
 * @date 2017/8/18 16:05
 */
@FeignClient(value = "course-service")
public interface SydwCourseServiceV1 {
    @RequestMapping(value = SYDW_TOTAL_LIST,method = RequestMethod.GET)
    NetSchoolResponse sydwTotalList(@RequestParam Map<String,Object> params);

    @RequestMapping(value = SYDW_COURSE_DATAIL,method = RequestMethod.GET)
    NetSchoolResponse courseDetail(@RequestParam Map<String,Object> params);
}
