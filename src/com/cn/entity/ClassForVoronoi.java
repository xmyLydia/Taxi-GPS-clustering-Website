package com.cn.entity;

import java.util.List;

public class ClassForVoronoi {
   public List<CPointCluster> clusterResult;
   public List<RawForVoronoi> rawPoints;
   
   public void setCPointCluster(List<CPointCluster> result){
	   this.clusterResult = result;
   }
   public List<CPointCluster> getCPointCluster(){
	   return clusterResult;
   }
   public void setRaw(List<RawForVoronoi> rawPoints2 ){
	   this.rawPoints = rawPoints2;
   }
   public List<RawForVoronoi> getRaw(){
	   return rawPoints;
   }
}
