package com.huatu.tiku.course.test;


import java.util.concurrent.ExecutionException;

import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import com.alibaba.fastjson.JSONObject;
import com.huatu.common.exception.BizException;
import com.huatu.common.test.BaseWebTest;
import com.huatu.common.utils.date.TimeMark;
import com.huatu.tiku.course.service.v6.PeriodTestServiceV6;
import com.huatu.tiku.course.util.EncryptUtils;
import com.huatu.ztk.paper.vo.PeriodTestSubmitlPayload;

import lombok.extern.slf4j.Slf4j;

/**
 * 阶段测试
 */
@Slf4j
public class PeriodTest extends BaseWebTest {
	@Autowired
	private PeriodTestServiceV6 periodTestServiceV6;
	
	@Autowired
	private EncryptUtils encryptUtil;
	
    @Test
    public void testUpload() throws InterruptedException, ExecutionException, BizException {
        TimeMark timeMark = TimeMark.newInstance();
        PeriodTestSubmitlPayload payload =  PeriodTestSubmitlPayload.builder().syllabusId(8361872L).userName("app_ztk620567022").isFinish(1).build();
		periodTestServiceV6.uploadPeriodStatus2PHP(payload);
        log.info(">>>>>>>>> payload request complete,used {} mills,total cost {} mills...",timeMark.mills(),timeMark.totalMills());

    }
    
    /**
     * 获取直播回调地址
     */
    @Test
    public void testBJY() {
    	JSONObject json = encryptUtil.getClassCallBackUrl();
    	log.info("获取直播回调地址:{}",json.toJSONString());
    }
    
    /**
     * 设置直播回调地址
     */
    @Test
    public void testSetCallBackUrlBJY() {
    	JSONObject json = encryptUtil.setClassCallBackUrl();
    	log.info("获取直播回调地址:{}",json.toJSONString());
    }
}
