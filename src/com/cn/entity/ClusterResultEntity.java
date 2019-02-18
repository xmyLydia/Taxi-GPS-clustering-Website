package com.cn.entity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name="cluster_result")
public class ClusterResultEntity {
	private int id;
	private String Cluster_id;
	private String center_longtitude;
	private String center_latitude;
	private String cluster_type;
	private String cp_type ;
	
 
	//private List<Cluster_Point> cluster_Points;

	 
	//@LazyCollection(LazyCollectionOption.EXTRA) //LazyCollection属性设置成EXTRA指定了当如果查询数据的个数时候，只会发出一条 count(*)的语句，提高性能
	 

	public ClusterResultEntity(){}
	
	public ClusterResultEntity(String clusterid,String centerLatitude,String centerLongtitude,String cluster_type,String cp) {
		super();
		this.Cluster_id = clusterid;
		this.center_latitude = centerLatitude;
		this.center_longtitude = centerLongtitude;
		this.cluster_type = cluster_type;
		this.cp_type = cp;
	}
	 
	@Id
	@Column(name="id")
	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	@Column(name="Cluster_id")//设置为主键:Cluster_id
	public String getClusterId() {
		return Cluster_id;
	}

	public void setClusterId(String Clusterid) {
		this.Cluster_id = Clusterid;
	}
	
	@Column(name="center_latitude")
	public String getCenter_Latitude() {
		return center_latitude;
	}
	public void setCenter_Latitude(String centerLatitude) {
		this.center_latitude = centerLatitude;
	}
	@Column(name="center_longtitude")
	public String getCenter_Longtitude() {
		return center_longtitude;
	}
	public void setCenter_Longtitude(String centerLongtitude) {
		this.center_longtitude = centerLongtitude;
	}
	@Column(name="cluster_type")
	public String getCluster_type() {
		return cluster_type;
	}
	public void setCluster_type(String clusterType) {
		this.cluster_type = clusterType;
	}
	@Column(name="cp_type")
	public String getCp_type() {
		return cp_type;
	}
	public void setCp_type(String cp_type) {
		this.cp_type = cp_type;
	}
 
	
	/*public boolean equals(ClusterResultEntity obj) {
		if(this.latitude.equals(obj.getLatitude())
		&&this.longtitude.equals(obj.getLongtitude())
		&&this.state.equals(obj.getState())
		&&this.speed.equals(obj.getSpeed())
		&&this.time.equals(obj.getTime())
		&&this.taxiId.equals(obj.getTaxiId())) return true;
		else return false;
	}
	*/
	
}
