package com.huatu.tiku.course.bean.practice;

import lombok.Builder;
import lombok.Data;

/**
 * Created by lijun on 2019/2/18
 */
@Data
@Builder
public class StudentRankBo {

    /**
     * 用户名
     */
    private String name;

    /**
     * 总时长
     */
    private String totalTime;

    /**
     * 总积分
     */
    private Integer totalIntegral;
}
