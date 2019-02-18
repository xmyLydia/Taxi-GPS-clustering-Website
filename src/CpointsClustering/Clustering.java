package CpointsClustering;

import java.io.FileWriter;
import java.io.IOException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

import com.cn.entity.CPointCluster;
import com.cn.entity.Cluster_Point;
import com.cn.entity.MopsiEntity;
import com.cn.entity.SegClustering;
import com.cn.entity.Taxi;

import CharacteristicPoints.GridClustering;
import CharacteristicPoints.GridClusteringSeg;
import CharacteristicPoints.Point;
import DataMining.Distance;
import DataMining.GetData;
import DataMining.Line;

public class Clustering {

public List<CPointCluster> InitialCluster(List<Taxi> list) {
		
		List<CPointCluster> originalClusters=new ArrayList<CPointCluster>();
        for(int i=0;i<list.size();i++){
            
            List<Taxi> tempDataPoints=new ArrayList<Taxi>();
            tempDataPoints.add(list.get(i));

            CPointCluster tempCluster=new CPointCluster();
            tempCluster.setTurningPoints(tempDataPoints);
            
            originalClusters.add(tempCluster);
        }

        return originalClusters;
	}
public double GetDistance(double aLat, double aLong, double bLat, double bLong) {
	double a, b, R;  
    R = 6378.137; // 地球半径  
    aLat = aLat * Math.PI / 180.0;  
    bLat = bLat * Math.PI / 180.0;  
	a = aLat - bLat;
	b = (aLong - bLong) * Math.PI / 180.0;
	double d;
	double sa2, sb2;
	sa2 = Math.sin(a / 2.0);
	sb2 = Math.sin(b / 2.0);
	double part1 = Math.sqrt(sa2 * sa2 + Math.cos(aLat) * Math.cos(bLat) * sb2 * sb2);
	if(part1<-1||part1>1){
	//	System.out.println("out of range"+part1);
	}
	d = 2 * R * Math.asin(Math.sqrt(sa2 * sa2 + Math.cos(aLat) * Math.cos(bLat) * sb2 * sb2));
	if(Double.isNaN(d)){
	//	System.out.println("aLat "+aLat+"bLat:"+bLat);
	//	System.out.println("aLongt "+aLong+"bLon:"+bLong);
	//	System.out.println(a+"a~ "+b+" b~"+"part1"+part1 );
	}
    return d;  
}
public double GetDistanceLines(int orderCluster,Line line1,Line line2){
	List<Taxi> list1 = LineToTaxi(line1);
	List<Taxi> list2 = LineToTaxi(line2);
	
	double result = GetDirectionSensitiveHausdorffDistance_inClustering(orderCluster,list1,list2);
	return result;
}
private double PointToLine(double x1, double y1, double x2, double y2, double x0, double y0){
	  double space = 0;
    double a, b, c;
    a = GetDistance(x1, y1, x2, y2);// 线段的长度
    b = GetDistance(x1, y1, x0, y0);// (x1,y1)到点的距离
    c = GetDistance(x2, y2, x0, y0);// (x2,y2)到点的距离
    if (c <= 0.000001 || b <= 0.000001) {
       space = 0;
       return space;
    }
    if (a <= 0.000001) {
       space = b;
       return space;
    }
    if (c * c >= a * a + b * b) {
       space = b;
       return space;
    }
    if (b * b >= a * a + c * c) {
       space = c;
       return space;
    }
    double p = (a + b + c) / 2;// 半周长
    double s = Math.sqrt(p * (p - a) * (p - b) * (p - c));// 海伦公式求面积
    space = 2 * s / a;// 返回点到线的距离（利用三角形面积公式求高）
  //  System.out.println("space:"+space+" a"+a);
    return space;
}
private double GetDirectionSensitiveHausdorffDistance_inClustering(int orderCluster, List<Taxi> TA, List<Taxi> TB) {
	double scoreAB=0, scoreBA=0, closestDistanceSumAToB = 0, closestDistanceSumBToA = 0;
	int numOfTA = TA.size();
	int TASegmentSize = numOfTA-1;
	int numOfTB = TB.size();
	int TBSegmentSize = numOfTB-1;
	int closestIndex = 0;
	//each point in A to TB
	for(int i = 0;i<numOfTA;i++){
		double closestDistance = Double.MAX_VALUE;
		Taxi taxi = TA.get(i);
		for(int j = closestIndex;j<TBSegmentSize;j++){
			double x1 = Double.parseDouble(TB.get(j).getLatitude());
			if(Double.isNaN(x1)){
				
				System.out.println("alert! the x1 is  NaN"+TB.get(j).getLatitude()+"TB SIZE:"+TB.size()+"CLOESTINDEX"+closestIndex+"the order of seg is:"+orderCluster);
			}
			double y1 = Double.parseDouble(TB.get(j).getLongtitude());
			double x2 = Double.parseDouble(TB.get(j+1).getLatitude());
			double y2 = Double.parseDouble(TB.get(j+1).getLongtitude());
			double x0 = Double.parseDouble(taxi.getLatitude());
			double y0 = Double.parseDouble(taxi.getLongtitude());
			double tempDistance = PointToLine(x1,y1,x2,y2,x0,y0);
			if(tempDistance<closestDistance){
				closestIndex = j;
				closestDistance = tempDistance;
			}
		}
		closestDistanceSumAToB = closestDistanceSumAToB + closestDistance;
	}
	scoreAB = closestDistanceSumAToB/numOfTA;
	
	//each point in B to TA
	closestIndex = 0;
	double closestDistance=-1;
	for(int i = 0;i<numOfTB;i++){
		  closestDistance = Double.MAX_VALUE;
		Taxi taxi = TB.get(i);
		for(int j = closestIndex;j<TASegmentSize;j++){
			double x1 = Double.parseDouble(TA.get(j).getLatitude());
			double y1 = Double.parseDouble(TA.get(j).getLongtitude());
			double x2 = Double.parseDouble(TA.get(j+1).getLatitude());
			double y2 = Double.parseDouble(TA.get(j+1).getLongtitude());
			double x0 = Double.parseDouble(taxi.getLatitude());
			double y0 = Double.parseDouble(taxi.getLongtitude());
			double tempDistance = PointToLine(x1,y1,x2,y2,x0,y0);
			if(tempDistance<closestDistance){
				closestIndex = j;
				closestDistance = tempDistance;
			}
		}
		closestDistanceSumBToA = closestDistanceSumBToA + closestDistance;
	}
	scoreBA = closestDistanceSumBToA/numOfTB;
	double resultData = (scoreAB+scoreBA)/2;
	
	if(Double.isInfinite(resultData)){
	System.out.println("ListA");
	System.out.println("scoreBA"+scoreBA+" closestDistanceSumBToA "+closestDistanceSumBToA+" closestDistance"+closestDistance);
	System.out.println((scoreAB+scoreBA)/2);
	}
	return (scoreAB+scoreBA)/2;
}
public List<Taxi> LineToTaxi(Line line){
	List<Taxi> list1 = new ArrayList<Taxi>();
	Taxi taxi1 = new Taxi();
	taxi1.setLatitude(String.valueOf(line.getS().x));
	taxi1.setLongtitude(String.valueOf(line.getS().y));
	list1.add(taxi1);
	Taxi taxi2 = new Taxi();
	taxi2.setLatitude(String.valueOf(line.getE().x));
	taxi2.setLongtitude(String.valueOf(line.getE().y));
	list1.add(taxi2);
	return list1;
}
public void CalculateCenter(List<CPointCluster> clusters) {
	//get each cluster
	for(int i =0;i<clusters.size();i++){
		//get list of turningpoints
		List<Taxi> turningPoints = clusters.get(i).getTurningPoints();
		double totalLatitude=0, totalLongtitude=0;
		int SizeOfTurningPoints = turningPoints.size();
		//get each turning point
		for(int j = 0;j<SizeOfTurningPoints;j++){
			totalLatitude=totalLatitude+Double.parseDouble(turningPoints.get(j).getLatitude());
			totalLongtitude=totalLongtitude+Double.parseDouble(turningPoints.get(j).getLongtitude());
		}
		clusters.get(i).setCenterLatitude(totalLatitude/SizeOfTurningPoints);
		clusters.get(i).setCenterLongtitude(totalLongtitude/SizeOfTurningPoints);
	}
	
}
public void CalculateCenterMopsi(List<CPointCluster> clusters) {
	//get each cluster
	for(int i =0;i<clusters.size();i++){
		//get list of turningpoints
		List<MopsiEntity> turningPointsMopsi = clusters.get(i).getTurningPointsMopsi();
		double totalLatitude=0, totalLongtitude=0;
		int SizeOfTurningPoints = turningPointsMopsi.size();
		//get each turning point
		for(int j = 0;j<SizeOfTurningPoints;j++){
			totalLatitude=totalLatitude+Double.parseDouble(turningPointsMopsi.get(j).getLatitude());
			totalLongtitude=totalLongtitude+Double.parseDouble(turningPointsMopsi.get(j).getLongtitude());
		}
		clusters.get(i).setCenterLatitude(totalLatitude/SizeOfTurningPoints);
		clusters.get(i).setCenterLongtitude(totalLongtitude/SizeOfTurningPoints);
	}
	
}
public void CalculateCenterSeg(List<SegClustering> clusters){
	for(int i =0;i<clusters.size();i++){
		//get list of turningpoints
		List<Line> segments = clusters.get(i).getSegments();
		
		double totalLatitudeS=0, totalLongtitudeS=0;
		double totalLatitudeE=0, totalLongtitudeE=0;
		int SizeOfSegments = segments.size();
		//get each turning point
		if(SizeOfSegments>1){
		for(int j = 0;j<SizeOfSegments;j++){
			totalLatitudeS=totalLatitudeS+segments.get(j).getS().x;
			totalLongtitudeS=totalLongtitudeS+segments.get(j).getS().y;
			totalLatitudeE=totalLatitudeE+segments.get(j).getE().x;
			totalLongtitudeE=totalLongtitudeE+segments.get(j).getE().y;
		}
		DataMining.Point centerS = new DataMining.Point();
		centerS.x = totalLatitudeS/SizeOfSegments;
		centerS.y = totalLongtitudeS/SizeOfSegments;
		DataMining.Point centerE = new DataMining.Point();
		centerE.x = totalLatitudeE/SizeOfSegments;
		centerE.y = totalLongtitudeE/SizeOfSegments;
		if(Double.isNaN(centerS.x)||Double.isNaN(centerS.y)||Double.isNaN(centerE.x)||Double.isNaN(centerE.y)){
			System.out.println("found nan");
		}
		if(Double.isInfinite(centerS.x)||Double.isInfinite(centerS.y)||Double.isInfinite(centerE.x)||Double.isInfinite(centerE.y)){
			System.out.println("found infi");
		}
		Line centerLine =new Line();
		centerLine.setS(centerS);
		centerLine.setE(centerE);
		clusters.get(i).setCenterSeg(centerLine);
		}
	}
}
public List<Taxi> DBSCANGetNeighbor(Taxi temp, List<Taxi> list, double e, int minP){
	List<Taxi> neighbor = new ArrayList<Taxi>();
	Iterator<Taxi> iter = list.iterator();
	while(iter.hasNext()){
		Taxi p = iter.next();
		double distance = GetDistance(Double.parseDouble(temp.getLatitude()), Double.parseDouble(temp.getLongtitude()),
				Double.parseDouble(p.getLatitude()), Double.parseDouble(p.getLongtitude()));
		//System.out.println("distance:"+distance);
		if(distance == 0)continue;
		if (distance < e) {
			neighbor.add(p);
		}
	}
	return neighbor;
}
public List<CPointCluster> KMeansInitial(List<Taxi> list, int num)
{
	List<CPointCluster> clusters = new ArrayList<>();
	for(int i =0;i<num;i++){
		CPointCluster tempCluster = new CPointCluster();
		tempCluster.setCenterLatitude(Double.parseDouble(list.get(i).getLatitude()));
		tempCluster.setCenterLongtitude(Double.parseDouble(list.get(i).getLongtitude()));
		clusters.add(tempCluster);
	}
	return clusters;
}
public List<CPointCluster> KMeansMopsiInitial(List<MopsiEntity> list, int num)
{//这里需要注意：初始点不能重复，比如第二个点和第三个点不可重复，lat和lon不能相等
	List<CPointCluster> clusters = new ArrayList<>();
	 
	for(int i =0;i<num;i++){
		CPointCluster tempCluster = new CPointCluster();
		System.out.println("initial lat:"+list.get(i).getLatitude()+"lon:"+list.get(i).getLongtitude());
		List<MopsiEntity> points = new ArrayList<MopsiEntity>();//用来装类内的点
		points.add(list.get(i+2));
	 
		tempCluster.setCenterLatitude(Double.parseDouble(list.get(i+2).getLatitude()));
		tempCluster.setCenterLongtitude(Double.parseDouble(list.get(i+2).getLongtitude()));
		tempCluster.setTurningPointMopsi(points);//初始的样本点
		clusters.add(tempCluster);
		 
	 
	}
	return clusters;
}
public  int[] randomCommon(int min, int max, int n){  
    if (n > (max - min + 1) || max < min) {  
           return null;  
       }  
    int[] result = new int[n];  
    int count = 0;  
    while(count < n) {  
        int num = (int) (Math.random() * (max - min)) + min;  
        boolean flag = true;  
        for (int j = 0; j < n; j++) {  
            if(num == result[j]){  
                flag = false;  
                break;  
            }  
        }  
        if(flag){  
            result[count] = num;  
            count++;  
        }  
    }  
    return result;  
}  
public List<SegClustering> KMeansInitial_seg(List<Line> list, int num)
{
	int[] randomOrder = randomCommon(0, list.size(), num);
	List<SegClustering> clusters = new ArrayList<>();
	for(int i =0;i<randomOrder.length;i++){
		SegClustering tempCluster = new SegClustering();
		int order = randomOrder[i];
		tempCluster.setCenterSeg(list.get(order));
		clusters.add(tempCluster);
	}
	return clusters;
}
public List<CPointCluster> DBScan(List<Taxi> list, double e, int minP) {
	int clusterID = 0;
	boolean allVisited = false;
	while(allVisited!=true){
		Iterator<Taxi> points = list.iterator();
		while(points.hasNext()){
			Taxi temp = points.next();
			if(temp.isVisited()) continue;;
			allVisited = false;
			temp.setVisited(true);
			List<Taxi> neighbor = DBSCANGetNeighbor(temp, list, e, minP);
			System.out.println("neighbor size:"+neighbor.size());
			if(neighbor.size()<minP){
				if(temp.getCid()<=0) temp.setCid(-1);
			}
			else{
				if(temp.getCid()<=0){
					clusterID++;
					DBSCANExpandCluster(temp, neighbor, clusterID, list, e, minP);
				}
				else{
					int id = temp.getCid();
					DBSCANExpandCluster(temp, neighbor, id, list, clusterID, minP);
				}
			}
			allVisited = true;
		}//end point
		System.out.println("clusterId:"+clusterID);
	}//end while allVisited
	System.out.println("all visited");
	
	List<CPointCluster> result = new ArrayList<>();
	//save the cluster
	for(int i = 0;i<clusterID;i++){
		result.add(new CPointCluster());
	}
	//add all the points to cluster
	for(int j = 0;j<list.size();j++){
		Taxi point = list.get(j);
		int clusterIndex = point.getCid();
		System.out.println("point——cluseterid:"+point.getCid());
		if(clusterIndex-1>=0){
		result.get(clusterIndex-1).getTurningPoints().add(point);
		}
	}
	System.out.println("number of cluster:"+result.size());
	System.out.println("begin calculate");
	//save cluster
	CalculateCenter(result);
	//for(int k =0;k<result.size();k++){
		/*int numberOfTurningPointSize = result.get(k).getTurningPoints().size();
		HierarchicalCluster cluster = result.get(k);
		cluster.setSize(numberOfTurningPointSize);
		cluster.setMethod("dbscan");
		cluster.setDivide("alllevel1start10"+"e"+e+"minP"+minP);
		hierarchicalClusterDao.save(cluster);
		for(int j = 0;j<numberOfTurningPointSize;j++){
			Cluster_Point cp = new Cluster_Point(cluster, cluster.getTurningPoints().get(j));
			//cp.setParent("101");
			clusterPointDao.save(cp);
		}*/
		//将数据加入到数据库
		//CPointCluster temp_cluster = result.get(k);
		 
	//}
	 for(int m=0;m<result.size();m++){
		 result.get(m).setId(m);
	 }
	return result;
}
public void DBSCANExpandCluster(Taxi temp, List<Taxi> neighbor, int clusterID, List<Taxi> list, double e, int minP){
	temp.setCid(clusterID);
	Iterator<Taxi> iterator = neighbor.iterator();
	while(iterator.hasNext()){
		Taxi p = iterator.next();
		if(!p.isVisited()){
			p.setVisited(true);
			List<Taxi> pNeighbor = DBSCANGetNeighbor(p, list, e, minP);
			if(pNeighbor.size()>=minP){
				Iterator<Taxi> pNeighborPoints = pNeighbor.iterator();
				while(pNeighborPoints.hasNext()){
					Taxi no = pNeighborPoints.next();
					if(no.getCid()<=0) no.setCid(clusterID);
				}
			}
		}
		if(p.getCid()<=0) p.setCid(clusterID);
	}
}
public List<SegClustering> DoGridSeg(ArrayList<Line>list,int m ,int nx,int ny){
	//ArrayList<Point> rawData = transmit(data);
	GridClusteringSeg grid_seg = new GridClusteringSeg(list, m, nx, ny);
	List<SegClustering> grid_cluster = new ArrayList<>();
	grid_seg.dataGridImage();
	int[][] seed = grid_seg.findSeeds(1,m);
	
	if(seed!=null){
	grid_seg.seedGrowing();
	grid_seg.assignClusterIDtoPoints();
	grid_cluster = grid_seg.getClusterResult();
	}
	 
	return grid_cluster;
}
public List<SegClustering> DoHierarchicalSeg(ArrayList<Line> list,int num){
	List<SegClustering> resultList = new ArrayList<>();
	List<SegClustering> originalList = InitialCluster_Agnes(list);
	resultList = originalList;
	DataMining.Distance disMeasure= new Distance();
	while (resultList.size() > num) {
		double min = Double.MAX_VALUE;
		int mergeIndexA = 0;
		int mergeIndexB = 0;
		//System.out.println("start cluster");
		for (int i = 0; i < resultList.size(); i++) {
			for (int j = 0; j < resultList.size(); j++) {
				if(i!=j){
					SegClustering clusterA = resultList.get(i);
					SegClustering clusterB = resultList.get(j);
					@SuppressWarnings("static-access")
					double tempDis = disMeasure.dist(clusterA.getCenterSeg(), clusterB.getCenterSeg());//A和B的中心线段距离
						//	GetDistance(clusterA.getCenterLatitude(), clusterA.getCenterLongtitude(), clusterB.getCenterLatitude(), clusterB.getCenterLongtitude());
					if(tempDis<min){
						min = tempDis;
						mergeIndexA = i;
						mergeIndexB = j;
					}
				}
			}
		}
		resultList=MergeClusterSeg(resultList,mergeIndexA,mergeIndexB);
		//System.out.println(resultList.size());
		if(resultList.size()==300){
			CalculateCenterSeg(resultList);
			for(int i = 0;i<300;i++)
			{
				int numberOfTurningPointSize = resultList.get(i).getTurningPoints().size();
				SegClustering cluster = resultList.get(i);
				resultList.get(i).setNumberOfCluster(300);
				resultList.get(i).setSize(numberOfTurningPointSize);
			}
		}
		if(resultList.size()==200){

			CalculateCenterSeg(resultList);
			for(int i = 0;i<200;i++)
			{
				int numberOfTurningPointSize = resultList.get(i).getTurningPoints().size();
				SegClustering cluster = resultList.get(i);
				resultList.get(i).setNumberOfCluster(200);
				resultList.get(i).setSize(numberOfTurningPointSize);
			}
		
		}
	}
	//end while loop
	CalculateCenterSeg(resultList);
	for(int i = 0;i<resultList.size();i++)
	{
		int numberOfTurningPointSize = resultList.get(i).getTurningPoints().size();
		SegClustering cluster = resultList.get(i);
		resultList.get(i).setNumberOfCluster(num);
		resultList.get(i).setSize(numberOfTurningPointSize);
	}
	 for(int m=0;m<resultList.size();m++){
		 resultList.get(m).setId(m);
		 for(int j=0;j<resultList.get(m).getSegments().size();j++){
			 resultList.get(m).getSegments().get(j).setClusterId(m);
		 }
	 }
	return resultList;
}
private List<SegClustering> InitialCluster_Agnes(ArrayList<Line> list) {
	 //将每个对象作为一个簇
	List<SegClustering> clusters = new ArrayList<>();
	for(int i =0;i<list.size();i++){
		ArrayList<Line> segs = new ArrayList<>();
		SegClustering tempCluster = new SegClustering();
		tempCluster.setCenterSeg(list.get(i));
		segs.add(list.get(i));
		tempCluster.setSegments(segs);
		clusters.add(tempCluster);
	}
	return clusters;
}
 
@SuppressWarnings("static-access")
public List<SegClustering> DoKmeansSeg(ArrayList<Line> list,int numOfCluster,int iteratorTime){
	System.out.println("kmeans_clustering");
	for(int i=0;i<list.size();i++){
		Line temp = list.get(i);
		if(temp.getE()==null||temp.getS()==null){
			System.out.println("the line appear null"+i);
		}
		if(temp.getE().x==temp.getS().x&&temp.getE().y==temp.getS().y){
			list.remove(i);//如果线段出现起点和终点一样的情况就丢掉
			i--;
		}
	}
	//对线段进行聚类
	int pointSize = list.size();
	List<SegClustering> clusters = KMeansInitial_seg(list, numOfCluster);
	int iterator = 0;
	DataMining.Distance disMeasure = new Distance();
	while(iterator<iteratorTime){
		
		for (int k = 0; k < clusters.size(); k++){
			//clusters.get(k).setTurningPoints(new ArrayList<Taxi>());
			clusters.get(k).setSegments(new ArrayList<Line>());
		}
	
		for (int i=0;i<pointSize;i++){
			Line temp_line = list.get(i);
			double minDistance =  Double.MAX_VALUE;
			int indexOfCluster =0;
			for (int j = 0; j < numOfCluster; j++) {
				SegClustering tempCluster = clusters.get(j);
				double distance = disMeasure.dist(temp_line, tempCluster.getCenterSeg());
				if(Double.isInfinite(distance)||Double.isNaN(distance)){
					  distance = disMeasure.disForTest(temp_line, tempCluster.getCenterSeg());
					System.out.println("I found the ");
				}
				if(distance<minDistance){
					minDistance = distance;
					indexOfCluster = j;
				}
			}//end of cluster
			clusters.get(indexOfCluster).getSegments().add(temp_line);//将每条线段都加入到离它最近的簇中
			 
		}//end of point
		//calculate new center
		CalculateCenterSeg(clusters); //重新计算中心线段
		iterator++;
		//System.out.println(iterator+"次");
	}
	CalculateCenterSeg(clusters);
    //在这里设置clusterId
	 for(int m=0;m<clusters.size();m++){
		 clusters.get(m).setId(m);
		 for(int j=0;j<clusters.get(m).getSegments().size();j++){
			 clusters.get(m).getSegments().get(j).setClusterId(m);
		 }
	 }
	return clusters;
}
public List<SegClustering> SetMopsiCluster(ArrayList<Line> list,List<String> idList){
 //这里关键是：clusterId以及segments
	//对线段进行聚类
 
	List<SegClustering> clusters = new ArrayList<SegClustering>();
	//CalculateCenterSeg(clusters);
    //在这里设置clusterId和segments
	 for(int m=0;m<idList.size();m++){
		 String cluster_id = idList.get(m);//获取cluster_id
		 List<Line> linelist = new ArrayList<Line>();
		 SegClustering segTemp = new SegClustering();//现在已有的Line：它里面的mopsiId就是taxi_id
		// clusters.get(m).setId(m);
		 for(int j=0;j<list.size();j++){
		//	 clusters.get(m).getSegments().get(j).setClusterId(m);
			// 这里要把每条line根据其对应的clusterid 进行匹配，而且segClustering要不断生成，然后add到clusters
			 Line temp = list.get(j) ;
			 if(temp.getMopsiId().equals(cluster_id)) {
				 linelist.add(temp);
				 list.remove(j);
				 j--;
			 }
		 }
		 //遍历一遍之后即可得到一个cluster的相关内容
		 segTemp.setSegments(linelist);//设置线段
		 int id = Integer.valueOf(cluster_id);//嘤嘤嘤，设置cluster_id可是我不想用Int类型啊
		 segTemp.setId(id);//id从1到10
		 //加入cluster
		 clusters.add(segTemp);
	 }
	return clusters;
}
public List<CPointCluster> DoKMeans(List<Taxi> list, int numOfCluster, int iteratorTime) {
	int pointSize = list.size();
	List<CPointCluster> clusters = KMeansInitial(list, numOfCluster);
	int iterator = 0;
	while(iterator<iteratorTime){
		
		for (int k = 0; k < clusters.size(); k++)
			clusters.get(k).setTurningPoints(new ArrayList<Taxi>());

		for (int i=0;i<pointSize;i++){
			double minDistance=Double.MAX_VALUE;
			int indexOfCluster = 0;
			Taxi tempPoint = list.get(i);
			for (int j = 0; j < numOfCluster; j++) {
				CPointCluster tempCluster = clusters.get(j);
				double distance = GetDistance(Double.parseDouble(tempPoint.getLatitude()),
						Double.parseDouble(tempPoint.getLongtitude()), tempCluster.getCenterLatitude(),
						tempCluster.getCenterLongtitude());
				if(distance<minDistance){
					minDistance = distance;
					indexOfCluster = j;
				}
			}//end of cluster
			clusters.get(indexOfCluster).getTurningPoints().add(tempPoint);
		}//end of point
		//calculate new center
		CalculateCenter(clusters); 
		iterator++;
		//System.out.println(iterator+"次");
	}
	CalculateCenter(clusters);
    //在这里设置clusterId
	 for(int m=0;m<clusters.size();m++){
		 clusters.get(m).setId(m);
	 }
	return clusters;
}
public List<CPointCluster> DoKMeansMopsi(List<MopsiEntity> list, int numOfCluster, int iteratorTime) {
	int pointSize = list.size();
	List<CPointCluster> clusters = KMeansMopsiInitial(list, numOfCluster);
	int iterator = 0;
	while(iterator<iteratorTime){
		
		for (int k = 0; k < clusters.size(); k++) {
			clusters.get(k).setTurningPointMopsi(new ArrayList<MopsiEntity>());
		}
		for (int i=0;i<pointSize;i++){
			double minDistance=Double.MAX_VALUE;
			int indexOfCluster = 0;
			MopsiEntity tempPoint = list.get(i);
			for (int j = 0; j < numOfCluster; j++) {//遍历每个类，找到最近的类
				CPointCluster tempCluster = clusters.get(j);
				double distance = GetDistance(Double.parseDouble(tempPoint.getLatitude()),
						Double.parseDouble(tempPoint.getLongtitude()), tempCluster.getCenterLatitude(),
						tempCluster.getCenterLongtitude());
				if(distance<minDistance){
					minDistance = distance;
					indexOfCluster = j;
				}
			}//end of cluster
			clusters.get(indexOfCluster).getTurningPointsMopsi().add(tempPoint);
		}//end of point
		//calculate new center
		CalculateCenterMopsi(clusters); 
		iterator++;
	}
	CalculateCenterMopsi(clusters);
    //在这里设置clusterId
	 for(int m=0;m<clusters.size();m++){
		 System.out.println(m+";"+clusters.get(m).getCenterLatitude());
		 clusters.get(m).setId(m);
	 }
	return clusters;
}
public List<CPointCluster> MergeCluster(List<CPointCluster> resultList, int index1, int index2) {
	if(index1 !=index2){
		//System.out.println("index1:"+index1+"  index2:"+index2);
		CPointCluster cluster1 = resultList.get(index1);
		CPointCluster cluster2 = resultList.get(index2);
		List<Taxi> dataSet1 = cluster1.getTurningPoints();
		List<Taxi> dataSet2 = cluster2.getTurningPoints();
		for(int i = 0;i<dataSet2.size();i++){
			dataSet1.add(dataSet2.get(i));
		}
		cluster1.setTurningPoints(dataSet1);
		resultList.remove(index2);
	}
	return resultList;
}
public List<SegClustering> MergeClusterSeg(List<SegClustering> resultList, int index1, int index2) {
	if(index1 !=index2){
		//System.out.println("index1:"+index1+"  index2:"+index2);
		SegClustering cluster1 = resultList.get(index1);
		SegClustering cluster2 = resultList.get(index2);
		//List<Taxi> dataSet1 = cluster1.getTurningPoints();
		//List<Taxi> dataSet2 = cluster2.getTurningPoints();
		List<Line> seg1 = cluster1.getSegments();
		List<Line> seg2 = cluster2.getSegments();
		for(int i = 0;i<seg2.size();i++){
			if(!seg1.contains(seg2.get(i))){
				seg1.add(seg2.get(i));//合并
			}
		}
		//cluster1.setTurningPoints(dataSet1);
		cluster1.setSegments(seg1);
		resultList.remove(index2);
	}
	return resultList;
}
	public List<CPointCluster> DoHierarchicalCluster(List<Taxi> list, int num) {
		//AGNES算法
		System.out.println(num);
		List<CPointCluster> resultList = new ArrayList<>();
		List<CPointCluster> originalList = InitialCluster(list);
		resultList = originalList;
		CalculateCenter(resultList);
		while (resultList.size() > num) {
			double min = Double.MAX_VALUE;
			int mergeIndexA = 0;
			int mergeIndexB = 0;
			//System.out.println("start cluster");
			for (int i = 0; i < resultList.size(); i++) {
				for (int j = 0; j < resultList.size(); j++) {
					if(i!=j){
						CPointCluster clusterA = resultList.get(i);
						CPointCluster clusterB = resultList.get(j);
						double tempDis = GetDistance(clusterA.getCenterLatitude(), clusterA.getCenterLongtitude(), clusterB.getCenterLatitude(), clusterB.getCenterLongtitude());
						if(tempDis<min){
							min = tempDis;
							mergeIndexA = i;
							mergeIndexB = j;
						}
					}
				}
			}
			resultList=MergeCluster(resultList,mergeIndexA,mergeIndexB);
		//	System.out.println(resultList.size());
			if(resultList.size()==300){
				CalculateCenter(resultList);
				for(int i = 0;i<300;i++)
				{
					int numberOfTurningPointSize = resultList.get(i).getTurningPoints().size();
					CPointCluster cluster = resultList.get(i);
					resultList.get(i).setNumberOfCluster(300);
					resultList.get(i).setSize(numberOfTurningPointSize);
				//	resultList.get(i).setDivide("alllevel1");
				//	resultList.get(i).setMethod("HierarchicalCluster");
					//hierarchicalClusterDao.save(cluster);//这里要修改
				/*	for(int j = 0;j<numberOfTurningPointSize;j++){
						CPoint cp = new Cluster_Point(cluster, cluster.getTurningPoints().get(j));
						//cp.setParent("101");
						clusterPointDao.save(cp);//这里也要修改
					}*/
				}
			}
			if(resultList.size()==200){

				CalculateCenter(resultList);
				for(int i = 0;i<200;i++)
				{
					int numberOfTurningPointSize = resultList.get(i).getTurningPoints().size();
					CPointCluster cluster = resultList.get(i);
					resultList.get(i).setNumberOfCluster(200);
					resultList.get(i).setSize(numberOfTurningPointSize);
				//	resultList.get(i).setDivide("alllevel1");
				//	resultList.get(i).setMethod("HierarchicalCluster");
				//	hierarchicalClusterDao.save(cluster);
				/*	for(int j = 0;j<numberOfTurningPointSize;j++){
						Cluster_Point cp = new Cluster_Point(cluster, cluster.getTurningPoints().get(j));
						//cp.setParent("101");
						clusterPointDao.save(cp);
					}
					*/
				}
			
			}
		}
		//end while loop
		CalculateCenter(resultList);
		for(int i = 0;i<resultList.size();i++)
		{
			int numberOfTurningPointSize = resultList.get(i).getTurningPoints().size();
			CPointCluster cluster = resultList.get(i);
			resultList.get(i).setNumberOfCluster(num);
			resultList.get(i).setSize(numberOfTurningPointSize);
			//resultList.get(i).setDivide("alllevel1");
			//resultList.get(i).setMethod("HierarchicalCluster");
			//hierarchicalClusterDao.save(cluster);
			/*for(int j = 0;j<numberOfTurningPointSize;j++){
				Cluster_Point cp = new Cluster_Point(cluster, cluster.getTurningPoints().get(j));
				//cp.setParent("101");
				clusterPointDao.save(cp);
			}*/
		}
		 for(int m=0;m<resultList.size();m++){
			 resultList.get(m).setId(m);
		 }
		return resultList;
	}
	public List<CPointCluster> Final_Kmeans(ArrayList<Line> segments, List<Taxi>data,String partition_type,String cluster_type,int k,int iterator) throws ClassNotFoundException, SQLException, IOException{
		//根据传入的参数进行聚类，并把聚类结果存入cluster_result
		 Clustering cluster = new Clustering();
	//	 GetData gt = new GetData();
	//	 gt.DbStoreHelper();
		 System.out.println("this is clustering: the partition_type is:"+partition_type);
		  
		// List<Taxi>data = gt.getCharacterResult(partition_type);//得到的数据为traclus提取的特征点
		 long startTime = System.currentTimeMillis();    //获取开始时间 
		 List<CPointCluster> kmeans_cluster = cluster.DoKMeans(data,k,iterator);
		 long endTime = System.currentTimeMillis();    //获取结束时间
		 System.out.println("程序运行时间：" + (endTime - startTime) + "ms");    //输出程序运行时间
		 //计算CH值
		// getCH_valForPoint(kmeans_cluster);
	   //  getCH_val(segments,k,iterator);
		 System.out.println("segments size is:"+segments.size());
		return kmeans_cluster;
	}
	public List<CPointCluster> Final_KmeansMopsi( List<MopsiEntity>data,int k,int iterator) throws ClassNotFoundException, SQLException, IOException{
		//根据传入的参数进行聚类，并把聚类结果存入cluster_result
		 Clustering cluster = new Clustering();
		 List<CPointCluster> kmeans_cluster = cluster.DoKMeansMopsi(data,k,iterator);
	     return kmeans_cluster;
	}
	public void  getCH_val_Grid(ArrayList<Line> list , int m, int nx, int ny) throws IOException{
		 Clustering cluster = new Clustering();
		 //data转换成List<Line>
		//计算CH值并进行实验
		 double CH = -1;
		 FileWriter fw = null;
	     fw = new FileWriter("G://data/Experiments/Grid_n.txt",true);
	  //  num=13;
	     for(int i=0;i<list.size();i++){
	 		Line temp = list.get(i);
	 		if(temp.getE()==null||temp.getS()==null){
	 			System.out.println("the line appear null"+i);
	 		}
	 		if(temp.getE().x==temp.getS().x&&temp.getE().y==temp.getS().y){
	 			list.remove(i);//如果线段出现起点和终点一样的情况就丢掉
	 			i--;
	 		}
	 	}
	    // System.out.println("the size of lines is "+list.size());
	     //line size is 1390
	    for(int a=1;a<100;a++){
	    	 m = a;//对迭代次数进行实验
		List<SegClustering> GridSegCluster = cluster.DoGridSeg(list, m,nx,ny);
		if(GridSegCluster!=null&&GridSegCluster.size()<1){
		double numerator_above = 0;
		double numerator =0;
		double denominator = 0;
		
		//System.out.println(kmeansSegCluster.size());
		for(int i=0;i<GridSegCluster.size();i++){
			SegClustering cluster_temp = GridSegCluster.get(i);
			int nk = cluster_temp.getSegments().size();//聚类的轨迹个数
			Line center_line_cluster = cluster_temp.getCenterSeg();//聚类的中心线段
			Line center_all = getCenterAllSegs(list);//所有线段的中心线段
			double dis = GetDistanceLines(i,center_line_cluster,center_all);//上面两条线段的距离：豪斯多夫
			numerator_above+=dis*dis*nk;
		}
		numerator = numerator_above/( GridSegCluster.size());//分子
		double denominator_above = 0;
		for(int i=0;i< GridSegCluster.size();i++){
			SegClustering clusterTemp =  GridSegCluster.get(i);
			Line centerClusterLine = clusterTemp.getCenterSeg();//每个聚类内的中心线段
			List<Line> segs = clusterTemp.getSegments();
			double clusterDis = 0 ;
			for(int j=0;j<segs.size();j++){
				Line line = segs.get(j);
				double dis = GetDistanceLines(i,centerClusterLine,line);
				clusterDis+= dis*dis;
			}
			denominator_above +=clusterDis;
		}
	    denominator = denominator_above/(list.size()- GridSegCluster.size());
	     CH = numerator/denominator;
		}else{
			 CH = -1;
		}
	    System.out.println(CH);
	     try {
            String c =  a+" "+CH+"\r\n";
            fw.write(c);
        } catch (IOException e1) {
            e1.printStackTrace();
            System.out.println("写入失败");
            System.exit(-1);
        } 
	     System.out.println("the order is:"+a);
	      }
	    fw.close();
	}
	public void getCH_val_hierarchical(ArrayList<Line> list,int num) throws IOException{
		 Clustering cluster = new Clustering();
		 //data转换成List<Line>
		//计算CH值并进行实验
		 FileWriter fw = null;
	     fw = new FileWriter("G://data/Experiments/Agnes_n_traclus_10_new1.txt",true);
	  //  num=13;
	     for(int i=0;i<list.size();i++){
	 		Line temp = list.get(i);
	 		if(temp.getE()==null||temp.getS()==null){
	 			System.out.println("the line appear null"+i);
	 		}
	 		if(temp.getE().x==temp.getS().x&&temp.getE().y==temp.getS().y){
	 			list.remove(i);//如果线段出现起点和终点一样的情况就丢掉
	 			i--;
	 		}
	 	}
	    // System.out.println("the size of lines is "+list.size());
	    for(int m=1;m<161;m++){
	    	 num = m;//对迭代次数进行实验
		List<SegClustering> AgnesSegCluster = cluster.DoHierarchicalSeg(list, num );
		double numerator_above = 0;
		double numerator =0;
		double denominator = 0;
		//System.out.println(kmeansSegCluster.size());
		for(int i=0;i<AgnesSegCluster.size();i++){
			SegClustering cluster_temp = AgnesSegCluster.get(i);
			int nk = cluster_temp.getSegments().size();//聚类的轨迹个数
			Line center_line_cluster = cluster_temp.getCenterSeg();//聚类的中心线段
			Line center_all = getCenterAllSegs(list);//所有线段的中心线段
			double dis = GetDistanceLines(i,center_line_cluster,center_all);//上面两条线段的距离：豪斯多夫
			numerator_above+=dis*dis*nk;
		}
		numerator = numerator_above/(AgnesSegCluster.size());//分子
		double denominator_above = 0;
		for(int i=0;i<AgnesSegCluster.size();i++){
			SegClustering clusterTemp = AgnesSegCluster.get(i);
			Line centerClusterLine = clusterTemp.getCenterSeg();//每个聚类内的中心线段
			List<Line> segs = clusterTemp.getSegments();
			double clusterDis = 0 ;
			for(int j=0;j<segs.size();j++){
				Line line = segs.get(j);
				double dis = GetDistanceLines(i,centerClusterLine,line);
				clusterDis+= dis*dis;
			}
			denominator_above +=clusterDis;
		}
	    denominator = denominator_above/(list.size()-AgnesSegCluster.size());
	    double CH = numerator/denominator;
	 //  System.out.println(CH);
	     try {
            String c =  m+" "+CH+"\r\n";
            fw.write(c);
        } catch (IOException e1) {
            e1.printStackTrace();
            System.out.println("写入失败");
            System.exit(-1);
        } 
	     System.out.println("the order is:"+m);
	      }
	    fw.close();
	}
	 public void getCH_valForPoint(List<CPointCluster> kmeans_cluster) throws IOException{
		 //data转换成List<Line>
		//计算CH值并进行实验
		 FileWriter fw = null;
	     fw = new FileWriter("G://data/Experiments/KmeansPoint.txt",true);
	     int  iterator = 2000;//对迭代次数设置为2000
	     
	  //for(int k=1;k<1391;k=k+1){
		double numerator_above = 0;
		double numerator =0;
		double denominator = 0;
		//System.out.println(kmeansSegCluster.size());
		for(int i=0;i<kmeans_cluster.size();i++){
			CPointCluster cluster_temp = kmeans_cluster.get(i);
			int nk = cluster_temp.getTurningPoints().size();//聚类的点个数
			Point center_point = new Point(cluster_temp.getCenterLatitude(),cluster_temp.getCenterLongtitude());//聚类的中心点
			DataMining.Point center_all =  getCenterPointForAll(kmeans_cluster);//所有点的中心点
			double dis = GetDistance(center_point.x,center_point.y,center_all.x,center_all.y);
			numerator_above+=dis*dis*nk;
		}
		numerator = numerator_above/(kmeans_cluster.size());//分子
		double denominator_above = 0;
		int countPoints = 0;
		for(int i=0;i<kmeans_cluster.size();i++){
			CPointCluster cluster_temp = kmeans_cluster.get(i);
			Point center_point = new Point(cluster_temp.getCenterLatitude(),cluster_temp.getCenterLongtitude());//聚类的中心点
			List<Taxi> points = cluster_temp.getTurningPoints();//类内点
			countPoints+=points.size();//计数所有点数
			double clusterDis = 0 ;
			for(int j=0;j<points.size();j++){
				Taxi tempPoint = points.get(j);
				double dis = GetDistance(center_point.x,center_point.y,Double.valueOf(tempPoint.getLatitude()),Double.valueOf(tempPoint.getLongtitude()));
				clusterDis+= dis*dis;
			}
			denominator_above +=clusterDis;
		}
	    denominator = denominator_above/(countPoints-kmeans_cluster.size());
	    double CH = numerator/denominator;
	   System.out.println("CH val is:"+CH+" and cluster size is "+kmeans_cluster.size());
	/*     try {
            String c =  k+" "+CH+"\r\n";
            fw.write(c);
        } catch (IOException e1) {
            e1.printStackTrace();
            System.out.println("写入失败");
            System.exit(-1);
        } */
	  //   System.out.println("k value is:"+k );
	       
	//}
	    fw.close();
	 }
	private DataMining.Point getCenterPointForAll(List<CPointCluster> kmeans_cluster) {
		// 计算所有点的中心点
		double totalLatitude=0, totalLongtitude=0;
		int countSize = 0;
		for(int i =0;i<kmeans_cluster.size();i++){
			//get list of turningpoints
			CPointCluster cluster_temp = kmeans_cluster.get(i);
		    List<Taxi> points =  cluster_temp.getTurningPoints();
			int SizeOfPoints = points.size();
			countSize+=SizeOfPoints;
			//get each turning point
			for(int j = 0;j<SizeOfPoints;j++){
				totalLatitude=totalLatitude+Double.parseDouble(points.get(j).getLatitude());
				totalLongtitude=totalLongtitude+Double.parseDouble(points.get(j).getLongtitude());
			}
		}
		double center_x = (totalLatitude/countSize);
		double center_y= (totalLongtitude/countSize);
		DataMining.Point centerPoint = new DataMining.Point();
		centerPoint.x = center_x;
		centerPoint.y = center_y;
		return centerPoint;
	}
	public void getCH_val(ArrayList<Line> list,int num,int iterator) throws IOException{
		 Clustering cluster = new Clustering();
		 //data转换成List<Line>
		//计算CH值并进行实验
		 FileWriter fw = null;
	     fw = new FileWriter("G://data/Experiments/Kmeans_k&iterator_100SGA1.txt",true);
	    num=13;
	    iterator = 1000;
	     for(int k=1;k<1391;k=k+1){
	    	 num=k;
	     for(int m=100;m<2000;m=m+100){
	    	  iterator = m;//对迭代次数进行实验
	    	//num=m;
		List<SegClustering> kmeansSegCluster = cluster.DoKmeansSeg(list, num , iterator );
		double numerator_above = 0;
		double numerator =0;
		double denominator = 0;
		//System.out.println(kmeansSegCluster.size());
		for(int i=0;i<kmeansSegCluster.size();i++){
			SegClustering cluster_temp = kmeansSegCluster.get(i);
			int nk = cluster_temp.getSegments().size();//聚类的轨迹个数
			Line center_line_cluster = cluster_temp.getCenterSeg();//聚类的中心线段
			Line center_all = getCenterAllSegs(list);//所有线段的中心线段
			double dis = GetDistanceLines(i,center_line_cluster,center_all);//上面两条线段的距离：豪斯多夫
			numerator_above+=dis*dis*nk;
		}
		numerator = numerator_above/(kmeansSegCluster.size());//分子
		double denominator_above = 0;
		for(int i=0;i<kmeansSegCluster.size();i++){
			SegClustering clusterTemp = kmeansSegCluster.get(i);
			Line centerClusterLine = clusterTemp.getCenterSeg();//每个聚类内的中心线段
			List<Line> segs = clusterTemp.getSegments();
			double clusterDis = 0 ;
			for(int j=0;j<segs.size();j++){
				Line line = segs.get(j);
				double dis = GetDistanceLines(i,centerClusterLine,line);
				clusterDis+= dis*dis;
			}
			denominator_above +=clusterDis;
		}
	    denominator = denominator_above/(list.size()-kmeansSegCluster.size());
	    double CH = numerator/denominator;
	   System.out.println(CH);
	     try {
            String c =  k+" "+m+" "+CH+"\r\n";
            fw.write(c);
        } catch (IOException e1) {
            e1.printStackTrace();
            System.out.println("写入失败");
            System.exit(-1);
        } 
	     System.out.println("k value is:"+k+" iterator is:"+m);
	      }
	}
	    fw.close();
	}
	public Line getCenterAllSegs(ArrayList<Line> list) {
		//获取所有轨迹的中心轨迹
		Line center = new Line();
		double allS_X=0;
		double allS_Y = 0;
		double allE_X = 0;
		double allE_Y = 0;
	for(int i=0;i<list.size();i++){
		//求所有轨迹的S点的avg和E点的avg
		Line temp = list.get(i);
		DataMining.Point S = temp.getS();
		DataMining.Point E = temp.getE();
		allS_X += S.x;
		allS_Y +=S.y;
		allE_X +=E.x;
		allE_Y +=E.y;
	}
	double centerS_X = allS_X/list.size();
	double centerS_Y = allS_Y/list.size();
	double centerE_X = allE_X/list.size();
	double centerE_Y = allE_Y/list.size();
	DataMining.Point CenterS = new DataMining.Point();
	CenterS.x = centerS_X;
	CenterS.y = centerS_Y;
	DataMining.Point CenterE = new DataMining.Point();
	CenterE.x = centerE_X;
	CenterE.y = centerE_Y;
	center.setS(CenterS);
	center.setE(CenterE);
	return center;
	}
	public List<CPointCluster> Final_Dbscan(List<Taxi> data2, String partition_type,String cluster_type, double eps, int minlns) throws ClassNotFoundException, SQLException, IOException {
		/* Clustering cluster = new Clustering();
		 GetData gt = new GetData();
		 gt.DbStoreHelper();
		 System.out.println("this is clustering: the partition_type is:"+partition_type);
		 List<Taxi>data = gt.getCharacterResult(partition_type);//得到的数据为traclus提取的特征点
		 List<CPointCluster> dbscan_cluster = cluster.DBScan(data,eps,minlns);
		 gt.AddIntoCluster(dbscan_cluster,cluster_type,partition_type);//将聚类中心点加入cluster_result
		 gt.AddCluster_rawPoint(dbscan_cluster,cluster_type,partition_type);//将raw point加入cluster_raw
		*/
		 Clustering cluster = new Clustering();
		// GetData gt = new GetData();
		// gt.DbStoreHelper();
		 System.out.println("this is clustering: the partition_type is:"+partition_type);
		 long startTime = System.currentTimeMillis();    //获取开始时间 
		 List<CPointCluster> Dbscan_cluster = cluster.DBScan(data2, eps, minlns);
		 long endTime = System.currentTimeMillis();    //获取结束时间
		 System.out.println("程序运行时间：" + (endTime - startTime) + "ms");    //输出程序运行时间
		// List<Taxi>data = gt.getCharacterResult(partition_type);//得到的数据为traclus提取的特征点
		// getCH_valForPoint(Dbscan_cluster);
		// gt.AddIntoCluster(kmeans_cluster,cluster_type,partition_type);//将聚类中心点加入cluster_result
		// gt.AddCluster_rawPoint(kmeans_cluster,cluster_type,partition_type);//将raw point加入cluster_raw
		return Dbscan_cluster;
	}
	public List<CPointCluster> Final_Hierarchical(ArrayList<Line> lineList, List<Taxi> data2, String partition_type,String cluster_type, int n) throws IOException {
		// 参数为：partition_type, clustering_type,n
		
		 Clustering cluster = new Clustering();
		 long startTime = System.currentTimeMillis();    //获取开始时间 
		 List<CPointCluster> agnes_cluster = cluster.DoHierarchicalCluster(data2, n);
		 long endTime = System.currentTimeMillis();    //获取结束时间
		 System.out.println("程序运行时间：" + (endTime - startTime) + "ms");    //输出程序运行时间
		 // getCH_valForPoint(agnes_cluster);
		// getCH_val_hierarchical(lineList,n);
		return agnes_cluster;
	}
	private ArrayList<Point> transmit(List<Taxi> data) {
		// 把List<List<Taxi>>转化为point
         ArrayList<Point> points = new ArrayList<Point>();
         
         for(int i=0;i<data.size();i++){
        	 Point temp = new Point();
        	 temp.setGPS_ID(data.get(i).getGPS_Id());
        	 temp.setTaxi_id(data.get(i).getTaxi_Id());
        	 temp.setX(Double.valueOf(data.get(i).getLatitude()));
        	 temp.setY(Double.valueOf(data.get(i).getLongtitude()));
        	 temp.setSpeed(data.get(i).getSpeed());
        	 temp.setAngle(data.get(i).getAngle());
        	 temp.setTime(data.get(i).getTime());
        	 temp.setState(data.get(i).getState());
        	 points.add(temp);
         }
		return points;
	}
	private List<CPointCluster> GridClustering(List<Taxi> data, int m, int nx, int ny) {
		ArrayList<Point> rawData = transmit(data);
		GridClustering grid = new GridClustering(rawData, 30, 12, 12);
		grid.dataGridImage();
		grid.findSeeds(1);
		grid.seedGrowing();
		grid.assignClusterIDtoPoints();
		 
		List<CPointCluster> grid_cluster = grid.getClusterResult();
		return grid_cluster;
		 
	}
	public List<CPointCluster> Final_Grid(ArrayList<Line> lineList, List<Taxi> data2, String partition_type,String cluster_type, int m, int nx, int ny) throws IOException {
		 Clustering cluster = new Clustering();
		// GetData gt = new GetData();
		// gt.DbStoreHelper();
		 System.out.println("this is clustering: the partition_type is:"+partition_type);
		 long startTime = System.currentTimeMillis();    //获取开始时间 
		 List<CPointCluster> grid_cluster = cluster.GridClustering(data2,m,nx,ny);
		 long endTime = System.currentTimeMillis();    //获取结束时间
		 System.out.println("程序运行时间：" + (endTime - startTime) + "ms");    //输出程序运行时间
	//	 getCH_valForPoint(grid_cluster);
		// getCH_val_Grid(lineList ,m,nx,ny);
		return grid_cluster;
	}

