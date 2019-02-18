package com.cn.entity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

 
public class RawForVoronoi {
    private String id;
	private String GPS_Id;
	private String Taxi_Id;
	private double Longtitude;
	private double Latitude;
	private String Speed;
	private String Angle;
	private String Time;
	private String State;
	private String cluster_id ;
	private String cluster_type;
	private String cp_type;
	
	
	 
	public String getId() {
		return id;
	}
	public void setId(String id2) {
		this.id = id2;
	}
	 
	public String getGPS_Id() {
		return GPS_Id;
	}
	public void setGPS_Id(String gPS_Id) {
		GPS_Id = gPS_Id;
	}
	 
	public String getCluster_id(){
		return cluster_id;
	}
	public void setCluster_id(String id){
		this.cluster_id = id;
	}
	
	 
	public String getCluster_type(){
		return cluster_type;
	}
	public void setCluster_type(String type){
		this.cluster_type = type;
	}
	 
	public String getCp_type(){
		return cp_type;
	}
	public void setCp_type(String type){
		this.cp_type = type;
	}
	
	 
	public String getTaxi_Id() {
		return Taxi_Id;
	}
	
	public void setTaxi_Id(String taxi_Id) {
		Taxi_Id = taxi_Id;
	}
	
	 
	public double getLongtitude() {
		return Longtitude;
	}
	
	public void setLongtitude(double longtitude) {
		Longtitude = longtitude;
	}
	 
	public double getLatitude() {
		return Latitude;
	}
	
	public void setLatitude(double latitude) {
		Latitude = latitude;
	}
	
	 
	public String getSpeed() {
		return Speed;
	}
	
	public void setSpeed(String speed) {
		Speed = speed;
	}
	
	 
	public String getAngle() {
		return Angle;
	}
	
	public void setAngle(String angle) {
		Angle = angle;
	}
	
	 
	public String getTime() {
		return Time;
	}
	
	public void setTime(String time) {
		Time = time;
	}
	
	 
	public String getState() {
		return State;
	}
	
	public void setState(String state) {
		State = state;
	}

	 
	public String toString() {
		return "Taxi [GPS_Id=" + GPS_Id + ", Taxi_Id=" + Taxi_Id + ", Longtitude=" + Longtitude + ", Latitude="
				+ Latitude + ", Speed=" + Speed + ", Angle=" + Angle + ", Time=" + Time + ", State=" + State + "]";
	} 

}
