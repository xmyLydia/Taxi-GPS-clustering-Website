package com.cn.entity;

import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.persistence.Transient;

import org.codehaus.jackson.annotate.JsonIgnore;
import org.hibernate.annotations.LazyCollection;
import org.hibernate.annotations.LazyCollectionOption;

@Entity
@Table(name="turningPoint")
public class TurningPoint {

	private int id;
	private String longtitude;
	private String latitude;
	private String state;
	private String time;
	private String taxiId;
	private String speed;
	private String type;
	private boolean isVisited = false;
	private int cid = 0; 
	
	@Transient
	public boolean isVisited() {
		return isVisited;
	}

	public void setVisited(boolean isVisited) {
		this.isVisited = isVisited;
	}

	@Transient
	public int getCid() {
		return cid;
	}

	public void setCid(int cid) {
		this.cid = cid;
	}

	private List<Cluster_Point> cluster_Points;

	@OneToMany(mappedBy="cp_turningPoint",cascade=CascadeType.REMOVE)
	@LazyCollection(LazyCollectionOption.EXTRA) //LazyCollection属性设置成EXTRA指定了当如果查询数据的个数时候，只会发出一条 count(*)的语句，提高性能
	@JsonIgnore
	public List<Cluster_Point> getCluster_Points() {
		return cluster_Points;
	}

	@JsonIgnore
	public void setCluster_Points(List<Cluster_Point> cluster_Points) {
		this.cluster_Points = cluster_Points;
	}

	public TurningPoint(){}
	
	public TurningPoint(String longtitude, String latitude, String state, String time, String taxiId, String speed, String type) {
		super();
		this.longtitude = longtitude;
		this.latitude = latitude;
		this.state = state;
		this.time = time;
		this.taxiId = taxiId;
		this.speed = speed;
		this.type = type;
	}
	
	@Column(name="type")
	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}
	
	@Column(name="time")
	public String getTime() {
		return time;
	}
	public void setTime(String time) {
		this.time = time;
	}
	@Id
	@GeneratedValue(strategy=GenerationType.IDENTITY)
	@Column(name="id")
	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	@Column(name="latitude")
	public String getLatitude() {
		return latitude;
	}
	public void setLatitude(String latitude) {
		this.latitude = latitude;
	}
	@Column(name="longtitude")
	public String getLongtitude() {
		return longtitude;
	}
	public void setLongtitude(String longtitude) {
		this.longtitude = longtitude;
	}
	@Column(name="state")
	public String getState() {
		return state;
	}
	public void setState(String state) {
		this.state = state;
	}
	@Column(name="taxi_id")
	public String getTaxiId() {
		return taxiId;
	}
	public void setTaxiId(String taxiId) {
		this.taxiId = taxiId;
	}
	@Column(name="speed")
	public String getSpeed() {
		return speed;
	}
	public void setSpeed(String speed) {
		this.speed = speed;
	}
	
	public boolean equals(TurningPoint obj) {
		if(this.latitude.equals(obj.getLatitude())
		&&this.longtitude.equals(obj.getLongtitude())
		&&this.state.equals(obj.getState())
		&&this.speed.equals(obj.getSpeed())
		&&this.time.equals(obj.getTime())
		&&this.taxiId.equals(obj.getTaxiId())) return true;
		else return false;
	}
	
	
}
