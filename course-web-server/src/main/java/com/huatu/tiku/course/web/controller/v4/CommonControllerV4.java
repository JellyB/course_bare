package com.huatu.tiku.course.web.controller.v4;

import com.google.common.collect.Maps;
import com.huatu.springboot.web.version.mapping.annotation.ApiVersion;
import com.huatu.tiku.common.bean.user.UserSession;
import com.huatu.tiku.course.netschool.api.v4.CommonServiceV4;
import com.huatu.tiku.course.util.ResponseUtil;
import com.huatu.tiku.springboot.users.support.Token;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;

/**
 * Created by junli on 2018/3/29.
 */
@RestController
@RequestMapping(value = "/common")
@ApiVersion("v4")
public class CommonControllerV4 {


    @Autowired
    private CommonServiceV4 commonServiceV4;

    /**
     * 面试状元封闭集训营
     *
     * @return
     */
    @GetMapping("/champion")
    public Object champion(
            @Token UserSession userSession,
            @RequestParam(value = "admissionTicket", required = true) String admissionTicket,
            @RequestParam(value = "areaId", required = true) String areaId,
            @RequestParam(value = "examNum", required = true) Integer examNum,
            @RequestParam(value = "examSort", required = false, defaultValue = "0") Integer examSort,
            @RequestParam(value = "name", required = true) String name
    ) {
        final HashMap<String, Object> params = Maps.newHashMap();
        params.put("userName",userSession.getUname());
        params.put("admissionticket", admissionTicket);
        params.put("area_id", areaId);
        params.put("exam_num", examNum);
        params.put("exam_sort", examSort);
        params.put("name", name);
        return ResponseUtil.build(commonServiceV4.champion(params));
    }

    /**
     * 面试状元封闭集训营信息完善
     *
     * @return
     */
    @GetMapping("/championupdate")
    public Object championUpdate(
            @RequestParam(value = "hid", required = true) Integer hid,
            @RequestParam(value = "phone", required = true) String phone,
            @RequestParam(value = "sid", required = true) String sid
    ) {
        final HashMap<String, Object> paramMap = Maps.newHashMap();
        paramMap.put("hid", hid);
        paramMap.put("phone", phone);
        paramMap.put("sid", sid);
        return ResponseUtil.build(commonServiceV4.championUpdate(paramMap));
    }
}
