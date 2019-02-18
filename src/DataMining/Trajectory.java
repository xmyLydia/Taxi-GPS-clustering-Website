package DataMining;

import java.util.ArrayList;
import java.util.List;

public class Trajectory {
	int clusterId = -1;//trajectory用于代表轨迹的时候需要clusterid
	List<Point> points = new ArrayList<Point>();
	
	public Trajectory(){
		
	}
	public int getClusterId(){
		return this.clusterId;
	}
	public void setClusterId(int clusterId){
		this.clusterId = clusterId;
	}
	public Trajectory(List<Point> points) {
		this.points.addAll(points);
	}
	public void insert(Point point){
		points.add(point);
	}
	public Point get(int index){
		if(index < points.size() - 1)
			return points.get(index);
		return null;
	}
	public List<Point> getPoints() {
		return points;
	}
	public void setPoints(List<Point> points) {
		this.points = points;
	}
}
