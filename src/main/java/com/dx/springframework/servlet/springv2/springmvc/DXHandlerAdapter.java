package com.dx.springframework.servlet.springv2.springmvc;



import com.dx.springframework.servlet.springv2.annotation.DXRequestParam;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

public class DXHandlerAdapter {

    public DXModelAndView handle(HttpServletRequest req, HttpServletResponse resp, DXHandlerMapping handler) throws Exception{
       //形参类型列表
        Map<String,Integer> parameIndexMapping=new HashMap<String, Integer>();
        Method method=handler.getMethod();
        Annotation[][] annotations= method.getParameterAnnotations();
        for (int i = 0; i < annotations.length; i++) {
            for (Annotation annotation : annotations[i]) {
                if (annotation instanceof DXRequestParam){
                    String parameName=((DXRequestParam) annotation).value();
                    if (parameName.trim()!=null){
                        parameIndexMapping.put(parameName,i);
                    }
                }
            }
        }

        Class<?>[] parameterTypes = method.getParameterTypes();
        for (int i = 0; i < parameterTypes.length; i++) {
            Class<?> parameterType=parameterTypes[i];
            if (parameterType==HttpServletResponse.class||parameterType==HttpServletRequest.class){
                parameIndexMapping.put(parameterType.getName(),i);
            }
        }


        //获取请求参数
        Map<String, String[]> parameterMap=req.getParameterMap();

        //声明实参列表
        Object[] parameters=new Object[parameterTypes.length];

        for (Map.Entry<String,String[]> param:parameterMap.entrySet()){
            String value=Arrays.toString(param.getValue())
                    .replaceAll("\\[|\\]","")
                    .replaceAll("\\s","");
            if (!parameIndexMapping.containsKey(param.getKey())){
                continue;
            }
            int index=parameIndexMapping.get(param.getKey());
            parameters[index]=caseStringValue(value,parameterTypes[index]);
        }

        if(parameIndexMapping.containsKey(HttpServletRequest.class.getName())){
            int index = parameIndexMapping.get(HttpServletRequest.class.getName());
            parameters[index] = req;
        }

        if(parameIndexMapping.containsKey(HttpServletResponse.class.getName())){
            int index = parameIndexMapping.get(HttpServletResponse.class.getName());
            parameters[index] = resp;
        }


        //执行方法
        Object reuslt=method.invoke(handler.getController(),parameters);
        if (reuslt==null || reuslt instanceof Void) {return null;}
        if (handler.getMethod().getReturnType()==DXModelAndView.class){
            return (DXModelAndView) reuslt;
        }
        return null;
    }

    private Object caseStringValue(String value, Class<?> parameterType) {
        if (parameterType==String.class){
            return value;
        }
        if (parameterType==Integer.class){
            return Integer.valueOf(value);
        }
        if (parameterType==Double.class){
            return Double.valueOf(value);
        }
        if (value!=null){
            return value;
        }
        return null;
    }
}
