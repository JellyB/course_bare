package com.huatu.tiku.course.web.controller.v1;

import com.huatu.springboot.web.version.mapping.annotation.ApiVersion;
import com.huatu.tiku.course.service.v1.ProcessReportService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

/**
 * 描述：直播、录播、回放数据上报接口
 *
 * @author biguodong
 * Create time 2019-02-22 上午11:12
 **/
@RequestMapping(value = "/report")
@ApiVersion(value = "/v1")
@RestController
public class ProcessReportController {

    @Autowired
    private ProcessReportService processReportService;


    /**
     * 直播数据回放
     * @param params
     * @return
     */
    @PostMapping(value = "live")
    public Object report(@RequestBody Map<String,Object> params){
        return processReportService.liveReport(params);
    }

    /**
     * 录播、回放 数据上报
     * @param params
     * @return
     */
    @PostMapping(value = "playBack")
    public Object reportPlayBack(@RequestBody Map<String,Object> params){
        return processReportService.playBackReport(params);
    }


}
