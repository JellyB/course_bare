package com.huatu.tiku.course.netschool.api.v6;

import com.huatu.tiku.course.bean.NetSchoolResponse;
import org.springframework.cloud.netflix.feign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.Map;

/**
 * 描述：我的收藏接口
 *
 * @author biguodong
 * Create time 2018-11-30 上午10:30
 **/
@FeignClient(value = "o-course-service", path = "/lumenapi")
public interface CollectionServiceV6 {

    /**
     * 添加我的课程收藏
     * @param params
     * @return
     */
    @PutMapping(value = "/v5/c/class/collection_add")
    NetSchoolResponse add(@RequestParam Map<String, Object> params);

    /**
     * 取消我的课程收藏
     * @param params
     * @return
     */
    @PutMapping(value = "/v5/c/class/collection_cancel")
    NetSchoolResponse cancel(@RequestParam Map<String, Object> params);

    /**
     * 我的收藏课程列表
     * @param params
     * @return
     */
    @GetMapping(value = "/v5/c/class/collection_list")
    NetSchoolResponse list(@RequestParam Map<String, Object> params);



}
