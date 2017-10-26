package com.gsst.eaf.cache.test.service;

import java.util.Date;
import java.util.List;

import com.gsst.eaf.cache.test.entity.TestEntity;
import com.gsst.eaf.cache.test.model.TestModel;

public interface TestService {

	TestModel getModel(Integer id);
	
	TestEntity getEntity(Integer entityId);
	
	int modelChanged(Integer id,Date date);
	
	int entityChanged(Integer id,Date date);
	
	int modelsChanged(List<?> models);
	
	int entitiesChanged(List<?> entities);
	
	List<TestModel> getModelList();
	
	List<TestEntity> getEntityList();
}
