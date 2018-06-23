package com.huatu.tiku.course.ztk.api.fail.paper;

import com.huatu.tiku.course.util.ZTKResponseUtil;
import com.huatu.tiku.course.ztk.api.v1.paper.PracticeCardServiceV1;
import org.springframework.stereotype.Component;

/**
 * Created by lijun on 2018/6/22
 */
@Component
public class PracticeCardServiceV1Fallback implements PracticeCardServiceV1 {

    @Override
    public Object createCourseExercisesPracticeCard(Integer terminal, Integer subject, Integer uid, String name, Integer courseType, Long courseId, String questionId) {
        return ZTKResponseUtil.defaultResult();
    }

    @Override
    public Object createCourseBreakPointPracticeCard(Integer terminal, Integer subject, Integer uid, String name, Integer courseType, Long courseId, String questionId) {
        return ZTKResponseUtil.defaultResult();
    }
}
