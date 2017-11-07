package com.h.web.main.init;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.stereotype.Component;


@Component
public class DemoInitializer implements ApplicationListener<ContextRefreshedEvent>  {
	
	private Logger logger = LoggerFactory.getLogger(this.getClass());

	
	@Override
	public void onApplicationEvent(ContextRefreshedEvent event) {
		logger.info("init ");
	}

	
}
