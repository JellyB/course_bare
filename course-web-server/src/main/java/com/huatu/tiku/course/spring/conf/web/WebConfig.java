package com.huatu.tiku.course.spring.conf.web;

import com.huatu.springboot.web.tools.exception.GlobalExceptionHandler;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.validation.beanvalidation.MethodValidationPostProcessor;

/**
 * @author hanchao
 * @date 2017/9/19 12:22
 */
@Configuration
public class WebConfig {
    /**
     * 统一异常处理
     * @return
     */
    @Bean
    public GlobalExceptionHandler globalExceptionHandler(){
        return new GlobalExceptionHandler();
    }

    /**
     * 方法级别的验证
     * @return
     */
    @Bean
    public MethodValidationPostProcessor methodValidationPostProcessor() {
        return new MethodValidationPostProcessor();
    }

}
