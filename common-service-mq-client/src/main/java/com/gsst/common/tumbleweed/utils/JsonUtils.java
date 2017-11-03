package com.gsst.common.tumbleweed.utils;

import java.io.StringWriter;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;

import com.fasterxml.jackson.core.JsonFactory;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonParser.Feature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.gsst.eaf.core.utils.DateUtils;

/**
 *   系统Json 工具类
 */
public final class JsonUtils {

	private static Logger log = Logger.getLogger(JsonUtils.class);

	private JsonUtils() {
	}

	private static final ObjectMapper MAPPER = new ObjectMapper();

	static {
		MAPPER.configure(Feature.ALLOW_UNQUOTED_FIELD_NAMES, false);
	}

	private static final JsonFactory JSONFACTORY = new JsonFactory();

	/**
	 * 转换Java Bean 为 json
	 */
	public static String beanToJson(Object o) {
		StringWriter sw = new StringWriter(300);
		JsonGenerator jsonGenerator = null;
		String result = "";
		try {
			jsonGenerator = JSONFACTORY.createJsonGenerator(sw);
			MAPPER.writeValue(jsonGenerator, o);
			result = sw.toString();
		} catch (Exception e) {
			log.error(e);
			return null;
		} finally {
			if (jsonGenerator != null) {
				try {
					jsonGenerator.close();
				} catch (Exception e) {
					log.error(e);
				}
			}
		}
		log.debug(result);
		return result;
	}

	/**
	 * 转换Java Bean 为 json 附加了个时间格式参数
	 * 
	 * @param o
	 * @param dateFormat
	 * @return
	 */
	public static String beanToJson(Object o, SimpleDateFormat dateFormat) {
		StringWriter sw = new StringWriter(300);
		JsonGenerator jsonGenerator = null;
		String result = "";
		try {
			MAPPER.setDateFormat(dateFormat);
			jsonGenerator = JSONFACTORY.createJsonGenerator(sw);
			MAPPER.writeValue(jsonGenerator, o);
			result = sw.toString();
			MAPPER.setDateFormat(new SimpleDateFormat(DateUtils.DateFormats.ISO_DATE_FORMAT));
		} catch (Exception e) {
			log.error(e);
			return null;
		} finally {
			if (jsonGenerator != null) {
				try {
					jsonGenerator.close();
				} catch (Exception e) {
					log.error(e);
				}
			}
		}
		log.debug(result);
		return result;
	}

	/**
	 * json 转 javabean
	 * 
	 * @param json
	 * @return
	 */
	public static <T> T jsonToBean(String json, Class<T> clazz) {
		try {
			return MAPPER.readValue(json, clazz);
		} catch (Exception e) {
			log.error(e);
			return null;
		}
	}

	public static <T> T jsonToBean(String json, Class<T> clazz,
			DateFormat dateFormat) {
		try {
			MAPPER.setDateFormat(dateFormat);
			T s = MAPPER.readValue(json, clazz);
			MAPPER.setDateFormat(new SimpleDateFormat(DateUtils.DateFormats.ISO_DATE_FORMAT));
			return s;
		} catch (Exception e) {
			log.error(e);
			return null;
		}
	}

	/**
	 * json 转 javabean 附加了个时间格式参数
	 * 
	 * @param json
	 * @param clazz
	 * @param dateFormat
	 * @return
	 */
	public static <T> T jsonToBean(String json, Class<T> clazz,
			SimpleDateFormat dateFormat) {
		try {
			MAPPER.setDateFormat(dateFormat);
			T object = MAPPER.readValue(json, clazz);
			MAPPER.setDateFormat(new SimpleDateFormat(DateUtils.DateFormats.ISO_DATE_FORMAT));
			return object;
		} catch (Exception e) {
			log.error(e);
			return null;
		}
	}

	/**
	 * 转换Java Bean 为 HashMap
	 */
	@SuppressWarnings("unchecked")
	public static Map<String, Object> beanToMap(Object o) {
		try {
			return MAPPER.readValue(beanToJson(o), HashMap.class);
		} catch (Exception e) {
			log.error(e);
			return null;
		}
	}

	/**
	 * 转换Json String 为 HashMap
	 */
	@SuppressWarnings("unchecked")
	public static Map<String, Object> jsonToMap(String json,
			boolean collToString) {
		try {
			Map<String, Object> map = MAPPER.readValue(json, HashMap.class);
			if (collToString) {
				for (Map.Entry<String, Object> entry : map.entrySet()) {
					if (entry.getValue() instanceof Collection
							|| entry.getValue() instanceof Map) {
						entry.setValue(beanToJson(entry.getValue()));
					}
				}
			}
			return map;
		} catch (Exception e) {
			log.error(e);
			return null;
		}
	}

	/**
	 * List 转换成json
	 * 
	 * @param list
	 * @return
	 */
	public static String listToJson(List<?> list) {
		JsonGenerator jsonGenerator = null;
		StringWriter sw = new StringWriter();
		try {
			jsonGenerator = JSONFACTORY.createJsonGenerator(sw);
			new ObjectMapper().writeValue(jsonGenerator, list);
			jsonGenerator.flush();
			return sw.toString();
		} catch (Exception e) {
			log.error(e);
			return null;
		} finally {
			if (jsonGenerator != null) {
				try {
					jsonGenerator.flush();
					jsonGenerator.close();
				} catch (Exception e) {
					log.error(e);
				}
			}
		}
	}

}
