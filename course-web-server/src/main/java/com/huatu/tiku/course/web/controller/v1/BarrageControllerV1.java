package com.huatu.tiku.course.web.controller.v1;

import com.huatu.common.exception.BizException;
import com.huatu.common.utils.collection.HashMapBuilder;
import com.huatu.springboot.web.version.mapping.annotation.ApiVersion;
import com.huatu.tiku.common.bean.user.UserSession;
import com.huatu.tiku.course.bean.NetSchoolResponse;
import com.huatu.tiku.course.netschool.BarrageServiceV1;
import com.huatu.tiku.course.util.ResponseUtil;
import com.huatu.tiku.springboot.users.support.Token;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

/**
 * @author zhouwei
 * @Description: 弹幕接口
 * @create 2018-07-19 下午7:56
 **/
@RestController
@RequestMapping("barrages")
@ApiVersion("v1")
public class BarrageControllerV1 {
    @Autowired
    BarrageServiceV1 barrageServiceV1;

    /**
     * 弹幕获取
     * @param userSession
     * @param classId
     * @param lessonId
     * @param page
     * @param pageSize
     * @return
     * @throws BizException
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

        return ResponseUtil.build(barrageServiceV1.barrageList(params));
    }

    /**
     * 弹幕提交
     * @param userSession
     * @param terminal
     * @param classId
     * @param content
     * @param lessonId
     * @return
     * @throws BizException
     */
    @PostMapping
    public Object addBarrage(@Token UserSession userSession, @RequestHeader int terminal, long classId, String content, long lessonId) throws BizException{
        Map<String,Object> params = HashMapBuilder.<String,Object>newBuilder()
                .put("classId",classId)
                .put("content",content)
                .put("userName",userSession.getUname())
                .put("lessonId",lessonId)
                .put("terminal",terminal).build();

        return ResponseUtil.build(barrageServiceV1.barrageAdd(params));
    }
}
