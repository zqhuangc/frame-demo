package com.melody.mvc.annocation;

import java.lang.annotation.*;

/**
 * @author zqhuangc
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface QHController {

    String value() default "";
}
