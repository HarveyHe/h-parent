package com.h.demo.service.impl;

import org.springframework.stereotype.Service;

import com.h.demo.entity.DemoEntity;
import com.h.demo.service.DemoService;

@Service
public class DemoServiceImpl implements DemoService {

	@Override
	public DemoEntity getDemoEntity() {
		DemoEntity entity = new DemoEntity();
		entity.setName("test demo");
		return entity;
	}

	
}
