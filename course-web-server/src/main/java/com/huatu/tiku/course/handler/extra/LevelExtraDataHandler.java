package com.huatu.tiku.course.handler.extra;

import com.huatu.springboot.report.product.ExtraDataHandler;
import com.huatu.tiku.common.bean.report.ReportMessage;
import com.huatu.tiku.common.bean.report.WebReportMessage;
import org.springframework.stereotype.Component;

/**
 * @author hanchao
 * @date 2018/1/15 14:06
 */
@Component
public class LevelExtraDataHandler implements ExtraDataHandler {
    @Override
    public Object extra(ReportMessage reportMessage) {
        if(reportMessage instanceof WebReportMessage){
            ((WebReportMessage) reportMessage).getBody();
            return "测试数据";
        }
        return null;
    }
}
