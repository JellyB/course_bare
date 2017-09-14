package com.huatu.tiku.course.netschool.api.fall;

import com.huatu.tiku.course.netschool.api.HtmlServiceV1;
import feign.hystrix.FallbackFactory;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

/**
 * @author hanchao
 * @date 2017/9/5 18:04
 */
@Component
@Slf4j
public class HtmlServiceV1FallbackFactory implements FallbackFactory<HtmlServiceV1> {
    @Override
    public HtmlServiceV1 create(Throwable cause) {
        return new HtmlServiceV1() {
            @Override
            public String courseDetail(int rid) {
                log.error("html service v1 fallback,params: {}, fall back reason: ",rid,cause);
                return "服务器人太多了....";
            }
        };
    }
}
