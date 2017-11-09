package com.h.web.schedule.service.impl;

import java.util.Date;

import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import com.h.web.schedule.service.DemoScheduleService;

@Service
public class DemoScheduleServiceImpl implements DemoScheduleService {


	@Override
	@Scheduled(cron="*/30 * * * * *")
	public void test() {
		Date now = new Date();
//		System.out.println(now);
	}

}
