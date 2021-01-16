package com.dx.springframework.servlet.springv2.beans.support;

import com.dx.springframework.servlet.springv2.beans.config.DXBeanDefinition;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

public class DXBeanDefinitionReader {
    private Properties contextConfig=new Properties();
    private List<String> registryBeanClasses=new ArrayList();

    public DXBeanDefinitionReader(String[] configLocations) {
        //加载
        doLoadConfig(configLocations[0]);
        //扫描
        doScanner(contextConfig.getProperty("ScanPackage"));
    }

    public List<DXBeanDefinition> doLoadBeanDefinition() {
        List<DXBeanDefinition> result=new ArrayList<DXBeanDefinition>();

        try {
            for (String registryBeanClass : registryBeanClasses) {
                System.out.println(registryBeanClass+"-----11");
                Class clazz = Class.forName(registryBeanClass);
                System.out.println(registryBeanClass+"-----22");
                if (clazz.isInterface()){continue;}
                result.add(doCreateBeanDefinition(toFirstLower(clazz.getSimpleName()),clazz.getName()));
                System.out.println(registryBeanClass+"-----33");
                for (Class i:clazz.getInterfaces()){
                    System.out.println(registryBeanClass+"----------s-"+i.getName());
                    result.add(doCreateBeanDefinition(i.getName(),clazz.getName()));
                }
                System.out.println(registryBeanClass+"-----44");
            }
        }catch (Exception e){
            e.printStackTrace();
        }

        for (DXBeanDefinition beanDefinition : result) {
            System.out.println("beanDefinition:"+beanDefinition.getFactoryBeanName()+"  "+beanDefinition.getBeanClassName());
        }
        return result;
    }

    private DXBeanDefinition doCreateBeanDefinition(String factoryBeanName, String beanClassName) {
        DXBeanDefinition beanDefinition=new DXBeanDefinition();
        beanDefinition.setFactoryBeanName(factoryBeanName);
        beanDefinition.setBeanClassName(beanClassName);
        return beanDefinition;
    }


    private void doLoadConfig(String contextConfigLocation) {
        InputStream is=this.getClass().getClassLoader().getResourceAsStream(contextConfigLocation);
        try {
            contextConfig.load(is);
        } catch (IOException e) {
            e.printStackTrace();
        }finally {
            if(null != is) {
                try {
                    is.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    private void doScanner(String scanPackage) {
        //获取class文件
        URL url=this.getClass().getClassLoader()
                .getResource("/"+scanPackage.replaceAll("\\.","/"));
        File files=new File(url.getFile());

        for (File file:files.listFiles()){
            if (file.isDirectory()){
                doScanner(scanPackage + "." +file.getName());
            }else {
                if (!file.getName().endsWith(".class")){
                    continue;
                }
                String className=scanPackage+"."+file.getName().replaceAll(".class","");
                registryBeanClasses.add(className);
            }

        }

    }

    //工具类，第一个字母大写转小写
    private String toFirstLower(String name) {
        char [] chars=name.toCharArray();
        //chars[0]+=32;
        if (chars[0]>'A'&&chars[0]<'Z')
            chars[0]+=32;
        return new String(chars);
    }

    public Properties getContextConfig() {
        return contextConfig;
    }
}
