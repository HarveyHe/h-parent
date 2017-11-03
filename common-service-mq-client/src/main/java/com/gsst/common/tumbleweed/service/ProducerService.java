package com.gsst.common.tumbleweed.service;

import java.util.List;
import java.util.Map;

import com.gsst.common.tumbleweed.client.annotation.Producer;
import com.gsst.eaf.core.service.BaseService;

public interface ProducerService extends BaseService {

	/**
	 * 注册 producer，发布到topic
	 * @param topic
	 * @param producerId
	 */
	void registerProducer(String topic, String type,String producerId);
	
	/**
	 * 批量注册 producer，发布到topic
	 * @param producers
	 */
	void registerProducers(List<Map<String,String>> producers);
	
	/**
	 * 发布消息
	 * @param producerId
	 * @param topic
	 * @param message
	 */
	void publishMessage(String topic, String producerId,String routingkey, String message);
	
	
	
}
