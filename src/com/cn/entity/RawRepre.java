package com.cn.entity;

import java.util.ArrayList;

import DataMining.Line;
import DataMining.Trajectory;

public class RawRepre {
 public ArrayList<Line> cluster;
 public Trajectory representative;
 public void setCluster(ArrayList<Line> cluster){
	 this.cluster = cluster;
 }
 public ArrayList<Line> getCluster(){
	 return this.cluster;
 }
 public void setRep(Trajectory rep){
	 this.representative = rep;
 }
 public Trajectory getRep(){
	 return representative;
 }
}
