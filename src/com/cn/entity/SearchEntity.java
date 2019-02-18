package com.cn.entity;

import java.util.List;

public class SearchEntity {

	private String taxiId;
	private int clusterId;
	private List<String> comparedTaxiIds; 
	private int index;
	public String partition_type;
    public String cluster_type;
	public int k;
	public int iterator;
	public double eps;
	public int minpts;
	public double traclus_eps;
	public int traclus_minlns;//����������������traclus�ľ����㷨
	public int n;//��ξ������
	public int m;//Grid�������
	public int nx;//Grid�������
	public int ny;//Grid�������
	public double minAngle ;
	public double minStopDuration;
	public double minDistance;
	public double maxDistance;//�����ĸ���SGA�㷨�Ĳ���
	public int taxi_order;//����D3��Ϣ��ʾʱѡ����һ���켣���켣���к�
	public int rep_order;//���ڵ������켣��չʾ�����߶μ���
	public String user;
	public int routes;
	public int method;//1:traclus  2:DBA
	
	public int getMethod() {
		return method;
	}
	
	public void setMethod(int method) {
		this.method = method;
	}
	public String getUser() {
		return user;
	}
	public void setUser(String user) {
		this.user = user;
	}
	public int getRoutes() {
		return routes;
	}
	public void setRoutes(int routes) {
		this.routes = routes;
	}
	public int getRep_order(){
		return rep_order;
	}
	public void setRep_order(int order){
		this.rep_order = order;
	}
	public int getTaxi_order(){
		return taxi_order;
	}
	public void setTaxi_order(int order){
		this.taxi_order = order;
	}
	public int getM() {
		return m;
	}
	public void setM(int m){
		this.m = m;
	}
	public int getNX() {
		return nx;
	}
	public void setNX(int nx){
		this.nx = nx;
	}
	public int getNY() {
		return ny;
	}
	public void setNY(int ny){
		this.ny = ny;
	}
	public int getN() {
		return n;
	}
	public void setN(int n){
		this.n = n;
	}
	public String getPartition_type() {
		return partition_type;
	}
	public void setPartitino_type(String type){
		this.partition_type = type;
	}
	public int getClusterId() {
		return clusterId;
	}
	public void setClusterId(int clusterId) {
		this.clusterId = clusterId;
	}
	public int getIndex() {
		return index;
	}
	public void setIndex(int index) {
		this.index = index;
	}
	public String getTaxiId() {
		return taxiId;
	}
	public void setTaxiId(String taxiId) {
		this.taxiId = taxiId;
	}
	public List<String> getComparedTaxiIds() {
		return comparedTaxiIds;
	}
	public void setComparedTaxiIds(List<String> comparedTaxiIds) {
		this.comparedTaxiIds = comparedTaxiIds;
	}
	public String getPaitition_type() {
		// TODO Auto-generated method stub
		return partition_type;
	}
	 
	public int getK() {
		// TODO Auto-generated method stub
		return k;
	}
	public int getIterator() {
		// TODO Auto-generated method stub
		return iterator;
	}
	public void setPaitition_type(String partition_type) {
		// TODO Auto-generated method stub
		this.partition_type=partition_type;
	}
	 
	public void setK(int k) {
		// TODO Auto-generated method stub
		   this.k=k;
	}
	public void setIterator(int iterator) {
		// TODO Auto-generated method stub
		   this.iterator = iterator;
	}
	public void setEps(double eps){
		this.eps = eps;
	}
	public double getEps(){
		return this.eps;
	}
	public void setMinlns(int minlns){
		this.minpts =minlns;
	}
	public int getMinlns(){
		return this.minpts;
	}
}
