package com.h.web.main.init;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.stereotype.Component;

import com.gsst.eaf.core.utils.JSON;
import com.h.demo.service.DemoService;


@Component
public class DemoInitializer implements ApplicationListener<ContextRefreshedEvent>  {
	
	private Logger logger = LoggerFactory.getLogger(this.getClass());

	@Autowired
	private DemoService demoService;
	
	@Override
	public void onApplicationEvent(ContextRefreshedEvent event) {
		System.out.println(JSON.serialize(demoService.getDemoEntity()));
		logger.info("test");
	}

	
}
