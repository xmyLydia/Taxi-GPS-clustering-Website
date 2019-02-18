package ParameterSelect;

import java.util.ArrayList;

public class Tracks {
	public static void main(String[] args){
		//测试将每条轨迹赋给一个聚类
		ArrayList<ArrayList<Integer>> ClusterAll = new ArrayList<ArrayList<Integer>>();//所有的聚类
	    for(int i = 0;i<10;i++){
	    	ArrayList<Integer> Cluster = new ArrayList<Integer>();//每条轨迹对应一个聚类
	    	if(Cluster.size()<1){
	    	Cluster.add(i);
	    	}
	    	ClusterAll.add(Cluster);	
	    }
	}
}
