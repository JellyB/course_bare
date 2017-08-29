package com.huatu.tiku.course.web.controller;

import com.google.common.collect.Maps;
import com.huatu.common.consts.TerminalType;
import com.huatu.tiku.common.consts.CatgoryType;
import com.huatu.tiku.course.netschool.api.CourseService;
import com.huatu.tiku.course.netschool.api.SydwCourseService;
import com.huatu.tiku.course.netschool.bean.NetSchoolResponse;
import com.huatu.tiku.springboot.users.bean.UserSession;
import com.huatu.tiku.springboot.users.support.Token;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

/**
 * @author hanchao
 * @date 2017/8/18 16:14
 */
@RestController
@RequestMapping(value = "v1/courses")
public class CourseController {
    public static final String IOS_NEW_VERSION = "2.3.4";
    public static final String ANDROID_NEW_VERSION = "2.3.3";

    @Autowired
    private SydwCourseService sydwCourseService;
    @Autowired
    private CourseService courseService;

    /**
     * 全部直播列表接口
     *
     * @param orderid    排序属性
     * @param categoryid 网校考试类型
     * @param dateid     考试日期筛选
     * @param priceid    按照价格筛选
     * @param page       分页数
     * @param terminal   终端类型
     * @param cv         版本号
     * @return
     * @throws Exception
     */
    @RequestMapping(value = "list", produces = MediaType.APPLICATION_JSON_UTF8_VALUE, method = RequestMethod.GET)
    public Object list(@RequestParam int orderid,
                       @RequestParam int categoryid,
                       @RequestParam int dateid,
                       @RequestParam int priceid,
                       @RequestParam int page,
                       @RequestHeader int terminal,
                       @RequestHeader String cv,
                       @RequestParam(required = false) String shortTitle,
                       @Token UserSession userSession) throws Exception {
        HashMap<String, Object> params = Maps.newHashMap();
        params.put("orderid", orderid);
        params.put("categoryid", categoryid);
        params.put("dateid", dateid);
        params.put("priceid", priceid);
        params.put("page", page);
        params.put("username", userSession.getUname());
        params.put("shortTitle", shortTitle);

        return getListByDevice(params,userSession.getCategory(),terminal,cv,shortTitle);
    }


    /**
     * 根据设备返回不同的课程列表数据
     * @param params
     * @param catgory
     * @param terminal
     * @param cv
     * @param shortTitle
     * @return
     */
    private NetSchoolResponse getListByDevice(Map<String,Object> params, int catgory, int terminal, String cv, String shortTitle) {
        if (catgory == CatgoryType.SHI_YE_DAN_WEI) {
            return sydwCourseService.sydwTotalList(params);
        }

        boolean newVersion = isNewVersion(catgory, terminal, cv);
        if (newVersion && StringUtils.isBlank(shortTitle)) {
            return courseService.allCollectionList(params);
        } else if (newVersion && StringUtils.isNoneBlank(shortTitle)) {
            return courseService.collectionDetail(params);
        } else{
            return courseService.totalList(params);
        }

    }

    private boolean isNewVersion(int catgory, int terminal, String cv) {
        if (catgory == CatgoryType.GONG_WU_YUAN) {
            boolean iosNewVersion = (terminal == TerminalType.IPHONE || terminal == TerminalType.IPHONE_IPAD)
                    && cv.compareTo(IOS_NEW_VERSION) >= 0;

            boolean androidNewVersion = (terminal == TerminalType.ANDROID || terminal == TerminalType.ANDROID_IPAD)
                    && cv.compareTo(ANDROID_NEW_VERSION) >= 0;

            return iosNewVersion || androidNewVersion;
        } else {
            return false;
        }
    }
}
