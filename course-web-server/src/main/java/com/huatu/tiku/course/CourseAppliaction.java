package com.huatu.tiku.course;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.netflix.feign.EnableFeignClients;
import org.springframework.context.annotation.ComponentScan;

/**
 * @author hanchao
 * @date 2017/8/18 15:43
 */
@SpringBootApplication
@ComponentScan("com.huatu")
@EnableFeignClients  // feign如果要做额外的配置，不能处于该包下，不能被component扫描到
public class CourseAppliaction {
    public static void main(String[] args){
        SpringApplication.run(CourseAppliaction.class,args);
    }
}
