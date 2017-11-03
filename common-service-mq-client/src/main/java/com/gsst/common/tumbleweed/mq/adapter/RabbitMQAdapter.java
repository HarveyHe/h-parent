package com.gsst.common.tumbleweed.mq.adapter;

import java.io.IOException;

import com.gsst.common.tumbleweed.mq.RabbitMQ;
import com.gsst.common.tumbleweed.mq.config.RabbitMQConfig;

public class RabbitMQAdapter implements MQAdapter {

	private RabbitMQ mq;
	
	public RabbitMQAdapter(RabbitMQConfig config) {
		mq = new RabbitMQ(config);
	}
	
	@Override
	public void createTopic(String topic,String exchangeType, String remark) throws IOException{
		try {
			mq.exchangeDeclare(topic, exchangeType);
			
		} catch (IOException e) {
			throw e;
		}
	}

	@Override
	public void createProducer(String topic,String exchangeType,String producerId) {
		mq.exchangeToExchange(topic+"_"+producerId, exchangeType, topic);
	}

	
	@Override
	public void publishMessage(String topic, String producerId, String routingkey, String message) {
		
		mq.pubishMessage(topic+"_"+producerId, routingkey, message);
	}

	@Override
	public void createConsumer(String topic, String consumerId,String routingkey) {
		mq.queueDeclare(topic+"_"+consumerId, topic, routingkey);
	}

	@Override
	public void receiveMessage(String topic, String consumerId, String targetClass, String targetMethod) {
		mq.subscribeMessage(topic+"_"+consumerId, topic+"_"+consumerId+"-"+Thread.currentThread().getId(), targetClass, targetMethod);
	}

	

	

	
}
