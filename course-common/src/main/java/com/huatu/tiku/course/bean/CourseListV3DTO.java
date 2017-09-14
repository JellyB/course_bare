package com.huatu.tiku.course.bean;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.io.Serializable;
import java.util.List;

/**
 * @author hanchao
 * @date 2017/9/14 14:14
 */
@Data
public class CourseListV3DTO implements Serializable{
    private static final long serialVersionUID = 1L;

    private int next;
    private List<CourseInfo> result;

    @Data
    public static class CourseInfo implements Serializable{
        private static final long serialVersionUID = 1L;

        @JsonProperty
        private String StartDate;
        @JsonProperty
        private String SubjectName;
        private int isRushOut;
        private int isverify;
        private String tuijian;
        private int isSaleOut;
        private int isCollect;
        private int iszhibo;
        private String buyNum;
        @JsonProperty
        private String ActualPrice;
        private String rid;
        private String title;
        private String activeEnd;
        private String saleEnd;
        private String activeStart;
        @JsonProperty
        private String TeacherDesc;
        @JsonProperty
        private String ClassNo;
        private int isNormal;
        private String zhibo;
        private String count;
        private int isRushClass;
        @JsonProperty
        private String NetClassId;
        private String endDate;
        @JsonProperty
        private String CourseLength;
        private String scaleimg;
        private String limitUserCount;
        private String hitsNum;
        private String saleStart;
        private Integer isBuy;

    }
}
