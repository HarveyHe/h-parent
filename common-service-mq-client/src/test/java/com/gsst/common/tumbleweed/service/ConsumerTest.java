package com.gsst.common.tumbleweed.service;

import java.util.List;
import java.util.Properties;

import com.aliyun.openservices.ons.api.Action;
import com.aliyun.openservices.ons.api.ConsumeContext;
import com.aliyun.openservices.ons.api.Consumer;
import com.aliyun.openservices.ons.api.Message;
import com.aliyun.openservices.ons.api.MessageListener;
import com.aliyun.openservices.ons.api.ONSFactory;
import com.aliyun.openservices.ons.api.PropertyKeyConst;
import com.aliyuncs.DefaultAcsClient;
import com.aliyuncs.IAcsClient;
import com.aliyuncs.exceptions.ClientException;
import com.aliyuncs.exceptions.ServerException;
import com.aliyuncs.http.FormatType;
import com.aliyuncs.ons.model.v20160503.OnsSubscriptionCreateRequest;
import com.aliyuncs.ons.model.v20160503.OnsSubscriptionCreateResponse;
import com.aliyuncs.ons.model.v20160503.OnsSubscriptionDeleteRequest;
import com.aliyuncs.ons.model.v20160503.OnsSubscriptionDeleteResponse;
import com.aliyuncs.ons.model.v20160503.OnsSubscriptionGetRequest;
import com.aliyuncs.ons.model.v20160503.OnsSubscriptionGetResponse;
import com.aliyuncs.ons.model.v20160503.OnsSubscriptionListRequest;
import com.aliyuncs.ons.model.v20160503.OnsSubscriptionListResponse;
import com.aliyuncs.profile.DefaultProfile;
import com.aliyuncs.profile.IClientProfile;

public class ConsumerTest {
	
	public static void receiveMessage(){
		Properties properties = new Properties();
        properties.put(PropertyKeyConst.ConsumerId, "CID_gsst_Test");// 您在MQ控制台创建的Consumer ID
        properties.put(PropertyKeyConst.AccessKey,"XUb1o8pHoT8Co1pH");// 鉴权用AccessKey，在阿里云服务器管理控制台创建
	    properties.put(PropertyKeyConst.SecretKey, "NQhZPbLId3xuRhwnJszWdQAfZmL3gK");// 鉴权用SecretKey，在阿里云服务器管理控制台创建
        Consumer consumer = ONSFactory.createConsumer(properties);
        
        consumer.subscribe("Topic_gsst_Test", "*", new MessageListener() {
        	
        	@Override
            public Action consume(Message message, ConsumeContext context) {
                System.out.println("Receive: " + message);
                return Action.CommitMessage;
            }
        });
        consumer.start();
        System.out.println("Consumer Started");		
	}
	
	public static IAcsClient getIAcsClient(){
		String regionId = "cn-shenzhen";
        String accessKey = "XUb1o8pHoT8Co1pH";
        String secretKey = "NQhZPbLId3xuRhwnJszWdQAfZmL3gK";
        String endPointName ="cn-shenzhen";
        String productName ="Ons";
        String domain ="ons.cn-shenzhen.aliyuncs.com";

        /**
        *根据自己所在的区域选择Region后,设置对应的接入点
        */
        try {
            DefaultProfile.addEndpoint(endPointName,regionId,productName,domain);
        } catch (ClientException e) {
            e.printStackTrace();
        }
        IClientProfile profile= DefaultProfile.getProfile(regionId,accessKey,secretKey);
        
        return new DefaultAcsClient(profile);
	}
	
	public static void createConsumer() {
		IAcsClient iAcsClient = getIAcsClient();
		
		OnsSubscriptionCreateRequest request = new OnsSubscriptionCreateRequest();
		request.setOnsRegionId("cn-qingdao-publictest");
		request.setPreventCache(System.currentTimeMillis());
		request.setAcceptFormat(FormatType.JSON);
		request.setTopic("Topic_gsst_Test");
		request.setConsumerId("CID_gsst_Test");
		try {
			OnsSubscriptionCreateResponse response = iAcsClient.getAcsResponse(request);
			System.out.println(response.getRequestId());
		} catch (ServerException e) {
			e.printStackTrace();
		} catch (ClientException e) {
			e.printStackTrace();
		}
	}
	
