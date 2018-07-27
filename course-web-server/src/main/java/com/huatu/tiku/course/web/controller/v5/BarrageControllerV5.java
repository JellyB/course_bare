package com.huatu.tiku.course.web.controller.v5;

import com.huatu.common.exception.BizException;
import com.huatu.common.utils.collection.HashMapBuilder;
import com.huatu.springboot.web.version.mapping.annotation.ApiVersion;
import com.huatu.tiku.common.bean.user.UserSession;
import com.huatu.tiku.course.netschool.api.v5.BarrageServiceV5;
import com.huatu.tiku.course.spring.conf.aspect.mapParam.LocalMapParam;
import com.huatu.tiku.course.spring.conf.aspect.mapParam.LocalMapParamHandler;
import com.huatu.tiku.course.util.ResponseUtil;
import com.huatu.tiku.springboot.users.support.Token;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

/**
 * @author zhouwei
 * @Description: 弹幕接口
 * @create 2018-07-19 下午7:56
 **/
@RestController
@RequestMapping("barrages")
@ApiVersion("v5")
public class BarrageControllerV5 {
    @Autowired
    BarrageServiceV5 barrageServiceV5;

    /**
     * 弹幕获取
     */
    @GetMapping
    public Object getBarrages(@Token UserSession userSession, long classId, long lessonId,
                              @RequestParam(defaultValue = "1") int page,
                              @RequestParam(defaultValue = "10") int pageSize) throws BizException{
        Map<String,Object> params = HashMapBuilder.<String,Object>newBuilder()
                .put("classId",classId)
                .put("userName",userSession.getUname())
                .put("page",page)
                .put("pageSize",pageSize)
                .put("lessonId",lessonId).build();

        return ResponseUtil.build(barrageServiceV5.barrageList(params));
    }

    /**
     * 弹幕提交
     */
    @LocalMapParam(checkToken = true)
    @PostMapping
    public Object addBarrage(long classId, String content, long lessonId, String videoNode, int background){
        HashMap<String, Object> map = LocalMapParamHandler.get();
        return ResponseUtil.build(barrageServiceV5.barrageAdd(map));
    }
}
