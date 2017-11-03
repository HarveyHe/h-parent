package com.gsst.common.tumbleweed.mq.adapter;

import com.gsst.common.tumbleweed.mq.RocketMQ;

public class RocketMQAdapter implements MQAdapter {
		
	@Override
	public void createTopic(String topic, String exchangeType,String remark) {
		if (!RocketMQ.isTopicExist(topic)) {
			RocketMQ.createTopic(topic, remark);
		}		
	}

	@Override
	public void createProducer(String topic,String exchangeType,String producerId) {
		if (!RocketMQ.isProducerExist(topic, producerId)) {
			RocketMQ.createProducer(topic, producerId);
		}		
	}

	@Override
	public void publishMessage(String topic, String producerId, String routingkey, String message) {
		RocketMQ.publishMessage(topic, producerId, message);
	}

	@Override
	public void createConsumer(String topic,String consumerId,String routingkey) {
		if(!RocketMQ.isConsumerExist(topic, consumerId)){
			RocketMQ.createConsumer(topic, consumerId);
		}
	}

	@Override
	public void receiveMessage(String topic, String consumerId, String targetClass, String targetMethod) {
		RocketMQ.receiveMessage(topic, consumerId, targetClass, targetMethod);
	}

	

	
	
}
