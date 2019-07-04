package com.huatu.tiku.course.consts;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.io.Serializable;

/**
 * 描述：
 *
 * @author biguodong
 * Create time 2019-06-06 9:37 PM
 **/
@Getter
@Setter
@NoArgsConstructor
public class SimpleUserInfo implements Serializable {
    private String userName;
    private Integer userId;
    private Integer terminal;
    private String cv;
    private Integer subject;

    @Builder
    public SimpleUserInfo(String userName, Integer userId, Integer terminal, String cv, Integer subject) {
        this.userName = userName;
        this.userId = userId;
        this.terminal = terminal;
        this.cv = cv;
        this.subject = subject;
    }
}
