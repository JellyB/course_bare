package com.huatu.tiku.course.bean.vo;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * 描述：
 *
 * @author biguodong
 * Create time 2019-03-06 7:34 PM
 **/

@NoArgsConstructor
@Getter
@Setter
public class CourseWorkWareVo {

    private String courseWareTitle;
    private long courseWareId;
    private String videoLength;
    private int serialNumber;
    private long answerCardId;
    private String questionIds;
    private String answerCardInfo;
    private int isAlert;

    @Builder
    public CourseWorkWareVo(String courseWareTitle, long courseWareId, String videoLength, int serialNumber, long answerCardId, String questionIds, String answerCardInfo, int isAlert) {
        this.courseWareTitle = courseWareTitle;
        this.courseWareId = courseWareId;
        this.videoLength = videoLength;
        this.serialNumber = serialNumber;
        this.answerCardId = answerCardId;
        this.questionIds = questionIds;
        this.answerCardInfo = answerCardInfo;
        this.isAlert = isAlert;
    }
}
