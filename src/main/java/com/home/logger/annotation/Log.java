package com.home.logger.annotation;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/**
 * Put above the class
 */
@Retention(RetentionPolicy.RUNTIME)
public @interface Log {
    boolean returnValue() default false;
    boolean arguments() default false;
}
