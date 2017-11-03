package com.gsst.common.tumbleweed.service;

import java.util.List;
import java.util.Map;

import com.gsst.eaf.core.service.BaseService;

public interface ConsumerService extends BaseService {

	/**
	 * 注册 consumer，订阅topic
	 * @param topic
	 * @param consumerId
	 */
	void registerConsumer(Map<String, String> consumer);
	
	/**
	 * 批量注册 consumer，订阅topic
	 * @param consumers
	 */
	void registerConsumers(List<Map<String, String>> consumers);
	
	
}
