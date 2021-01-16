package com.dx.springframework.servlet.springv2.annotation;

import java.lang.annotation.*;

@Target({ElementType.TYPE,ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface DXRequestMapping {
    String value() default "";
}
