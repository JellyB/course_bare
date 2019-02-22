package com.huatu.tiku.course.web.controller;


import com.alibaba.fastjson.JSON;
import com.huatu.tiku.course.bean.NetSchoolResponse;
import com.huatu.tiku.course.common.ArticleTypeListEnum;
import com.huatu.tiku.course.netschool.api.ExamNetSchoolService;
import com.huatu.tiku.course.util.ResponseUtil;
import com.huatu.tiku.springboot.users.service.UserSessionService;
import lombok.extern.slf4j.Slf4j;
import org.apache.logging.log4j.core.util.JsonUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

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
    ExamNetSchoolService examNetSchoolService;

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
        param.put("pageSize", pageSize);
        return ResponseUtil.build(examNetSchoolService.getArticleList(param));
    }

    /**
     * 获取文章详情
     *
     * @param aid
     * @return
     */
    @GetMapping(value = "detail/{aid}", produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public Object detail(@PathVariable int aid) {
        HashMap map = new HashMap();
        map.put("id", aid);
        NetSchoolResponse detail = examNetSchoolService.detail(map);
        return ResponseUtil.build(detail);
    }

    /**
     * 用户点赞
     *
     * @param aid
     * @param type 1 点赞;-1取消点赞
     * @return
     */
    @PostMapping(value = "like/{aid}", produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public Object like(@PathVariable int aid,
                       @RequestParam(defaultValue = "1") int type) {

        HashMap paramMap = new HashMap();
        paramMap.put("id", aid);
        paramMap.put("type", type);
        NetSchoolResponse like = examNetSchoolService.like(paramMap);
        return ResponseUtil.build(like);
    }

    /**
     * 备考精华分类列表
     *
     * @return
     */
    @GetMapping(value = "typeList", produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public Object typeList() {
        ArticleTypeListEnum[] values = ArticleTypeListEnum.values();
        List<HashMap<String, Object>> result = new ArrayList<>();
        for (ArticleTypeListEnum article : values) {
            HashMap map = new HashMap();
            map.put("sort", article.getSort());
            map.put("type", article.getCode());
            map.put("name", article.getName());
            result.add(map);
        }
        return result;
    }
}
