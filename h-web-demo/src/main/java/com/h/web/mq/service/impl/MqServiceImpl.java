package  com.h.web.mq.service.impl;

import com.gsst.common.tumbleweed.client.annotation.Consumer;
import com.gsst.common.tumbleweed.client.annotation.Producer;
import com.gsst.common.tumbleweed.utils.JsonUtils;
import com.gsst.eaf.core.service.BaseService;
import com.h.web.mq.service.MqService;


import java.util.HashMap;
import java.util.Map;

import org.springframework.stereotype.Service;

/**
 * Created by Eason.Lu on 2017/11/3.
 */
@Service
public class MqServiceImpl implements MqService,BaseService{
	
	private final String tipicTest = "exchange_message_test_mq";

    /**
     * 统一接收MQ信息
     */
	@Override
    @Consumer(topic = tipicTest)
    public Map<String, Object> receiveMsg(String message) throws Exception{

        Map<String, Object> data = JsonUtils.jsonToMap(message, false);

        System.out.println("exchange_message_fc1");
        return data;

    }
	@Override
    @Consumer(topic = tipicTest)
    public Map<String, Object> receiveMsg2(String message) throws Exception{
    	
    	Map<String, Object> data = JsonUtils.jsonToMap(message, false);
    	System.out.println("exchange_message_fc2");
    	return data;
    	
    }
	@Override
	@Consumer(topic = tipicTest)
	public void receiveMsg3(String message) throws Exception{
		
		Map<String, Object> data = JsonUtils.jsonToMap(message, false);
		System.out.println("exchange_message_fc3");
		
	}
	@Override
    @Producer(topic = tipicTest)
    public Map<String, String> publishReceive()
            throws Exception {

        Map<String, String> map = new HashMap<String, String>();
        map.put("content", "Hssss");
        return map;
    }
}
