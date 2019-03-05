package com.huatu.tiku.course.bean.practice;

import lombok.AllArgsConstructor;
import lombok.Data;

/**
 * Created by lijun on 2019/3/5
 */
@Data
@AllArgsConstructor
public class LiveCallbackBo {

    /**
     * 直播ID
     */
    private Long liveCourseId;

    /**
     * 录播ID
     */
    private Long recordCourseId;
}
