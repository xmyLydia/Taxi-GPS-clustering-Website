package com.cn.entity;

import java.util.ArrayList;
import java.util.List;
public class CompareEntity {

	private List<List<Taxi>> fragment;
	public List<List<RawEntity>> rawFragment;
	public ArrayList<DataMining.Line> lineList ;
	private double[][] similarity;
    public int clusterId;
    public int ClusterNumber;
    public void setNumber(int number){
    	this.ClusterNumber = number;
    }
    public int getNumber(){
    	return this.ClusterNumber;
    }
    public void setLineList(ArrayList<DataMining.Line> list){
    	this.lineList = list;
    }
    public ArrayList<DataMining.Line> getLineList(){
    	return this.lineList;
    }
	public double[][] getSimilarity() {
		return similarity;
	}
    public int getClusterId(){
    	return this.clusterId;
    }
    public void setClusterId(int id){
    	this.clusterId = id;
    }
	public void setSimilarity(double[][] similarity) {
		this.similarity = similarity;
	}

	public List<List<Taxi>> getFragment() {
		return fragment;
	}

	public void setFragment(List<List<Taxi>> fragment) {
		this.fragment = fragment;
	}

	public void setFragmentRaw(List<List<RawEntity>> trajectory) {
		// TODO Auto-generated method stub
		this.rawFragment = trajectory;
	}
	public List<List<RawEntity>>getRaw(){
		return this.rawFragment;
	}
	
}
