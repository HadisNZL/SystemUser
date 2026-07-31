package com.system.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 接口限流注解。
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface RateLimit {

    /**
     * 限流窗口秒数。
     */
    int seconds() default 60;

    /**
     * 窗口内最大请求次数。
     */
    int maxCount() default 10;

    /**
     * 业务限流标识，默认使用请求路径。
     */
    String key() default "";
}
