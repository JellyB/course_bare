package com.huatu.tiku.course.netschool.api.fall;

import com.huatu.tiku.course.netschool.api.CourseServiceV1;
import com.huatu.tiku.course.netschool.bean.NetSchoolResponse;
import org.springframework.stereotype.Component;

import java.util.Map;

/**
 * @author hanchao
 * @date 2017/9/5 16:49
 */
@Component
public class CourseServiceFallback implements CourseServiceV1 {
    @Override
    public NetSchoolResponse totalList(Map<String, Object> params) {
        return NetSchoolResponse.DEFAULT;
    }

    @Override
    public NetSchoolResponse allCollectionList(Map<String, Object> params) {
        return NetSchoolResponse.DEFAULT;
    }

    @Override
    public NetSchoolResponse collectionDetail(Map<String, Object> params) {
        return NetSchoolResponse.DEFAULT;
    }

    @Override
    public NetSchoolResponse courseDetail(Map<String, Object> params) {
        return NetSchoolResponse.DEFAULT;
    }

    @Override
    public NetSchoolResponse myAndroidDetail(Map<String, Object> params) {
        return NetSchoolResponse.DEFAULT;
    }

    @Override
    public NetSchoolResponse myIosDetail(Map<String, Object> params) {
        return NetSchoolResponse.DEFAULT;
    }

    @Override
    public NetSchoolResponse getHandouts(Map<String, Object> params) {
        return NetSchoolResponse.DEFAULT;
    }

    @Override
    public NetSchoolResponse sendFree(Map<String, Object> params) {
        return NetSchoolResponse.DEFAULT;
    }
}
