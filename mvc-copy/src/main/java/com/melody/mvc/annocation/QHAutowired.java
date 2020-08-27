package com.melody.mvc.annocation;

import java.lang.annotation.*;

/**
 * @author zqhuangc
 */
@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface QHAutowired {

    String value() default "";
}
