package com.cn.dao;

import java.util.List;

import com.cn.entity.CPointCluster;
import com.cn.entity.ClusterResultEntity;
import com.cn.entity.Taxi;
import com.cn.entity.TurningPoint;

public interface ClusterDao {

	void Store(List<CPointCluster> result, String string2,String cp);

	List<ClusterResultEntity> Search();

	void save(ClusterResultEntity clusterEntity);

	void Delete();

	void merge(ClusterResultEntity clusterEntity);

	List<ClusterResultEntity> SearchForMSE();

}
