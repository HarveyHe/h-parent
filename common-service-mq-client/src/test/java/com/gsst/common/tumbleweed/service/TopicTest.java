package com.gsst.common.tumbleweed.service;

import java.util.List;

import com.aliyuncs.DefaultAcsClient;
import com.aliyuncs.IAcsClient;
import com.aliyuncs.exceptions.ClientException;
import com.aliyuncs.exceptions.ServerException;
import com.aliyuncs.http.FormatType;
import com.aliyuncs.ons.model.v20160503.OnsRegionListRequest;
import com.aliyuncs.ons.model.v20160503.OnsRegionListResponse;
import com.aliyuncs.ons.model.v20160503.OnsTopicCreateRequest;
import com.aliyuncs.ons.model.v20160503.OnsTopicCreateResponse;
import com.aliyuncs.ons.model.v20160503.OnsTopicDeleteRequest;
import com.aliyuncs.ons.model.v20160503.OnsTopicDeleteResponse;
import com.aliyuncs.ons.model.v20160503.OnsTopicGetRequest;
import com.aliyuncs.ons.model.v20160503.OnsTopicGetResponse;
import com.aliyuncs.ons.model.v20160503.OnsTopicListRequest;
import com.aliyuncs.ons.model.v20160503.OnsTopicListResponse;
import com.aliyuncs.profile.DefaultProfile;
import com.aliyuncs.profile.IClientProfile;

public class TopicTest {
	
	public static IAcsClient getIAcsClient(){
		String regionId = "cn-shenzhen";						// API的网关所在区域
        String accessKey = "XUb1o8pHoT8Co1pH";					// 用户在阿里云官网上获取的AK
        String secretKey = "NQhZPbLId3xuRhwnJszWdQAfZmL3gK";	// 用户在阿里云官网上获得的SK
        String endPointName ="cn-shenzhen";						// 接入点名称
        String productName ="Ons";								// OpenAPI的产品名称
        String domain ="ons."+regionId+".aliyuncs.com";			// OpenAPI的接入点Domain

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
	
	public static void getOnsRegionList(){
        IAcsClient iAcsClient= getIAcsClient();
        
        OnsRegionListRequest request = new OnsRegionListRequest();
        request.setAcceptFormat(FormatType.JSON);
        request.setPreventCache(System.currentTimeMillis());
        try {
            OnsRegionListResponse response = iAcsClient.getAcsResponse(request);
            List<OnsRegionListResponse.RegionDo> regionDoList=response.getData();
            for (OnsRegionListResponse.RegionDo regionDo:regionDoList){
                System.out.println(regionDo.getId()+"  "+
                regionDo.getOnsRegionId()+"  "+
                regionDo.getRegionName()+"  "+
                regionDo.getChannelId()+"  "+
                regionDo.getChannelName()+"  "+
                regionDo.getCreateTime()+"  "+
                regionDo.getUpdateTime());
            }
        } catch (ClientException e) {
            e.printStackTrace();
        }
        catch (Exception e) {
            e.printStackTrace();
        }		
	}
	
	public static void getTopicList(){
		IAcsClient iAcsClient= getIAcsClient();
		
        OnsTopicListRequest request = new OnsTopicListRequest();
        /**
        *ONSRegionId是指你需要API访问ONS哪个区域的资源。
        *该值必须要根据OnsRegionList方法获取的列表来选择和配置，因为OnsRegionId是变动的，不能够写固定值
        */
        request.setOnsRegionId("cn-qingdao-publictest");
        request.setPreventCache(System.currentTimeMillis());
        //request.setTopic("XXXXXXXXXXXXX");
        try {
            OnsTopicListResponse response = iAcsClient.getAcsResponse(request);
            List<OnsTopicListResponse.PublishInfoDo> publishInfoDoList = response.getData();
            for(OnsTopicListResponse.PublishInfoDo publishInfoDo : publishInfoDoList){
                System.out.println(publishInfoDo.getTopic()+"     "+ publishInfoDo.getOwner());
            }
        } catch (ServerException e) {
            e.printStackTrace();
        } catch (ClientException e) {
            e.printStackTrace();
        }
	}
	
	public static void createTopic(){
		IAcsClient iAcsClient= getIAcsClient();

        OnsTopicCreateRequest request = new OnsTopicCreateRequest();
        request.setAcceptFormat(FormatType.JSON);
        request.setTopic("Topic_gsst_Test_1");        
        request.setRemark("test");
        request.setStatus(0);
        request.setOnsRegionId("cn-qingdao-publictest");
        
        //request.setCluster("taobaodaily");
        
        request.setPreventCache(System.currentTimeMillis());
        try {
            OnsTopicCreateResponse response = iAcsClient.getAcsResponse(request);
            System.out.println(response.getRequestId());
        }
        catch (ServerException e) {
            e.printStackTrace();
        }
        catch (ClientException e) {
            e.printStackTrace();
        }
        catch (Exception e) {
            e.printStackTrace();
        }
	}
	
	public static void deleteTopic(){
		IAcsClient iAcsClient= getIAcsClient();
		
        OnsTopicDeleteRequest request =new OnsTopicDeleteRequest();
        //request.setCluster("taobaodaily");
        request.setPreventCache(System.currentTimeMillis());
        request.setOnsRegionId("cn-qingdao-publictest");
        request.setTopic("Topic_gsst_Test_1");
        try {
            OnsTopicDeleteResponse response = iAcsClient.getAcsResponse(request);
            System.out.println(response.getRequestId());
        }
        catch (ServerException e) {
            e.printStackTrace();
        }
        catch (ClientException e) {
            e.printStackTrace();
        }
        catch (Exception e) {
            e.printStackTrace();
        }
	}
	
	public static void getTopic(){
		IAcsClient iAcsClient = getIAcsClient();

        OnsTopicGetRequest request = new OnsTopicGetRequest();
        request.setAcceptFormat(FormatType.JSON);
        request.setTopic("Topic_gsst_Test");
        //request.setOnsRegionId("cn-qingdao-publictest");
        request.setPreventCache(System.currentTimeMillis());
        try {
            OnsTopicGetResponse response = iAcsClient.getAcsResponse(request);
            List<OnsTopicGetResponse.PublishInfoDo> publishInfoDoList=response.getData();
            for (OnsTopicGetResponse.PublishInfoDo publishInfoDo:publishInfoDoList){
                System.out.println(publishInfoDo.getId()+"  "+
                        publishInfoDo.getChannelId()+"  "+
		                publishInfoDo.getChannelName()+"  "+
		                publishInfoDo.getOnsRegionId()+"  "+
		                publishInfoDo.getRegionName()+"  "+
		                publishInfoDo.getTopic()+"  "+
		                publishInfoDo.getOwner()+"  "+
		                publishInfoDo.getRelation()+"  "+
		                publishInfoDo.getRelationName()+"  "+
		                publishInfoDo.getStatus()+"  "+
		                publishInfoDo.getStatusName()+"  "+
		                publishInfoDo.getAppkey()+"  "+
		                publishInfoDo.getCreateTime()+"  "+
		                publishInfoDo.getUpdateTime()+"  "+
		                publishInfoDo.getRemark());
            }
            System.out.println(response.getRequestId());
        }
        catch (ServerException e) {
            e.printStackTrace();
        }
        catch (ClientException e) {
            e.printStackTrace();
        }
        catch (Exception e) {
            e.printStackTrace();
        }
	}
	
	
	
	public static void main (String[]args) {
        //getOnsRegionList();
        //getTopicList();
        createTopic();
        //deleteTopic();
		//getTopic();
    }
}