	/*public static void main(String[] args) throws ClassNotFoundException, SQLException {
		// TODO Auto-generated method stub
		 Clustering cluster = new Clustering();
		 GetData gt = new GetData();
		 gt.DbStoreHelper();
		 String cp_type = "SGA";
		 List<Taxi>data = gt.getCharacterResult(cp_type);//得到的数据为traclus提取的特征点
		//List<CPointCluster> kmeans_cluster = cluster.DoKMeans(data,5,100);
		 //List<CPointCluster> dbscan_cluster = cluster.DBScan(data, 8, 3);//data,eps,minpoints
		 List<CPointCluster> agnes_cluster = cluster.DoHierarchicalCluster(data, 6);//data,eps,minpoints
		//存入数据库
		 String cluster_type="agnes";//聚类方法：有DBSCAN,Grid,等等
		 
		//gt.AddIntoCluster(kmeans_cluster,cluster_type,cp_type);//将聚类中心点加入cluster_result
		//gt.AddCluster_rawPoint(kmeans_cluster,cluster_type,cp_type);//将raw point加入cluster_raw
		//  gt.AddIntoCluster(dbscan_cluster,cluster_type,cp_type);//将聚类中心点加入cluster_result
		//  gt.AddCluster_rawPoint(dbscan_cluster,cluster_type,cp_type);//将raw point加入cluster_raw
		  gt.AddIntoCluster(agnes_cluster,cluster_type,cp_type);//将聚类中心点加入cluster_result
	      gt.AddCluster_rawPoint(agnes_cluster,cluster_type,cp_type);//将raw point加入cluster_raw
	}*/
	public boolean CHECK(List<CPointCluster> result) {
		// 判断Kmeans结果是否准确
		double dis;
		ArrayList<Double> distance = new ArrayList<>();
		for(int i=0;i<result.size();i++){
			CPointCluster cluster = result.get(i);
			List<Taxi> list = cluster.getTurningPoints();
			
			int clusterid = cluster.getId();
		 
			for(int j=0;j<list.size();j++){
				Taxi temp = list.get(j);
				double lat = Double.parseDouble(temp.getLatitude());
				double lon = Double.parseDouble(temp.getLongtitude());
				ArrayList<Double> check = new ArrayList<>();
				for(int k=0;k<result.size();k++){
					double x = result.get(k).getCenterLatitude();
					double y = result.get(k).getCenterLongtitude();
					int id = result.get(k).getId();
					dis = (x-lat)*(x-lat)+(y-lon)*(y-lon);
					System.out.print("dis "+dis+" id"+id+ " ");
					check.add(dis);
				}
				String str=null;
				double minreal = Collections.min(check);
				if(minreal==check.get(clusterid)){
					str = "true";
				}
				System.out.println("original_id"+clusterid+"  cloest on:"+str);
			//	distance.add(dis);
			 
			}
	}
		
	 
	
	return true;
	
}
 
	public static void main(String args[]) throws ClassNotFoundException, SQLException{
		Clustering c = new Clustering();
	//	c.CHECK(c.Final_Kmeans("traclus","kmeans",5,100));
	}
	public List<MopsiEntity> PutAllRoutesToList(List<List<MopsiEntity>> data_routes) {
		List<MopsiEntity> result = new ArrayList<MopsiEntity>();
		List<MopsiEntity> temp = new ArrayList<MopsiEntity>();
		for(int i=0;i<data_routes.size();i++) {
			temp = data_routes.get(i);
			result.addAll(temp);
		}
		
		return result;
	}
	
}
