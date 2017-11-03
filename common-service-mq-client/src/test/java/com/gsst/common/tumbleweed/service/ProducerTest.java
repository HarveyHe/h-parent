package com.gsst.common.tumbleweed.service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import com.aliyun.openservices.ons.api.Message;
import com.aliyun.openservices.ons.api.ONSFactory;
import com.aliyun.openservices.ons.api.Producer;
import com.aliyun.openservices.ons.api.PropertyKeyConst;
import com.aliyun.openservices.ons.api.SendResult;
import com.aliyun.openservices.shade.com.alibaba.rocketmq.shade.com.alibaba.fastjson.JSONObject;
import com.aliyuncs.DefaultAcsClient;
import com.aliyuncs.IAcsClient;
import com.aliyuncs.exceptions.ClientException;
import com.aliyuncs.exceptions.ServerException;
import com.aliyuncs.http.FormatType;
import com.aliyuncs.ons.model.v20160503.OnsPublishCreateRequest;
import com.aliyuncs.ons.model.v20160503.OnsPublishCreateResponse;
import com.aliyuncs.ons.model.v20160503.OnsPublishDeleteRequest;
import com.aliyuncs.ons.model.v20160503.OnsPublishDeleteResponse;
import com.aliyuncs.ons.model.v20160503.OnsPublishGetRequest;
import com.aliyuncs.ons.model.v20160503.OnsPublishGetResponse;
import com.aliyuncs.ons.model.v20160503.OnsPublishListRequest;
import com.aliyuncs.ons.model.v20160503.OnsPublishListResponse;
import com.aliyuncs.profile.DefaultProfile;
import com.aliyuncs.profile.IClientProfile;

public class ProducerTest {
	
