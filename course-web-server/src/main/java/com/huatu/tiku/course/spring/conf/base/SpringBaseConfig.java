package com.huatu.tiku.course.spring.conf.base;

import com.ctrip.framework.apollo.spring.annotation.EnableApolloConfig;
import com.huatu.common.spring.web.converter.FormMessageConverter;
import com.huatu.tiku.springboot.users.support.EnableUserSessions;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

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
}
