package com.dx.springframework.servlet.springv2.aop.support;

import com.dx.springframework.servlet.springv2.aop.aspect.DXAdvice;
import com.dx.springframework.servlet.springv2.aop.config.DXAopConfig;

import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class DXAdiceSupport {
    private Class<?> targetClass;
    private Object target;

    private DXAopConfig config;
    private Pattern pointCutClassPattern;
    private Map<Method,Map<String,DXAdvice>> methodCache;


    public DXAdiceSupport(DXAopConfig config) {
        this.config=config;
    }

    public boolean pointCutMatch() {
        return this.pointCutClassPattern.matcher(this.targetClass.getName()).matches();
    }

    public void setTargetClass(Class<?> clazz) {
        this.targetClass=clazz;
        parse();
    }

    private void parse() {
        String pointCut = config.getPointCut()
                .replaceAll("\\.","\\\\.")
                .replaceAll("\\\\.\\*",".*")
                .replaceAll("\\(","\\\\(")
                .replaceAll("\\)","\\\\)");

        String pointCutForClassRegex=pointCut.substring(pointCut.indexOf(" ")+1,pointCut.lastIndexOf("\\(")-4);

        pointCutClassPattern=Pattern.compile(pointCutForClassRegex);

        methodCache=new HashMap<Method, Map<String, DXAdvice>>();
        Pattern pointCutPattern=Pattern.compile(pointCut);
        try {
            Map<String, Method> aspectMethods = new HashMap<String, Method>();
            Class aspectClass=Class.forName(this.config.getAspectClass());
            for (Method method : aspectClass.getMethods()) {
                aspectMethods.put(method.getName(),method);
            }

            for (Method method : this.targetClass.getMethods()) {
                String methodString=method.toString();
                if (methodString.contains("thows")){
                    methodString=methodString.substring(0,methodString.lastIndexOf("thows")).trim();
                }
                Matcher matcher=pointCutPattern.matcher(methodString);
                if (matcher.matches()){
                    Map<String,DXAdvice> advices=new HashMap<String, DXAdvice>();
                    if (!(null==this.config.getAspectBefore() || "".equals(this.config.getAspectBefore()))){
                        advices.put("before",new DXAdvice(aspectClass.newInstance(),aspectMethods.get(this.config.getAspectBefore())));
                    }
                    if (!(null==this.config.getAspecAfter() || "".equals(this.config.getAspecAfter()))){
                        advices.put("after",new DXAdvice(aspectClass.newInstance(),aspectMethods.get(this.config.getAspecAfter())));
                    }
                    if (!(null==this.config.getAspecAfterThrow() || "".equals(this.config.getAspecAfterThrow()))){

                        DXAdvice advice=new DXAdvice(aspectClass.newInstance(),aspectMethods.get(this.config.getAspecAfterThrow()));
                        advice.setThrowName(this.config.getAspecAfterThrowingName());
                        advices.put("afterThrowing",advice);
                    }
                    methodCache.put(method,advices);
                }
            }
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    public void setTarget(Object instance) {
        this.target=instance;
    }

    public Class<?> getTargetClass() {
        return targetClass;
    }

    public Object getTarget() {
        return target;
    }

    public Map<String, DXAdvice> getAdvice(Method method, Class<?> targetClass) throws NoSuchMethodException {
        Map<String, DXAdvice> cache = methodCache.get(method);
        if (null==cache){
            Method m=targetClass.getMethod(method.getName(),method.getParameterTypes());
            cache=methodCache.get(m);
            methodCache.put(m,cache);
        }
        return cache;
    }
}
