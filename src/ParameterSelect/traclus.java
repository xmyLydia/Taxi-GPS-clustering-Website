package ParameterSelect;

import java.awt.Color;
import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map.Entry;
import java.util.Scanner;

 

 
 
/**
 * @description 
 * @author xinjian lv(xinjian.lv@gmail.com)
 *
 */
public class traclus {
	
	private int MDL_COST_ADVANTAGE = 0; 
	/*
	 * partition ֮����߶�
	 */
	private ArrayList<Line> lines = new ArrayList<Line>();
	/*
	 * ��ʼ�켣
	 */
	private HashMap<Integer , Trajectory> trajectorys = new HashMap<Integer, Trajectory>();
	private HashMap<Integer , ArrayList<Line>> cluster = new HashMap<Integer, ArrayList<Line>>();
	RTra rtra = new RTra();
	int minLines = 8;
	double eps = 29;
	public ArrayList<Line> getLines() {
		return lines;
	}

	public void setMDL_COST_ADVANTAGE(int mDL_COST_ADVANTAGE) {
		MDL_COST_ADVANTAGE = mDL_COST_ADVANTAGE;
	}

	public HashMap<Integer, Trajectory> getTrajectorys() {
		return trajectorys;
	}
	
	public void setParameter(int minLines , double eps){
		this.minLines = minLines;
		this.eps = eps;
	}
	
	public void loadPoints(String filename){
		try{
			Scanner scan = new Scanner(new File(filename));
			while (scan.hasNext()) {
				int order = scan.nextInt();
				int lineNum = scan.nextInt();
				double x = scan.nextDouble();
				double y = scan.nextDouble();
				Point point = new Point(x , y);
				point.setNum(lineNum);
				point.setOrder(order);
				Trajectory tra= new Trajectory();
				if(trajectorys.containsKey(lineNum))
					tra = trajectorys.get(lineNum);
				tra.insert(point);
				trajectorys.put(lineNum, tra);
			}
			scan.close();
		}catch(Exception e){
			e.printStackTrace();
		}
	}
	public void loadTrajectory(String filename){
		try{
			Scanner scan = new Scanner(new File(filename));
			int order = 0;
			int c = 0;
			while (scan.hasNext()) {
			
				int lineNum = scan.nextInt();
				int pointSum = scan.nextInt();
	
				while (pointSum > 0) {
					double x = scan.nextDouble() + 500;
					double y = scan.nextDouble() + 500;
					Point point = new Point(x, y);
					point.setNum(lineNum);
					point.setOrder(order++);
					Trajectory tra = new Trajectory();
					if (trajectorys.containsKey(lineNum))
						tra = trajectorys.get(lineNum);
					tra.insert(point);
					trajectorys.put(lineNum, tra);
					pointSum--;
				}
				if(c > 1000)
					break;
				if (c % 1000 == 0)
					System.out.printf("load %d trajectory.\n",c);
				c++;
			}
			scan.close();
		}catch(Exception e){
			e.printStackTrace();
		}
	}
	
	public void partition(){
		System.out.println("partition...");
		System.out.println("size "+trajectorys.size());
		int c = 0;
		for(Entry<Integer,Trajectory> en : trajectorys.entrySet()){
			List<Line> ls = partition(en.getValue().getPoints());
			lines.addAll(ls);
			if(c %1000 == 0)
				System.out.println("processed(partition) " + c + "...");
			c++;
		}
		System.out.println("lines " + lines.size());
	}

