package com.huatu.tiku.course.netschool.api;

import com.huatu.tiku.course.netschool.bean.NetSchoolResponse;
import org.springframework.cloud.netflix.feign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.Map;

import static com.huatu.tiku.course.netschool.consts.NetSchoolUrlConst.*;


/**
 * @author hanchao
 * @date 2017/8/28 23:08
 */
@FeignClient(value = "course-service")
public interface CourseServiceV1 {
    @GetMapping(value = TOTAL_LIST)
    NetSchoolResponse totalList(@RequestParam Map<String,Object> params);


    @GetMapping(value = ALL_COLLECTION_LIST)
    NetSchoolResponse allCollectionList( @RequestParam Map<String,Object> params);

    @GetMapping(value = ALL_COLLECTION_LIST)
    NetSchoolResponse collectionDetail(@RequestParam Map<String,Object> params);


    @GetMapping(value = COURSE_DATAIL_V2)
    NetSchoolResponse courseDetail(@RequestParam Map<String,Object> params);


    @GetMapping(value = MY_COURSE_DATAIL_ANDROID)
    NetSchoolResponse myAndroidDetail(@RequestParam Map<String,Object> params);

    @GetMapping(value = MY_COURSE_DATAIL_IOS)
    NetSchoolResponse myIosDetail(@RequestParam Map<String,Object> params);

    @GetMapping(value = HANDOUT_LIST)
    NetSchoolResponse getHandouts(@RequestParam Map<String,Object> params);
}
