package com.huatu.tiku.course.bean.vo;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * 描述：
 *
 * @author biguodong
 * Create time 2019-03-05 5:35 PM
 **/


@NoArgsConstructor
@Getter
@Setter
public class SyllabusWareInfo {
    private String className;
    private Long classId;
    private Long syllabusId;
    private String coursewareName;
    private Long coursewareId;
    private Integer serialNumber;
    private Integer videoType;
    private String length;
    private String roomId;

    @Builder

    public SyllabusWareInfo(String className, Long classId, Long syllabusId, String coursewareName, Long coursewareId, Integer serialNumber, Integer videoType, String length, String roomId) {
        this.className = className;
        this.classId = classId;
        this.syllabusId = syllabusId;
        this.coursewareName = coursewareName;
        this.coursewareId = coursewareId;
        this.serialNumber = serialNumber;
        this.videoType = videoType;
        this.length = length;
        this.roomId = roomId;
    }
}
