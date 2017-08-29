package com.huatu.tiku.course.netschool.api;

import com.huatu.tiku.course.netschool.bean.NetSchoolResponse;
import org.springframework.cloud.netflix.feign.FeignClient;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.Map;

import static com.huatu.tiku.course.netschool.consts.NetSchoolUrlConst.ALL_COLLECTION_LIST;
import static com.huatu.tiku.course.netschool.consts.NetSchoolUrlConst.COURSE_DATAIL_V2;
import static com.huatu.tiku.course.netschool.consts.NetSchoolUrlConst.TOTAL_LIST;


/**
 * @author hanchao
 * @date 2017/8/28 23:08
 */
@FeignClient(value = "course-service")
public interface CourseServiceV1 {
    @RequestMapping(value = TOTAL_LIST,method = RequestMethod.GET )
    NetSchoolResponse totalList(@RequestParam Map<String,Object> params);


    @RequestMapping(value = ALL_COLLECTION_LIST,method = RequestMethod.GET)
    NetSchoolResponse allCollectionList( @RequestParam Map<String,Object> params);

    @RequestMapping(value = ALL_COLLECTION_LIST,method = RequestMethod.GET)
    NetSchoolResponse collectionDetail(@RequestParam Map<String,Object> params);


    @RequestMapping(value = COURSE_DATAIL_V2,method = RequestMethod.GET)
    NetSchoolResponse courseDetail(@RequestParam Map<String,Object> params);
}
