package com.dx.springframework.servlet.springv2.context;


import com.dx.springframework.servlet.springv2.annotation.DXAutowired;
import com.dx.springframework.servlet.springv2.annotation.DXController;
import com.dx.springframework.servlet.springv2.annotation.DXService;
import com.dx.springframework.servlet.springv2.aop.DXJdkDynamicAopProxy;
import com.dx.springframework.servlet.springv2.aop.aspect.DXAdvice;
import com.dx.springframework.servlet.springv2.aop.config.DXAopConfig;
import com.dx.springframework.servlet.springv2.aop.support.DXAdiceSupport;
import com.dx.springframework.servlet.springv2.beans.DXBeanWrapper;
import com.dx.springframework.servlet.springv2.beans.config.DXBeanDefinition;
import com.dx.springframework.servlet.springv2.beans.support.DXBeanDefinitionReader;

import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;

public class DXApplicationContext {
    private String[] configLocations;

    private DXBeanDefinitionReader reader;

    private Map<String,DXBeanDefinition> beanDefinitionMap= new HashMap<String,DXBeanDefinition>();

    private Map<String,DXBeanWrapper> facotryBeanInstanceCache=new HashMap<String, DXBeanWrapper>();

    private Map<String,Object> facotryObjectCache=new HashMap<String, Object>();


    public DXApplicationContext(String... configLocations) throws Exception {
        this.configLocations=configLocations;
        this.reader=new DXBeanDefinitionReader(this.configLocations);

        //加载配置文件
        List<DXBeanDefinition> beanDefinitions=null;
        beanDefinitions=reader.doLoadBeanDefinition();
        //注册到beanDefinitionMap
        doRegitryBeanDefinition(beanDefinitions);
        //创建IOC容器
        doCreateBean();
    }

    private void doCreateBean() {
        for (Map.Entry<String,DXBeanDefinition> beanDefinitionEntry:beanDefinitionMap.entrySet()){
            String beanName=beanDefinitionEntry.getKey();
            getBean(beanName);
        }
    }

    private void doRegitryBeanDefinition(List<DXBeanDefinition> beanDefinitions) throws Exception {
        for (DXBeanDefinition beanDefinition: beanDefinitions){
            if (this.beanDefinitionMap.containsKey(beanDefinition.getFactoryBeanName())){
                throw new Exception("The beanName"+beanDefinition.getFactoryBeanName()+"is exists!");
            }
            this.beanDefinitionMap.put(beanDefinition.getFactoryBeanName(),beanDefinition);
            this.beanDefinitionMap.put(beanDefinition.getBeanClassName(),beanDefinition);
        }
    }

    public Object getBean(Class className){
        return getBean(className.getName());
    }

    public Object getBean(String beanName){
        //1.拿到beanName对应的配置信息BeanDefinition
        DXBeanDefinition beanDefinition=this.beanDefinitionMap.get(beanName);

        beanDefinition.getFactoryBeanName();
        //2.实例化对象
        Object instance=instaniateBean(beanName,beanDefinition);
        //3.将实例化对象封装为BeanWrapper
        DXBeanWrapper beanWrapper=new DXBeanWrapper(instance);
        //4.将BeanWrapper缓存到IOC容器中
        facotryBeanInstanceCache.put(beanName,beanWrapper);
        //5.完成依赖注入
        populateBean(beanName,beanDefinition,beanWrapper);

        return facotryBeanInstanceCache.get(beanName).getWrapperInstance();
    }

    private void populateBean(String beanName, DXBeanDefinition beanDefinition, DXBeanWrapper beanWrapper) {
        Object instance=beanWrapper.getWrapperInstance();
        Class beanClass=beanWrapper.getWrapperClass();
        if (!(beanClass.isAnnotationPresent(DXController.class)||beanClass.isAnnotationPresent(DXService.class))){
            return;
        }
            for (Field field:beanClass.getDeclaredFields()){
                //如果该字段加了DXAutowired注解
                if(field.isAnnotationPresent(DXAutowired.class)){
                    try {
                        DXAutowired autowired=field.getAnnotation(DXAutowired.class);
                        String aotowiredBeanName=autowired.value().trim();
                        if ("".equals(aotowiredBeanName)){
                            aotowiredBeanName=field.getType().getName();
                        }
                        //强制赋值
                        field.setAccessible(true);
                        //注入
                        field.set(instance,this.facotryBeanInstanceCache.get(aotowiredBeanName).getWrapperInstance());
                    } catch (IllegalAccessException e) {
                        e.printStackTrace();
                        continue;
                    }
                }
            }

    }

    private Object instaniateBean(String beanName, DXBeanDefinition beanDefinition) {
        String className=beanDefinition.getBeanClassName();
        Object instance=null;
        try {
            Class<?> clazz=Class.forName(className);
            instance=clazz.newInstance();

            //aop保留代码空间

            DXAdiceSupport config =instantionAopConfig(beanDefinition);
            config.setTargetClass(clazz);
            config.setTarget(instance);
            //如果满足切面规则，生成代理类，覆盖原生类
            if (config.pointCutMatch()){
                instance=new DXJdkDynamicAopProxy(config).getProxy();
            }

            facotryObjectCache.put(beanName,instance);
        }catch (Exception e){
            e.printStackTrace();
        }

        return instance;
    }

    private DXAdiceSupport instantionAopConfig(DXBeanDefinition beanDefinition) {
        DXAopConfig config=new DXAopConfig();
        config.setPointCut(this.reader.getContextConfig().getProperty("pointCut"));
        config.setAspectClass(this.reader.getContextConfig().getProperty("aspectClass"));
        config.setAspectBefore(this.reader.getContextConfig().getProperty("aspectBefore"));
        config.setAspecAfter(this.reader.getContextConfig().getProperty("aspectAfter"));
        config.setAspecAfterThrow(this.reader.getContextConfig().getProperty("aspectAfterThrow"));
        config.setAspecAfterThrowingName(this.reader.getContextConfig().getProperty("aspectAfterThrowingName"));

        return new DXAdiceSupport(config);
    }

    public int getBeanDefintionCount() {
        return beanDefinitionMap.size();
    }

    public String[] getBeanDefintionNames() {
        String [] beanDefintionNames=new String[beanDefinitionMap.keySet().size()];
        return beanDefinitionMap.keySet().toArray(beanDefintionNames);
    }

    public Properties getConfig() {
        return reader.getContextConfig();
    }
}
