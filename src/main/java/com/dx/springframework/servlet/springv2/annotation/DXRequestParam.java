package com.dx.springframework.servlet.springv2.annotation;

import java.lang.annotation.*;

@Target(ElementType.PARAMETER)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface DXRequestParam {
    String value() default "";
}
