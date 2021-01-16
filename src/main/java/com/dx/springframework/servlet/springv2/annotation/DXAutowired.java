package com.dx.springframework.servlet.springv2.annotation;

import java.lang.annotation.*;

@Target({ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface DXAutowired {
    String value() default "";
}

