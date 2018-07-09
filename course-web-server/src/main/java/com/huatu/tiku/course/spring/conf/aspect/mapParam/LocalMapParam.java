package com.huatu.tiku.course.spring.conf.aspect.mapParam;

import java.lang.annotation.*;

/**
 * Created by lijun on 2018/7/9
 */
@Documented
@Target({ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
@Inherited
public @interface LocalMapParam {
    /**
     * 描述 -
     */
    String description() default "";

    /**
     * token - name 类型
     */
    TokenType tokenType() default TokenType.ZTK;

    /**
     * 是否需要校验 token 能够取到用户信息
     * @return
     */
    boolean checkToken() default false;
}
