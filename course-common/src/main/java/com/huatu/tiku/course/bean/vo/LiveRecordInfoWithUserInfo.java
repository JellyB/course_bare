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
 * Create time 2019-03-23 2:30 PM
 **/
@NoArgsConstructor
@Getter
@Setter
public class LiveRecordInfoWithUserInfo implements Serializable{
    private LiveRecordInfo liveRecordInfo;
    private int userId;
    private int subject;
    private int terminal;

    @Builder
    public LiveRecordInfoWithUserInfo(LiveRecordInfo liveRecordInfo, int userId, int subject, int terminal) {
        this.liveRecordInfo = liveRecordInfo;
        this.userId = userId;
        this.subject = subject;
        this.terminal = terminal;
    }
}
