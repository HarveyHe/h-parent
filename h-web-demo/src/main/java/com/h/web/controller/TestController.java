package com.h.web.controller;


import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.gsst.eaf.core.context.Context;
import com.gsst.eaf.core.utils.JSON;
import com.h.web.mq.service.MqService;

@RestController
@RequestMapping("/public/test")
public class TestController {
	@SuppressWarnings("unused")
	private Logger logger = LoggerFactory.getLogger(TestController.class);
	
	
	@RequestMapping(value = "/test.do",method = { RequestMethod.POST ,RequestMethod.GET})
	public void test(HttpServletRequest request, HttpServletResponse response) throws Exception {
		MqService mqService = Context.getBean("mqService");
		mqService.publishReceive();
//		System.out.println(JSON.serialize("HHHHHHHHHHHHHHHHHHHHHHHHHHHH"));
	}
}
