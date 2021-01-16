package com.dx.springframework.servlet.springv2.aop.aspect;

import java.lang.reflect.Method;

public class DXAdvice {
    private Object target;
    private Method method;
    private String throwName;

    public DXAdvice(Object newInstance, Method method) {
        this.target=newInstance;
        this.method=method;
    }

    public Object getTarget() {
        return target;
    }

    public void setTarget(Object target) {
        this.target = target;
    }

    public Method getMethod() {
        return method;
    }

    public void setMethod(Method method) {
        this.method = method;
    }

    public String getThrowName() {
        return throwName;
    }

    public void setThrowName(String throwName) {
        this.throwName = throwName;
    }
}
