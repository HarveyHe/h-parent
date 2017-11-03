package com.gsst.common.tumbleweed.client.aop;

import java.lang.reflect.Method;
import java.util.Base64;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.gsst.common.tumbleweed.client.annotation.Producer;
import com.gsst.common.tumbleweed.service.ProducerService;
import com.gsst.common.tumbleweed.utils.JsonUtils;
import com.gsst.common.tumbleweed.utils.ProducerUtils;

@Aspect
@Component
public class TumbleweedPublishMessageAspect {

	Logger logger = LoggerFactory.getLogger(this.getClass());

	@Autowired
	private ProducerService producerService;

	@Pointcut(value = "execution(* *..service.*Service.*(..))")
	public void servicePublishMethod() {
	}

	@Around("servicePublishMethod() and @annotation(producer)")
	public Object around(ProceedingJoinPoint pjp, Producer producer) throws Throwable {

		Object retVal = pjp.proceed();

		MethodSignature msig = (MethodSignature) pjp.getSignature();
		Method currentMethod = pjp.getTarget().getClass().getMethod(msig.getName(), msig.getParameterTypes());

		String jsonData = JsonUtils.beanToJson(retVal);
		
		String producerId = null;
		
		if (StringUtils.isBlank(producer.producerId())) {
			producerId = ProducerUtils.getProducerId(currentMethod);
		} else {
			producerId = producer.producerId();
		}
		
		String key = null;
		if(StringUtils.isNotBlank(producer.key())){
			key = producer.key();
		}else{
			key = "";
		}
		
		
		if(retVal instanceof Map){
			Map<String,Object> retValMap = (Map<String,Object>)retVal;
			
			logger.info("retValMap = {}",JsonUtils.beanToJson(retValMap));
			String routingkey = null;
			//检查是否有playload
			if(retValMap.containsKey("playload")){
				Map playload = (Map) retValMap.get("playload");
				routingkey = (String)playload.get(key);
			}else{
				//判断是否存在对应的key 
				if(retValMap.containsKey(key)){
					routingkey = retValMap.get(key).toString();
				}
			}
			
			if(StringUtils.isBlank(routingkey)){
				routingkey = "*";
			}

			logger.info("publish topic={},producerId={},routingkey={},jsonData={}", producer.topic(), producerId,routingkey, jsonData);

			this.producerService.publishMessage(producer.topic(), producerId,routingkey, Base64.getEncoder().encodeToString(jsonData.getBytes()));
			
		}else{
			logger.warn("retVal's data type is not supported!");
		}
		
		return retVal;
		
	}

}
