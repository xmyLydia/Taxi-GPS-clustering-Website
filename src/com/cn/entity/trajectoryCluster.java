package com.cn.entity;

import java.util.ArrayList;
import java.util.List;

public class trajectoryCluster {
	private String GPS_Id;
	private String Taxi_Id;
	private String Longtitude;
	private String Latitude;
	private String Speed;
	private String Angle;
	private String Time;
	private String State;
	private int numberOfCluster;
	private int maxlength;
	
	public int getMaxlength(){
		return maxlength;
	}
	
	public void setMaxLength(int length){
		maxlength = length;
	}
    private List<Taxi> centerTrajectory;
	
	private List<List<Taxi>> trajectoryList = new ArrayList<>();
	
	public List<Taxi> getCenterTracks(){
	      return centerTrajectory;
		}
	public void setCenterTracks(List<Taxi> trajectory){
      centerTrajectory= trajectory;
	}
	public List<List<Taxi>> getTrajectoryList(){
		return trajectoryList ;
	}
	public void setTrajectoryList(List<List<Taxi>> trajectory_List){
		trajectoryList =  trajectory_List;
	}
	public String getGPS_Id() {
		return GPS_Id;
	}
	
	public void setGPS_Id(String gPS_Id) {
		GPS_Id = gPS_Id;
	}
	
	public String getTaxi_Id() {
		return Taxi_Id;
	}
	
	public void setTaxi_Id(String taxi_Id) {
		Taxi_Id = taxi_Id;
	}
	
	public String getLongtitude() {
		return Longtitude;
	}
	
	public void setLongtitude(String longtitude) {
		Longtitude = longtitude;
	}
	 
	public String getLatitude() {
		return Latitude;
	}
	
	public void setLatitude(String latitude) {
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
	public void setNumberOfCluster(int number) {
		numberOfCluster=number;
	}
	@Override
	public String toString() {
		return "Taxi [GPS_Id=" + GPS_Id + ", Taxi_Id=" + Taxi_Id + ", Longtitude=" + Longtitude + ", Latitude="
				+ Latitude + ", Speed=" + Speed + ", Angle=" + Angle + ", Time=" + Time + ", State=" + State + "]";
	}
}
