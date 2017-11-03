package com.gsst.common.tumbleweed.service;

import java.util.Map;

import com.gsst.eaf.core.service.BaseService;

public interface TopicService extends BaseService {

	/**
	 * 创建topic
	 * @param topic
	 * @param remark
	 * @return
	 * @throws Exception 
	 */
	void registerTopic(String topic,String routingkey,String remark) throws Exception;
	
	/**
	 * 批量创建topic
	 * @param topics
	 * @return
	 * @throws Exception 
	 */
	void registerTopics(Map<String,String> topics) throws Exception;
	
}
