package com.huatu.tiku.course.spring.conf.base;

import com.ctrip.framework.apollo.spring.annotation.EnableApolloConfig;
import org.springframework.context.annotation.Configuration;

/**
 * @author hanchao
 * @date 2017/9/22 15:26
 */
@Configuration
@EnableApolloConfig("htonline.metrics")
public class MetricsConfig {

}
