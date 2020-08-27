package com.melody.mvc.annocation;

import java.lang.annotation.*;

/**
 * @author zqhuangc
 */
@Target({ElementType.TYPE,ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface QHRequestMapping {
    String value() default "";
}
