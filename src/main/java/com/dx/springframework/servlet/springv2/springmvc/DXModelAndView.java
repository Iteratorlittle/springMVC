package com.dx.springframework.servlet.springv2.springmvc;

import java.util.Map;

public class DXModelAndView {
    private String viewName;
    private Map<String,?> model;
    public DXModelAndView(String s) {
        viewName=s;
    }
    public DXModelAndView(String s,Map<String,?> model) {
        viewName=s;
        this.model=model;
    }

    public String getViewName() {
        return viewName;
    }

    public void setViewName(String viewName) {
        this.viewName = viewName;
    }

    public Map<String, ?> getModel() {
        return model;
    }

    public void setModel(Map<String, ?> model) {
        this.model = model;
    }
}
