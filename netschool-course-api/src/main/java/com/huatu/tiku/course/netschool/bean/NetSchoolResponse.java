package com.huatu.tiku.course.netschool.bean;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.cglib.beans.ImmutableBean;

/**
 * @author hanchao
 * @date 2017/8/25 18:40
 */
@Data
@NoArgsConstructor(force = true)
@AllArgsConstructor
@Builder
public class NetSchoolResponse<T> {
    /**
     * mock
     */
    public static final NetSchoolResponse DEFAULT = (NetSchoolResponse) ImmutableBean.create(new NetSchoolResponse(0,"",null));

    private int code;
    private String msg;
    private T data;
}
