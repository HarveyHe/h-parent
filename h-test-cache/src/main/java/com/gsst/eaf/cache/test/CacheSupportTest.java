package com.gsst.eaf.cache.test;

import java.util.List;
import java.util.Map;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.util.Assert;

import com.gsst.eaf.cache.test.entity.TestEntity;
import com.gsst.eaf.cache.test.model.TestModel;
import com.gsst.eaf.cache.test.support.TestEntityCacheSupport;
import com.gsst.eaf.cache.test.support.TestModelCacheSupport;
import com.gsst.eaf.core.spring.ContextInitializer;
import com.gsst.eaf.core.utils.JSON;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(initializers = ContextInitializer.class)
public class CacheSupportTest {

	@Autowired
	private TestModelCacheSupport testModelCacheSupport;

	@Autowired
	private TestEntityCacheSupport testEntityCacheSupport;

	@Test
	public void testCacheSupport() throws Exception {
//		this.testModelSupport();
		this.testEntitySupport();
//		testSupport();
		Thread.currentThread().join(1000L);
	}

	private void testSupport() throws InterruptedException {
		TestModel model = this.testModelCacheSupport.get(4);
		Assert.notNull(model);
		System.out.println(JSON.serialize(model));
		Thread.sleep(5000l);
		model.setName("ssssssssssssssssssss");
		testModelCacheSupport.put(model);
		Thread.sleep(5000l);
		model = this.testModelCacheSupport.get(4);
		Assert.notNull(model);
		System.out.println(JSON.serialize(model));
		
		List<TestModel> models = this.testModelCacheSupport.getAll();
		Assert.notEmpty(models);
		System.out.println(JSON.serialize(models));
	}
	private void testModelSupport() {
		TestModel model = this.testModelCacheSupport.get(4);
		Assert.notNull(model);
		System.out.println(JSON.serialize(model));
		Map<String, Object> modelMap = this.testModelCacheSupport.getAsMap(5);
		Assert.notNull(modelMap);
		System.out.println(modelMap);

		List<TestModel> models = this.testModelCacheSupport.getAll();
		Assert.notEmpty(models);
		System.out.println(JSON.serialize(models));

		List<Map<String, Object>> modelMaps = this.testModelCacheSupport.getAllAsMap();
		Assert.notEmpty(modelMaps);
		System.out.println(modelMaps);

		this.testModelCacheSupport.delete(4);
		boolean bool = this.testModelCacheSupport.exists(4);
		Assert.isTrue(!bool);
	}

	private void testEntitySupport() {
		TestEntity entity = this.testEntityCacheSupport.get(4);
		Assert.notNull(entity);
		System.out.println(JSON.serialize(entity));
		Map<String, Object> entityMap = this.testEntityCacheSupport.getAsMap(5);
		Assert.notNull(entityMap);
		System.out.println(entityMap);

		List<TestEntity> entities = this.testEntityCacheSupport.getAll();
		Assert.notEmpty(entities);
		System.out.println(JSON.serialize(entities));

		List<Map<String, Object>> entityMaps = this.testEntityCacheSupport.getAllAsMap();
		Assert.notEmpty(entityMaps);
		System.out.println(entityMaps);

		this.testEntityCacheSupport.delete(4);
		boolean bool = this.testEntityCacheSupport.exists(4);
		Assert.isTrue(!bool);
	}

}
