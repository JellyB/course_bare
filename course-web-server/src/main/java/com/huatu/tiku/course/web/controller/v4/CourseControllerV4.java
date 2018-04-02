package com.huatu.tiku.course.web.controller.v4;

import com.huatu.common.utils.collection.HashMapBuilder;
import com.huatu.springboot.web.version.mapping.annotation.ApiVersion;
import com.huatu.tiku.common.bean.AreaConstants;
import com.huatu.tiku.common.bean.user.UserSession;
import com.huatu.tiku.course.bean.NetSchoolResponse;
import com.huatu.tiku.course.netschool.api.fall.CourseServiceV4Fallback;
import com.huatu.tiku.course.netschool.api.v4.CourseServiceV4;
import com.huatu.tiku.course.util.ResponseUtil;
import com.huatu.tiku.springboot.users.support.Token;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

/**
 * @author hanchao
 * @date 2018/3/6 15:15
 */
@RestController
@RequestMapping(value = "/courses")
@ApiVersion("v4")
public class CourseControllerV4 {
    @Autowired
    private CourseServiceV4 courseServiceV4;
    @Autowired
    private CourseServiceV4Fallback courseServiceV4Fallback;
    /**
     * 录播课程列表
     * @param categoryid
     * @param orderid
     * @param page
     * @param userSession
     * @return
     */
    @GetMapping("/recordings")
    public Object recordingList(@RequestParam(required = false,defaultValue = "1001") int categoryid,
                                @RequestParam(required = false) String cv,
                                @RequestParam(required = false,defaultValue = "1") int orderid,
                                @RequestParam int page,
                                @RequestParam(required = false,defaultValue = "") String keywords,
                                @RequestParam(required = false,defaultValue = "1000") int subjectid,
                                @Token UserSession userSession) {
        int provinceId = AreaConstants.getNetSchoolProvinceId(userSession.getArea());
        Map<String,Object> params = HashMapBuilder.<String,Object>newBuilder()
                .put("categoryid",categoryid)
                .put("username",userSession.getUname())
                .put("orderid",orderid)
                .put("page",page)
                .put("subjectid",1000)//临时写死
                .put("keywords",keywords)
                .put("cv",cv)
                .put("provinceid",provinceId).build();
        NetSchoolResponse recordingList = courseServiceV4.findRecordingList(params);
        courseServiceV4Fallback.setRecordingList(params,recordingList);
        return ResponseUtil.build(recordingList);
    }
}
