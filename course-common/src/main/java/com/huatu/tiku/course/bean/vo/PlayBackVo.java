package com.huatu.tiku.course.bean.vo;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * 描述：
 *
 * @author biguodong
 * Create time 2019-03-15 10:18 AM
 **/

@NoArgsConstructor
@Getter
@Setter
public class PlayBackVo {

    private long syllabusId;
    private String userName;

    @Builder
    public PlayBackVo(long syllabusId, String userName) {
        this.syllabusId = syllabusId;
        this.userName = userName;
    }
}
