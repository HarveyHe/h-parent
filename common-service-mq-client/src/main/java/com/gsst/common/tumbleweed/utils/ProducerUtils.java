package com.gsst.common.tumbleweed.utils;

import java.lang.reflect.Method;

import org.apache.commons.lang3.StringUtils;

import com.gsst.common.tumbleweed.constant.Constant;

public class ProducerUtils {

	public static String getProducerId(Method method) {
		
		String beanName = StringUtils.uncapitalize(method.getDeclaringClass().getSimpleName());
		beanName = StringUtils.replace(beanName, "ServiceImpl", "");
		
		String topicPrefix = Constant.TumbleweedTopicPrefix;
		
		if(StringUtils.isNotBlank(topicPrefix)) {
			return "PID_" + topicPrefix + beanName + "_" + method.getName();
			
		} else {
			return "PID_" + beanName + "_" + method.getName();
		}
		
	}
}
