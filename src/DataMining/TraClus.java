package DataMining;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map.Entry;
import java.util.Scanner;
import java.util.Set;

import com.cn.entity.CPointCluster;
import com.cn.entity.ClusterResultEntity;
import com.cn.entity.RawEntity;
import com.cn.entity.RawRepre;
import com.cn.entity.SegClustering;
import com.cn.entity.Taxi;
import com.cn.entity.char_SGAEntity;
import com.cn.entity.char_TraclusEntity;

import CpointsClustering.Clustering;
 
/**
 * @description 
 * @author xinjian lv(xinjian.lv@gmail.com)
 *
 */
public class TraClus {
	
	private int MDL_COST_ADVANTAGE = 0; 
	/*
	 * partition 涔嬪悗鐨勭嚎娈�
	 */
	public ArrayList<Line> lines = new ArrayList<Line>();
	/*
	 * 鍒濆杞ㄨ抗
	 */
	private HashMap<Integer , Trajectory> trajectorys = new HashMap<Integer, Trajectory>();
	private HashMap<Integer , ArrayList<Line>> cluster = new HashMap<Integer, ArrayList<Line>>();
	public RTra rtra = new RTra();
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
	public void loadPointsFromDao(List<List<Taxi>> allTracks){
			try{
			//	List<List<Taxi>>allTracks = gt.getPointForCharacter() ;
				for(int i=0;i<allTracks.size();i++){
					List<Taxi> temp_trajectory = allTracks.get(i);
					Trajectory tra= new Trajectory();
					int lineNum = i;
					String taxi_id = temp_trajectory.get(0).getTaxi_Id();
				 for(int j=0;j<temp_trajectory.size();j++){
					 Taxi temp =  temp_trajectory.get(j);
					int order =  j;
					//double x = Double.parseDouble(temp.getLatitude())*100;
					//double y = Double.parseDouble(temp.getLongtitude())*100;
					double x = Double.parseDouble(temp.getLatitude())*100;
					 double y = Double.parseDouble(temp.getLongtitude())*100;
					String gps_id = temp.getGPS_Id();
					String taxiId = temp.getTaxi_Id();
					String angle = temp.getAngle();
					String speed = temp.getSpeed();
					String state = temp.getState();
					String time = temp.getTime();
					//String str = gps_id + "\t" +taxiId +"\t" + x +"\t" + y +"\t"+ angle+"\t"+ time+"\t"+state;
					String str = gps_id + " " +taxiId +" " + x +" " + y +" "+ angle+" "+ time+" "+state;
					Point point = new Point(x , y);
					point.setNum(lineNum);
					point.setOrder(order);
					point.setTaxi_id(taxi_id);
					point.setGPS_ID(gps_id);
					point.setAngle(angle);
					point.setSpeed(speed);
					point.setState(state);
					point.setTime(time);
					
					if(trajectorys.containsKey(lineNum))
						tra = trajectorys.get(lineNum);
					tra.insert(point);
					
					}trajectorys.put(lineNum, tra);
					System.out.println("第"+i+"条： "+tra.getPoints().size());
				}
				//scan.close();
				System.out.println("end");
			}catch(Exception e){
				e.printStackTrace();
			}
		}
	
