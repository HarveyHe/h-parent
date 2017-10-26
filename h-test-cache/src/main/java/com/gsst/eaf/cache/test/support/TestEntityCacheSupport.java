package com.gsst.eaf.cache.test.support;

import java.util.List;
import java.util.Map;

import com.gsst.eaf.cache.Cacheable;
import com.gsst.eaf.cache.annotation.CacheEval;
import com.gsst.eaf.cache.annotation.CacheEvict;
import com.gsst.eaf.cache.annotation.CacheGet;
import com.gsst.eaf.cache.annotation.CacheLoad;
import com.gsst.eaf.cache.annotation.CachePut;
import com.gsst.eaf.cache.test.entity.TestEntity;

public interface TestEntityCacheSupport extends Cacheable {

	@CacheGet(prefix = "TestEntity", valueClazz = TestEntity.class)
	@CacheLoad(key = "result.entityId",load = "@testService.getEntity(arg[0])",dataChanged="@testService.entityChanged(result.entityId,result.entityDate)")
	TestEntity get(Integer id);
	
	@CacheGet(prefix = "TestEntity", valueClazz = Map.class)
	@CacheLoad(key = "result.entityId",load = "@testService.getEntity(arg[0])",dataChanged="@testService.entityChanged(result.entityId,result.entityDate)")
	Map<String,Object> getAsMap(Integer id);
	
	@CacheGet(prefix = "TestEntity", valueClazz = TestEntity.class)
	@CacheLoad(key = "result[index].entityId",load = "@testService.getEntityList()",dataChanged="@testService.entitiesChanged(result)")
	List<TestEntity> getAll();
	
	@CacheGet(prefix = "TestEntity", valueClazz = Map.class)
	@CacheLoad(key = "result[index].entityId",load = "@testService.getEntityList()",dataChanged="@testService.entitiesChanged(result)")
	List<Map<String,Object>> getAllAsMap();
	
	@CachePut(prefix = "TestEntity",key = "arg[0].entityId",value = "arg[0]")
	void put(TestEntity entity);
	
	@CachePut(prefix = "TestEntity",key = "item.entityId",value = "arg[0]")
	void putAll(List<TestEntity> entities);
	
	@CacheEvict(prefix = "TestEntity")
	void delete(Integer id);
	
	@CacheEval(prefix="TestEntity",eval = "exists(prefix,arg[0])")
	boolean exists(Integer id);
}
