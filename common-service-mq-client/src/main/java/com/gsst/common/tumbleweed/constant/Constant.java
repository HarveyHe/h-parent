package com.gsst.common.tumbleweed.constant;

import com.gsst.eaf.core.config.Config;

public final class Constant {

	public static final String TumbleweedConsumeService = "common.tumbleweed.consumeService";
	public static final String TumbleweedProduceService ="common.tumbleweed.produceService";
	public static final String TumbleweedBaseService ="common.tumbleweed.baseService";
	
	public static final String TumbleweedTopicPrefix = Config.get("common.tumbleweed.topic.prefix");
}
