package com.huatu.tiku.course.bean;

import lombok.Data;

/**
 * @author hanchao
 * @date 2017/9/13 13:15
 */
@Data
public class CourseDetailV2 {
    /**
     * classInfo : {"rid":"55495","Title":"2017教师资格证笔试-教育教学知识与能力（小学）","limitUserCount":0,"TimeLength":"72","ActualPrice":59,"TypeName":"系统精讲班","startTime":0,"stopTime":0,"scaleimg":"http://upload.htexam.net/classimg/class/1482392281.jpg","studyDate":"1月7日-3月31日","TeacherName":"高倩倩，孟贞贞"}
     */

    private ClassInfo classInfo;

    @Data
    public static class ClassInfo {
        /**
         * rid : 55495
         * Title : 2017教师资格证笔试-教育教学知识与能力（小学）
         * limitUserCount : 0
         * TimeLength : 72
         * ActualPrice : 59
         * TypeName : 系统精讲班
         * startTime : 0
         * stopTime : 0
         * scaleimg : http://upload.htexam.net/classimg/class/1482392281.jpg
         * studyDate : 1月7日-3月31日
         * TeacherName : 高倩倩，孟贞贞
         */

        private String rid;
        private String Title;
        private int limitUserCount;
        private String TimeLength;
        private int ActualPrice;
        private String TypeName;
        private int startTime;
        private int stopTime;
        private String scaleimg;
        private String studyDate;
        private String TeacherName;

    }
}
