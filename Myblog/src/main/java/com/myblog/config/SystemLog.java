package com.myblog.config;


import java.lang.annotation.*;


@Target({ElementType.PARAMETER, ElementType.METHOD})//作用于参数或方法上
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface SystemLog {
    String description() default "";
    LogTypeEnum type() default LogTypeEnum.OPERATION;
}
