package com.huatu.tiku.course.bean.vo;

import lombok.*;

/**
 * 描述：
 *
 * @author biguodong
 * Create time 2019-03-05 10:41 AM
 **/


@NoArgsConstructor
@Setter
@Getter
public class CourseWorkListVo {

    private String courseWareTitle;
    private Long courseWareId;
    private String videoLength;
    private int serialNumber;
    private long answerCardId;
    private String questionIds;
    private String answerCardInfo;
    private Integer isAlert;

    @Builder
    public CourseWorkListVo(String courseWareTitle, Long courseWareId, String videoLength, int serialNumber, long answerCardId, String questionIds, String answerCardInfo, int isAlert) {
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
