package com.gsst.common.tumbleweed.service.impl;

import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.gsst.common.tumbleweed.constant.Constant;
import com.gsst.common.tumbleweed.mq.adapter.MQAdapter;
import com.gsst.common.tumbleweed.service.TopicService;

@Service
public class TopicServiceImpl implements TopicService {

	private Logger logger = LoggerFactory.getLogger(this.getClass());
	@Autowired
	private MQAdapter mq;

	@Override
	public void registerTopic(String topic,String exchangeType,String remark) throws Exception {
		logger.info("create topic={}, remark={}", topic, remark);

		String topicPrefix = Constant.TumbleweedTopicPrefix;
		
		if(StringUtils.isNotBlank(topicPrefix)) {
			topic = topicPrefix + topic;
		}
		try {
			mq.createTopic(topic,exchangeType,remark);
			
		} catch (Exception e) {
			logger.error(e.getMessage(),e);
			throw e;
		}
	}

	@Override
	public void registerTopics(Map<String,String> topics) throws Exception {
		if (topics.isEmpty()) {
			return;
		}
		
		Iterator<Map.Entry<String, String>> entries = topics.entrySet().iterator();  
		  
		while (entries.hasNext()) {  
		  
		    Entry<String, String> entry = entries.next(); 
		    String topic = entry.getKey();
		    String exchangeType = entry.getValue();
		    this.registerTopic(topic,exchangeType,null);
		}  
		
	}

}
