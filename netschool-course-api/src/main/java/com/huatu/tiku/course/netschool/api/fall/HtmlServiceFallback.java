package com.huatu.tiku.course.netschool.api.fall;

import com.huatu.tiku.course.netschool.api.HtmlServiceV1;
import org.springframework.stereotype.Component;

/**
 * @author hanchao
 * @date 2017/9/5 16:56
 */
@Component
public class HtmlServiceFallback implements HtmlServiceV1 {
    @Override
    public String courseDetail(int rid) {
        return "服务器人太多了....";
    }
}
