package com.huatu.tiku.course.netschool.api;

import com.huatu.tiku.course.netschool.bean.NetSchoolResponse;
import org.springframework.cloud.netflix.feign.FeignClient;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import static com.huatu.tiku.course.netschool.consts.NetSchoolSydwUrlConst.SYDW_MY_LIST;
import static com.huatu.tiku.course.netschool.consts.NetSchoolUrlConst.MY_LIST;
import static com.huatu.tiku.course.netschool.consts.NetSchoolUrlConst.MY_LIVE_SUIT_LIST;

/**
 * 我的直播
 * @author hanchao
 * @date 2017/8/28 23:11
 */
@FeignClient(value = "course-service")
public interface UserCoursesService {
    @RequestMapping(method = RequestMethod.GET,value=SYDW_MY_LIST)
    NetSchoolResponse mySydwList( String username, int order, int categoryid);

    @RequestMapping(method = RequestMethod.GET,value=MY_LIVE_SUIT_LIST)
    NetSchoolResponse myListNew( String username,int order,int categoryid);

    @RequestMapping(method = RequestMethod.GET,value=MY_LIST)
    NetSchoolResponse myList(  String username,int order,int categoryid);
}
