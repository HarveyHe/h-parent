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
import com.gsst.common.tumbleweed.service.ConsumerService;
import com.gsst.common.tumbleweed.utils.JsonUtils;

@Service
public class ConsumerServiceImpl implements ConsumerService {

	private Logger logger = LoggerFactory.getLogger(this.getClass());
	@Autowired
	private MQAdapter mq;
	
	@Override
	public void registerConsumer(Map<String, String> consumer) {
		String topic = consumer.get("topic");
		String consumerId = consumer.get("consumerId");
		String targetClass = consumer.get("targetClass");
		String targetMethod = consumer.get("targetMethod");
		String routingkey = consumer.get("routingkey");
		
		String topicPrefix = Constant.TumbleweedTopicPrefix;
		
		if(StringUtils.isNotBlank(topicPrefix)) {
			topic = topicPrefix + topic;
		}
		
		logger.info("[{}]Registr Consumer={}", topic, JsonUtils.beanToJson(consumer));
		
		// 注册Consumer
		mq.createConsumer(topic,consumerId,routingkey);
		// 订阅 topic消息
		mq.receiveMessage(topic, consumerId, targetClass, targetMethod);
	}

	@Override
	public void registerConsumers(List<Map<String, String>> consumers) {
		if(consumers==null || consumers.size()<1){
			return ;
		}
		for(Map<String, String> consumer : consumers){
			this.registerConsumer(consumer);
		}
	}


}
