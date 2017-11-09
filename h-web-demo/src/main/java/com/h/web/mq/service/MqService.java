package com.h.web.mq.service;
import java.util.Map;

import com.gsst.eaf.core.service.BaseService;

/**
 * Created by Eason.Lu on 2017/11/3.
 */
public interface MqService extends BaseService {
    Map<String, Object> receiveMsg(String message) throws Exception;

    Map<String, String> publishReceive() 
            throws Exception;

	Map<String, Object> receiveMsg2(String message) throws Exception;

	void receiveMsg3(String message) throws Exception;
}
