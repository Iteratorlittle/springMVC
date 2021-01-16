package com.dx.springframework.demo.action;

import java.io.IOException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.dx.springframework.demo.service.IModifyService;
import com.dx.springframework.demo.service.IQueryService;
import com.dx.springframework.servlet.springv2.annotation.DXAutowired;
import com.dx.springframework.servlet.springv2.annotation.DXController;
import com.dx.springframework.servlet.springv2.annotation.DXRequestMapping;
import com.dx.springframework.servlet.springv2.annotation.DXRequestParam;
import com.dx.springframework.servlet.springv2.springmvc.DXModelAndView;


/**
 * 公布接口url
 * @author Tom
 *
 */
@DXController
@DXRequestMapping("/web")
public class MyAction {

	@DXAutowired
	IQueryService queryService;
	@DXAutowired
	IModifyService modifyService;

	@DXRequestMapping("/query.json")
	public DXModelAndView query(HttpServletRequest request, HttpServletResponse response,
								@DXRequestParam("name") String name){
		String result = queryService.query(name);
		return out(response,result);
	}
	
	@DXRequestMapping("/add*.json")
	public DXModelAndView add(HttpServletRequest request,HttpServletResponse response,
			   @DXRequestParam("name") String name,@DXRequestParam("addr") String addr){
		String result = null;
		try {
			result = modifyService.add(name,addr);
		} catch (Exception e) {
			Map<String,Object> model = new HashMap<String, Object>();
			model.put("detail","500 Exception Detail," + e.getMessage());
			return new DXModelAndView("500.jsp",model);
		}

		return out(response,result);
	}
	
	@DXRequestMapping("/remove.json")
	public DXModelAndView remove(HttpServletRequest request,HttpServletResponse response,
		   @DXRequestParam("id") Integer id){
		String result = modifyService.remove(id);
		return out(response,result);
	}


	@DXRequestMapping("/calc.json")
	public DXModelAndView remove(HttpServletResponse response,
								 @DXRequestParam("a") Integer a,@DXRequestParam("b") Integer b){
		String result = modifyService.calc(a,b);
		return out(response,result);
	}
	
	@DXRequestMapping("/edit.json")
	public DXModelAndView edit(HttpServletRequest request,HttpServletResponse response,
			@DXRequestParam("id") Integer id,
			@DXRequestParam("name") String name){
		String result = modifyService.edit(id,name);
		return out(response,result);
	}
	
	
	
	private DXModelAndView out(HttpServletResponse resp,String str){
		try {
			resp.getWriter().write(str);
		} catch (IOException e) {
			e.printStackTrace();
		}
		return null;
	}
	
}
