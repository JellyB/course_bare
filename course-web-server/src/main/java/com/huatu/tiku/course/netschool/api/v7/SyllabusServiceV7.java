package com.huatu.tiku.course.netschool.api.v7;

import com.huatu.tiku.course.bean.NetSchoolResponse;
import com.huatu.tiku.course.netschool.api.fall.SyllabusServiceFallback;
import javafx.scene.chart.ValueAxis;
import org.springframework.cloud.netflix.feign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.Map;

/**
 * 描述：
 *
 * @author biguodong
 * Create time 2019-03-05 10:59 AM
 **/

@FeignClient(value = "o-course-service", path = "/lumenapi", fallback = SyllabusServiceFallback.class)
public interface SyllabusServiceV7 {

    /**
     * 根据大纲id获取课件信息，多个大纲，逗号分隔
     * @param params
     * @return
     */
    @GetMapping(value = "/v5/c/syllabus/courseware_info")
    NetSchoolResponse courseWareInfo(@RequestParam Map<String, Object> params);
}
