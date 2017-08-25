package com.huatu.tiku.course.web.controller;

import com.huatu.tiku.course.api.CourseService;
import com.huatu.tiku.springboot.users.bean.UserSession;
import com.huatu.tiku.springboot.users.support.Token;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

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
        System.out.println(courseService.sydwList());
        return 1;
    }
}
