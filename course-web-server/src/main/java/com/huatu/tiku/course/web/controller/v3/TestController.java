package com.huatu.tiku.course.web.controller.v3;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author hanchao
 * @date 2018/1/16 17:51
 */
@RestController
public class TestController {
    @RequestMapping("/test")
    public Object test(){
        throw new IllegalStateException("1231231231231");
    }
}
