package com.huatu.tiku.course.spring.conf.web;

import com.huatu.common.spring.web.advice.WrapperResponseBodyAdvice;
import com.huatu.common.spring.web.resolver.CommonHandlerExceptionResolver;
import com.huatu.tiku.springboot.users.support.TokenMethodArgumentResolver;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.web.servlet.ServletComponentScan;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurerAdapter;

import java.util.List;

/**
 * @author hanchao
 * @date 2017/8/25 17:40
 */
@Configuration
@ServletComponentScan("com.huatu")//servlet扫描配置
public class WebMvcConfig extends WebMvcConfigurerAdapter {
    @Autowired
    private TokenMethodArgumentResolver tokenMethodArgumentResolver;

    @Override
    public void addArgumentResolvers(List<HandlerMethodArgumentResolver> argumentResolvers) {
        argumentResolvers.add(tokenMethodArgumentResolver);//用户session参数装配
        super.addArgumentResolvers(argumentResolvers);
    }

    @Bean
    public WrapperResponseBodyAdvice responseBodyAdvice(){
        return new WrapperResponseBodyAdvice();
    }

    /**
     * 统一异常处理
     * @return
     */
    @Bean
    public CommonHandlerExceptionResolver commonHandlerExceptionResolver(){
        return new CommonHandlerExceptionResolver();
    }
}