	public static void deleteConsumer() {
		IAcsClient iAcsClient = getIAcsClient();
		
		OnsSubscriptionDeleteRequest request = new OnsSubscriptionDeleteRequest();
		request.setOnsRegionId("cn-qingdao-publictest");
		request.setPreventCache(System.currentTimeMillis());
		request.setAcceptFormat(FormatType.JSON);
		request.setTopic("Topic_gsst_Test");
		request.setConsumerId("CID_gsst_Test");
		try {
			OnsSubscriptionDeleteResponse response = iAcsClient.getAcsResponse(request);
			System.out.println(response.getRequestId());
		} catch (ServerException e) {
			e.printStackTrace();
		} catch (ClientException e) {
			e.printStackTrace();
		}
	}
	
	public static void getConsumer() {
		IAcsClient iAcsClient = getIAcsClient();
		
		OnsSubscriptionGetRequest request = new OnsSubscriptionGetRequest();
		request.setOnsRegionId("cn-qingdao-publictest");
		request.setPreventCache(System.currentTimeMillis());
		request.setAcceptFormat(FormatType.JSON);
		request.setTopic("Topic_gsst_Test");
		request.setConsumerId("CID_gsst_Test");
		try {
			OnsSubscriptionGetResponse response = iAcsClient.getAcsResponse(request);
			List<OnsSubscriptionGetResponse.SubscribeInfoDo> subscribeInfoDoList = response.getData();
			for (OnsSubscriptionGetResponse.SubscribeInfoDo subscribeInfoDo : subscribeInfoDoList) {
				System.out.println(subscribeInfoDo.getId() + "  " + subscribeInfoDo.getChannelId() + "  "
						+ subscribeInfoDo.getChannelName() + "  " + subscribeInfoDo.getOnsRegionId() + "  "
						+ subscribeInfoDo.getRegionName() + "  " + subscribeInfoDo.getOwner() + "  "
						+ subscribeInfoDo.getConsumerId() + "  " + subscribeInfoDo.getTopic() + "  "
						+ subscribeInfoDo.getStatus() + "  " + subscribeInfoDo.getStatusName() + " "
						+ subscribeInfoDo.getCreateTime() + "  " + subscribeInfoDo.getUpdateTime());
			}
		} catch (ServerException e) {
			e.printStackTrace();
		} catch (ClientException e) {
			e.printStackTrace();
		}
	}
	
	public static void getConsumerList() {
		IAcsClient iAcsClient = getIAcsClient();
		
		OnsSubscriptionListRequest request = new OnsSubscriptionListRequest();
		request.setOnsRegionId("cn-qingdao-publictest");
		request.setPreventCache(System.currentTimeMillis());
		request.setAcceptFormat(FormatType.JSON);
		try {
			OnsSubscriptionListResponse response = iAcsClient.getAcsResponse(request);
			List<OnsSubscriptionListResponse.SubscribeInfoDo> subscribeInfoDoList = response.getData();
			for (OnsSubscriptionListResponse.SubscribeInfoDo subscribeInfoDo : subscribeInfoDoList) {
				System.out.println(subscribeInfoDo.getId() + "  " + subscribeInfoDo.getChannelId() + "  "
						+ subscribeInfoDo.getChannelName() + "  " + subscribeInfoDo.getOnsRegionId() + "  "
						+ subscribeInfoDo.getRegionName() + "  " + subscribeInfoDo.getOwner() + "  "
						+ subscribeInfoDo.getConsumerId() + "  " + subscribeInfoDo.getTopic() + "  "
						+ subscribeInfoDo.getStatus() + "  " + subscribeInfoDo.getStatusName() + " "
						+ subscribeInfoDo.getCreateTime() + "  " + subscribeInfoDo.getUpdateTime());
			}
		} catch (ServerException e) {
			e.printStackTrace();
		} catch (ClientException e) {
			e.printStackTrace();
		}
	}
	
    public static void main(String[] args) {
        //createConsumer();
    	//deleteConsumer();
    	//getConsumer();
    	//getConsumerList();
    	receiveMessage();
    }
}
