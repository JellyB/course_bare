package com.huatu.tiku.course.bean;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.io.Serializable;
import java.util.List;

/**
 * @author hanchao
 * @date 2017/9/13 13:23
 */
@Data
public class CourseListV2DTO implements Serializable{
    private static final long serialVersionUID = 1L;


    private int next;
    private List<CourseInfo> result;

    @Data
    public static class CourseInfo implements Serializable {
        private static final long serialVersionUID = 1L;
        /**
         * rid : 60984
         * title : 2018年国考小班密训营十二期1班
         * TeacherDesc : 张冬菊，贺睿锐 , 李洪军，杨洁，刘邵思，李玲玲 ,  左宏帅 , 曾舟 ,李彩风 ,  刘有珍 ,车春艺
         * ActualPrice : 980
         * scaleimg : http://upload.htexam.net/classimg/class/1497428148.jpg
         * StartDate : 9月16日
         * limitUserCount : 50
         * SubjectName : 行测＋申论
         * ClassNo : ZH_0_K_1_20170911172747
         * tuijian : 5
         * iszhibo : 0
         * zhibo :
         * isCollect : 0
         * NetClassId : 60984
         * endDate : 11月16日
         * CourseLength : 500 课时
         * isRushClass : 1
         * hitsNum : 23
         * isNormal : 0
         * isSaleOut : 0
         * isRushOut : 0
         * buyNum : 23
         * isverify : 1
         * count : 23
         * activeStart : 09月11日 00:00
         * activeEnd : 09月17日 00:00
         * saleStart : 0
         * saleEnd : 297465
         */

        private String rid;
        private String title;
        @JsonProperty
        private String TeacherDesc;
        @JsonProperty
        private String ActualPrice;
        private String scaleimg;
        @JsonProperty
        private String StartDate;
        private String limitUserCount;
        @JsonProperty
        private String SubjectName;
        @JsonProperty
        private String ClassNo;
        private String tuijian;
        private Integer iszhibo;
        private String zhibo;
        private Integer isCollect;
        @JsonProperty
        private String NetClassId;
        private String endDate;
        @JsonProperty
        private String CourseLength;
        private Integer isRushClass;
        private String hitsNum;
        private Integer isNormal;
        private Integer isSaleOut;
        private Integer isRushOut;
        private String buyNum;
        private Integer isverify;
        private String count;
        private String activeStart;
        private String activeEnd;
        private String saleStart;
        private String saleEnd;
        private Integer isBuy;

    }
}
