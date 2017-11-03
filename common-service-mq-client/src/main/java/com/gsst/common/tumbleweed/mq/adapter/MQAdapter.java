package com.gsst.common.tumbleweed.mq.adapter;

import java.io.IOException;

/**
 * MQ适配器
 * @author Allan.Liu
 */
public interface MQAdapter {
	
	void createTopic(String topic,String exchangeType, String remark) throws IOException;
	
	void createProducer(String topic,String exchangeType, String producerId);
	
	void publishMessage(String topic, String producerId, String routingkey, String message);
	
	void createConsumer(String topic,String consumerId,String routingkey);
	
	void receiveMessage(String topic, String consumerId, String targetClass, String targetMethod);

	
	
}
