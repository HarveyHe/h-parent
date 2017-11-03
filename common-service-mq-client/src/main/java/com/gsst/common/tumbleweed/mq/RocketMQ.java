package com.gsst.common.tumbleweed.mq;

import java.util.Base64;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.Assert;

import com.aliyun.openservices.ons.api.Action;
import com.aliyun.openservices.ons.api.ConsumeContext;
import com.aliyun.openservices.ons.api.Consumer;
import com.aliyun.openservices.ons.api.Message;
import com.aliyun.openservices.ons.api.MessageListener;
import com.aliyun.openservices.ons.api.ONSFactory;
import com.aliyun.openservices.ons.api.OnExceptionContext;
import com.aliyun.openservices.ons.api.Producer;
import com.aliyun.openservices.ons.api.PropertyKeyConst;
import com.aliyun.openservices.ons.api.SendCallback;
import com.aliyun.openservices.ons.api.SendResult;
import com.aliyuncs.DefaultAcsClient;
import com.aliyuncs.IAcsClient;
import com.aliyuncs.exceptions.ClientException;
import com.aliyuncs.exceptions.ServerException;
import com.aliyuncs.http.FormatType;
import com.aliyuncs.ons.model.v20160503.OnsMessageGetByMsgIdRequest;
import com.aliyuncs.ons.model.v20160503.OnsMessageGetByMsgIdResponse;
import com.aliyuncs.ons.model.v20160503.OnsPublishCreateRequest;
import com.aliyuncs.ons.model.v20160503.OnsPublishCreateResponse;
import com.aliyuncs.ons.model.v20160503.OnsPublishGetRequest;
import com.aliyuncs.ons.model.v20160503.OnsPublishGetResponse;
import com.aliyuncs.ons.model.v20160503.OnsSubscriptionCreateRequest;
import com.aliyuncs.ons.model.v20160503.OnsSubscriptionCreateResponse;
import com.aliyuncs.ons.model.v20160503.OnsSubscriptionGetRequest;
import com.aliyuncs.ons.model.v20160503.OnsSubscriptionGetResponse;
import com.aliyuncs.ons.model.v20160503.OnsTopicCreateRequest;
import com.aliyuncs.ons.model.v20160503.OnsTopicCreateResponse;
import com.aliyuncs.ons.model.v20160503.OnsTopicGetRequest;
import com.aliyuncs.ons.model.v20160503.OnsTopicGetResponse;
import com.aliyuncs.profile.DefaultProfile;
import com.aliyuncs.profile.IClientProfile;
import com.gsst.common.tumbleweed.constant.Constant;
import com.gsst.eaf.core.config.Config;
import com.gsst.eaf.core.context.Context;
import com.gsst.eaf.core.reflectasm.MethodAccess;

public class RocketMQ {

	private static Logger logger = LoggerFactory.getLogger(RocketMQ.class);

	private static Map<String, Producer> producerMap = new HashMap<String, Producer>();
	/**
	 * 创建ACS客户端
	 * 
	 * @return
	 */
	private static IAcsClient getIAcsClient() {
		String regionId = Config.get("ons.OpenAPI.regionId"); // API的网关所在区域
		String endPointName = Config.get("ons.OpenAPI.endPointName"); // 接入点名称
		String accessKey = Config.get("ons.accessKey"); // 用户在阿里云官网上获取的AK
		String secretKey = Config.get("ons.secretKey"); // 用户在阿里云官网上获得的SK
		String productName = "Ons"; // OpenAPI的产品名称
		String domain = "ons." + regionId + ".aliyuncs.com"; // OpenAPI的接入点Domain

		/**
		 * 根据自己所在的区域选择Region后,设置对应的接入点
		 */
		try {
			DefaultProfile.addEndpoint(endPointName, regionId, productName, domain);
		} catch (ClientException e) {
			e.printStackTrace();
		}
		IClientProfile profile = DefaultProfile.getProfile(regionId, accessKey, secretKey);

		return new DefaultAcsClient(profile);
	}

