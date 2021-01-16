package com.dx.springframework.demo.service.impl;

import java.text.SimpleDateFormat;
import java.util.Date;
import com.dx.springframework.demo.service.IQueryService;
import com.dx.springframework.servlet.springv2.annotation.DXService;
//import lombok.extern.slf4j.Slf4j;

/**
 * 查询业务
 * @author Tom
 *
 */
@DXService
public class QueryService implements IQueryService {

	/**
	 * 查询
	 */
	public String query(String name) {
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		String time = sdf.format(new Date());
		String json = "{name:\"" + name + "\",time:\"" + time + "\"}";
		System.out.println("这是在业务方法中打印的：" + json);
		return json;
	}

}
