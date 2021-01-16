package com.dx.springframework.servlet.springv2.annotation;

import java.lang.annotation.*;

@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface DXService {
    String value() default "";
}