	public void loadPoints(){
		try{
			GetData gt = new GetData();
			List<List<Taxi>>allTracks = gt.getPointForCharacter() ;
			for(int i=0;i<allTracks.size();i++){
				List<Taxi> temp_trajectory = allTracks.get(i);
				Trajectory tra= new Trajectory();
				int lineNum = i;
				String taxi_id = temp_trajectory.get(0).getTaxi_Id();
			 for(int j=0;j<temp_trajectory.size();j++){
				Taxi temp =  temp_trajectory.get(j);
				int order =  j;
				//double x = Double.parseDouble(temp.getLatitude())*100;
				//double y = Double.parseDouble(temp.getLongtitude())*100;
				double x = Double.parseDouble(temp.getLatitude())*100;
				 double y = Double.parseDouble(temp.getLongtitude())*100;
				String gps_id = temp.getGPS_Id();
				String taxiId = temp.getTaxi_Id();
				String angle = temp.getAngle();
				String speed = temp.getSpeed();
				String state = temp.getState();
				String time = temp.getTime();
				//String str = gps_id + "\t" +taxiId +"\t" + x +"\t" + y +"\t"+ angle+"\t"+ time+"\t"+state;
				String str = gps_id + " " +taxiId +" " + x +" " + y +" "+ angle+" "+ time+" "+state;
				//写入文件
			/*	 FileWriter fw = null;
			        try {
			            fw = new FileWriter("G://data/data.txt",true);
			            String c = str+"\r\n";
			            fw.write(c);
			            fw.close();
			        } catch (IOException e1) {
			            e1.printStackTrace();
			            System.out.println("写入失败");
			            System.exit(-1);
			        }*/
				//以上写入文件
				Point point = new Point(x , y);
				point.setNum(lineNum);
				point.setOrder(order);
				point.setTaxi_id(taxi_id);
				point.setGPS_ID(gps_id);
				point.setAngle(angle);
				point.setSpeed(speed);
				point.setState(state);
				point.setTime(time);
				
				if(trajectorys.containsKey(lineNum))
					tra = trajectorys.get(lineNum);
				tra.insert(point);
				
				}trajectorys.put(lineNum, tra);
				System.out.println("第"+i+"条： "+tra.getPoints().size());
			}
			//scan.close();
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
	public void setPartitioned(List<List<Taxi>> data){
		for(int i=0;i<data.size();i++){
			List<Line> line_list = new ArrayList<Line>();
			 String id = data.get(i).get(0).getTaxi_Id();
			 List<Taxi> list = data.get(i);
			 for(int j=0;j<list.size()-1;j++){
				 Taxi taxi = list.get(j);
				 Taxi taxi_next = list.get(j+1);
				 Line temp = new Line();
				 temp.setNum(i);//第i号轨迹
				 Point s = TaxiToPoint(taxi);
				 Point e = TaxiToPoint(taxi_next);
				 temp.setS(s);
				 temp.setE(e);
				 line_list.add(temp);
			 }
			 lines.addAll(line_list);
			
		}
	}
	public Point TaxiToPoint(Taxi taxi){
		Point p = new Point();
		p.Angle = taxi.getAngle();
		p.GPS_ID = taxi.getGPS_Id();
		p.Speed=taxi.getSpeed();
		p.state=taxi.getState();
		p.taxi_id=taxi.getTaxi_Id();
		p.x = Double.valueOf(taxi.getLatitude());
		p.y = Double.valueOf(taxi.getLongtitude());
		p.time=taxi.getTime();
		return p;
	}
	public void partition() throws ClassNotFoundException, SQLException{
		System.out.println("partition...");
		System.out.println("size "+trajectorys.size());
		int c = 0;
		//int count = 0;
		for(Entry<Integer,Trajectory> en : trajectorys.entrySet()){
			List<Line> ls = partition(en.getValue().getPoints());//一条线段对应两个点S点和E点作为唯二的特征点（考虑到划分已经生成的线段起点和终点都是由相邻特征点组成的）
			//在这里进行插入数据库较为合适
		 //	GetData gt = new GetData();
		 //	gt.DbStoreHelper();
		 //	gt.AddIntoCharactersTraclus(ls, "traclus");
			lines.addAll(ls);
			if(c %1000 == 0)
				System.out.println("processed(partition) " + c + "...");
			c++;
		}
		System.out.println("lines " + lines.size()+"共有："+lines.size()*2+"个特征点");
	}
    public List<Point> getCharacters(List<Point>points){
    	try{
			int start = 0;
			int end = 1;
			//List<Line> list = new ArrayList<Line>();
			List<Point> point_list = new ArrayList<Point>();
			double cost_par , cost_nopar;
			cost_par = cost_nopar = 0.0;
			while(end < points.size()){
				cost_par = Distance.distance_mdl_par(points, start, end);
				cost_nopar = Distance.distance_mdl_nopar(points, start, end);
				if(cost_par > cost_nopar + MDL_COST_ADVANTAGE){
				//if(cost_par > cost_nopar ){
					//Line line = new Line(points.get(start), points.get(end - 1));
					//line.setNum(points.get(0).getNum());
					//list.add(line);
					point_list.add(points.get(end-1));
					start = end - 1 ;
				}else{
					end++;
				}
			}
			//濡傛灉鍙墿涓�涓偣锛屽垯鍔犲叆鍒颁箣鍓嶇殑鐨勭嚎娈典腑
			if ((points.size() - 1) - start == 1) {
				//point_list.get(list.size() - 1).setE(points.get(end - 1));
				point_list.add(points.get(end-1));
			}
			//鍓╁涓偣锛屽垯鍔犲叆鍒版渶鍚�
			else{
			/*	for(int i=start;i<end;i++){
					point_list.add(points.get(i));
				}
				*/
			}
       System.out.println("共"+points.size()+"个点，得到特征点："+point_list.size()+"个");
			return point_list;
		}catch(Exception e){
			e.printStackTrace();
		}
		return null;
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
				System.out.println("cost_par:"+cost_par+",cost_nopar:"+cost_nopar);
				if(cost_par > cost_nopar + MDL_COST_ADVANTAGE){
				//if(cost_par > cost_nopar ){
					Line line = new Line(points.get(start), points.get(end - 1));
					line.setNum(points.get(0).getNum());
					list.add(line);
					start = end - 1 ;
				}else{
					end++;
				}
			}
			//濡傛灉鍙墿涓�涓偣锛屽垯鍔犲叆鍒颁箣鍓嶇殑鐨勭嚎娈典腑
			if ((points.size() - 1) - start == 1) {
			 if(list.size()!=0)
				list.get(list.size() - 1).setE(points.get(end - 1));
				//list.get(last).setE(points.get(next));
			}
			//鍓╁涓偣锛屽垯鍔犲叆鍒版渶鍚�
			else{
				Line temp = new Line(points.get(start), points.get(end - 1));
				temp.setNum(points.get(0).getNum());
				list.add(temp);
			}
            System.out.println(list.size());
			return list;
		}catch(Exception e){
			e.printStackTrace();
		}
		return null;
	}
	
