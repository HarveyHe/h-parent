package com.gsst.common.tumbleweed.mq;

import java.io.IOException;
import java.util.Base64;
import java.util.concurrent.TimeoutException;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.gsst.common.tumbleweed.mq.config.RabbitMQConfig;
import com.gsst.eaf.core.context.Context;
import com.gsst.eaf.core.reflectasm.MethodAccess;
import com.rabbitmq.client.AMQP;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;
import com.rabbitmq.client.Consumer;
import com.rabbitmq.client.DefaultConsumer;
import com.rabbitmq.client.Envelope;
import com.rabbitmq.client.MessageProperties;

public class RabbitMQ {

	private static Logger logger = LoggerFactory.getLogger(RabbitMQ.class);

	private ConnectionFactory factory = null;
	private Connection connection = null;

	public RabbitMQ(RabbitMQConfig config){
		factory = new ConnectionFactory();
		factory.setHost(config.getHost());
		factory.setUsername(config.getUsername());
		factory.setPassword(config.getPassword());
		factory.setPort(config.getPort());
		factory.setVirtualHost(config.getVirtualHost());
	}
	
	/**
	 * 获取Channel
	 */
	public Channel getChannel() {
		Channel channel = null;
		try {
			if(connection == null || !connection.isOpen()){				
				connection = factory.newConnection();
			}			
			channel = connection.createChannel();
		} catch (Exception e) {
			logger.error(e.getMessage(),e);
		}
		return channel;
	}
	
	/**
	 * 声明 Exchange（创建 Topic）
	 * @throws IOException 
	 */
	public void exchangeDeclare(String exchange, String exchangeType) throws IOException{
		Channel channel = getChannel();
		try {
			channel.exchangeDeclare(exchange, exchangeType, true, false, false, null);
		} catch (IOException e) {
			logger.error(e.getMessage(),e);
			throw e;
		} finally {
			try {
				channel.close();
			} catch (IOException | TimeoutException e) {
				logger.error(e.getMessage(),e);
			}
		}
	}
	
	/**
	 * 删除Exchange
	 */
	public void exchangeDelete(String exchange){
		Channel channel = getChannel();
		try {
			channel.exchangeDelete(exchange, true);
		} catch (IOException e) {
			logger.error(e.getMessage(),e);
		} finally {
			try {
				channel.close();
			} catch (IOException | TimeoutException e) {
				logger.error(e.getMessage(),e);
			}
		}
	}
	
	/**
	 * 声明 Exchange，绑定到某个 Exchange （创建Producer）
	 */
	public void exchangeToExchange(String exchange, String exchangeType, String destination){
		Channel channel = getChannel();
		try {
			channel.exchangeDeclare(exchange, exchangeType, true, false, false, null);
			channel.exchangeBind(destination, exchange, "*", null);
		} catch (IOException e) {
			logger.error(e.getMessage(),e);
		} finally {
			try {
				channel.close();
			} catch (IOException | TimeoutException e) {
				logger.error(e.getMessage(),e);
			}
		}
	}
	
	/**
	 * 创建Queue
	 */
	public void queueDeclare(String queue, String exchange, String routingKey){
		Channel channel = getChannel();
		try {
			channel.queueDeclare(queue, true, false, false, null);
			channel.queueBind(queue, exchange, routingKey, null);
		} catch (IOException e) {
			logger.error(e.getMessage(),e);
		} finally {
			try {
				channel.close();
			} catch (IOException | TimeoutException e) {
				logger.error(e.getMessage(),e);
			}
		}
	}
	
	/**
	 * 删除Queue
	 */
	public void queueDelete(String queue){
		Channel channel = getChannel();
		try {
			channel.queueDelete(queue, true, true);
		} catch (IOException e) {
			logger.error(e.getMessage(),e);
		} finally {
			try {
				channel.close();
			} catch (IOException | TimeoutException e) {
				logger.error(e.getMessage(),e);
			}
		}
	}
	
	/**
	 * 发布消息
	 */
	public void pubishMessage(String exchange, String routingKey, String message){
		Channel channel = getChannel();
		try {
			channel.basicPublish(exchange, routingKey, MessageProperties.PERSISTENT_TEXT_PLAIN, message.getBytes());
		} catch (IOException e) {
			logger.error(e.getMessage(),e);
		} finally {
			try {
				channel.close();
			} catch (IOException | TimeoutException e) {
				logger.error(e.getMessage(),e);
			}
		}
	}
	
	/**
	 * 接收消息，执行目标方法
	 */
	public void subscribeMessage(String queue, String consumerTag, String targetClass, String targetMethod){
		Channel channel = getChannel();
		try {
			channel.basicQos(1);
			Consumer consumer = new DefaultConsumer(channel) {
				@Override
				public void handleDelivery(String consumerTag, Envelope envelope, AMQP.BasicProperties properties, byte[] body) throws IOException {
					String message = new String(body, "UTF-8");
					
					logger.info("Consume message queue={}", queue);
					logger.info("Consume message targetClass={}, targetMethod={}", targetClass, targetMethod);
					
					byte[] msgbody = Base64.getDecoder().decode(message);
					String msgContent = new String(msgbody);
					
					try {
						// 消息内容，json 字符串
						Class<?> clz = Class.forName(targetClass);
						String beanName = StringUtils.uncapitalize(clz.getSimpleName());
						Object bean = Context.getContext().getBean(beanName);

						MethodAccess acc = MethodAccess.get(clz);
						acc.invoke(bean, targetMethod, msgContent);
						
						channel.basicAck(envelope.getDeliveryTag(), false);  // 返回确认状态
						
					} catch (Exception e) {
						logger.error(e.getMessage(),e);
						logger.error("Fail to consume message, msgContent={}", msgContent);
						logger.error("Fail to consume message", e);
						
						channel.basicAck(envelope.getDeliveryTag(), false);
					}

				}
			};
			// 消息如果未被正常消费，则交给其他worker
			channel.basicConsume(queue, false, consumerTag, consumer); // 监听队列，手动返回完成
		} catch (IOException e) {
			logger.error(e.getMessage(),e);
		}
	}
	
	/**
	 * 关闭连接
	 */
	public void closeConnection(){
		if(connection != null){
			try {
				connection.close();
			} catch (IOException e) {
				logger.error(e.getMessage(),e);
			}
		}
	}
	
	public static final class ExchangeType {
		public static final String FANOUT = "fanout";
		public static final String DIRECT = "direct";
		public static final String TOPIC = "topic";
	}
}
