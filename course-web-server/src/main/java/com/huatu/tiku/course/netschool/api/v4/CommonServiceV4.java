package com.huatu.tiku.course.netschool.api.v4;

import com.huatu.tiku.course.bean.NetSchoolResponse;
import org.springframework.cloud.netflix.feign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.Map;

/**
 * Created by junli on 2018/3/29.
 */
@FeignClient(value = "o-course-service")
public interface CommonServiceV4 {


    /**
     * 面试状元0元赠送课程
     */
    @GetMapping("/lumenapi/v4/common/champion/sendnetclasslist")
    NetSchoolResponse sendnetclasslist(@RequestParam("userName") String userName);

    /**
     * 面试状元封闭集训营
     */
    @GetMapping("/lumenapi/v4/common/champion")
    NetSchoolResponse champion(@RequestParam Map<String, Object> params);

    /**
     * 面试状元封闭集训营信息完善
     */
    @GetMapping("/lumenapi/v4/common/championupdate")
    NetSchoolResponse championUpdate(@RequestParam Map<String, Object> params);

    /**
     * 面试状元赠送课程列表接口
     */
    @GetMapping("/lumenapi/v4/common/championclasslist")
    NetSchoolResponse championClassList();

}
