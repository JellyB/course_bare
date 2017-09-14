package com.huatu.tiku.course.netschool.api.fall;

import com.huatu.tiku.course.netschool.api.CourseServiceV1;
import com.huatu.tiku.course.netschool.bean.NetSchoolResponse;
import feign.hystrix.FallbackFactory;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.Map;

/**
 * @author hanchao
 * @date 2017/9/5 17:55
 */
@Component
@Slf4j
public class CourseServiceFallbackFactory implements FallbackFactory<CourseServiceV1> {
    @Override
    public CourseServiceV1 create(Throwable cause) {
        return new CourseServiceV1() {
            @Override
            public NetSchoolResponse totalList(Map<String, Object> params) {
                log.error("fall back reason: ",cause);
                return NetSchoolResponse.DEFAULT;
            }

            @Override
            public NetSchoolResponse collectionList(Map<String, Object> params) {
                log.error("fall back reason: ",cause);
                return NetSchoolResponse.DEFAULT;
            }

            @Override
            public NetSchoolResponse allCollectionList(Map<String, Object> params) {
                log.error("fall back reason: ",cause);
                return NetSchoolResponse.DEFAULT;
            }

            @Override
            public NetSchoolResponse collectionDetail(Map<String, Object> params) {
                log.error("fall back reason: ",cause);
                return NetSchoolResponse.DEFAULT;
            }

            @Override
            public NetSchoolResponse courseDetail(Map<String, Object> params) {
                log.error("fall back reason: ",cause);
                return NetSchoolResponse.DEFAULT;
            }

            @Override
            public NetSchoolResponse myAndroidDetail(Map<String, Object> params) {
                log.error("fall back reason: ",cause);
                return NetSchoolResponse.DEFAULT;
            }

            @Override
            public NetSchoolResponse myIosDetail(Map<String, Object> params) {
                log.error("fall back reason: ",cause);
                return NetSchoolResponse.DEFAULT;
            }

            @Override
            public NetSchoolResponse getHandouts(Map<String, Object> params) {
                log.error("fall back reason: ",cause);
                return NetSchoolResponse.DEFAULT;
            }

            @Override
            public NetSchoolResponse sendFree(Map<String, Object> params) {
                log.error("fall back reason: ",cause);
                return NetSchoolResponse.DEFAULT;
            }
        };
    }
}
