package com.cn.entity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name="cp_raw")
public class RawEntity {
    private String id;
	private String GPS_Id;
	private String Taxi_Id;
	private String Longtitude;
	private String Latitude;
	private String Speed;
	private String Angle;
	private String Time;
	private String State;
	private String cluster_id ;
	private String cluster_type;
	private String cp_type;
	
	
	@Column(name="id")
	public String getId() {
		return id;
	}
	public void setId(String id2) {
		this.id = id2;
	}
	@Id
	@Column(name="GPS_Id")
	public String getGPS_Id() {
		return GPS_Id;
	}
	public void setGPS_Id(String gPS_Id) {
		GPS_Id = gPS_Id;
	}
	@Column(name="cluster_id")
	public String getCluster_id(){
		return cluster_id;
	}
	public void setCluster_id(String id){
		this.cluster_id = id;
	}
	
	@Column(name="cluster_type")
	public String getCluster_type(){
		return cluster_type;
	}
	public void setCluster_type(String type){
		this.cluster_type = type;
	}
	@Column(name="cp_type")
	public String getCp_type(){
		return cp_type;
	}
	public void setCp_type(String type){
		this.cp_type = type;
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

}
