package com.huatu.tiku.course.web.controller;

import com.huatu.common.exception.UnauthorizedException;
import com.huatu.common.utils.collection.HashMapBuilder;
import com.huatu.springboot.report.annotation.WebReport;
import com.huatu.springboot.report.product.ExtraDataHandler;
import com.huatu.tiku.common.bean.report.ReportMessage;
import com.huatu.tiku.common.bean.report.WebReportMessage;
import com.huatu.tiku.common.bean.user.UserSession;
import com.huatu.tiku.springboot.users.support.Token;
import org.springframework.stereotype.Component;
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

    @WebReport(value = "test",extraHandler = TestReportExtraHandler.class)
    @RequestMapping("/report")
    public Object report(@Token UserSession session){
        return HashMapBuilder.newBuilder().put("llll","llsdfdfd").buildUnsafe();
    }

    @GetMapping("/exm")
    public void exMessage(){
        throw new UnauthorizedException("测试异常");
    }

    @Component
    public static class TestReportExtraHandler implements ExtraDataHandler {

        @Override
        public Object extra(ReportMessage reportMessage) {
            if(reportMessage instanceof WebReportMessage){
                String body = ((WebReportMessage) reportMessage).getBody();
                //比如用户请求的是json或者什么的，我们需要保存某些信息，或者计算某些信息(无法从response直接取得数据去判断，而且和controller异步执行，所以可能需要重复逻辑)
                return body.length();
            }
            return null;
        }
    }

}
