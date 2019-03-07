package com.huatu.tiku.course.common;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 描述：
 *
 * @author biguodong
 * Create time 2019-03-06 5:22 PM
 **/
@Getter
@AllArgsConstructor
public enum  BizStatusEnum {

    UN_DONE(0, "未做"),
    UN_FINISH(1, "未完成"),
    FINISHED(2, "已完成");
    private int key;
    private String value;
}