	private List<Line> partition(List<Point> points){
		try{
			int start = 0;
			int end = 1;
			List<Line> list = new ArrayList<Line>();
			double cost_par , cost_nopar;
			cost_par = cost_nopar = 0.0;
			while(end < points.size()){
				cost_par = Distance.distance_mdl_par(points, start, end);
				cost_nopar = Distance.distance_mdl_nopar(points, start, end);
				if(cost_par > cost_nopar + MDL_COST_ADVANTAGE){
					Line line = new Line(points.get(start), points.get(end - 1));
					line.setNum(points.get(0).getNum());
					list.add(line);
					start = end - 1 ;
				}else{
					end++;
				}
			}
			//���ֻʣһ���㣬����뵽֮ǰ�ĵ��߶���
			if ((points.size() - 1) - start == 1) {
				list.get(list.size() - 1).setE(points.get(end - 1));
			}
			//ʣ����㣬����뵽���
			else{
				Line temp = new Line(points.get(start), points.get(end - 1));
				temp.setNum(points.get(0).getNum());
				list.add(temp);
			}

			return list;
		}catch(Exception e){
			e.printStackTrace();
		}
		return null;
	}
	
	
	public void cluster(){
		System.out.println("cluster...");
		System.out.println("line size : " + lines.size());
		int end = lines.size();
		int clusterId = 1;
		for(int i = 0 ; i < end ; i++){
			if(i % 10 == 0)
				System.out.println("processed(cluster) " + i + "...");
			Line l = lines.get(i);
			if(l.getClassifiy() == 0){
				ArrayList<Integer> neighbor = getNeighbor(i);
				if(neighbor.size() + 1 >= minLines){
					lines.get(i).setClassifiy(2);
					lines.get(i).addCluster(neighbor);
					lines.get(i).addCluster(i);
					lines.get(i).setClusterId(clusterId);
					for(int ndx : neighbor){
						lines.get(ndx).setClassifiy(2);
					}
					ExpandCluster(i , neighbor);
					clusterId++;
				}else{
					l.setClassifiy(1);
				}
			}
		}
		

		for(int i = 0 ; i < lines.size() ; i++){
			if(lines.get(i).getClusterId() > 0){
				ArrayList<Line> ls = new ArrayList<Line>();
				clusterId = lines.get(i).getClusterId();
				for(int j : lines.get(i).getCluster()){
					ls.add(lines.get(j));
				}
				cluster.put(clusterId, ls);
			}
		}
		System.out.println("cluster end...");
	} 
	public void sortLine(){
		try{
			Collections.sort(lines, new Comparator<Line>(){
				@Override
				public int compare(Line l1, Line l2) {
					return l1.getNum() - l2.getNum();
				}
			});
		}catch(Exception e){
			e.printStackTrace();
		}
	}
	
	public HashMap<Integer , ArrayList<Line>> getCluster(){
		return cluster;
	}
	 
	private void ExpandCluster(int center , ArrayList<Integer> neighbor){
		System.out.println("Expanding...");
		while(neighbor.size() > 0){
			int index = neighbor.get(0);
			ArrayList<Integer> sub_neighbor = getNeighbor(index);
			if (sub_neighbor.size() + 1>= minLines) {
				for (int ndx : sub_neighbor) {
					if (lines.get(ndx).getClassifiy() == 0 || lines.get(ndx).getClassifiy() == 1) {
						lines.get(center).addCluster(ndx);
						lines.get(ndx).setClassifiy(2);
					}
					if(lines.get(ndx).getClassifiy() == 0){
						neighbor.add(ndx);
					}
				}
			}
			neighbor.remove(0);
		}
	}
	
	private ArrayList<Integer> getNeighbor(int index){
		ArrayList<Integer> ndxs = new ArrayList<Integer>();
		Line l = lines.get(index);
		Line llong = new Line();
		Line lshort = new Line();
		for(int i = 0 ; i < lines.size() ;i++){
			if(i == index)
				continue;
			Line ltemp = lines.get(i);
			if (Distance.distance(l) >= Distance.distance(ltemp)) {
				llong = l ; lshort = ltemp;
			}else{
				llong = ltemp ; lshort = l;
			}
			double dis = Distance.dist(llong,lshort);
			if (dis <= eps) {
				ndxs.add(i);
			}
		}
		return ndxs;
	}
	 
	
	public ArrayList<Trajectory> getRTrajectory( int min  , int radius){
		rtra.setParameter(min, radius);
		for(Entry<Integer, ArrayList<Line>> en : cluster.entrySet()){
			ArrayList<Line> ctra = new ArrayList<Line>();
			ctra.addAll(en.getValue());
			rtra.setCluster(ctra);
			rtra.getRTra();
			rtra.clearData();
		}
		return rtra.getRTrajectory();
	}
	public boolean isBZEOR(ArrayList<Line> list){
		try{
			for(Line l : list){
				Point p1 = l.getS();
				Point p2 = l.getE();
				if(p1.x < 0 || p1.y < 0){
					return false;
				}
				if(p2.x < 0 || p2.y < 0){
					return false;
				}
					
			}
		}catch(Exception e){
			e.printStackTrace();
		}
		return true;
	}
	 
	
	public void check_tra_num(int n){
		HashMap<Integer , ArrayList<Line>> tempCluster = new HashMap<Integer, ArrayList<Line>>(cluster);
		for(Entry<Integer , ArrayList<Line>> en : tempCluster.entrySet()){
			HashSet<Integer> set = new HashSet<Integer>();
			for(Line l : en.getValue()){
				set.add(l.getNum());
			}
			if(set.size() < n){
				cluster.remove(en.getKey());
			}
		}
	}
 
	/*public static void main(String[] args) {
		try{
			 
		}catch(Exception e){
			e.printStackTrace();
		}
	}*/
}