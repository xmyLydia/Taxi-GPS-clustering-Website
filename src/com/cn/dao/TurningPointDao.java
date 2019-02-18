package com.cn.dao;

import java.util.List;

import com.cn.entity.TurningPoint;

public interface TurningPointDao {
	public void save(TurningPoint turningPoint);
	public List<TurningPoint> GetAllByTime();
	public List<TurningPoint> GetAllByTaxiId();
	public void delete(TurningPoint turningPoint);
	public List<TurningPoint> GetById(String id);
	public List<String> GetAllIds();
	public List<TurningPoint> GetStartPoint();
	public List<TurningPoint> GetDropPoint();
	public List<TurningPoint> GetStartPointAtTen();
	public List<TurningPoint> GetByCluster(int clusterId);
	public void merge(TurningPoint temp, TurningPoint pre, TurningPoint aft);
}
