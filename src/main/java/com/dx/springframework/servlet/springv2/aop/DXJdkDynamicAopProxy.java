package com.dx.springframework.servlet.springv2.aop;

import com.dx.springframework.servlet.springv2.aop.aspect.DXAdvice;
import com.dx.springframework.servlet.springv2.aop.support.DXAdiceSupport;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.Map;

public class DXJdkDynamicAopProxy implements InvocationHandler {
    private DXAdiceSupport config;
    public DXJdkDynamicAopProxy(DXAdiceSupport config) {
        this.config=config;
    }

    public Object getProxy() {
        return Proxy.newProxyInstance(config.getTargetClass().getClassLoader(),config.getTarget().getClass().getInterfaces(),this);
    }

    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        Map<String, DXAdvice> advices = this.config.getAdvice(method,this.config.getTargetClass());

        Object returnValue=null;
        //前置增强
        invokeAdvie(advices.get("before"));
        try {
            returnValue = method.invoke(this.config.getTarget(),args);
        }catch (Exception e){
            //异常通知
            invokeAdvie(advices.get("afterThrowing"));
        }
        //后置增强
        invokeAdvie(advices.get("after"));

        return returnValue;
    }

    private void invokeAdvie(DXAdvice advice) throws InvocationTargetException, IllegalAccessException {
        advice.getMethod().invoke(advice.getTarget());
    }
}
