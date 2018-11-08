package com.huatu.tiku.course.netschool.api.fall;

import com.huatu.tiku.course.bean.NetSchoolResponse;
import com.huatu.tiku.course.netschool.api.v4.AppServiceV4;
import com.huatu.tiku.course.service.cache.CourseCacheKey;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by lijun on 2018/7/5
 */
@Component
public class AppServiceV4Fallback implements AppServiceV4 {

    @Override
    public NetSchoolResponse lessionEvaluate(Map<String, Object> params) {
        return NetSchoolResponse.DEFAULT;
    }

    @Override
    public NetSchoolResponse lessionToken(String bjyRoomId, String bjySessionId, String videoId) {
        return NetSchoolResponse.DEFAULT;
    }

    /**
     * 课程合集 - 使用 RocksDB 实时缓存数据 响应失败回调
     */
    @Override
    public NetSchoolResponse collectionClasses(HashMap map) {
        String key = CourseCacheKey.collectionClassesKeyV4(map);
        return FallbackCacheHolder.get(key);
    }

    @Override
    public NetSchoolResponse specialColumn() {
        return NetSchoolResponse.DEFAULT;
    }
}
