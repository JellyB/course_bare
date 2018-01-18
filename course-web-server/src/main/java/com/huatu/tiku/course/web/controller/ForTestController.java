package com.huatu.tiku.course.web.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author hanchao
 * @date 2018/1/18 9:54
 */
@RequestMapping("/test")
@RestController
public class ForTestController {
    @GetMapping("/ex")
    public void exceptionTest(){
        throw new NullPointerException("抛出了一个空指针！");
    }
}
