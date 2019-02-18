package com.cn.service;

import java.util.List;

import com.cn.entity.HierarchicalCluster;
import com.cn.entity.Taxi;
import com.cn.entity.TurningPoint;

public interface HierarchicalClusterService {
	public List<HierarchicalCluster> DoHierarchicalCluster(List<TurningPoint> list,int num);
	public List<HierarchicalCluster> InitialCluster(List<TurningPoint> list);
	public double GetDistance(double aLat, double aLong, double bLat, double bLong);
	public List<HierarchicalCluster> MergeCluster(List<HierarchicalCluster> list, int index1, int index2);
	public void CalculateCenter(List<HierarchicalCluster> list);
	public int Save(HierarchicalCluster cluster);
	public List<HierarchicalCluster> DoKMeans(List<TurningPoint> list,int num,  int iteratorTime);
	public List<HierarchicalCluster> GetStartPointAtTen();
	public void Update(List<HierarchicalCluster> list);
	public void Transform(List<HierarchicalCluster> list);
	public List<HierarchicalCluster> DBScan(List<TurningPoint> list, double e, int minP);
	public HierarchicalCluster GetClusterById(int id);
	public List<HierarchicalCluster> GetClusterByParentId(int parentId);
	
	//show cluster method
	public List<HierarchicalCluster> GetStartPointAtTenByDBSCAN();
	public List<HierarchicalCluster> GetStartPointAtTenByAGNES();
	public List<HierarchicalCluster> GetStartPointAtTenByKMeans();

}
