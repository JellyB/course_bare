package com.huatu.tiku.course.hbase.api.fail;

import com.huatu.tiku.course.hbase.api.v1.VideoServiceV1;
import org.springframework.stereotype.Component;

import java.util.HashMap;

/**
 * Created by lijun on 2018/5/21
 */
@Component
public class VideoServiceV1Fallback implements VideoServiceV1 {

    @Override
    public Object videoProcessDetailV1(String token, String terminal, String cv, HashMap<String, Object> params) {
        return null;
    }
}
