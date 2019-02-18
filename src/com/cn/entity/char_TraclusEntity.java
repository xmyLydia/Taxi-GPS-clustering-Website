package com.cn.entity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name="char_traclus")
public class char_TraclusEntity {

	private String GPS_Id;
	private String Taxi_Id;
	private String Longtitude;
	private String Latitude;
	private String Speed;
	private String Angle;
	private String Time;
	private String State;
	private String Algor_type;
	
	@Column(name="Algor_type")
	public String getAlgor_type() {
		return this.Algor_type;
	}
    public void setAlgor_type(String algor_type){
    	this.Algor_type = algor_type;
    }
    @Id
	@Column(name="GPS_Id")
	public String getGPS_Id() {
		return GPS_Id;
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
	 
}
