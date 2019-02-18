package com.cn.entity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name="taxi")
public class Taxi {

	private String GPS_Id;
	private String Taxi_Id;
	private String Longtitude;
	private String Latitude;
	private String Speed;
	private String Angle;
	private String Time;
	private String State;
	private boolean isVisited = false;
	private int cid = 0; 
	private int cluster_id ;
	@Id
	@Column(name="GPS_Id")
	public String getGPS_Id() {
		return GPS_Id;
	}
	public int getCluster_id(){
		return cluster_id;
	}
	public void setCluster_id(int id){
		this.cluster_id = id;
	}
	public void setGPS_Id(String gPS_Id) {
		GPS_Id = gPS_Id;
	}
	
	@Column(name="Taxi_Id")
	public String getTaxi_Id() {
		return Taxi_Id;
	}
	
	public void setTaxi_Id(String taxi_Id) {
		Taxi_Id = taxi_Id;
	}
	
	@Column(name="Longtitude")
	public String getLongtitude() {
		return Longtitude;
	}
	
	public void setLongtitude(String longtitude) {
		Longtitude = longtitude;
	}
	@Column(name="Latitude")
	public String getLatitude() {
		return Latitude;
	}
	
	public void setLatitude(String latitude) {
		Latitude = latitude;
	}
	
	@Column(name="Speed")
	public String getSpeed() {
		return Speed;
	}
	
	public void setSpeed(String speed) {
		Speed = speed;
	}
	
	@Column(name="Angle")
	public String getAngle() {
		return Angle;
	}
	
	public void setAngle(String angle) {
		Angle = angle;
	}
	
	@Column(name="Time")
	public String getTime() {
		return Time;
	}
	
	public void setTime(String time) {
		Time = time;
	}
	
	@Column(name="State")
	public String getState() {
		return State;
	}
	
	public void setState(String state) {
		State = state;
	}

	@Override
	public String toString() {
		return "Taxi [GPS_Id=" + GPS_Id + ", Taxi_Id=" + Taxi_Id + ", Longtitude=" + Longtitude + ", Latitude="
				+ Latitude + ", Speed=" + Speed + ", Angle=" + Angle + ", Time=" + Time + ", State=" + State + "]";
	}

	public boolean isVisited() {
		// TODO Auto-generated method stub
		return isVisited;
	}

	public void setVisited(boolean isVisited) {
		this.isVisited = isVisited;
	}

	public int getCid() {
		return cid;
	}

	public void setCid(int cid) {
		this.cid = cid;
	}
	 
}
