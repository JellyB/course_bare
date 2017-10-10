package com.huatu.tiku.course;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.SpringBootConfiguration;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.cloud.netflix.feign.EnableFeignClients;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.ComponentScans;
import org.springframework.stereotype.Controller;

/**
 * @author hanchao
 * @date 2017/8/18 15:43
 */

@ComponentScans({@ComponentScan(value = "com.huatu.tiku.course",excludeFilters = @ComponentScan.Filter(Controller.class)),
                 @ComponentScan(value = "com.huatu.tiku.course.web.controller.v3",includeFilters = @ComponentScan.Filter(Controller.class))})
@EnableAutoConfiguration
@EnableFeignClients  // feign如果要做额外的配置，不能处于该包下，不能被component扫描到
@SpringBootConfiguration
public class CourseAppliaction {
    public static void main(String[] args){
        SpringApplication.run(CourseAppliaction.class, args);
    }
}
