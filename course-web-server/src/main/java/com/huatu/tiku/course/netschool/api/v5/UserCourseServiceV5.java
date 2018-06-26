package com.huatu.tiku.course.netschool.api.v5;

import com.huatu.tiku.course.bean.NetSchoolResponse;
import org.springframework.cloud.netflix.feign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.Map;

/**
 * 用户个人课程信息相关
 * Created by lijun on 2018/6/25
 */
@FeignClient(value = "o-course-service", path = "/lumenapi")
public interface UserCourseServiceV5 {

    /**
     * 查询我的课程-已删除
     *
     * @param params
     */
    @GetMapping(value = "/v4/common/user/my_course?isDelete=1")
    NetSchoolResponse getMyDeletedClasses(Map<String, Object> params);

    /**
     * 查询我的课程-未删除
     */
    @GetMapping(value = "/v4/common/user/my_course?isDelete=0")
    NetSchoolResponse getMyNotDeletedClasses(Map<String, Object> params);
}
