package com.huatu.tiku.course.web.controller.v7;

import com.huatu.common.SuccessMessage;
import com.huatu.springboot.web.version.mapping.annotation.ApiVersion;
import com.huatu.tiku.common.bean.user.UserSession;
import com.huatu.tiku.course.bean.vo.LiveRecordInfo;
import com.huatu.tiku.course.netschool.api.v6.UserCourseServiceV6;
import com.huatu.tiku.course.service.v7.UserCourseBizV7Service;
import com.huatu.tiku.course.spring.conf.aspect.mapParam.LocalMapParam;
import com.huatu.tiku.course.spring.conf.aspect.mapParam.LocalMapParamHandler;
import com.huatu.tiku.springboot.users.support.Token;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

/**
 * 描述：
 *
 * @author biguodong
 * Create time 2019-03-22 9:46 PM
 **/

@Slf4j
@RestController
@RequestMapping("/my")
@ApiVersion("v7")
public class UserCourseControllerV7 {


    @Autowired
    private UserCourseBizV7Service userCourseBizV7Service;




    /**
     * 直播学习记录上报
     * @param userSession
     * @return
     */
    @PostMapping(value = "liveRecord")
    public Object saveLiveRecord(@Token UserSession userSession,
                                 @RequestHeader(value = "terminal") int terminal,
                                 @RequestHeader(value = "cv") String cv,
                                 @RequestBody LiveRecordInfo liveRecordInfo){

        userCourseBizV7Service.dealLiveReport(userSession.getId(), userSession.getUname(), userSession.getSubject(), terminal, cv, liveRecordInfo);
        return SuccessMessage.create();

    }


    /**
     * 全部已读
     * @param userSession
     * @param type
     * @return
     */
    @PutMapping(value = "allRead/{type}")
    public Object allReadCourseWork(@Token UserSession userSession,
                                    @PathVariable(value = "type")String type){

        userCourseBizV7Service.allReadByType(userSession.getId(), type, userSession.getUname());
        return SuccessMessage.create("操作成功");
    }


    /**
     * 课后作业&阶段考试列表
     * @param userSession
     * @return
     */
    @GetMapping(value = "courseWork/{type}/detailList")
    public Object studyList(@Token UserSession userSession,
                            @PathVariable(value = "type") String type,
                            @RequestParam(value = "page", defaultValue = "1")int page,
                            @RequestParam(value = "size", defaultValue = "20") int size){

        return userCourseBizV7Service.courseWorkList(userSession.getId(), type, page, size);
    }
}
