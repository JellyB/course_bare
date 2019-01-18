package com.huatu.tiku.course.web.controller.v6;

import com.google.common.collect.Maps;
import com.huatu.springboot.web.version.mapping.annotation.ApiVersion;
import com.huatu.tiku.course.bean.NetSchoolResponse;
import com.huatu.tiku.course.netschool.api.v6.LessonServiceV6;
import com.huatu.tiku.course.spring.conf.aspect.mapParam.LocalMapParam;
import com.huatu.tiku.course.spring.conf.aspect.mapParam.LocalMapParamHandler;
import com.huatu.tiku.course.util.ResponseUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

/**
 * 描述：
 *
 * @author biguodong
 * Create time 2019-01-17 下午6:22
 **/



@Slf4j
@RestController
@RequestMapping("/lesson")
@ApiVersion("v6")
public class LessonControllerV6 {


    @Autowired
    private LessonServiceV6 lessonService;

    /**
     * 图书扫码听课详情
     * @param lessionId
     * @return
     */
    @GetMapping("/{lessionId}")
    @LocalMapParam
    public Object playLesson(@PathVariable(value = "lessionId") String lessionId){
        Map<String,Object> params = LocalMapParamHandler.get();
        NetSchoolResponse netSchoolResponse = lessonService.playLesson(params);
        return ResponseUtil.build(netSchoolResponse);
    }
}
