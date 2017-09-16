package com.huatu.tiku.course.netschool.api.fall;

import com.huatu.tiku.course.netschool.api.v3.CourseServiceV3;
import feign.hystrix.FallbackFactory;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

/**
 * @author hanchao
 * @date 2017/9/13 15:14
 */
@Component
@Slf4j
public class CourseServiceV3FallbackFactory implements FallbackFactory<CourseServiceV3> {
    @Override
    public CourseServiceV3 create(final Throwable cause) {
        return null;
    }
}
