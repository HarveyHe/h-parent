package com.gsst.common.tumbleweed.service;

import java.io.IOException;
import java.util.concurrent.TimeoutException;

import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;
import com.rabbitmq.client.MessageProperties;

public class RabbitMQProducerTest {
	
	private static final String HOST = "192.168.12.17";
	private static Connection connection = null;
	
	public static Channel getChannel() {
		ConnectionFactory factory = new ConnectionFactory();
		factory.setHost(HOST);
		factory.setUsername("pkt");
		factory.setPassword("123456");
		factory.setPort(7001);
		factory.setVirtualHost("/pkt");
		Channel channel = null;
		try {
			connection = factory.newConnection();
			channel = connection.createChannel();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return channel;		
	}
	
	/**
	 * 推送到默认的Exchange，推送到指定的队列
	 */
	public static void publishMessageToDefaultExchange(String exchangeName, String queueName, String message) {
		Channel channel = getChannel();
		try {
			boolean durable = true; // 消息队列定义为 持久化，即使服务器挂了，消息队列也不会丢失，保存到硬盘了  ps：不是非常保险，短期内有可能有一些木有保存过去
			channel.queueDeclare(queueName, durable, false, false, null);
			channel.basicPublish(exchangeName, queueName, MessageProperties.PERSISTENT_TEXT_PLAIN, message.getBytes());
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			try {
				if(channel != null){
					channel.close();
				}
			} catch (Exception e2) {
				e2.printStackTrace();
			}
		}		
	}
	
	/**
	 * 推送到指定的Exchange
	 * fanout : broadcasts all messages to all consumers.
	 * direct : a message goes to the queues whose binding key exactly matches the routing key of the message.
	 * topic : 
	 */
	public static void publishMessageToParticularExchange(String exchangeName, String exchange, String message, String route){
		Channel channel = getChannel();
		try {			
			channel.exchangeDeclare(exchangeName, exchange);
			
			channel.basicPublish(exchangeName, route, null, message.getBytes());
			
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			try {
				if(channel != null){
					channel.close();
				}
			} catch (Exception e2) {
				e2.printStackTrace();
			}
		}		
	}
	
	public static void publishExchangeToExchangeMessage(String exchangeName, String desExchange, String desRouteKey, String desQueue, String message){
		Channel channel = getChannel();
		try {
			channel.exchangeDeclare(exchangeName, "fanout");
			channel.exchangeBind(desExchange, exchangeName, desRouteKey);
			channel.queueBind(desQueue, exchangeName, "");
			
			channel.basicPublish(exchangeName, desRouteKey, null, message.getBytes());
			
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			try {
				if(channel != null){
					channel.close();
				}
			} catch (Exception e2) {
				e2.printStackTrace();
			}
		}	
	}
	
	public static void main(String[] args) throws IOException, TimeoutException {

		publishMessageToParticularExchange("TEST_OUT", "fanout", "测试中文", "");
		//publishMessageToParticularExchange("direct_logs", "direct", "Hello World Two.", "two");
		//publishMessageToDefaultExchange("", "route_key_01", "route_key_01_hello");		
		//publishMessageToParticularExchange("direct_logs", "direct", "write_to_db_info", "write_to_db");
		//publishExchangeToExchangeMessage("pre_fanout_logs", "direct_logs", "write_to_db", "order_create", "info : Hello World! "+new Date());
		
		// 创建 topic
		//RabbitMQTest.exchangeDeclare("TEST_OUT", RabbitMQTest.ExchangeType.FANOUT);
		
		// 创建 producer
		//RabbitMQTest.exchangeToExchange("producer_create_order", RabbitMQTest.ExchangeType.FANOUT, "topic_create_order");
		
		// 创建消息队列
		//RabbitMQTest.queueDeclare("q_producer_create_order_logs", "producer_create_order", "");
		//RabbitMQTest.queueDeclare("q_create_reserve_order", "topic_create_order", "");
		//RabbitMQTest.queueDeclare("q_create_temporary_order", "topic_create_order", "");
				
		// 发布消息
//		RabbitMQ.pubishMessage("producer_create_order", "", "create a reserve order! "+new Date());
//		RabbitMQ.pubishMessage("producer_create_order", "", "create a temporary order! "+new Date());
//		
//		RabbitMQ.closeConnection();
		
//		RabbitMQConfig config = new RabbitMQConfig();
//		config.setHost("192.168.12.17");
//		config.setPassword("abc123");
//		config.setPort(7001);
//		config.setUsername("sharedparking");
//		config.setVirtualHost("/sharedparking");
//		RabbitMQ mq = new RabbitMQ(config);
//		
//		mq.exchangeDeclare("xxx", RabbitMQ.ExchangeType.FANOUT);
		
		
	}
	
	
}
