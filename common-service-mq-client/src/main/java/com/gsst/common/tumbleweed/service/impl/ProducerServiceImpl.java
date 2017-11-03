package com.gsst.common.tumbleweed.service.impl;

import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.gsst.common.tumbleweed.constant.Constant;
import com.gsst.common.tumbleweed.mq.adapter.MQAdapter;
import com.gsst.common.tumbleweed.service.ProducerService;

@Service
public class ProducerServiceImpl implements ProducerService {

	private Logger logger = LoggerFactory.getLogger(this.getClass());
	@Autowired
	private MQAdapter mq;
	
	@Override
	public void registerProducer(String topic,String exchangeType,String producerId) {

		String topicPrefix = Constant.TumbleweedTopicPrefix;
		
		if(StringUtils.isNotBlank(topicPrefix)) {
			topic = topicPrefix + topic;
		}
		
		logger.info("[{}]Registr Producer topic={},exchangeType={},producerId={}", topic,exchangeType,producerId);
		
		// 创建 producer
		mq.createProducer(topic,exchangeType,producerId);
	}

	@Override
	public void registerProducers(List<Map<String, String>> producers) {
		if (producers == null || producers.size() < 1) {
			return;
		}
		for (Map<String, String> producer : producers) {
			this.registerProducer(producer.get("topic"),producer.get("exchangeType"),producer.get("producerId"));
		}
	}

	@Override
	public void publishMessage(String topic, String producerId,String routingkey, String message){
		String topicPrefix = Constant.TumbleweedTopicPrefix;
		
		if(StringUtils.isNotBlank(topicPrefix)) {
			topic = topicPrefix + topic;
		}
		
		mq.publishMessage(topic, producerId,routingkey, message);
	}
	


}
