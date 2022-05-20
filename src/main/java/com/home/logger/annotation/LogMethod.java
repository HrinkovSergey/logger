package com.home.logger.annotation;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/**
 * Put above the method
 */
@Retention(RetentionPolicy.RUNTIME)
public @interface LogMethod {
    boolean returnValue() default true;
    boolean arguments() default true;
}
