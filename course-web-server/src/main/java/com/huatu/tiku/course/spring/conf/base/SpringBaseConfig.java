package com.huatu.tiku.course.spring.conf.base;

import com.ctrip.framework.apollo.spring.annotation.EnableApolloConfig;
import com.huatu.common.spring.web.converter.FormMessageConverter;
import com.huatu.tiku.springboot.users.support.EnableUserSessions;
import feign.Logger;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.ConfigurableEnvironment;


/**
 * @author hanchao
 * @date 2017/8/18 15:47
 */
@Configuration
@EnableApolloConfig
@EnableUserSessions
public class SpringBaseConfig {

    /**
     * 支持map转url encode
     * @return
     */
    @Bean
    public FormMessageConverter formMessageConverter(){
        return new FormMessageConverter();
    }

    @Bean
    //@Profile("")
    public Logger.Level feignLoggerLevel(ConfigurableEnvironment environment){
        if(environment.acceptsProfiles("product")){ //生产环境使用基本日志，否则使用完整日志
            return Logger.Level.BASIC;
        }else{
            return Logger.Level.FULL;
        }
    }

}
