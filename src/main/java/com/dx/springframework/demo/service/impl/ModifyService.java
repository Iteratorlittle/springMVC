package com.dx.springframework.demo.service.impl;

import com.dx.springframework.demo.service.IModifyService;
import com.dx.springframework.servlet.springv2.annotation.DXService;


/**
 * 增删改业务
 * @author Tom
 *
 */
@DXService
public class ModifyService implements IModifyService {
	/**
	 * 增加
	 */
	public String add(String name,String addr) throws Exception{
		throw new Exception("500异常示例");
		//return "modifyService add,name=" + name + ",addr=" + addr;
	}

	/**
	 * 修改
	 */
	public String edit(Integer id,String name) {
		return "modifyService edit,id=" + id + ",name=" + name;
	}

	/**
	 * 删除
	 */
	public String remove(Integer id) {
		return "modifyService id=" + id;
	}

	@Override
	public String calc(Integer a, Integer b) {
		return a + " + " + b + " = " + (a + b);
	}

}
