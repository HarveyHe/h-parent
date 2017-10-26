package com.gsst.eaf.cache.test.service.impl;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.springframework.stereotype.Service;

import com.gsst.eaf.cache.test.entity.TestEntity;
import com.gsst.eaf.cache.test.model.TestModel;
import com.gsst.eaf.cache.test.service.TestService;

@Service
public class TestServiceImpl implements TestService {

	private List<TestModel> models = genTestModel();
	private List<TestEntity> entities = genTestEntity();
	
	@Override
	public TestModel getModel(Integer id) {
		TestModel model = null;
		for(TestModel item : models) {
			if(item.getId().equals(id)) {
				model = item;
				break;
			}
		}
		return model;
	}

	@Override
	public TestEntity getEntity(Integer entityId) {
		TestEntity entity = null;
		for(TestEntity item : entities) {
			if(item.getEntityId().equals(entityId)) {
				entity = item;
				break;
			}
		}
		return entity;
	}

	@Override
	public List<TestModel> getModelList() {
		return this.models;
	}

	@Override
	public List<TestEntity> getEntityList() {
		return this.entities;
	}
	
	private List<TestModel> genTestModel() {
		List<TestModel> result = new ArrayList<>();
		for (int i = 0; i < 10; i++) {
			TestModel model = new TestModel();
			model.setId(i);
			model.setName("test model " + i);
			model.setDate(new Date());
			result.add(model);
		}
		return result;
	}

	private List<TestEntity> genTestEntity() {
		List<TestEntity> result = new ArrayList<>();
		for (int i = 0; i < 10; i++) {
			TestEntity model = new TestEntity();
			model.setEntityId(i);
			model.setEntityName("test entity " + i);
			model.setEntityDate(new Date());
			result.add(model);
		}
		return result;
	}

	@Override
	public int modelChanged(Integer id,Date date) {
		TestModel model = this.getModel(id);
		if(model == null) {
			//数据已删除,返回2,擦除缓存数据
			return 2;
		}else if(model.getDate().getTime() != date.getTime()) {
			//数据有更新,刷新缓存数据
			return 1;
		}
		//数据没有变化		
		return 0;
	}

	@Override
	public int entityChanged(Integer id,Date date) {
		TestEntity entity = this.getEntity(id);
		if(entity == null) {
			//数据已删除,返回2,擦除缓存数据
			return 2;
		}else if(entity.getEntityDate().getTime() != date.getTime()) {
			//数据有更新,刷新缓存数据
			return 1;
		}
		//数据没有变化
		return 0;
	}

	@Override
	public int modelsChanged(List<?> models) {
		if(models.size() != this.models.size()) {
			//总行数不一致,需要刷新
			return 2;
		}
		return 0;
	}

	@Override
	public int entitiesChanged(List<?> entities) {
		if(entities.size() != this.entities.size()) {
			//总行数不一致,需要刷新
			return 2;
		}
		return 0;
	}

}
