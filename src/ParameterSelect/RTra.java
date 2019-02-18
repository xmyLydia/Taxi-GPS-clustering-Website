package ParameterSelect;


import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

 
public class RTra {
	private int minlines = 1;
	private double radius = 0.0;
	private double sinv ;
	private double cosv ;
	private ArrayList<Point> points = new ArrayList<Point>();
	private ArrayList<Line> tracluster = new ArrayList<Line>();
	private ArrayList<Trajectory> rtrajectory = new ArrayList<Trajectory>();
	public void setParameter(int minlines , double radius){
		this.minlines = minlines;
		this.radius = radius;
	}
	public void sortPoints(){
		try{
			Collections.sort(points, new Comparator<Point>(){
				@Override
				public int compare(Point p1, Point p2) {
					if( p1.getX() >  p2.getX())
						return 1;
					if( p1.getX() < p2.getX())
						return -1;
					return 0;
				}
			});
		}catch(Exception e){
			e.printStackTrace();
		}
	}
	public void sortLine(){
		try{
			Collections.sort(tracluster, new Comparator<Line>(){
				@Override
				public int compare(Line l1, Line l2) {
					double v1 = getMinX(l1.getS(), l1.getE());
					double v2 = getMinX(l2.getS(), l2.getE());
					if(v1 > v2)
						return 1;
					if(v1 < v2)
						return -1;
					return 0;
				}
			});
		}catch(Exception e){
			e.printStackTrace();
		}
	}
	private double getMinX(Point p1 , Point p2){
		return Math.min(p1.getX(), p2.getX());
	}
	public ArrayList<Line> getCluster() {
		return tracluster;
	}
	public void setCluster(ArrayList<Line> cluster) {
		this.tracluster = new ArrayList<Line>(cluster);
	}
	public void clearData(){
		this.tracluster.clear();
		this.points.clear();
	}

	public void getRTra(){
		compute_avgv();
		rotate_axes();
		sweepline();
	}
	public ArrayList<Trajectory> getRTrajectory(){
		return this.rtrajectory;
	}
	/**
	 * @description ���������߶ε�ƽ���н�
	 */
	private void compute_avgv(){
		//�����߶ε�ƽ��x�᳤��
	    double px=0;
	    //�����߶ε�ƽ��y�᳤��
		double py=0;
		for (Line l : tracluster) {
			if (l.getS().getOrder() > l.getE().getOrder()) {
				px += (l.getS().x - l.getE().x);
				py += (l.getS().y - l.getE().y);
			} else {
				px += (l.getE().x - l.getS().x);
				py += (l.getE().y - l.getS().y);
			}
		}
		px /= tracluster.size();
		py /= tracluster.size();
		
		//ƽ���߶γ���
		double l=Math.sqrt(px*px+py*py);
		//cos�н�
		cosv=px/l;
		//sin�н�
		sinv=py/l;
	}
	/**
	 * @description ��ת����ϵ���㲻��������ϵ����
	 */
	private void rotate_axes(){
		for(int i = 0 ; i < tracluster.size() ; i++){
			Line l = tracluster.get(i);
			Point s = l.getS();
			Point stemp = new Point();
			stemp.setX(s.x * cosv + s.y * sinv);
			stemp.setY(-s.x * sinv + s.y * cosv );
			
			points.add(stemp);
			Point e = l.getE();
			Point etemp = new Point();
			etemp.setX(e.x * cosv + e.y * sinv);
			etemp.setY(-e.x * sinv + e.y * cosv );
			points.add(etemp);
			
			Line save = new Line(stemp, etemp);
			tracluster.remove(i);
			tracluster.add(i, save);
		}
	}

	private void sweepline(){
		sortPoints();
		sortLine();
		Trajectory tra = new Trajectory();
		double preX = 0.0;
		boolean mark = false;
		for(int i = 0 ; i < points.size() ; i++){
			Point p = points.get(i);
			CalcBean neighbor = getNeighbor(p);
			if(!mark && neighbor.getCount() > minlines){
				double avgy = neighbor.getSumy() / neighbor.getCount();
				//ȡ��תǰ������
				double x = p.x * cosv-avgy*sinv;
				double y=(p.x + avgy*sinv * cosv-p.x *cosv*cosv)/sinv;
				tra.insert(new Point(x , y));
				preX = p.x;
				mark = true;
			}
			else if(neighbor.getCount() > minlines && mark){
				if(p.x - preX >= radius){
					double avgy = neighbor.getSumy() / neighbor.getCount();
					//ȡ��תǰ������
					double x = p.x * cosv-avgy*sinv;
					double y=(p.x + avgy*sinv * cosv-p.x *cosv*cosv)/sinv;
					tra.insert(new Point(x , y));
					preX = p.x;
				}
			}
		}
		this.rtrajectory.add(tra);
	}
	
	private CalcBean getNeighbor(Point p){
		CalcBean bean = new CalcBean();
		for(int i = 0 ; i < tracluster.size() ; i++){
			Line l = tracluster.get(i);
			double max = l.getS().x;
			double min = l.getE().x;
			if(max < min){
				double temp = max ;
				max = min;
				min = temp;
			}
			if(min > p.getX())
				break;
			if(min < p.getX() && max > p.getX()){
				bean.incrementCount();
				bean.incrementSumY( getY(l , p));
			}
		}
		return bean;
	}
	private double getY(Line l , Point p){
		double disX = (l.getS().x - l.getE().x);
		if(disX == 0){
			return (l.getE().y - l.getS().y) / 2;
		}
		//б��
		double k = (l.getS().y - l.getE().y) / disX;
		return (l.getS().y + k * (p.getX() - l.getS().x));
	}
}