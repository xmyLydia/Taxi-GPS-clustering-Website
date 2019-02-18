package com.cn.dao;

import java.util.List;
import java.util.Set;

import com.cn.entity.MopsiEntity;
import com.cn.entity.Taxi;

public interface MopsiDao {
	public List<MopsiEntity> Search(String taxiId, String time);
	public List<List<MopsiEntity>> SearchForList(List<String> taxiList);
	public Set<String> getAllRouteId();//same as get all taxi id
}
