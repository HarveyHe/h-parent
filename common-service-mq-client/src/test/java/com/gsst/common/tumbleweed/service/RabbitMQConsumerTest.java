package com.gsst.common.tumbleweed.service;

import java.io.IOException;
import java.util.concurrent.TimeoutException;

import com.rabbitmq.client.AMQP;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;
import com.rabbitmq.client.Consumer;
import com.rabbitmq.client.DefaultConsumer;
import com.rabbitmq.client.Envelope;

public class RabbitMQConsumerTest {

	private static final String HOST = "192.168.12.17";

	public static Channel getChannel() {
		ConnectionFactory factory = new ConnectionFactory();
		factory.setHost(HOST);
		factory.setUsername("guest");
		factory.setPassword("guest");
		factory.setPort(7001);
		factory.setConnectionTimeout(10000);
		Channel channel = null;
		try {
			Connection connection = factory.newConnection();
			channel = connection.createChannel();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return channel;
	}

	public static void consumeMessageFromDefaultExchange(String queueName) {
		Channel channel = getChannel();
		try {
			channel.basicQos(1);	// 一次获取一条消息，处理完再处理下一条消息
			boolean durable = true; // 消息队列定义为 持久化，即使服务器挂了，消息队列也不会丢失
			channel.queueDeclare(queueName, durable, false, false, null);
			Consumer consumer = new DefaultConsumer(channel) {
				@Override
				public void handleDelivery(String consumerTag, Envelope envelope, AMQP.BasicProperties properties,
						byte[] body) throws IOException {
					String message = new String(body, "UTF-8");
					System.out.println("01_ReceiveMessage====> " + message);

					try {
						Thread.sleep(3000);
						System.out.println("01_ReceiveMessage===>task: "+message+" finish");
					} catch (Exception e) {
					} finally {
						channel.basicAck(envelope.getDeliveryTag(), false);  // 返回确认状态
					}
				}
			};
			// 消息如果未被正常消费，则交给其他worker
			boolean autoAck = false; // 自动应答，为false 表示手动
			channel.basicConsume(queueName, autoAck, consumer); // 监听队列，手动返回完成 
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	
	public static void consumeMessageFromParticularExchange(String exchangeName, String exchange, String route, String consumerTag) {
		Channel channel = getChannel();
		try {
			channel.exchangeDeclare(exchangeName, exchange);			
			String queueName = channel.queueDeclare().getQueue(); // 临时队列：非持久化，可执行的，自动删除的队列			
			channel.queueBind(queueName, exchangeName, route);
			System.out.println(consumerTag + "====>Waiting for message....");
			Consumer consumer = new DefaultConsumer(channel) {
				@Override
				public void handleDelivery(String consumerTag, Envelope envelope, AMQP.BasicProperties properties,
						byte[] body) throws IOException {
					String message = new String(body, "UTF-8");
					
					System.out.println(consumerTag + "====> " + message);
					channel.basicAck(envelope.getDeliveryTag(), false);  // 返回确认状态
				}
			};
			channel.basicConsume(queueName, false, consumerTag, consumer);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public static void worker(String taskQueueName, String consumerTag) {
		Channel channel = getChannel();
		try {
			channel.basicQos(1);
			System.out.println(consumerTag + "====>Waiting for message....");
			Consumer consumer = new DefaultConsumer(channel) {
				@Override
				public void handleDelivery(String consumerTag, Envelope envelope, AMQP.BasicProperties properties,
						byte[] body) throws IOException {
					String message = new String(body, "UTF-8");
					
					System.out.println(consumerTag + "====> " + message);
					channel.basicAck(envelope.getDeliveryTag(), false);  // 返回确认状态
				}
			};
			channel.basicConsume(taskQueueName, false, consumerTag, consumer);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	
	

	public static void main(String[] args) throws IOException, TimeoutException {
		//consumeMessageFromDefaultExchange("queue_02");
		//consumeMessageFromParticularExchange("logs", "fanout", "");
		//consumeMessageFromParticularExchange("direct_logs", "direct", "one");
		//worker("direct_logs", "write_to_db", "write_to_db", "worker_02");
		//orderWorker("order_create", "order_worker_01");
		
		// 消费消息
		//RabbitMQTest.subscribeMessage("q_create_reserve_order", "reserve_order_worker_01");
		//RabbitMQTest.subscribeMessage("q_create_temporary_order", "temporary_order_worker_01");
		//RabbitMQTest.subscribeMessage("q_producer_create_order_logs", "producer_create_order_logs_worker_01");
		
	}
	
	
	
}
