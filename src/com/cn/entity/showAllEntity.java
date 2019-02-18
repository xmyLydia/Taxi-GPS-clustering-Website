package com.cn.entity;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import DataMining.Trajectory;

public class showAllEntity {

	private List<List<Taxi>> fragment;
	
	private List<List<MopsiEntity>> fragmentMopsi = new ArrayList<List<MopsiEntity>>();
	
	public List<Trajectory> reRoutes;
	private HashMap<Integer,ArrayList<Double>> similarity;

	public HashMap<Integer,ArrayList<Double>> getSimilarity() {
		return similarity;
	}

	public void setSimilarity(HashMap<Integer,ArrayList<Double>> similarity) {
		this.similarity = similarity;
	}
    public void setRep(List<Trajectory> routes) {
    	this.reRoutes = routes;//设置计算得到的代表轨迹组
    }
    public List<Trajectory> getRep(){
    	return reRoutes;
    }
	public List<List<Taxi>> getFragment() {
		return fragment;
	}

	public void setFragment(List<List<Taxi>> fragment) {
		this.fragment = fragment;
	}
	public void setFragmentMopsi(List<List<MopsiEntity>> fragment_mopsi) {
		this.fragmentMopsi = fragment_mopsi;
	}
	public List<List<MopsiEntity>> getFragmentMopsi() {
		return fragmentMopsi;
	}
}
