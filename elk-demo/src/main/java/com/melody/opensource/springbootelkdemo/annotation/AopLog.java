package com.melody.opensource.springbootelkdemo.annotation;

import org.springframework.core.annotation.AliasFor;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface AopLog {

    String value() default "";

    /**
     * 是否启用
     */
    boolean enable() default true;

    @AliasFor("value")
    String description() default "";
}
