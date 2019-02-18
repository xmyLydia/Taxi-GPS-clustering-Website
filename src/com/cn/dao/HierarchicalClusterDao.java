package com.cn.dao;

import java.util.List;

import com.cn.entity.HierarchicalCluster;

public interface HierarchicalClusterDao {
	public int save(HierarchicalCluster cluster);
	public void update(HierarchicalCluster cluster);
	public List<HierarchicalCluster> GetStartPointAtTen();
	public List<HierarchicalCluster> GetStartPointAtTenByKMeans();
	public List<HierarchicalCluster> GetStartPointAtTenByAGNES();
	public List<HierarchicalCluster> GetStartPointAtTenByDBSCAN();
	public HierarchicalCluster GetClusterById(int id);
	public List<HierarchicalCluster> GetClusterByParentId(int parentId);
}
