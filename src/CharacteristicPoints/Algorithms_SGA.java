package CharacteristicPoints;

import java.io.FileWriter;
import java.io.IOException;
import java.sql.SQLException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import DataMining.GetData;

public class Algorithms_SGA {
	public double maxDis = 0;
	public double minDis = 0;
	public int sumPoint = 0;
	//给出轨迹T，获取特征点
// spatial generalization and aggregation of massive movement data
	public static void main(String[] args) throws ParseException, ClassNotFoundException, SQLException, IOException {
		Algorithms_SGA  am = new Algorithms_SGA();
		// TODO Auto-generated method stub
		GetTrajectory get_Track = new GetTrajectory();
		GetData gt = new GetData();
	    ArrayList<String> list_all = gt.AllTaxiId();
	    for(int i=0;i<100;i++){
	    String temp_id = list_all.get(i);
	    ArrayList<Point> Points =get_Track.getTracks(temp_id);
        Trajectory track = new Trajectory(Points);
        ArrayList<Point> Cluster = am.algorithm(track,3,3,0.0001,0.1);
        am.AddIntoDBSGA(Cluster);
	    }
	    System.out.println("n");
	}
	public ArrayList<Point> copyOfAlgorithm(Trajectory track,double MinAngle,double MinStopDuration,double MinDistance,double MaxDistance) throws ParseException, IOException{
		//写入文件
    	// FileWriter fw = null;
		// fw = new FileWriter("G://data/Experiments/Algor.txt",true);
    	//以上为写入文件
    	int i=1;
    	int j=i+1;
    	int n=track.getLinePoints().size();//n是轨迹总的点数
    	//System.out.println(n);
    	ArrayList<Point> Cluster = new ArrayList<Point>();//聚类
    	Point p_start = track.getStart();
    	p_start.order = 0;
    	Cluster.add(p_start);//将第一个点加到聚类当中
    
    	while(j<n){
    		if(j==n-1){
    			break;
    		}
    	//	System.out.println(j);
    	//compute dSpaceij = spatial_distance((xi,yi),(xj,yj))计算i点和j点的距离
    	Point p_i = track.getLinePoints().get(i);
    	p_i.order=i;
    	Point p_j = track.getLinePoints().get(j);
    	p_j.order=j;
        double dSpaceij = Math.abs(spatial_distance(p_i,p_j));
        if(maxDis<dSpaceij){
        	maxDis = dSpaceij;
        }
        if(minDis>dSpaceij){
        	minDis = dSpaceij;
        }
    	if(dSpaceij>=MaxDistance){
    		//那么第j个点就是代表点
    		Cluster.add(p_j);//将第j个点加入聚类当中
    		i=j;
    		j=i+1;
    	}
    	for(int k=j+1;k<n;k++){
    		Point p_k = track.getLinePoints().get(k);
    		p_k.order=k;
    		double dSpacejk = Math.abs(spatial_distance(p_j,p_k));
    		double writeTime=-1 ;
    		if(dSpacejk>=MinDistance){
    			if(k>j+1){
    				Point p_beforeK = track.getLinePoints().get(k-1);
    				double dTime = Math.abs(parseDate(p_beforeK.getTime()).getTime() - parseDate(p_j.getTime()).getTime());
    				System.out.println("dTime"+dTime);
    				writeTime = dTime;
    			    if(dTime >= MinStopDuration){
    			    	Cluster.add(p_j);
    			    	i=j;
    			    	j=k;
    			    	break;
    			    }else{
    			    	double Xave = get_mean(j,k-1,track,"latitude");
    			    	double Yave = get_mean(j,k-1,track,"longitude");
    			    	//find m , j<=m<=k-1 , spatial_measure(m,ave)<=spatial_distance(p,ave)
    			    	Point p_ave = new Point(Xave,Yave);
    			    	int m = findClosest(j,k-1,track,p_ave);//m点是最近的
    			    	j=m;
    			    }
    			}
    		}
    		//compute aturn = angle between_vector((i,j),(j,k))
    		double aTurn = angle_between_vector(i,j,j,k,track);
    		if(aTurn>=MinAngle){
    			Cluster.add(p_j);
    			i=j;
    			j=k;
    		}else{
    			j=j+1;
    	      }
    	//j=i+1;
    		
    		/* try {
    			 String str = dSpaceij+" "+ dSpacejk+" "+writeTime+" "+aTurn+"\n";
		            fw.write(str);
		        } catch (IOException e1) {
		            e1.printStackTrace();
		            System.out.println("写入失败");
		            System.exit(-1);
		        }*/
    	}
    	
    	}
    	Point p_end = track.getEnd();
    	Cluster.add(p_end);//将最后一个点加入到聚类当中
    	//System.out.println("clustersize:"+Cluster.size());
    	//fw.close();
    	this.sumPoint = Cluster.size();
    	return Cluster;
	}
    public ArrayList<Point> algorithm(Trajectory track,double MinAngle,double MinStopDuration,double MinDistance,double MaxDistance) throws ParseException, IOException{
    	//写入文件
    	// FileWriter fw = null;
		// fw = new FileWriter("G://data/Experiments/Algor.txt",true);
    	//以上为写入文件
    	int i=1;
    	int j=i+1;
    	int n=track.getLinePoints().size();//n是轨迹总的点数
    	//System.out.println(n);
    	ArrayList<Point> Cluster = new ArrayList<Point>();//聚类
    	Point p_start = track.getStart();
    	p_start.order = 0;
    	Cluster.add(p_start);//将第一个点加到聚类当中
    
    	while(j<n){
    		if(j==n-1){
    			break;
    		}
    	//	System.out.println(j);
    	//compute dSpaceij = spatial_distance((xi,yi),(xj,yj))计算i点和j点的距离
    	Point p_i = track.getLinePoints().get(i);
    	p_i.order=i;
    	Point p_j = track.getLinePoints().get(j);
    	p_j.order=j;
        double dSpaceij = Math.abs(spatial_distance(p_i,p_j));
        if(maxDis<dSpaceij){
        	maxDis = dSpaceij;
        }
        if(minDis>dSpaceij){
        	minDis = dSpaceij;
        }
    	if(dSpaceij>=MaxDistance){
    		//那么第j个点就是代表点
    		Cluster.add(p_j);//将第j个点加入聚类当中
    		i=j;
    		j=i+1;
    	}
    	for(int k=j+1;k<n;k++){
    		Point p_k = track.getLinePoints().get(k);
    		p_k.order=k;
    		double dSpacejk = Math.abs(spatial_distance(p_j,p_k));
    		double writeTime=-1 ;
    		if(dSpacejk>=MinDistance){
    			if(k>j+1){
    				Point p_beforeK = track.getLinePoints().get(k-1);
    				p_beforeK.order = k-1;
    				double dTime = Math.abs(parseDate(p_beforeK.getTime()).getTime() - parseDate(p_j.getTime()).getTime());
    				System.out.println("dTime"+dTime);
    				writeTime = dTime;
    			    if(dTime >= MinStopDuration){
    			    	Cluster.add(p_j);
    			    	i=j;
    			    	j=k;
    			    	break;
    			    }else{
    			    	double Xave = get_mean(j,k-1,track,"latitude");
    			    	double Yave = get_mean(j,k-1,track,"longitude");
    			    	//find m , j<=m<=k-1 , spatial_measure(m,ave)<=spatial_distance(p,ave)
    			    	Point p_ave = new Point(Xave,Yave);
    			    	int m = findClosest(j,k-1,track,p_ave);//m点是最近的
    			    	j=m;
    			    }
    			}
    		}
    		//compute aturn = angle between_vector((i,j),(j,k))
    		double aTurn = angle_between_vector(i,j,j,k,track);
    		if(aTurn>=MinAngle){
    			Cluster.add(p_j);
    			i=j;
    			j=k;
    		}else{
    			j=j+1;
    	      }
    	//j=i+1;
    		
    		/* try {
    			 String str = dSpaceij+" "+ dSpacejk+" "+writeTime+" "+aTurn+"\n";
		            fw.write(str);
		        } catch (IOException e1) {
		            e1.printStackTrace();
		            System.out.println("写入失败");
		            System.exit(-1);
		        }*/
    	}
    	
    	}
    	Point p_end = track.getEnd();
    	p_end.order = track.getLinePoints().size()-1;
    	Cluster.add(p_end);//将最后一个点加入到聚类当中
    	//System.out.println("clustersize:"+Cluster.size());
    	//fw.close();
    	this.sumPoint = Cluster.size();
    	return Cluster;
    }
    public void AddIntoDBSGA(ArrayList<Point>cluster ) throws ClassNotFoundException, SQLException{
    	GetData gt = new GetData();
    	gt.DbStoreHelper();
    	gt.AddIntoCharactersSGA(cluster, "SGA");
    }
    public double angle_between_vector(int a,int b,int c,int d,Trajectory track){
    	double angle = 0.0;
    	double x_a = track.getLinePoints().get(a).getX();
    	double y_a = track.getLinePoints().get(a).getY();
    	double x_b = track.getLinePoints().get(b).getX();
    	double y_b = track.getLinePoints().get(b).getY();
    	double x_c = track.getLinePoints().get(c).getX();
    	double y_c = track.getLinePoints().get(c).getY();
    	double x_d = track.getLinePoints().get(d).getX();
    	double y_d = track.getLinePoints().get(d).getY();
    	double distanceA = Math.sqrt((x_a-x_b)*(x_a-x_b)+(y_a-y_b)*(y_a-y_b));
    	double distanceB = Math.sqrt((x_c-x_d)*(x_c-x_d)+(y_c-y_d)*(y_c-y_d));
    	double cos = ((x_a-x_b)*(x_c-x_d)+(y_a-y_b)*(y_c-y_d))/distanceA*distanceB;
    	angle = Math.acos(cos);
    	
    	return angle;
    }
    private int findClosest(int j, int i, Trajectory track, Point p_ave) {
		// TODO Auto-generated method stub
    	int closest = -1;
    	double closest_value = 0.0; 
    	Point first = track.getLinePoints().get(j);
    	closest_value = Math.abs(spatial_distance(first, p_ave));
    	for(int k=j+1;k<=i;k++){
    		Point temp = track.getLinePoints().get(k);
    		double distance = Math.abs(spatial_distance(temp, p_ave));
    		if(closest_value>distance){
    			closest = k;
    			closest_value = distance;
    	}
    	}
		return closest;
	}
	private double get_mean(int j, int i, Trajectory track,String type) {
		// TODO Auto-generated method stub
    	double sum = 0.0;
    	if(type.equals("latitude")){//latitude求平均
    		for(int k=j;k<=i;k++){
        		Point p_temp = track.getLinePoints().get(k);
        		sum += p_temp.getY();
        	}
    	}
    	else{
    		for(int k=j;k<=i;k++){//longitude求平均
        		Point p_temp = track.getLinePoints().get(k);
        		sum += p_temp.getY();
        	}
    	}
    	double average = sum / (i-j+1);
		return average;
	}
	public Date parseDate(String time) throws ParseException{
    	//将string类型转化为date类型
    	java.text.SimpleDateFormat formatter = new SimpleDateFormat( "yyyy-MM-dd HH:mm:ss");
    	Date date =  formatter.parse(time);
    	return date;
    }
    public  double HaverSin(double theta)
    {
        double v = Math.sin(theta / 2);
        return v * v;
    }
    public double ConvertRadiansToDegrees(double radian)
    {
        return radian * 180.0 / Math.PI;
    }
    public static double ConvertDegreesToRadians(double degrees)
    {
        return degrees * Math.PI / 180;
    }
	private double spatial_distance(Point p_i, Point p_j) {
		double EARTH_RADIUS = 6371.0;//km 地球半径 平均值，千米
		//这里使用的方法是HaverSin距离公式
		// 计算i点和j点的距离
		double lat1 = ConvertDegreesToRadians(p_i.getX());
		double lon1 = ConvertDegreesToRadians(p_i.getY());
		double lat2 = ConvertDegreesToRadians(p_j.getX());
		double lon2 = ConvertDegreesToRadians(p_j.getY());
	    //差值
		double vLon= Math.abs(lon1 - lon2);
		double vLat= Math.abs(lat1 - lat2);
		double h = HaverSin(vLat) + Math.cos(lat1) * Math.cos(lat2) * HaverSin(vLon);
		double distance = 2 * EARTH_RADIUS *Math.asin(Math.sqrt(h));
		return distance;
	}
}
