package com.dx.springframework.servlet.springv2.springmvc;

import java.lang.reflect.Method;
import java.util.regex.Pattern;

public class DXHandlerMapping {
    private Pattern pattern;
    private Method method;
    private Object controller;

    public DXHandlerMapping(Pattern pattern, Object controller, Method method) {
        this.pattern = pattern;
        this.method = method;
        this.controller = controller;
    }

    public Pattern getPattern() {
        return this.pattern;
    }

    public void setUrl(Pattern pattern) {
        this.pattern = pattern;
    }

    public Method getMethod() {
        return method;
    }

    public void setMethod(Method method) {
        this.method = method;
    }

    public Object getController() {
        return controller;
    }

    public void setController(Object controller) {
        this.controller = controller;
    }
}