	public ArrayList<Line> clusterForRaw(int order){
		/*System.out.println("cluster...");
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
		check_tra_num(1);*/
		return cluster.get(order);
	}
	public void cluster(){
		//System.out.println("cluster...");
		//System.out.println("line size : " + lines.size());
		int end = lines.size();
		int clusterId = 1;
		for(int i = 0 ; i < end ; i++){
			//if(i % 10 == 0)
				//System.out.println("processed(cluster) " + i + "...");
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
		//System.out.println("cluster end...");
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
	public void outputCluster(String ofile) throws IOException{
		ArrayList<String> save = new ArrayList<String>();
		for(Entry<Integer , ArrayList<Line>> en : this.cluster.entrySet()){
			for(Line l : en.getValue()){
				System.out.println("outputCluster"+l.toString());
				save.add(en.getKey() + "\t" + l.toString());
			}
		}
		SaveListToFile(save, ofile, false);
	}
	private void ExpandCluster(int center , ArrayList<Integer> neighbor){
		//System.out.println("Expanding...");
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
	public double getEps(){
		System.out.println("starting eps");
		ArrayList<Double> Hx_eps = new ArrayList<>();
		ArrayList<Double> Eps_list = new ArrayList<>();
		double Eps = 0.0001;
		while(Eps<5){
		 
		Eps_list.add(Eps);
		//计算使得H(x)最小的eps,自变量是eps,应变量是H(x)
		//先求p(xi)
		ArrayList<Integer> list = new ArrayList<>();
		int total = 0;
		for(int i=0;i<lines.size();i++){
			int num = getNeighborForEps(i,Eps).size();
			list.add(num);
			total+=num;//total是Px的分母
		}
	   ArrayList<Double> px_list = new ArrayList<>();
	   for(int i=0;i<list.size();i++){
		   double px = list.get(i)/total;
		   px_list.add(px);
	   }
	   //最后一步
	   double count_final =0.0;
	   for(int i=0;i<px_list.size();i++){
		   double px_val = px_list.get(i);
		   double temp = px_val*(Math.log(px_val)/Math.log(2));
		   count_final+=temp;
	   }
	   Hx_eps.add((-1)*count_final);
	   Eps=Eps+0.001;
		}
		int minIndex = Hx_eps.indexOf(Collections.min(Hx_eps));
		System.out.println("the min for eps is :"+ Eps_list.get(minIndex));
		return Eps_list.get(minIndex);
	}
	private ArrayList<Integer> getNeighborForEps(int index,double eps){
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
	public void ouputLines(String ofile){
		List<String> ls = new ArrayList<String>();
		HashMap<Integer, Integer> map = new HashMap<Integer, Integer>();
		int order = 0;
		for(Line l : lines){
			l.setOrder(order++);
			ls.add(l.toString());
			if(map.containsKey(l.getNum())){
				map.put(l.getNum(), map.get(l.getNum()) + 1);
			}else{
				map.put(l.getNum() , 1);
			}
		}
		SaveListToFile(ls, ofile, false);
		
		for(Entry<Integer , Integer> en : map.entrySet()){
			System.out.println(en.getKey() + "\t" + en.getValue());
		}
	}
	
	public ArrayList<Trajectory> getRTrajectoryGeneral(HashMap<Integer, List<Line>> map){
		rtra.setParameter(2, 0.01);//这里统一将minlns设置为2，有2个交点就计算均值，平滑系数设置为0
		//对于kmeans和层次聚类的聚类方法
		for(Entry<Integer, List<Line>> en : map.entrySet()){
			ArrayList<Line> ctra = new ArrayList<Line>();
			ctra.addAll(en.getValue());
			rtra.setCluster(ctra);
			rtra.getRTra(en.getKey());//将clusterId传入每条代表轨迹中
			rtra.clearData();
		}
		return rtra.getRTrajectory();
		
	}

	public ArrayList<Trajectory> getRTrajectory( int min  , double radius){
		rtra.setParameter(min, radius);
		//int count = 0;
		for(Entry<Integer, ArrayList<Line>> en : cluster.entrySet()){
			ArrayList<Line> ctra = new ArrayList<Line>();
			ctra.addAll(en.getValue());
			rtra.setCluster(ctra);
			rtra.getRTra(en.getKey());//将clusterId传入每条代表轨迹中
			rtra.clearData();
		//	rtra.setClusterId(en.getKey());//每一个代表轨迹后面都有一个对应的聚类（线段集合）
		//	System.out.println("the cluster id for this rtra is"+rtra.getRTrajectory().get(count).clusterId);
		//	System.out.println("the id order in the rtra list for this rtra is"+count);
		//	System.out.println("the points in rtra is :"+rtra.pointSize);
		//	count++;
		}
		return rtra.getRTrajectory();
	}
	public ArrayList<Trajectory> getGeneral(HashMap<Integer , ArrayList<Line>> cluster_general){
		rtra.setParameter(2, 0.1);
		 
		for(Entry<Integer, ArrayList<Line>> en :cluster_general.entrySet()){
			ArrayList<Line> ctra = new ArrayList<Line>();
			ctra.addAll(en.getValue());
			rtra.setCluster(ctra);
			rtra.getRTra(en.getKey());//将clusterId传入每条代表轨迹中
			rtra.clearData();
		 
		}
		ArrayList<Trajectory> result = rtra.getRTrajectory();
		return result;
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
	public void saveRtrajectory(String filepath) throws IOException{
		ArrayList<Trajectory> rTrajectory  = rtra.getRTrajectory();
		ArrayList<String> list = new ArrayList<String>();
		for(int i = 0 ; i < rTrajectory.size() ; i++){
			Trajectory rt = rTrajectory.get(i);
			for(Point p : rt.getPoints()){
				list.add(i + "\t" + p.toString());
			}
		}
		SaveListToFile(list, filepath, false);
	}
/*	public void overWriteLines(String ifile , int p1x , int p1y , int p2x , int p2y){
		try{
			lines.clear();
			ArrayList<String> list = FileTool.LoadListFromFile(ifile, 0, Charset.forName("utf-8"));
			for(String line : list) {
				String [] array = line.split("\t");
				Point s = new Point(Double.parseDouble(array[p1x]) , Double.parseDouble(array[p1y]));
				Point e = new Point(Double.parseDouble(array[p2x]) , Double.parseDouble(array[p2y]));
				Line l = new Line(s, e);
				l.setNum(Integer.parseInt(array[1]));
				l.setOrder(Integer.parseInt(array[0]));
				lines.add(l);
			}
		}catch(Exception e){
			e.printStackTrace();
		}
	}*/
	
	private void SaveListToFile(ArrayList<String> list, String filepath, boolean b) throws IOException {
		// TODO Auto-generated method stub
	//	ArrayList<String> allList = AllTaxiId();//所有出租车id在10月12日
		  for (int i = 0; i < list.size(); i++) {
		//  List<Taxi> temp = list.get(i);
		  FileWriter fileWriter=new FileWriter("G:/data/轨迹聚类/compression/UEF_compression源码/Trac_cluster/original_"+filepath+(i+1)+".txt");
		//  for(int j=0;j<temp.size();j++){
			/*  Taxi point = temp.get(j);
			  String lat = point.getLatitude();
			  String lon = point.getLongtitude();
			  String taxi_id = allList.get(i);
			  String[] time = point.getTime().split("\\s+");//年月日  时分秒
			 */
		  String str = list.get(i);
			//  String str = lat +" " + lon + " " +time[0].trim()+ " " + time[1].trim();
		       fileWriter.write(str+" \r\n");
		//	}
			fileWriter.flush();
			fileWriter.close();
		  }
		
	}
	private void SaveListToFile(List<String> ls, String ofile, boolean b) {
		// TODO Auto-generated method stub
	/*	List<List<Taxi>>allTracks = getAllTracks_Original();//所有出租车id的原始轨迹
		ArrayList<String> allList = AllTaxiId();//所有出租车id在10月12日
		  for (int i = 0; i < allTracks.size(); i++) {
		  List<Taxi> temp = allTracks.get(i);
		  FileWriter fileWriter=new FileWriter("G:/data/轨迹聚类/compression/UEF_compression源码/myData/"+(i+1)+".txt");
		  for(int j=0;j<temp.size();j++){
			  Taxi point = temp.get(j);
			  String lat = point.getLatitude();
			  String lon = point.getLongtitude();
			  String taxi_id = allList.get(i);
			  String[] time = point.getTime().split("\\s+");//年月日  时分秒
			  
			  String str = lat +" " + lon + " " +time[0].trim()+ " " + time[1].trim();
		       fileWriter.write(str+" \r\n");
			}
			fileWriter.flush();
			fileWriter.close();
		  }
		*/
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
	//private cn.nocml.Pair<Double , Integer> calclateParameter(){
	/*	private HashMap<Double , Integer> calclateParameter(){
		int n = lines.size();
		int sigma = 0;
		double prob = 0.0;
		for(int i = 0 ; i < n ; i++){
//			if( lines.get(i).getCluster().size() == 0)
//				System.out.println("pause");
			sigma += lines.get(i).getCluster().size() + 1;
		} 
		for(int i = 0 ; i < n ;i++){
			double nx = lines.get(i).getCluster().size() + 1;
			double px = nx / sigma;
			prob +=(px * MathTool.log(px, 2));
		}
		prob = -1 * prob;
		int avg = sigma / n;
		HashMap<Double,Integer> result = new HashMap<Double,Integer>();
		result.put(prob, avg);
		return result;
	}
	*/
	
 	/* public static void main(String[] args) {
 		TraClus traClus = new TraClus();
        traClus.loadPoints();//加载的是100条轨迹10月12日
	   // traClus.loadPointsFromDao(allTracks);
	    traClus.setParameter(2, 0.01);//minlns和eps
	 	traClus.setMDL_COST_ADVANTAGE(0);
    	 try {
			traClus.partition();
		} catch (ClassNotFoundException | SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}//partition
 	 }*/
	public RawRepre getGeneralSingleRtrajectory(Set<String> idList,List<RawEntity> list, String cluster_type,
			String partition_type, int order){
		RawRepre rawRep = new RawRepre();
		ArrayList<Trajectory> rTrajectory = new ArrayList<>();
		try{
			//allTracks是partition之后的结果
			TraClus traClus = new TraClus();
		//之前的submit操作已经向cp_raw里面存入用来聚类的特征点和clusterId,从而获取类的信息
		HashMap<Integer , ArrayList<Line>> cluster_general = new HashMap<Integer, ArrayList<Line>>();
			cluster_general = getClusterFromCpRaw(idList,list);//从数据库获取cluster
			//按照获取rtrajectory的方式获取代表轨迹
			 
			rTrajectory = traClus.getGeneral(cluster_general);
			for(int i=0;i<rTrajectory.size();i++){
				if(rTrajectory.get(i).getPoints().size()<2){
					rTrajectory.remove(i);
					i--;
				}
			}
			//返回给service，由于是平均值求得(sweepLine)因此无法显示GPSID，taxi_id的信息，展示的信息为纬度，经度，和点的个数
			rawRep.setRep(rTrajectory.get(order));
			rawRep.setCluster(cluster_general.get(order));
		}catch(Exception e){
			e.printStackTrace();
		}
		
		return rawRep;
	}
	public RawRepre getSingleRtrajectory( List<List<Taxi>> allTracks,int order,double eps,int minlns){
		// minlns = 2;
		// eps = 0.1;
		RawRepre rawRep = new RawRepre();
		ArrayList<Line> result = new ArrayList<>();
		ArrayList<Trajectory> rTrajectory = new ArrayList<>();
		try{
			//allTracks是partition之后的结果
			TraClus traClus = new TraClus();
		  //  traClus.loadPointsFromDao(allTracks);
		    traClus.setParameter(minlns, eps);//minlns和eps,应该为2,0.01
		 	traClus.setMDL_COST_ADVANTAGE(0);
	    	// traClus.partition();//partition
		 	traClus.setPartitioned(allTracks);
		  	//partition之后将乘以100的值全部恢复
		 	traClus.SetReverse();
		 	traClus.sortLine();//sort the line
		 	traClus.setMDL_COST_ADVANTAGE(10);
		 	traClus.cluster();//cluster 
			 
			traClus.check_tra_num(1);
			rTrajectory = traClus.getRTrajectory(minlns,eps);
			for(int i=0;i<rTrajectory.size();i++){
				if(rTrajectory.get(i).getPoints().size()<2){
					rTrajectory.remove(i);
					i--;
				}
			}
			//根据代表轨迹的order确定
			int clusterId = rTrajectory.get(order).getClusterId();
		 	result = traClus.clusterForRaw(clusterId);//cluster 
			//traClus.check_tra_num(1);
		}catch(Exception e){
			e.printStackTrace();
		}
		//获取代表轨迹的聚类，需要通过clusterId来获取
	 rawRep.setCluster(result);
	 rawRep.setRep(rTrajectory.get(order));
	 return rawRep;
	}
	public List<CPointCluster> getCenterResult(List<List<Taxi>>data,double eps,int minlns){
		List<CPointCluster> centerPoint = new ArrayList<>();//聚类中心点
		try{
			//allTracks是partition之后的结果
			TraClus traClus = new TraClus();
		    //   traClus.loadPoints();//加载的是100条轨迹10月12日
		//	GetData gt = new GetData();
			
		  //  traClus.loadPointsFromDao(data);
		    traClus.setParameter(minlns, eps);//minlns和eps：应选择2,0.01
		 	traClus.setMDL_COST_ADVANTAGE(0);
	    	// traClus.partition();//partition
		 	traClus.setPartitioned(data);
	    	 //这里获取最优eps
	     
		  	//partition之后将乘以100的值全部恢复
		 	traClus.SetReverse();
		 	traClus.sortLine();//sort the line
		 	traClus.setMDL_COST_ADVANTAGE(10);
		 	traClus.cluster();//cluster 
			traClus.check_tra_num(1);
			//目的是对cluster提取特征点并计算中心点
			HashMap<Integer,ArrayList<Point>> Cpoint_list = new HashMap<>();
			
			Cpoint_list =traClus.getPointsFromCluster();
			centerPoint = traClus.getCenterFromCluster(Cpoint_list );
			System.out.println(centerPoint.size());
		}catch(Exception e){
			e.printStackTrace();
		}
		return centerPoint;
	}
	public static void main(String[] args){
		int minlns = 2;
		double eps = 0.1;
		ArrayList<Trajectory> rTrajectory = new ArrayList<>();
		
		try{
			//allTracks是partition之后的结果
			TraClus traClus = new TraClus();
		    //   traClus.loadPoints();//加载的是100条轨迹10月12日
			GetData gt = new GetData();
			
		    traClus.loadPointsFromDao(gt.getPointForCharacter());
		    traClus.setParameter(minlns, eps);//minlns和eps：应选择2,0.01
		 	traClus.setMDL_COST_ADVANTAGE(0);
	    	 traClus.partition();//partition
	    	 //这里获取最优eps
	    	// traClus.getEps();
		  	//partition之后将乘以100的值全部恢复
		 	traClus.SetReverse();
		 	traClus.sortLine();//sort the line
		 	traClus.setMDL_COST_ADVANTAGE(10);
			
		 	traClus.cluster();//cluster 
			 
			traClus.check_tra_num(1);
			//目的是对cluster提取特征点并计算中心点
			HashMap<Integer,ArrayList<Point>> Cpoint_list = new HashMap<>();
			List<CPointCluster> centerPoint = new ArrayList<>();//聚类中心点
			Cpoint_list =traClus.getPointsFromCluster();
			centerPoint = traClus.getCenterFromCluster(Cpoint_list);
			System.out.println(centerPoint.size());
		/*	rTrajectory = traClus.getRTrajectory(minlns,eps);
			for(int i=0;i<rTrajectory.size();i++){
				if(rTrajectory.get(i).getPoints().size()<2){
					rTrajectory.remove(i);
					i--;
				}
			} */
			
		}catch(Exception e){
			e.printStackTrace();
		}
	}
	public CPointCluster CalculateCenter(ArrayList<Point> points) {
		//get each cluster
		    CPointCluster cp = new CPointCluster();
			double totalLatitude=0, totalLongtitude=0;
			int SizeOfPoints = points.size();
			//get each turning point
			for(int j = 0;j<points.size();j++){
				totalLatitude=totalLatitude+points.get(j).getX();
				totalLongtitude=totalLongtitude+points.get(j).getY();
			}
			cp.setCenterLatitude(totalLatitude/SizeOfPoints);
			cp.setCenterLongtitude(totalLongtitude/SizeOfPoints);
			System.out.println("centerLa:"+totalLatitude/SizeOfPoints+" centerLon"+totalLongtitude/SizeOfPoints);
		    return cp;
	}
	private List<CPointCluster> getCenterFromCluster(HashMap<Integer, ArrayList<Point>> cpoint_list ) {
		// 根据簇内的特征点获取中心点，调用service里面的computer center
		List<CPointCluster> list = new ArrayList<>();
		HashMap<Integer,ArrayList<Point>> Maplist = new HashMap<>();
		Set set = cpoint_list.keySet();
		Iterator iter = set.iterator();
		ArrayList<Integer> keyList =new ArrayList<>();
		while(iter.hasNext()){
			keyList.add((Integer)iter.next());//key
		}
		for(int i=0;i<cpoint_list.size();i++){
			int cluster_id = keyList.get(i);
			ArrayList<Point> points = cpoint_list.get(cluster_id);//原始点
			CPointCluster cp = new CPointCluster();
			cp = CalculateCenter(points);
		    cp.setTurningPoints(PointToTaxi(points));
		    cp.setId(cluster_id);
			list.add(cp);
		}
		return list;
	}
    private List<Taxi> PointToTaxi(ArrayList<Point> points){
    	List<Taxi> list = new ArrayList<Taxi>();
    	//把Point类型转化为Taxi类型方便存入cp_raw
    	for(int i=0;i<points.size();i++){
    		Taxi temp = new Taxi();
    		double lat = points.get(i).x;
    		double lon = points.get(i).y;
    		String gpsId = points.get(i).GPS_ID;
    		String taxiId = points.get(i).taxi_id;
    		String speed = points.get(i).Speed;
    		String angle  = points.get(i).Angle;
    		String state = points.get(i).state;
    		String time = points.get(i).time;
    		temp.setGPS_Id(gpsId);
    		temp.setTaxi_Id(taxiId);
    		temp.setLatitude(String.valueOf(lat));
    		temp.setLongtitude(String.valueOf(lon));
    		temp.setSpeed(speed);
    		temp.setAngle(angle);
    		temp.setState(state);
    		temp.setTime(time);
    		list.add(temp);
    	}
    	return list;
     }
	private HashMap<Integer,ArrayList<Point>> getPointsFromCluster() {
		//System.out.println("size of cluster is:"+cluster.size());
		// 从已经聚类好的几个聚类分别获取特征点
		int count = 0;
		HashMap<Integer,ArrayList<Point>> list = new HashMap<>();
		Set set = cluster.keySet();
		Iterator iter = set.iterator();
		ArrayList<Integer> keyList =new ArrayList<>();
		while(iter.hasNext()){
			keyList.add((Integer)iter.next());
		}
		int countIt = 0;
		//开始提取特征点
		for(int i=0;i<keyList.size();i++){
			ArrayList<Point> cpoint = new ArrayList<>();//每个cluster存放的特征点集合
			int cluster_id = keyList.get(i);//cluster_id
			 ArrayList<Line> tempLines = cluster.get(cluster_id);
		 
			 for(int j=0;j<tempLines.size();j++){
				 Line line = tempLines.get(j);
				 Point s = line.getS();
				 Point e = line.getE();
				 cpoint.add(s);
				 cpoint.add(e);
				 countIt+=2;
			 }
			// System.out.println("clusterId:"+cluster_id +" size of cpoints is+"+cpoint.size());
			ArrayList<Point> p_list = new ArrayList<>();
			 p_list = RemoveDuplicate(cpoint);
			 count+=cpoint.size();
			 //去重复之后放入结果中
			 list.put(cluster_id,p_list);
		}
		System.out.println(count);
		return list;
	}
    public ArrayList<Point> RemoveDuplicate(ArrayList<Point> points){
		ArrayList<Point> list = points;
    	for(int i=0;i<list.size();i++){
    		Point p = list.get(i);
    		for(int j=i;j<list.size();j++){
    			Point p_compare = list.get(j);
    			//if((p.x==p_compare.x)&&(p.y==p_compare.y)&&(p.taxi_id.equals(p_compare.taxi_id))){
    			if(p.GPS_ID.equals(p_compare.GPS_ID)){
    				list.remove(j);
    				j--;
    			}
    		}
    	}
    	return list;
    }
    public ArrayList<Trajectory> getRtra(List<List<Taxi>> allTracks,double eps, int minlns){
    	ArrayList<Trajectory> rTrajectory = new ArrayList<>();
		try{
			//allTracks是partition之后的结果
			//TraClus traClus = new TraClus();
		    //   traClus.loadPoints();//加载的是100条轨迹10月12日
		 //   traClus.loadPointsFromDao(allTracks);
		    this.setParameter(minlns, eps);//minlns和eps：应选择2,0.01
		    this.setMDL_COST_ADVANTAGE(0);
	    	// traClus.partition();//partition
		    this.setPartitioned(allTracks);
	    	 //这里获取最优eps
	    	// traClus.getEps();
		  	//partition之后将乘以100的值全部恢复
		    this.SetReverse();
		    this.sortLine();//sort the line
		    this.setMDL_COST_ADVANTAGE(10);
			
		    this.cluster();//cluster 
			 
		    this.check_tra_num(1);
		//	traClus.outputCluster("cluster_my_checktaxi1");
			rTrajectory = this.getRTrajectory(minlns,eps);
			for(int i=0;i<rTrajectory.size();i++){
				if(rTrajectory.get(i).getPoints().size()<2){
					rTrajectory.remove(i);
					i--;
				}
			}
			//返回给service，由于是平均值求得(sweepLine)因此无法显示GPSID，taxi_id的信息，展示的信息为纬度，经度，和点的个数
			int countflag = 0;
			for(int i=0;i<allTracks.size();i++){
				countflag +=allTracks.get(i).size();
			}
			System.out.println("for allTracks there are :"+countflag+" points");
			System.out.println("we get the rTraject size is"+rTrajectory.size());
			
		}catch(Exception e){
			e.printStackTrace();
		}
	 return rTrajectory;
    }
    public ArrayList<Trajectory> getRtrajectoryNew(TraClus traClus,ArrayList<Line>segments,double eps, int minlns){
		 
		ArrayList<Trajectory> rTrajectory = new ArrayList<>();
		try{
			//allTracks是partition之后的结果
		//	TraClus traClus = new TraClus();
		    //   traClus.loadPoints();//加载的是100条轨迹10月12日
		 //   traClus.loadPointsFromDao(allTracks);
		    this.setParameter(minlns, eps);//minlns和eps：应选择2,0.01
		 	this.setMDL_COST_ADVANTAGE(0);
	    	// traClus.partition();//partition
		 	this.setPartitionedNew(segments);
	    	 //这里获取最优eps
	    	// traClus.getEps();
		  	//partition之后将乘以100的值全部恢复
		 	this.SetReverse();
		 	this.sortLine();//sort the line
		 	this.setMDL_COST_ADVANTAGE(10);
			
		 	this.cluster();//cluster 
			 
		 	this.check_tra_num(1);
		//	traClus.outputCluster("cluster_my_checktaxi1");
			rTrajectory = this.getRTrajectory(minlns,eps);
			for(int i=0;i<rTrajectory.size();i++){
				if(rTrajectory.get(i).getPoints().size()<2){
					rTrajectory.remove(i);
					i--;
				}
			} 
		}catch(Exception e){
			e.printStackTrace();
		}
	 return rTrajectory;
		 
	}
	 
	private void setPartitionedNew(ArrayList<Line> segments) {
		// TODO Auto-generated method stub
		lines.addAll(segments);
	}

	public ArrayList<Trajectory> getRtrajectory(TraClus traClus,List<List<Taxi>> allTracks,double eps, int minlns){
		 
		ArrayList<Trajectory> rTrajectory = new ArrayList<>();
		try{
			//allTracks是partition之后的结果
		//	TraClus traClus = new TraClus();
		    //   traClus.loadPoints();//加载的是100条轨迹10月12日
		 //   traClus.loadPointsFromDao(allTracks);
		    traClus.setParameter(minlns, eps);//minlns和eps：应选择2,0.01
		 	traClus.setMDL_COST_ADVANTAGE(0);
	    	// traClus.partition();//partition
		 	traClus.setPartitioned(allTracks);
	    	 //这里获取最优eps
	    	// traClus.getEps();
		  	//partition之后将乘以100的值全部恢复
		 	traClus.SetReverse();
		 	traClus.sortLine();//sort the line
		 	traClus.setMDL_COST_ADVANTAGE(10);
			
		 	traClus.cluster();//cluster 
			 
			traClus.check_tra_num(1);
		//	traClus.outputCluster("cluster_my_checktaxi1");
			rTrajectory = traClus.getRTrajectory(minlns,eps);
			for(int i=0;i<rTrajectory.size();i++){
				if(rTrajectory.get(i).getPoints().size()<2){
					rTrajectory.remove(i);
					i--;
				}
			}
			//返回给service，由于是平均值求得(sweepLine)因此无法显示GPSID，taxi_id的信息，展示的信息为纬度，经度，和点的个数
			int countflag = 0;
			for(int i=0;i<allTracks.size();i++){
				countflag +=allTracks.get(i).size();
			}
			System.out.println("for allTracks there are :"+countflag+" points");
			System.out.println("we get the rTraject size is"+rTrajectory.size());
			
		}catch(Exception e){
			e.printStackTrace();
		}
	 return rTrajectory;
		 
	}
	 
	private void SetReverse() {
		//之前将经纬度都乘以100用于traclus的mdl分割较多的点，此时用于聚类则需要将分割得到的经纬度点还原
		for(int i=0;i<this.lines.size();i++){
			Line temp = lines.get(i);
			
			 if(lines.get(i).getS().x>90||lines.get(i).getS().y>180){
			double x_s= (temp.getS().x)/100;
			double y_s = (temp.getS().y)/100;
			lines.get(i).getS().setX(x_s);
			lines.get(i).getS().setY(y_s);
			 }
			 if(lines.get(i).getE().x>90||lines.get(i).getE().y>180){
			double x_e = (temp.getE().x)/100;
			double y_e = (temp.getE().y)/100;
			
			lines.get(i).getE().setX(x_e);
			lines.get(i).getE().setY(y_e); 
			 }
			 if(lines.get(i).getS().x>90||lines.get(i).getS().y>180||lines.get(i).getE().x>90||lines.get(i).getE().y>180){
				 System.out.println("i:"+i+lines.get(i).getS().x);
			 }
		}
		}

	private List<Point> loadPointsForCharacter() {
			//获得100条轨迹用于characteristic点的展示
		ArrayList<Point> points = new ArrayList<>();
		try{
			//Scanner scan = new Scanner(new File(filename));
			GetData gt = new GetData();
			List<List<Taxi>>allTracks = gt.getPointForCharacter();
			//while (scan.hasNext()) {
			for(int i=0;i<allTracks.size();i++){
				List<Taxi> temp_trajectory = allTracks.get(i);
				Trajectory tra= new Trajectory();
				int lineNum = i;
				String taxi_id = temp_trajectory.get(0).getTaxi_Id();
			 for(int j=0;j<temp_trajectory.size();j++){
				Taxi temp =  temp_trajectory.get(j);
				int order =  j;
				double x = Double.parseDouble(temp.getLatitude());
				double y = Double.parseDouble(temp.getLongtitude());
				String gps_id = temp.getGPS_Id();
				String angle = temp.getAngle();
				String speed = temp.getSpeed();
				String state = temp.getState();
				String time = temp.getTime();
				Point point = new Point(x , y);
				point.setNum(lineNum);
				point.setOrder(order);
				point.setTaxi_id(taxi_id);
				point.setGPS_ID(gps_id);
				point.setAngle(angle);
				point.setSpeed(speed);
				point.setState(state);
				point.setTime(time);
				points.add(point);
				} 
			//	System.out.println("第"+i+"条： "+tra.getPoints().size());
			}
			//scan.close();
		}catch(Exception e){
			e.printStackTrace();
		}
		return points;
			
		}

	private static void SaveRtrajectory(ArrayList<Trajectory> rTrajectory) throws IOException, ClassNotFoundException, SQLException {
		// TODO Auto-generated method stub
		//用于存储生成的Representative trajectory
	//	List<List<Taxi>>allTracks = getAllTracks_Original();//所有出租车id的原始轨迹
//		ArrayList<String> allList = AllTaxiId();//所有出租车id在10月12日
		  for (int i = 0; i < rTrajectory.size(); i++) {
		  List<Point> temp = rTrajectory.get(i).getPoints();//第i条轨迹的所有点
		//  FileWriter fileWriter=new FileWriter("G:/data/轨迹聚类/compression/UEF_compression源码/Trac_cluster/rep"+(i+1)+".txt");
		  for(int j=0;j<temp.size();j++){
			  Point point = temp.get(j);
			  String lat = String.valueOf(point.getX());
			  String lon = String.valueOf(point.getY());
			 
			  //String[] time = point.getTime().split("\\s+");//年月日  时分秒
			  String str = lat +" "+lon;
		 // String str = lat +" " + lon + " " +time[0].trim()+ " " + time[1].trim();
			  System.out.println("track"+i+":"+str);
		  //     fileWriter.write(str+" \r\n");
			}
		//	fileWriter.flush();
		//	fileWriter.close();
		  }
		  //GetData gt = new GetData();
		  //gt.DbStoreHelperTraclus();
		//  gt.setTrajectory_num(rTrajectory.size());
		  //gt.AddIntoDB_Traclus(rTrajectory);
	}

	public List<Trajectory> getGeneralRTrajectory(Set<String> idList,List<RawEntity> list, String cluster_type,
			String partition_type) {
		ArrayList<Trajectory> rTrajectory = new ArrayList<>();
		try{
			//allTracks是partition之后的结果
			TraClus traClus = new TraClus();
		//之前的submit操作已经向cp_raw里面存入用来聚类的特征点和clusterId,从而获取类的信息
		HashMap<Integer , ArrayList<Line>> cluster_general = new HashMap<Integer, ArrayList<Line>>();
			cluster_general = getClusterFromCpRaw(idList,list);//从数据库获取cluster
			//按照获取rtrajectory的方式获取代表轨迹
			 
			rTrajectory = traClus.getGeneral(cluster_general);
			for(int i=0;i<rTrajectory.size();i++){
				if(rTrajectory.get(i).getPoints().size()<2){
					rTrajectory.remove(i);
					i--;
				}
			}
			//返回给service，由于是平均值求得(sweepLine)因此无法显示GPSID，taxi_id的信息，展示的信息为纬度，经度，和点的个数
		 
		}catch(Exception e){
			e.printStackTrace();
		}
	 return rTrajectory;
		 
	}

	private HashMap<Integer, ArrayList<Line>> getClusterFromCpRaw(Set<String> idSet,List<RawEntity> list) {
		HashMap<Integer, ArrayList<Line>> result = new HashMap<>();
		// 把所有从cp_raw中获取的数据按照cluster_id进行分配
		Iterator it = idSet.iterator();
		while(it.hasNext()){
			String clusterId = String.valueOf(it.next());
			List<RawEntity> list_cluster = new ArrayList<>();
			ArrayList<Line> tempLineList = new ArrayList<>();
			for(int i=0;i<list.size();i++){
				RawEntity temp = list.get(i);
				String tempClusterId = temp.getCluster_id();
				if(tempClusterId.equals(clusterId)){
					//则归在该类下面
					list_cluster.add(temp);
				}
			}
			//循环结束后,要把List<RawEntity>转化为ArrayList<Line>
			tempLineList = RawEntityToLines(Integer.valueOf(clusterId),list_cluster);
			result.put(Integer.valueOf(clusterId), tempLineList);
		}
		return result;
	}

	private ArrayList<Line> RawEntityToLines(int clusterId, List<RawEntity> list_cluster) {
		//要把List<RawEntity>转化为ArrayList<Line>
		ArrayList<Line> result = new ArrayList<>();
		List<String> taxiIdList = new ArrayList<>();
		for(int i=0;i<list_cluster.size();i++){
			//线段的划分是基于原始轨迹的，而原始轨迹以taxiid作为划分依据
			String taxiId = list_cluster.get(i).getTaxi_Id();
			if(!taxiIdList.contains(taxiId)){
				taxiIdList.add(taxiId);
			}
		}
		for(int i=0;i<taxiIdList.size();i++){
			String id = taxiIdList.get(i);
			List<RawEntity> rawList = new ArrayList<>();
			for(int j=0;j<list_cluster.size();j++){
				RawEntity entity = list_cluster.get(j);
				if(entity.getTaxi_Id().equals(id)){
					rawList.add(entity);
				}
			}
			//连成Line,例如三个点连成两条线
			for(int k=0;k<rawList.size()-1;k++){
				RawEntity raw = rawList.get(k);
				RawEntity rawNext = rawList.get(k+1);
				Line line = new Line();
				line = PointRawToLine(raw,rawNext);
				line.clusterId=clusterId;
				line.num = i;
				line.order = k;
				result.add(line);
			}
		}
		return result;
	}

	private Line PointRawToLine(RawEntity raw, RawEntity rawNext) {
		Line line = new Line();
		//将两个entity转化成为Point，然后连成line
		Point s = new Point();
		Point e = new Point();
		double s_x = Double.valueOf(raw.getLatitude());
		double s_y = Double.valueOf(raw.getLongtitude());
		double e_x = Double.valueOf(rawNext.getLatitude());
		double e_y = Double.valueOf(rawNext.getLongtitude());
		s.setX(s_x);
		s.setY(s_y);
		e.setX(e_x);
		e.setY(e_y);
		line.setS(s);
		line.setE(e);
		return line;
	}

	/*public List<Trajectory> getGeneralRTrajectory_sga(List<char_SGAEntity> data_sga, String cluster_type, String partition_type) {
		// TODO Auto-generated method stub
		ArrayList<Trajectory> rTrajectory = new ArrayList<>();
		try{
			//allTracks是partition之后的结果
			TraClus traClus = new TraClus();
		//之前的submit操作已经向cp_raw里面存入用来聚类的特征点和clusterId,从而获取类的信息
		HashMap<Integer , ArrayList<Line>> cluster_general = new HashMap<Integer, ArrayList<Line>>();
			cluster_general = getClusterFromCpRaw(idList,list);//从数据库获取cluster
			//按照获取rtrajectory的方式获取代表轨迹
			 
			rTrajectory = traClus.getGeneral(cluster_general);
			for(int i=0;i<rTrajectory.size();i++){
				if(rTrajectory.get(i).getPoints().size()<2){
					rTrajectory.remove(i);
					i--;
				}
			}
			//返回给service，由于是平均值求得(sweepLine)因此无法显示GPSID，taxi_id的信息，展示的信息为纬度，经度，和点的个数
		 
		}catch(Exception e){
			e.printStackTrace();
		}
	 return rTrajectory;
	}*/
}
