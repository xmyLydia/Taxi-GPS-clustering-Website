package com.cn.entity;

import java.util.ArrayList;
import java.util.List;

public class CPointCluster {
	//单个聚类的实体类
	private int id;
	
	private List<Taxi> turningPoints = new ArrayList<>();
	
	private List<MopsiEntity> turningPointsMopsi = new ArrayList<>();
	
	private double centerLatitude;
	
	private double centerLongtitude;
	
	private List<Cluster_Point> cluster_Points;
	
	//num of turning point
	private int size;
	
	//initialize number
	private int numberOfCluster;
	
	private String name;
	
	//start or stop or all
	private String divide;
	
	private String method;

	private String cluster_type;
	
	public String cp_type;
	 
	public List<Cluster_Point> getCluster_Points() {
		return cluster_Points;
	}

	public void setCluster_Points(List<Cluster_Point> cluster_Points) {
		this.cluster_Points = cluster_Points;
	}

	public void setTurningPointMopsi(List<MopsiEntity>list) {
		this.turningPointsMopsi = list;
	}
	public List<MopsiEntity> getTurningPointsMopsi(){
		return turningPointsMopsi;
	}
	public String getMethod() {
		return method;
	}

	public void setMethod(String method) {
		this.method = method;
	}

	 
	public String getDivide() {
		return divide;
	}

	public void setDivide(String divide) {
		this.divide = divide;
	}

	 
	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	 
	public int getNumberOfCluster() {
		return numberOfCluster;
	}
	
	public void setNumberOfCluster(int numberOfCluster) {
		this.numberOfCluster = numberOfCluster;
	}
	
	 
	public int getSize() {
		return size;
	}
	
	public void setSize(int size) {
		this.size = size;
	}
	
	 
	public int getId() {
		return id;
	}
	
	public void setId(int id) {
		this.id = id;
	}
	
	 
	public List<Taxi> getTurningPoints() {
		return turningPoints;
	}
	
	public void setTurningPoints(List<Taxi> dataSet1) {
		this.turningPoints = dataSet1;
	}
	
	 
	public double getCenterLatitude() {
		return centerLatitude;
	}
	
	public void setCenterLatitude(double centerLatitude) {
		this.centerLatitude = centerLatitude;
	}
	
	 
	public double getCenterLongtitude() {
		return centerLongtitude;
	}
	
	public void setCenterLongtitude(double centerLongtitude) {
		this.centerLongtitude = centerLongtitude;
	}

	public void setCluster_type(String cluster_type) {
		// TODO Auto-generated method stub
		this.cluster_type=cluster_type;
	}
    public String getCluster_type(){
    	return this.cluster_type;
    }
	 public void setCP_type(String cp_type){
		 this.cp_type = cp_type;
	 }
	 public String getCP_type(){
		 return cp_type;
	 }
	
}
