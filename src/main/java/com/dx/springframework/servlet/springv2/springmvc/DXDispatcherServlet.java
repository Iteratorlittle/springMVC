package com.dx.springframework.servlet.springv2.springmvc;


import com.dx.springframework.servlet.springv2.annotation.DXController;
import com.dx.springframework.servlet.springv2.annotation.DXRequestMapping;
import com.dx.springframework.servlet.springv2.context.DXApplicationContext;
//import com.sun.xml.internal.ws.wsdl.writer.document.Part;


import javax.servlet.RequestDispatcher;
import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.*;
import java.lang.reflect.Method;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

//前端控制器
public class DXDispatcherServlet extends HttpServlet {
    private DXApplicationContext context;

    private List<DXHandlerMapping> handlerMapings=new ArrayList<DXHandlerMapping>();
    private Map<DXHandlerMapping, DXHandlerAdapter> handlerAdapters=new HashMap<DXHandlerMapping, DXHandlerAdapter>();
    private List<DXViewResolver> viewResolvers=new ArrayList<DXViewResolver>();

    //初始化阶段
    @Override
    public void init(ServletConfig config) throws ServletException {

        try {
            context=new DXApplicationContext(config.getInitParameter("contextConfigLocation"));
        } catch (Exception e) {
            e.printStackTrace();
        }
        initStrategies(context);
        System.out.println("DX_Spring 1.0 init");

    }

    protected void initStrategies(DXApplicationContext context) {
        //初始化
        initHandlerMappings(context);
        initHandlerAdapters(context);
        initViewResolvers(context);
    }

    private void initViewResolvers(DXApplicationContext context) {
        String templateRoot=context.getConfig().getProperty("templateRoot");
        String templateRootPath=this.getClass().getClassLoader().getResource(templateRoot).getFile();

        File templateRootDir=new File(templateRootPath);
        for (File file : templateRootDir.listFiles()) {
            this.viewResolvers.add(new DXViewResolver(templateRoot));
        }
    }

    private void initHandlerAdapters(DXApplicationContext context) {
        for (DXHandlerMapping handlerMaping : handlerMapings) {
            handlerAdapters.put(handlerMaping,new DXHandlerAdapter());
        }
    }

    private void initHandlerMappings(DXApplicationContext context) {

            //ioc容器为空
            if (context.getBeanDefintionCount()==0){ return; }

            String[] beanNames=context.getBeanDefintionNames();

            for (String beanName : beanNames) {
                System.out.println("BeanName : " +beanName);
                Object instance=context.getBean(beanName);
                Class<?> clazz=instance.getClass();
                String baseUrl="";
                //如果该类加了DXController注解
                if (clazz.isAnnotationPresent(DXController.class)){
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
                            String url=("/"+baseUrl+"/"+method.getAnnotation(DXRequestMapping.class).value())
                                    .replaceAll("//","/")
                                    .replaceAll("\\*",".*");
                            Pattern pattern=Pattern .compile(url);
                            handlerMapings.add(new DXHandlerMapping(pattern,instance,method));
                            System.out.println("Mapped : " + url + "," + method);
                        }
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
        //判断文件
        String type=req.getContentType();
        if (null!=type) {
            upFile(req, resp);
        }else {
            //运行阶段
            try {
                doDispatch(req, resp);
            } catch (Exception e) {
                e.printStackTrace();
                Map<String, Object> model = new HashMap<String, Object>();
                model.put("detail", "500 Exception detail");
                model.put("stackTrace", Arrays.toString(e.getStackTrace()));
                processDispatchResult(req, resp, new DXModelAndView("500", model));
            }
        }
    }

    private void doDispatch (HttpServletRequest req, HttpServletResponse resp) throws Exception{
        //根据url找到对应的handlerMapping
        DXHandlerMapping handler=getHandler(req);

        if (null==handler){
            processDispatchResult(req,resp,new DXModelAndView("404"));
            return;
        }

        //根据url找到对应的handlerMapping找到对应的handlerAdapter
        DXHandlerAdapter handlerAdapter =getHandlerAdapter(handler);

        //根据handlerAdapter的匹配结果，返回ModelAndView
        DXModelAndView modelAndView=handlerAdapter.handle(req,resp,handler);

        //根据ModelAndView内容匹配对应的ViewResolver
        processDispatchResult(req,resp,modelAndView);
    }

    private DXHandlerAdapter getHandlerAdapter(DXHandlerMapping handler) {
        if (this.handlerAdapters.isEmpty()){
            return null;
        }
        return this.handlerAdapters.get(handler);
    }

    private void processDispatchResult(HttpServletRequest req, HttpServletResponse resp, DXModelAndView modelAndView) throws IOException {
        if(modelAndView==null){
            return;
        }
        if (this.viewResolvers.isEmpty()){
            return;
        }
        for (DXViewResolver viewResolver : viewResolvers) {
            DXView view=viewResolver.resolveViewName(modelAndView.getViewName());
            view.render(req,resp,modelAndView.getModel());
            return;
        }
    }

    private DXHandlerMapping getHandler(HttpServletRequest req) {
        String url=req.getRequestURI().replaceAll(req.getContextPath(),"");
        for (DXHandlerMapping handlerMaping : handlerMapings) {
            Matcher matcher=handlerMaping.getPattern().matcher(url);
            if (!matcher.matches()) { continue;}
            return handlerMaping;
        }
        return null;
    }

    //文件上传
    void upFile(HttpServletRequest request,HttpServletResponse response) {
        try {
            request.setCharacterEncoding("utf-8");
            response.setCharacterEncoding("utf-8");
            response.setContentType("text/html;charset=utf-8");//更改响应字符流使用的编码，还能告知浏览器用什么编码进行显示

            //从request中获取文本输入流信息
            InputStream fileSourceStream = request.getInputStream();

            String tempFileName = "D:/tempFile";

            //设置临时文件，保存待上传的文本输入流
            File tempFile = new File(tempFileName);

            //outputStram文件输出流指向这个tempFile
            FileOutputStream outputStream = new FileOutputStream(tempFile);

            //读取文件流
            byte temp[] = new byte[1024];
            int n;
            while ((n = fileSourceStream.read(temp)) != -1) {
                outputStream.write(temp, 0, n);
            }
            outputStream.close();
            fileSourceStream.close();

            //获取上传文件的名称
            RandomAccessFile randomFile = new RandomAccessFile(tempFile, "r");
            randomFile.readLine();
            String str = randomFile.readLine();
            int start = str.lastIndexOf("=") + 2;
            int end = str.lastIndexOf("\"");
            String filename = str.substring(start, end);

            //定位文件指针到文件头
            randomFile.seek(0);
            long startIndex = 0;
            int i = 1;
            //获取文件内容的开始位置
            while ((n = randomFile.readByte()) != -1 && i <= 4) {
                if (n == '\n') {
                    startIndex = randomFile.getFilePointer();
                    i++;
                }
            }
            startIndex = startIndex - 1; //这里一定要减1，因为前面多读了一个，这里很容易忽略
            //获取文件内容结束位置
            randomFile.seek(randomFile.length());
            long endIndex = randomFile.getFilePointer();
            int j = 1;
            while (endIndex >= 0 && j <= 2) {
                endIndex--;
                randomFile.seek(endIndex);
                if (randomFile.readByte() == '\n') {
                    j++;
                }
            }
            //关闭输入输出流
            randomFile.close();

        } catch (Exception e) {
            e.printStackTrace();
        }

    }
}


