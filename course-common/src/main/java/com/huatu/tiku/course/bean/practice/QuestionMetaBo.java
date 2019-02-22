package com.huatu.tiku.course.bean.practice;

/**
 * 试题统计信息
 * Created by lijun on 2019/2/18
 */
public class QuestionMetaBo {

    /**
     * ID
     */
    private Integer id;

    /**
     * 做题次数
     */
    private int count;

    /**
     * 用户选择答案列表
     */
    private int[] percents;

    /**
     * 正确答案序号
     */
    private Integer rightIndex;

    /**
     * 平均答题用时
     */
    private Integer avgTime;

    /**
     * 剩余时间
     */
    private Integer remainingTime;

}
