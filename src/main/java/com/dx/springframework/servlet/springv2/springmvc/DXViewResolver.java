package com.dx.springframework.servlet.springv2.springmvc;

import java.io.File;

public class DXViewResolver {
    private final String DEFAULT_TEMPLATE_SUFFIX=".jsp";
    private File templateDir;

    public DXViewResolver(String templateRoot) {
        String templateRootPath=this.getClass().getClassLoader().getResource(templateRoot).getFile();
        templateDir=new File(templateRootPath);
    }

    public DXView resolveViewName(String viewName) {
        if (null==viewName||"".equals(viewName)){
            return null;
        }
        viewName=viewName.endsWith(DEFAULT_TEMPLATE_SUFFIX)?viewName:(viewName+DEFAULT_TEMPLATE_SUFFIX);
        File templateFile=new File((templateDir.getPath()+"/"+viewName).replaceAll("/+","/"));

        return new DXView(templateFile);
    }
}
