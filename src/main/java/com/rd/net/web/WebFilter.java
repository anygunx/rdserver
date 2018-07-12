package com.rd.net.web;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * WebAction上的注解，分发
 *
 * @author Created by U-Demon on 2016年10月26日 下午1:24:04
 * @version 1.0.0
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
public @interface WebFilter {

    /**
     * Filter的名称
     *
     * @return
     */
    String filter() default "";

    /**
     * 请求是否解析成Json
     *
     * @return
     */
    boolean isJson() default false;

}
