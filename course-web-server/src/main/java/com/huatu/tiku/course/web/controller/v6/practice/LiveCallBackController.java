package com.huatu.tiku.course.web.controller.v6.practice;

import com.huatu.common.SuccessMessage;
import com.huatu.springboot.web.version.mapping.annotation.ApiVersion;
import com.huatu.tiku.course.bean.practice.LiveCallbackBo;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * Created by lijun on 2019/3/5
 */
@RestController
@RequestMapping("practice/liveCallBack")
@RequiredArgsConstructor
@ApiVersion("v6")
@Slf4j
public class LiveCallBackController {



    @PostMapping(value = "/{roomId}/liveCallBack")
    public Object liveCallBack(@PathVariable Long roomId, @RequestBody List<LiveCallbackBo> liveCallbackBoList) {

        log.info("roomId = {},liveCallbackBoList = {}",roomId,liveCallbackBoList);

        return SuccessMessage.create();
    }
}
