package com.gsst.common.tumbleweed.listener;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.AnnotationUtils;
import org.springframework.util.Assert;
import org.springframework.util.ClassUtils;

import com.gsst.common.tumbleweed.client.annotation.Consumer;
import com.gsst.common.tumbleweed.client.annotation.Producer;
import com.gsst.common.tumbleweed.constant.Constant;
import com.gsst.common.tumbleweed.mq.RabbitMQ;
import com.gsst.common.tumbleweed.service.ConsumerService;
import com.gsst.common.tumbleweed.service.ProducerService;
import com.gsst.common.tumbleweed.service.TopicService;
import com.gsst.common.tumbleweed.utils.ConsumerUtils;
import com.gsst.common.tumbleweed.utils.ProducerUtils;
import com.gsst.eaf.core.config.Config;
import com.gsst.eaf.core.config.listener.ContextConfigListener;
import com.gsst.eaf.core.utils.ReflectionUtils;

public class ContextConfigInitialListener implements ContextConfigListener, Ordered {

	Logger logger = LoggerFactory.getLogger(this.getClass());
	
	private Map<String,String> topics = new HashMap<String,String>();
	private List<Map<String, String>> producers = new ArrayList<>();
	private List<Map<String, String>> consumers = new ArrayList<>();

	@Override
	public void beforeStartup(final ConfigurableApplicationContext context) {
		
		/*context.addBeanFactoryPostProcessor(new BeanFactoryPostProcessor() {

            @Override
            public void postProcessBeanFactory(ConfigurableListableBeanFactory beanFactory) throws BeansException {
            	
            	
            }
        });*/
	}

	@Override
	public void afterStartup(ConfigurableApplicationContext context) {
		
		System.out.println("######################Tumebleweed Register Service######################");
		this.registerService(context);
		
		// bean初始化完成后, 先注册 topic
		if(!topics.isEmpty()){
			TopicService topicService = context.getBean(TopicService.class);
			try {
				topicService.registerTopics(topics);
			} catch (Exception e) {
				logger.error(e.getMessage(),e);
				throw new RuntimeException(e.getMessage(),e);
			}
		}
		
		// 注册 producer，发布到topic
		if(producers!=null && producers.size()>0){
			ProducerService producerService = context.getBean(ProducerService.class);	
			producerService.registerProducers(producers);
		}
		
		// 注册 consumer，订阅topic
		if(consumers!=null && consumers.size()>0){
			ConsumerService consumerService = context.getBean(ConsumerService.class);	
			consumerService.registerConsumers(consumers);
		}
		
	}

	/**
	 * 建立生产/消费方服务
	 * 
	 * @param context
	 */
	private void registerService(ConfigurableApplicationContext context) {

		Class<?> baseClass = this.getBaseClass();
		
		Map<String, ?> beans = context.getBeansOfType(baseClass);
		
		for (Object bean : beans.values()) {
			
			Method[] methods = ClassUtils.getUserClass(bean).getDeclaredMethods();
			
			for (Method method : methods) {
				
				Producer producer = AnnotationUtils.findAnnotation(method, Producer.class);
				
				if (producer != null) {
					if (Boolean.FALSE.toString().equalsIgnoreCase(Config.get(Constant.TumbleweedProduceService))) {
						
					} else {
						Map<String, String> producerMap = new HashMap<>();
						
						String key = producer.key();
						String exchangeType = RabbitMQ.ExchangeType.FANOUT;
						if(StringUtils.isNotBlank(key)){
							exchangeType = RabbitMQ.ExchangeType.TOPIC;
						}
						checkTopic(producer.topic(), exchangeType);
						topics.put(producer.topic(), exchangeType);
						
						producerMap.put("topic", producer.topic());
						producerMap.put("exchangeType", exchangeType);
						
						if(StringUtils.isNotBlank(producer.producerId())) {
							producerMap.put("producerId", producer.producerId());
						
						} else {
							producerMap.put("producerId", ProducerUtils.getProducerId(method));
						}
						
						producers.add(producerMap);
					}
				}
				
				Consumer consumer = AnnotationUtils.findAnnotation(method, Consumer.class);
				
				if (consumer != null) {
					
					if (Boolean.FALSE.toString().equalsIgnoreCase(Config.get(Constant.TumbleweedConsumeService))) {
						
					} else {
						
						Map<String, String> consumerMap = new HashMap<>();
						
						String routingkey = consumer.routingkey();
						
						if(StringUtils.isNotBlank(routingkey)){
							//匹配所有binding
							routingkey = "*";
						}
						
						String exchangeType = RabbitMQ.ExchangeType.FANOUT;
						if(StringUtils.isNotBlank(routingkey)){
							exchangeType = RabbitMQ.ExchangeType.TOPIC;
						}
						checkTopic(consumer.topic(), exchangeType);
						topics.put(consumer.topic(), exchangeType);
						
						if(StringUtils.isNotBlank(consumer.consumerId())) {
							consumerMap.put("consumerId", consumer.consumerId());
						} else {
							consumerMap.put("consumerId", ConsumerUtils.getConsumerId(method));
						}
						
						consumerMap.put("topic", consumer.topic());
						consumerMap.put("targetClass", ReflectionUtils.getServiceInterface(bean.getClass(), baseClass).getName());
						consumerMap.put("targetMethod", method.getName());
						consumerMap.put("routingkey", routingkey);
						
						consumers.add(consumerMap);
					}
				}
			}
		}
	}

	/**
	 * 检查是否有topics是否有相同的topic但是exchangeType不相同的topic
	 * @param topic
	 * @param mqType
	 */
	private void checkTopic(String topic,String exchangeType){
		String value = topics.get(topic);
		if(StringUtils.isNotBlank(value)){
			if(!value.equals(exchangeType)){
				logger.error("创建topic={}失败，存在topic名称相同但是mq类型不一致",topic);
				throw new RuntimeException("创建topic失败，存在topic名称相同但是mq类型不一致");
			}
		}
	}
	
	private Class<?> getBaseClass() {
		String clazzStr = Config.get(Constant.TumbleweedBaseService);

		Assert.notNull(clazzStr);

		try {
			return Class.forName(clazzStr);
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
			return null;
		}
	}
	
	@Override
	public int getOrder() {
		return Integer.MIN_VALUE;
	}
}
