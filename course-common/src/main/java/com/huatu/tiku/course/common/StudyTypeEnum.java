package com.huatu.tiku.course.common;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 描述：
 *
 * @author biguodong
 * Create time 2019-02-15 下午5:08
 **/
@AllArgsConstructor
@Getter
public enum  StudyTypeEnum {

    COURSE_WORK("courseWork"),
    PERIOD_TEST("periodTest");
    private String type;
}
