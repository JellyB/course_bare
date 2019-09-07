package com.huatu.tiku.course.bean.vo;


import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * 描述：申论课后作业信息
 *
 * @author biguodong
 * Create time 2019-08-30 2:54 PM
 **/


@Getter
@Setter
@NoArgsConstructor
public class EssayAnswerCardInfo extends AnswerCardInfo{

    /* 学员所得分数 */
    private double examScore;
    /*  试题总分 */
    private double score;
    /* 已完成数量 */
    private int fcount;

    private String clickContent;

    private Integer afterCoreseNum;
    // 0 单题 1 套题
    private Integer buildType;

    public EssayAnswerCardInfo(double examScore, double score, int fcount, String clickContent, Integer afterCoreseNum, Integer buildType) {
        this.examScore = examScore;
        this.score = score;
        this.fcount = fcount;
        this.clickContent = clickContent;
        this.afterCoreseNum = afterCoreseNum;
        this.buildType = buildType;
    }

    public EssayAnswerCardInfo(int status, int wcount, int ucount, int rcount, int qcount, long id, double examScore, double score, int fcount, String clickContent, Integer afterCoreseNum, Integer buildType) {
        super(status, wcount, ucount, rcount, qcount, id);
        this.examScore = examScore;
        this.score = score;
        this.fcount = fcount;
        this.clickContent = clickContent;
        this.afterCoreseNum = afterCoreseNum;
        this.buildType = buildType;
    }
}
