package DataMining;

import java.util.ArrayList;


public class Line {
	Point s = new Point();
	Point e = new Point();
	//序号
	public int s_grid = 0;
	public int t_grid = 0;
	int order = -1;
	int num = -1;
	int classifiy = 0;
	int clusterId = -1;
	String mopsi_id="";
	ArrayList<Integer> cluster = new ArrayList<Integer>();
	public Line() {
	}
	public Line(Point s , Point e) {
		this.s = s;
		this.e = e;
	}
    public String getMopsiId() {
    	return mopsi_id;
    }
    public void setMopsiId(String id) {
    	this.mopsi_id = id;
    }
	public int getT_grid(){
		return t_grid;
	}
	public void setT_grid(int t){
		this.t_grid = t;
	}
	public int getS_grid(){
		return s_grid;
	}
	public void setS_gird(int s){
		this.s_grid = s;
	}
	public int getClusterId() {
		return clusterId;
	}
	public void setClusterId(int clusterId) {
		this.clusterId = clusterId;
	}
	public int getOrder() {
		return order;
	}


	public void setOrder(int order) {
		this.order = order;
	}


	public int getClassifiy() {
		return classifiy;
	}


	public void setClassifiy(int classifiy) {
		this.classifiy = classifiy;
	}


	public ArrayList<Integer> getCluster() {
		return cluster;
	}


	public void setCluster(ArrayList<Integer> cluster) {
		this.cluster = cluster;
	}


	public Point getS() {
		return s;
	}

	public void setS(Point s) {
		this.s = s;
	}

	public Point getE() {
		return e;
	}

	public void setE(Point e) {
		this.e = e;
	}

	public void addCluster(ArrayList<Integer> cl){
		this.cluster.addAll(cl);
	}
	public void addCluster(int index){
		this.cluster.add(index);
	}
	
	
	public int getNum() {
		return num;
	}

	/**
	 * @description 设置读入时的原始行号
	 * @param num
	 */
	public void setNum(int num) {
		this.num = num;
	}


	@Override
	public String toString() {
		return order + "\t" + num + "\t" + s +"\t"+ e;
	}


	
	
}
