package com.cn.entity;


import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import org.codehaus.jackson.annotate.JsonIgnore;

@Entity
@Table(name="cluster_point")
public class Cluster_Point {
	private int id;
	private HierarchicalCluster cp_hierarchicalCluster;
	private TurningPoint cp_turningPoint;
	private String parent;
	
	@Column(name="parent")
	public String getParent() {
		return parent;
	}
	public void setParent(String parent) {
		this.parent = parent;
	}
	@Id
	@GeneratedValue
	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	
	@ManyToOne
	@JoinColumn(name="cluster_id")
	@JsonIgnore
	public HierarchicalCluster getCp_hierarchicalCluster() {
		return cp_hierarchicalCluster;
	}
	public void setCp_hierarchicalCluster(HierarchicalCluster cp_hierarchicalCluster) {
		this.cp_hierarchicalCluster = cp_hierarchicalCluster;
	}
	
	@ManyToOne
	@JoinColumn(name="turningPoint_id")
	public TurningPoint getCp_turningPoint() {
		return cp_turningPoint;
	}
	public void setCp_turningPoint(TurningPoint cp_turningPoint) {
		this.cp_turningPoint = cp_turningPoint;
	}
	public Cluster_Point(HierarchicalCluster cp_hierarchicalCluster, TurningPoint cp_turningPoint) {
		super();
		this.cp_hierarchicalCluster = cp_hierarchicalCluster;
		this.cp_turningPoint = cp_turningPoint;
	}
	
	public Cluster_Point(){}
}
