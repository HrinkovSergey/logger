package com.home.logger.annotation;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/**
 * Put above the class
 */
@Retention(RetentionPolicy.RUNTIME)
public @interface Log {
    boolean returnValue() default true;
    boolean arguments() default true;
}
