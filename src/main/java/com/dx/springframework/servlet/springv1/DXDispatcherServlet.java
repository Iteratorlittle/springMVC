package com.dx.springframework.servlet.springv1;


import com.dx.springframework.servlet.springv2.annotation.*;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.lang.annotation.Annotation;
import java.lang.reflect.*;

import java.net.URL;
import java.util.*;

//前端控制器
public class DXDispatcherServlet extends HttpServlet {

    private Map<String ,Object> ioc=new HashMap<String, Object>();

    private Properties contextConfig=new Properties();

    private List<String> classNames=new ArrayList();

    private Map<String, Method> handlerMapings=new HashMap<String, Method>();
    //初始化阶段
    @Override
    public void init(ServletConfig config) throws ServletException {
        //1.加载配置文件
        doLoadConfig(config.getInitParameter("contextConfigLocation"));

        //2.扫描相关的类
        doScanner(contextConfig.getProperty("ScanPackage"));

        //3.实例化相关的类，将实例缓存到IOC容器中
        doInstance();

        //4.DI,完成依赖注入
        doAutoWirted();

        //5.初始化HandlerMaping
        doInitHandlerMaping();

        System.out.println("DX_Spring 1.0 init");
    }

    private void doInitHandlerMaping() {
        //ioc为空
        if (ioc.isEmpty()){ return; }

        for (Map.Entry<String,Object> entry:ioc.entrySet()){
            Class clazz=entry.getValue().getClass();
            //如果该类加了DXController注解
            if (clazz.isAnnotationPresent(DXController.class)){
                String baseUrl="";
                if (clazz.isAnnotationPresent(DXRequestMapping.class)){
                    baseUrl = ((DXRequestMapping) clazz.getAnnotation(DXRequestMapping.class)).value();
                }
                //获取所有方法
                Method[] methods = clazz.getMethods();
                //遍历
                for (Method method:methods){
                    //如果controller中的方法加了DXRequestMapping注解
                    if (method.isAnnotationPresent(DXRequestMapping.class)){
                        //获取url并存储
                        String url=("/"+baseUrl+"/"+method.getAnnotation(DXRequestMapping.class).value()).replaceAll("//","/");
                        handlerMapings.put(url,method);
                    }
                }
            }
        }
    }

    private void doAutoWirted() {
        if (ioc.isEmpty()){ return; }

        //获取ioc容器中的每一个对象
        for (Map.Entry<String ,Object> entry:ioc.entrySet()){
            Field[] fields=entry.getValue().getClass().getDeclaredFields();
            for (Field field:fields){
                //如果该字段加了DXAutowired注解
                if(field.isAnnotationPresent(DXAutowired.class)){
                    try {
                        DXAutowired autowired=field.getAnnotation(DXAutowired.class);
                        String beanName=autowired.value().trim();
                        if ("".equals(beanName)){
                            beanName=field.getType().getName();
                        }
                        //强制赋值
                        field.setAccessible(true);
                        //注入
                        field.set(entry.getValue(),ioc.get(beanName));
                    } catch (IllegalAccessException e) {
                        e.printStackTrace();
                    }
                }
            }
        }
    }

    private void doInstance() {
        if (classNames.isEmpty()){
            return;
        }
        try {
            for (String classname:classNames){
                Class<?> clazz=Class.forName(classname);
                if (clazz.isAnnotationPresent(DXController.class) ) {
                    Object instance = clazz.newInstance();
                    String beanName = toFirstLower(clazz.getName());
                    ioc.put(beanName, instance);
                }else if (clazz.isAnnotationPresent(DXService.class)){
                    //1.默认是类名小写
                    Object instance = clazz.newInstance();
                    String beanName = toFirstLower(clazz.getName());

                    //2.不同包下出现同名，自定义命名
                    DXService service= clazz.getAnnotation(DXService.class);
                    if (!"".equals(service.value())){
                        beanName=service.value();
                    }
                    ioc.put(beanName, instance);

                    //3.接口
                    for (Class i:clazz.getInterfaces()){
                        if (ioc.containsKey(i.getName())){
                            throw new Exception("the beanName is exists!");
                        }
                        ioc.put(i.getName(), instance);
                    }
                }

            }
        }catch (ClassNotFoundException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (InstantiationException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
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

    private void doScanner(String scanPackage) {
        //获取class文件
        URL url=this.getClass().getClassLoader()
                .getResource("/"+scanPackage.replaceAll("\\.","/"));

        System.out.println(url.toString()+"--------");
        File files=new File(url.getFile());

        for (File file:files.listFiles()){
            if (file.isDirectory()){
                doScanner(scanPackage + "." +file.getName());
            }else {
                if (!file.getName().endsWith(".class")){
                    continue;
                }
                String className=scanPackage+"."+file.getName().replaceAll(".class","");
                classNames.add(className);
            }

        }


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

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        doPost(req,resp);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        //运行阶段
        try {
            doDispatch(req,resp);
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
    }

    private void doDispatch(HttpServletRequest req, HttpServletResponse resp) throws IOException, InvocationTargetException, IllegalAccessException {
        //拿到请求路径
        String url=req.getRequestURI().replaceAll(req.getContextPath(),"");

        if (!handlerMapings.containsKey(url)){
            resp.getWriter().print("400 Not Found!");
            resp.getWriter().close();
            return;
        }

        //获取url对应的处理方法
        Method method=this.handlerMapings.get(url);
        //获取请求参数
        Map<String, String[]> parameterMap=req.getParameterMap();
        //获取形参类型
        Class<?>[] parameterTypes = method.getParameterTypes();
        //声明实参列表
        Object[] parameters=new Object[parameterTypes.length];
        //获取参数注解
        Annotation[][] annotations= method.getParameterAnnotations();
        //构造实参列表
        for (int i=0;i<parameterTypes.length;i++){
            Class paramterType = parameterTypes[i];
            if (paramterType==HttpServletRequest.class){
                parameters[i]=req;
            }else if(paramterType==HttpServletResponse.class){
                parameters[i]=resp;
            }else if(paramterType==String.class){
                for (Annotation annotation:annotations[i]){
                    if (annotation instanceof DXRequestParam){
                        String paramName=((DXRequestParam) annotation).value();
                        if (!"".equals(paramName))
                        parameters[i]=Arrays.toString(parameterMap.get(paramName))
                                .replaceAll("\\[|\\]","")
                                .replaceAll("\\s","");
                    }
                }
            }
        }


        //beanName
        String beanName=toFirstLower(method.getDeclaringClass().getName());
        //执行方法
        method.invoke(ioc.get(beanName),parameters);
    }

}
