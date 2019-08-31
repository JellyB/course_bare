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

    private int correctNum;
    /* 学员所得分数 */
    private double examScore;
    /*  试题总分 */
    private double score;

    private long similarId;

    private int questionType;
    private Long questionBaseId;
    private Long paperId;

    public EssayAnswerCardInfo(int correctNum, double examScore, double score, long similarId, int questionType, Long questionBaseId, Long paperId) {
        this.correctNum = correctNum;
        this.examScore = examScore;
        this.score = score;
        this.similarId = similarId;
        this.questionType = questionType;
        this.questionBaseId = questionBaseId;
        this.paperId = paperId;
    }

    public EssayAnswerCardInfo(int type, int status, int wcount, int ucount, int rcount, int qcount, long id, int correctNum, double examScore, double score, long similarId, int questionType, Long questionBaseId, Long paperId) {
        super(type, status, wcount, ucount, rcount, qcount, id);
        this.correctNum = correctNum;
        this.examScore = examScore;
        this.score = score;
        this.similarId = similarId;
        this.questionType = questionType;
        this.questionBaseId = questionBaseId;
        this.paperId = paperId;
    }
}
