package com.gsst.eaf.cache.test.support;

import java.util.List;
import java.util.Map;

import com.gsst.eaf.cache.Cacheable;
import com.gsst.eaf.cache.annotation.CacheEval;
import com.gsst.eaf.cache.annotation.CacheEvict;
import com.gsst.eaf.cache.annotation.CacheGet;
import com.gsst.eaf.cache.annotation.CacheLoad;
import com.gsst.eaf.cache.annotation.CachePut;
import com.gsst.eaf.cache.test.model.TestModel;

public interface TestModelCacheSupport extends Cacheable {

	@CacheGet(prefix = "TestModel", valueClazz = TestModel.class)
	@CacheLoad(key = "result.id",load = "@testService.getModel(arg[0])",dataChanged = "@testService.modelChanged(result.id,result.date)")
	TestModel get(Integer id);
	
	@CacheGet(prefix = "TestModel", valueClazz = Map.class)
	@CacheLoad(key = "result.id",load = "@testService.getModel(arg[0])",dataChanged = "@testService.modelChanged(result.id,result.date)")
	Map<String,Object> getAsMap(Integer id);
	
	@CacheGet(prefix = "TestModel", valueClazz = TestModel.class)
	@CacheLoad(key = "result[index].id",load = "@testService.getModelList()",dataChanged = "@testService.modelsChanged(result)")
	List<TestModel> getAll();
	
	@CacheGet(prefix = "TestModel", valueClazz = Map.class)
	@CacheLoad(key = "result[index].id",load = "@testService.getModelList()",dataChanged = "@testService.modelsChanged(result)")
	List<Map<String,Object>> getAllAsMap();
	
	@CachePut(prefix = "TestModel",key = "arg[0].id",value = "arg[0]")
	void put(TestModel model);
	
	@CachePut(prefix = "TestModel")
	void putAll(List<TestModel> models);
	
	/**
	 * TestModel为数据库实体映射,可以自动识别主键值
	 * @param modle
	 */
	@CachePut(prefix = "TestModel")
	void putModel(TestModel modle);
	
	@CacheEvict(prefix = "TestModel")
	void delete(Integer id);
	
	@CacheEval(prefix="TestModel",eval = "exists(prefix,arg[0])")
	boolean exists(Integer id);
}
