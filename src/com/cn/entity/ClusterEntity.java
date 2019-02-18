package com.cn.entity;

import java.util.ArrayList;
import java.util.List;

public class ClusterEntity {
  public String cluster_id;
  public List<RawEntity> pointList ;
  public ClusterEntity(){
	 this.pointList = new ArrayList<RawEntity>();
  }
  public String getCluster_id(){
	  return this.cluster_id;
  }
  public void setCluster_id(String id){
	  this.cluster_id = id;
  }
  public List<RawEntity> getPointList(){
	  return this.pointList;
  }
  public void setPointList(List<RawEntity> list){
	  this.pointList = list;
  }
}
