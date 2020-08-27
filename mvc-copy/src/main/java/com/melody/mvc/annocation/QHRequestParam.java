package com.melody.mvc.annocation;

import java.lang.annotation.*;

/**
 * @author zqhuangc
 */
@Target(ElementType.PARAMETER)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface QHRequestParam {
    String value() default "";
    boolean required() default false;
}