	public static void sendMessage(){
		Properties properties = new Properties();
	     properties.put(PropertyKeyConst.ProducerId, "PID_gsst_Test");// 您在MQ控制台创建的Producer ID
	     properties.put(PropertyKeyConst.AccessKey,"XUb1o8pHoT8Co1pH");// 鉴权用AccessKey，在阿里云服务器管理控制台创建
	     properties.put(PropertyKeyConst.SecretKey, "NQhZPbLId3xuRhwnJszWdQAfZmL3gK");// 鉴权用SecretKey，在阿里云服务器管理控制台创建
	     Producer producer = ONSFactory.createProducer(properties);
	     // 在发送消息前，必须调用start方法来启动Producer，只需调用一次即可
	     producer.start();
	    Map<String,String> map = new HashMap<>();
	    map.put("name", "allan");
	    map.put("age", "25");
	    String data = JSONObject.toJSON(map).toString();
        Message msg = new Message(
            // Message Topic
            "Topic_gsst_Test",
            // Message Tag,
            // 可理解为Gmail中的标签，对消息进行再归类，方便Consumer指定过滤条件在MQ服务器过滤
            "TagA",
            // Message Body
            // 任何二进制形式的数据， MQ不做任何干预，
            // 需要Producer与Consumer协商好一致的序列化和反序列化方式
            data.getBytes());
        // 设置代表消息的业务关键属性，请尽可能全局唯一，以方便您在无法正常收到消息情况下，可通过MQ控制台查询消息并补发
        // 注意：不设置也不会影响消息正常收发
        msg.setKey("ORDERID_100");
        // 发送消息，只要不抛异常就是成功
        // 打印Message ID，以便用于消息发送状态查询
        SendResult sendResult = producer.send(msg);
        System.out.println("Send Message success. Message ID is: " + sendResult.getMessageId());
        
        System.out.println(sendResult);
        
	     // 在应用退出前，可以销毁Producer对象
	     // 注意：如果不销毁也没有问题
	     producer.shutdown();
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
	
	public static void createProducer() {
		IAcsClient iAcsClient = getIAcsClient();
		
		OnsPublishCreateRequest request = new OnsPublishCreateRequest();
		request.setOnsRegionId("cn-qingdao-publictest");
		request.setPreventCache(System.currentTimeMillis());
		request.setAcceptFormat(FormatType.JSON);
		request.setTopic("Topic_gsst_Test_1");
		request.setProducerId("PID_gsst_Test_1");
		try {
			OnsPublishCreateResponse response = iAcsClient.getAcsResponse(request);
			System.out.println(response.getRequestId());
		} catch (ServerException e) {
			e.printStackTrace();
		} catch (ClientException e) {
			e.printStackTrace();
		}
	}
	
	public static void deleteProducer(){
		IAcsClient iAcsClient = getIAcsClient();
		
		OnsPublishDeleteRequest request = new OnsPublishDeleteRequest();        
	    request.setOnsRegionId("cn-qingdao-publictest");
	    request.setPreventCache(System.currentTimeMillis());
	    request.setAcceptFormat(FormatType.JSON);
	    request.setTopic("Topic_gsst_Test");
	    request.setProducerId("PID_gsst_Test");
	    try {
	        OnsPublishDeleteResponse response=iAcsClient.getAcsResponse(request);
	        System.out.println(response.getRequestId());
	    } catch (ServerException e) {
	        e.printStackTrace();
	    } catch (ClientException e) {
	        e.printStackTrace();
	    }
	}
	
	public static void getProducer(){
		IAcsClient iAcsClient = getIAcsClient();
		
		OnsPublishGetRequest request = new OnsPublishGetRequest();
        request.setOnsRegionId("cn-qingdao-publictest");
        request.setPreventCache(System.currentTimeMillis());
        request.setAcceptFormat(FormatType.JSON);
        request.setTopic("Topic_gsst_Test");
        request.setProducerId("PID_gsst_Test");
        try {
            OnsPublishGetResponse response=iAcsClient.getAcsResponse(request);
            List<OnsPublishGetResponse.PublishInfoDo> publishInfoDoList =response.getData();
            for (OnsPublishGetResponse.PublishInfoDo publishInfoDo:publishInfoDoList){
                System.out.println(publishInfoDo.getId()+"  "+
                publishInfoDo.getChannelId()+"  "+
                publishInfoDo.getChannelName()+"  "+
                publishInfoDo.getOnsRegionId()+"  "+
                publishInfoDo.getRegionName()+"  "+
                publishInfoDo.getOwner()+"  "+
                publishInfoDo.getProducerId()+"  "+
                publishInfoDo.getTopic()+"  "+
                publishInfoDo.getStatus()+"  "+
                publishInfoDo.getStatusName()+"  "+
                publishInfoDo.getCreateTime()+"  "+
                publishInfoDo.getUpdateTime());
            }
        } catch (ServerException e) {
            e.printStackTrace();
        } catch (ClientException e) {
            e.printStackTrace();
        }		
	}
	
	public static void getProducerList(){
		IAcsClient iAcsClient = getIAcsClient();
		
		OnsPublishListRequest request = new OnsPublishListRequest();
        request.setOnsRegionId("cn-qingdao-publictest");
        request.setPreventCache(System.currentTimeMillis());
        request.setAcceptFormat(FormatType.JSON);
        try {
            OnsPublishListResponse response=iAcsClient.getAcsResponse(request);
            List<OnsPublishListResponse.PublishInfoDo> publishInfoDoList =response.getData();
            for (OnsPublishListResponse.PublishInfoDo publishInfoDo:publishInfoDoList){
                System.out.println(publishInfoDo.getId()+"  "+
                        publishInfoDo.getChannelId()+"  "+
                        publishInfoDo.getChannelName()+"  "+
                        publishInfoDo.getOnsRegionId()+"  "+
                        publishInfoDo.getRegionName()+"  "+
                        publishInfoDo.getOwner()+"  "+
                        publishInfoDo.getProducerId()+"  "+
                        publishInfoDo.getTopic()+"  "+
                        publishInfoDo.getStatus()+"  "+
                        publishInfoDo.getStatusName()+"  "+
                        publishInfoDo.getCreateTime()+"  "+
                        publishInfoDo.getUpdateTime());
            }
        } catch (ServerException e) {
            e.printStackTrace();
        } catch (ClientException e) {
            e.printStackTrace();
        }
	}
	
	public static void main(String[] args) {
		//createProducer();
		//deleteProducer();
		//getProducer();
		//getProducerList();
		sendMessage();
	}
	 
	 
}
