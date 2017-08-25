package com.huatu.tiku.course.netschool.bean;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author hanchao
 * @date 2017/8/25 18:40
 */
@Data
@NoArgsConstructor(force = true)
@AllArgsConstructor
@Builder
public class NetSchoolResponse {
    /**
     * mock
     */
    public static final NetSchoolResponse DEFAULT = new NetSchoolResponse(0,"",null);

    private int code;
    private String msg;
    private Object data;
}