	/******************************************** Topic **********************************************/
	/**
	 * topic 是否已存在
	 * 
	 * @param topic
	 * @return
	 */
	public static boolean isTopicExist(String topic) {
		IAcsClient iAcsClient = getIAcsClient();
		OnsTopicGetRequest request = new OnsTopicGetRequest();
		request.setAcceptFormat(FormatType.JSON);
		request.setTopic(topic);
		request.setPreventCache(System.currentTimeMillis());
		try {
			OnsTopicGetResponse response = iAcsClient.getAcsResponse(request);
			return !response.getData().isEmpty();
		} catch (ServerException e) {
			e.printStackTrace();
		} catch (ClientException e) {
			e.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return false;
	}

	/**
	 * 创建topic
	 * 
	 * @param topic
	 * @param remark
	 */
	public static boolean createTopic(String topic, String remark) {
		IAcsClient iAcsClient = getIAcsClient();
		OnsTopicCreateRequest request = new OnsTopicCreateRequest();
		request.setAcceptFormat(FormatType.JSON);
		request.setOnsRegionId(Config.get("ons.test.regionId")); // FIXME
		request.setTopic(topic);
		request.setPreventCache(System.currentTimeMillis());
		if (remark != null && !"".equals(remark)) {
			request.setRemark(remark);
		}

		String requestId = null;
		try {
			OnsTopicCreateResponse response = iAcsClient.getAcsResponse(request);
			requestId = response.getRequestId();
		} catch (ServerException e) {
			e.printStackTrace();
		} catch (ClientException e) {
			e.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return StringUtils.isNotBlank(requestId);
	}

	/******************************************** Producer **********************************************/
	/**
	 * producer 是否已存在
	 * 
	 * @param producerId
	 * @param topic
	 * @return
	 */
	public static boolean isProducerExist(String topic, String producerId) {
		IAcsClient iAcsClient = getIAcsClient();
		OnsPublishGetRequest request = new OnsPublishGetRequest();
		request.setOnsRegionId(Config.get("ons.test.regionId")); // FIXME
		request.setPreventCache(System.currentTimeMillis());
		request.setAcceptFormat(FormatType.JSON);
		request.setTopic(topic);
		request.setProducerId(producerId);
		try {
			OnsPublishGetResponse response = iAcsClient.getAcsResponse(request);
			logger.info("response data={}", response);
			return !response.getData().isEmpty();
		} catch (ServerException e) {
			e.printStackTrace();
		} catch (ClientException e) {
			e.printStackTrace();
		}
		return false;
	}

	/**
	 * 创建producer
	 * 
	 * @param topic
	 * @param producerId
	 */
	public static boolean createProducer(String topic, String producerId) {
		
		Assert.isTrue(producerId.length() <=64);
		
		IAcsClient iAcsClient = getIAcsClient();
		OnsPublishCreateRequest request = new OnsPublishCreateRequest();
		request.setOnsRegionId(Config.get("ons.test.regionId")); // FIXME
		request.setPreventCache(System.currentTimeMillis());
		request.setAcceptFormat(FormatType.JSON);
		request.setTopic(topic);
		request.setProducerId(producerId);
		String requestId = null;
		try {
			OnsPublishCreateResponse response = iAcsClient.getAcsResponse(request);
			requestId = response.getRequestId();
		} catch (ServerException e) {
			e.printStackTrace();
		} catch (ClientException e) {
			e.printStackTrace();
		}
		return StringUtils.isNoneBlank(requestId);
	}

	/**
	 * 发布消息
	 * 
	 * @param topic
	 * @param producerId
	 * @param message
	 */
	public static void publishMessage(String topic, String producerId, String message) {
		
		Producer producer = RocketMQ.getProducer(producerId);
		
		Message msg = new Message(topic, null, message.getBytes());
		
		producer.sendAsync(msg, new SendCallback() {
			@Override
			public void onSuccess(final SendResult sendResult) {
				// 消费发送成功
				logger.info("send message success. topic=" + sendResult.getTopic() + ", msgId=" + sendResult.getMessageId());
			}

			@Override
			public void onException(OnExceptionContext context) {
				// 消息发送失败
				logger.info("send message failed. topic=" + context.getTopic() + ", msgId=" + context.getMessageId());
			}
		});
		logger.info("publish msgId={}", msg.getMsgID());
	}
	
	public static Producer getProducer(String producerId) {
		
		Producer producer = producerMap.get(producerId);
		
		if(producer == null) {
			logger.info("Create producer,producerId={}", producerId);
			Properties properties = new Properties();
			properties.put(PropertyKeyConst.ProducerId, producerId);
			properties.put(PropertyKeyConst.AccessKey, Config.get("ons.accessKey"));
			properties.put(PropertyKeyConst.SecretKey, Config.get("ons.secretKey"));
			producer = ONSFactory.createProducer(properties);
			producer.start();
			logger.info("Create producer complete,producerId={}" + producerId);
			
			producerMap.put(producerId, producer);
			
		}
		
		return producer;
	}

	/******************************************** Consumer **********************************************/
	/**
	 * Consumer 是否已存在
	 * 
	 * @param topic
	 * @param consumerId
	 */
	public static boolean isConsumerExist(String topic, String consumerId) {
		IAcsClient iAcsClient = getIAcsClient();
		OnsSubscriptionGetRequest request = new OnsSubscriptionGetRequest();
		request.setOnsRegionId(Config.get("ons.test.regionId")); // FIXME
		request.setPreventCache(System.currentTimeMillis());
		request.setAcceptFormat(FormatType.JSON);
		request.setTopic(topic);
		request.setConsumerId(consumerId);
		try {
			OnsSubscriptionGetResponse response = iAcsClient.getAcsResponse(request);
			return !response.getData().isEmpty();
		} catch (ServerException e) {
			e.printStackTrace();
		} catch (ClientException e) {
			e.printStackTrace();
		}
		return false;
	}

	/**
	 * 创建consumer
	 * 
	 * @param topic
	 * @param consumerId
	 * @return
	 */
	public static boolean createConsumer(String topic, String consumerId) {
		IAcsClient iAcsClient = getIAcsClient();

		Assert.isTrue(consumerId.length() <=64, consumerId);
		
		OnsSubscriptionCreateRequest request = new OnsSubscriptionCreateRequest();
		request.setOnsRegionId(Config.get("ons.test.regionId")); // FIXME
		request.setAcceptFormat(FormatType.JSON);
		request.setPreventCache(System.currentTimeMillis());
		request.setTopic(topic);
		request.setConsumerId(consumerId);
		String requestId = null;
		try {
			OnsSubscriptionCreateResponse response = iAcsClient.getAcsResponse(request);
			requestId = response.getRequestId();
		} catch (ServerException e) {
			e.printStackTrace();
		} catch (ClientException e) {
			e.printStackTrace();
		}
		return StringUtils.isNoneBlank(requestId);
	}

	/**
	 * 接收消息，执行目标方法
	 * 
	 * @param topic
	 * @param consumerId
	 * @param targetClass
	 * @param targetMethod
	 */
	public static void receiveMessage(String topic, String consumerId, String targetClass, String targetMethod) {
		Properties properties = new Properties();
		properties.put(PropertyKeyConst.ConsumerId, consumerId);
		properties.put(PropertyKeyConst.AccessKey, Config.get("ons.accessKey"));
		properties.put(PropertyKeyConst.SecretKey, Config.get("ons.secretKey"));
		Consumer consumer = ONSFactory.createConsumer(properties);

		consumer.subscribe(topic, "*", new MessageListener() {
			
			@Override
			public Action consume(Message message, ConsumeContext context) {
				
				logger.info("Consume message topic={}, msgID={}", message.getTopic(), message.getMsgID());
				logger.info("Consume message targetClass={}, targetMethod={}", targetClass, targetMethod);
				
				String base64MsgContent = new String( message.getBody());
				byte[] msgbody = Base64.getDecoder().decode(base64MsgContent);
				String msgContent = new String(msgbody);
				
				try {
					// 消息内容，json 字符串
					Class<?> clz = Class.forName(targetClass);
					String beanName = StringUtils.uncapitalize(clz.getSimpleName());
					Object bean = Context.getContext().getBean(beanName);

					MethodAccess acc = MethodAccess.get(clz);
					acc.invoke(bean, targetMethod, msgContent);
					
				} catch (Exception e) {
					e.printStackTrace();
					logger.error("Fail to consume message, msgContent={}", msgContent);
					logger.error("Fail to consume message", e);
					
					return Action.ReconsumeLater;
				}

				return Action.CommitMessage;
			}
		});

		consumer.start();
	}

	/**
	 * 读取消息内容
	 * 
	 * @param topic
	 * @param msgId
	 * @return
	 * @throws ClientException 
	 * @throws ServerException 
	 */
	@Deprecated
	public static String getMessageByMsgId(String topic, String msgId) throws ServerException, ClientException {
		IAcsClient iAcsClient = getIAcsClient();
		OnsMessageGetByMsgIdRequest request = new OnsMessageGetByMsgIdRequest();
		request.setOnsRegionId(Config.get("ons.test.regionId")); // FIXME
		request.setPreventCache(System.currentTimeMillis());
		request.setAcceptFormat(FormatType.JSON);
		request.setTopic(topic);
		request.setMsgId(msgId);
		String message = null;
		OnsMessageGetByMsgIdResponse response = iAcsClient.getAcsResponse(request);
		String dataBody = response.getData().getBody();
		byte[] msgbody = Base64.getDecoder().decode(dataBody);
		message = new String(msgbody);

		return message;
	}

}
