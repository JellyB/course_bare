package com.huatu.tiku.course.service.v1.impl.practice;

import com.huatu.tiku.course.service.v1.practice.PracticeQuestionInfoService;
import com.huatu.tiku.entity.CoursePracticeQuestionInfo;
import org.springframework.stereotype.Service;
import service.impl.BaseServiceHelperImpl;

/**
 * Created by lijun on 2019/3/7
 */
@Service
public class PracticeQuestionInfoServiceImpl extends BaseServiceHelperImpl<CoursePracticeQuestionInfo> implements PracticeQuestionInfoService {

    public PracticeQuestionInfoServiceImpl() {
        super(CoursePracticeQuestionInfo.class);
    }
}
