package com.huatu.tiku.course.bean.vo;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.io.Serializable;

/**
 * 描述：
 *
 * @author biguodong
 * Create time 2019-08-31 5:30 PM
 **/


@Getter
@Setter
@NoArgsConstructor
public class EssayCourseWorkSyllabusInfo implements Serializable{

    private Long questionId;

    private Long paperId;

    private Integer questionType;

    private Long similarId;

    private String areaName;

    private String paperName;

    private Integer bizStatus;

    /* 退回原因（退回学员原因,其他类型原因均在操作日志表中查询）*/
    private String correctMemo;


    @Builder
    public EssayCourseWorkSyllabusInfo(Long questionId, Long paperId, Integer questionType, Long similarId, String areaName, String paperName, Integer bizStatus, String correctMemo) {
        this.questionId = questionId;
        this.paperId = paperId;
        this.questionType = questionType;
        this.similarId = similarId;
        this.areaName = areaName;
        this.paperName = paperName;
        this.bizStatus = bizStatus;
        this.correctMemo = correctMemo;
    }
}
