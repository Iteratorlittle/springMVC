package com.dx.springframework.servlet.springv2.springmvc;

//import com.sun.org.apache.xerces.internal.impl.xpath.regex.Match;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class DXView {
    private File viewFile;
    public DXView(File templateFile) {
        this.viewFile=templateFile;
    }

    public void render(HttpServletRequest req, HttpServletResponse resp,
                       Map<String, ?> model) throws IOException {

        RandomAccessFile ra=new RandomAccessFile(this.viewFile,"r");
        StringBuffer sb=new StringBuffer();

        String line=null;
        while (null !=(line=ra.readLine())){
            line=new String(line.getBytes("ISO-8859-1"),"utf-8");

            Pattern pattern=Pattern.compile("￥\\{[^\\}]+\\}",Pattern.CASE_INSENSITIVE);
            Matcher matcher=pattern.matcher(line);
            while (matcher.find()){
                String paramName=matcher.group();
                paramName=paramName.replaceAll("￥\\{|\\}","");
                Object paramValue=model.get(paramName);
                line=matcher.replaceFirst(paramValue.toString());
                matcher=pattern.matcher(line);
            }
            sb.append(line);
        }
        resp.setCharacterEncoding("utf-8");
        resp.getWriter().write(sb.toString());
    }
}
