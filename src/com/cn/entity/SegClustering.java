package com.cn.entity;

import java.util.ArrayList;
import java.util.List;

import DataMining.Line;
public class SegClustering {


		private int id;
		
		private List<Taxi> turningPoints = new ArrayList<>();
		
		private Line centerSeg;
		
		private List<Line> segments; 
		 
		
		//num of turning point
		private int size;
		
		//initialize number
		private int numberOfCluster;
		
		private String name;
		
		//start or stop or all
		private String divide;
		
		private String method;

		private String cluster_type;
		
		public String cp_type;
		 
		public void setSegments(List<Line> seg){
			this.segments = seg;
		}
		public List<Line> getSegments(){
			return segments;
		}
		public Line getCenterSeg(){
			return centerSeg;
		}
		public void setCenterSeg(Line line){
			this.centerSeg = line;
		}
		public String getMethod() {
			return method;
		}

		public void setMethod(String method) {
			this.method = method;
		}

		 
		public String getDivide() {
			return divide;
		}

		public void setDivide(String divide) {
			this.divide = divide;
		}

		 
		public String getName() {
			return name;
		}

		public void setName(String name) {
			this.name = name;
		}

		 
		public int getNumberOfCluster() {
			return numberOfCluster;
		}
		
		public void setNumberOfCluster(int numberOfCluster) {
			this.numberOfCluster = numberOfCluster;
		}
		
		 
		public int getSize() {
			return size;
		}
		
		public void setSize(int size) {
			this.size = size;
		}
		
		 
		public int getId() {
			return id;
		}
		
		public void setId(int id) {
			this.id = id;
		}
		
		 
		public List<Taxi> getTurningPoints() {
			return turningPoints;
		}
		
		public void setTurningPoints(List<Taxi> dataSet1) {
			this.turningPoints = dataSet1;
		}
		
		public void setCluster_type(String cluster_type) {
			// TODO Auto-generated method stub
			this.cluster_type=cluster_type;
		}
	    public String getCluster_type(){
	    	return this.cluster_type;
	    }
		 public void setCP_type(String cp_type){
			 this.cp_type = cp_type;
		 }
		 public String getCP_type(){
			 return cp_type;
		 }
		
	 

}
