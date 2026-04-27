package com.flance.framework.common.datasource.config;

import java.lang.annotation.*;


@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface Schema {
    String value(); // 模式名
}