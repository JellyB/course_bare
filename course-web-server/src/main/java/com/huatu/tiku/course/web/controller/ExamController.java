package com.huatu.tiku.course.web.controller;

import com.huatu.tiku.common.bean.user.UserSession;
import com.huatu.tiku.course.netschool.api.ExamNetSchoolService;
import com.huatu.tiku.course.service.ExamService;
import com.huatu.tiku.springboot.users.service.UserSessionService;
import com.huatu.tiku.springboot.users.support.Token;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;

/**
 * @创建人 lizhenjuan
 * @创建时间 2019/2/15
 * @描述 备考精华
 */

@RestController
@RequestMapping(value = "v1/exam")
@Slf4j
public class ExamController {

    @Autowired
    private ExamService examService;

    @Autowired
    ExamNetSchoolService examNetSchoolService;

    @Autowired
    UserSessionService userSessionService;

    /**
     * 获取备考精华文章列表
     *
     * @param type
     * @param page
     * @param pageSize
     * @return
     */
    @RequestMapping(value = "{type}", produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public Object getArticleList(@PathVariable int type,
                                 @RequestParam(defaultValue = "1") int page,
                                 @RequestParam(defaultValue = "10") int pageSize) {
        HashMap param = new HashMap();
        param.put("type", type);
        param.put("page", page);
        param.put("size", pageSize);
        Object object = examService.getArticleList(param);
        return object;
    }

    /**
     * 获取文章详情
     *
     * @param aid
     * @return
     */
    @GetMapping(value = "detail/{aid}", produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public Object detail(@PathVariable int aid) {
        return examService.detail(aid);
    }

    /**
     * 用户点赞
     *
     * @param aid
     * @param userSession
     * @return
     */
    @GetMapping(value = "like/{aid}", produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public Object like(@PathVariable int aid,
                       @Token(required = false) UserSession userSession) {
        String uname = null;
        if (null != userSession) {
            uname = userSession.getUname();
        }
        HashMap paramMap = new HashMap();
        paramMap.put("uname", uname);
        paramMap.put("aid", aid);
        return examService.like(paramMap);
    }


}
