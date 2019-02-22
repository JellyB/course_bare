package com.huatu.tiku.course.bean.practice;

import lombok.Builder;
import lombok.Data;

/**
 * Created by lijun on 2019/2/19
 */
@Data
@Builder
public class StudentQuestionMetaBo extends QuestionMetaBo{

    /**
     * 正确答案
     */
    private String rightAnswer;

    /**
     * 用户答案
     */
    private String userAnswer;

    /***
     * 用时
     */
    private Integer time;

    /**
     * 已测试题总量
     */
    private Integer totalQuestionNum;

    /**
     * 用户已答试题数量
     */
    private Integer userQuestionNum;

    /**
     * 正确试题数量
     */
    private Integer rightQuestionNum;
}
