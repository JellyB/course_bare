package com.huatu.tiku.course;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.netflix.feign.EnableFeignClients;

/**
 * @author hanchao
 * @date 2017/8/18 15:43
 */
@SpringBootApplication
@EnableFeignClients
public class CourseAppliaction {
    public static void main(String[] args){
        SpringApplication.run(CourseAppliaction.class,args);
    }
}
