package com.dx.springframework.demo.action;

import com.dx.springframework.demo.service.IQueryService;
import com.dx.springframework.servlet.springv2.annotation.DXAutowired;
import com.dx.springframework.servlet.springv2.annotation.DXController;
import com.dx.springframework.servlet.springv2.annotation.DXRequestMapping;
import com.dx.springframework.servlet.springv2.annotation.DXRequestParam;
import com.dx.springframework.servlet.springv2.springmvc.DXModelAndView;


import java.util.HashMap;
import java.util.Map;

/**
 * 公布接口url
 * @author Tom
 *
 */
@DXController
@DXRequestMapping("/page")
public class PageAction {

    @DXAutowired
    IQueryService queryService;

    @DXRequestMapping("/first.jsp")
    public DXModelAndView query(@DXRequestParam("name") String name){
        String result = queryService.query(name);
        Map<String,Object> model = new HashMap<String,Object>();
        model.put("name", name);
        model.put("data", result);
        model.put("token", "123456");
        return new DXModelAndView("first.jsp",model);
    }

    @DXRequestMapping("/sss.jsp")
    public DXModelAndView upFile(){
        return new DXModelAndView("sss.jsp");
    }
    @DXRequestMapping("/200.jsp")
    public DXModelAndView succee(){
        return new DXModelAndView("200.jsp");
    }
}
