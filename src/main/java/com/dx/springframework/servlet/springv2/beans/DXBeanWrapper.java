package com.dx.springframework.servlet.springv2.beans;

public class DXBeanWrapper {
    private Object wrapperInstance;
    private Class<?> wrapperClass;
    public DXBeanWrapper(Object instance) {
        this.wrapperInstance=instance;
        this.wrapperClass=instance.getClass();
    }

    public Object getWrapperInstance() {
        return wrapperInstance;
    }

    public Class<?> getWrapperClass() {
        return wrapperClass;
    }
}
