package com.cn.dao;

import java.util.List;
import java.util.Set;

import com.cn.entity.CPointCluster;
import com.cn.entity.ClusterResultEntity;
import com.cn.entity.RawEntity;
import com.cn.entity.Taxi;

public interface RawDao {

	public void Store(List<CPointCluster> result,String cluster_type,String cp_type);

	public List<RawEntity> Search(String cluster_type, String cp_type);

	public void DeleteRaw();

	public void saveRaw(RawEntity rawEntity);

	public void merge(RawEntity rawEntity);

	public List<RawEntity> SearchAll();

	void Traclus_DbscanStore(List<List<Taxi>> data, String cluster_type, String cp_type);

	Set getClusterId();
	 
}
