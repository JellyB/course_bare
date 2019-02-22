package com.huatu.tiku.course.common;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * @创建人 lizhenjuan
 * @创建时间 2019/2/18
 * @描述
 */
@AllArgsConstructor
@Getter
public enum ArticleTypeListEnum {

    HOT(1, 1, "热门"),
    SHI_ZHENG_HOT(2, 2, "时政热点"),
    ANSWER_SKILL(3, 3, "答题技巧");

    private Integer code;
    private Integer sort;
    private String name;

}
