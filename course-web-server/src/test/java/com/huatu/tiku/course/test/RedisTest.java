package com.huatu.tiku.course.test;

import com.huatu.common.test.BaseWebTest;
import com.huatu.tiku.course.service.VersionService;
import lombok.extern.slf4j.Slf4j;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;


/**
 * 描述：
 *
 * @author biguodong
 * Create time 2019-06-14 10:42 AM
 **/
@Slf4j
public class RedisTest extends BaseWebTest {


    @Autowired
    private VersionService versionService;


    @Test
    public void iosAudit(){
        String cv = "7.1.41";
        Boolean audit = versionService.isIosAudit(2, cv);
        log.error("audit --------- {}", audit);
    }
}
