package com.cn.service;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import javax.annotation.Resource;

import org.springframework.stereotype.Service;

import com.cn.dao.ClusterPointDao;
import com.cn.dao.HierarchicalClusterDao;
import com.cn.entity.Cluster_Point;
import com.cn.entity.HierarchicalCluster;
import com.cn.entity.Taxi;
import com.cn.entity.TurningPoint;

@Service("hierarchicalClusterService")
public class HierarchicalClusterServiceImpl implements HierarchicalClusterService{

	private HierarchicalClusterDao hierarchicalClusterDao;
	
	private ClusterPointDao clusterPointDao;
	
	public ClusterPointDao getClusterPointDao() {
		return clusterPointDao;
	}

	@Resource
	public void setClusterPointDao(ClusterPointDao clusterPointDao) {
		this.clusterPointDao = clusterPointDao;
	}

	public HierarchicalClusterDao getHierarchicalClusterDao() {
		return hierarchicalClusterDao;
	}

	@Resource
	public void setHierarchicalClusterDao(HierarchicalClusterDao hierarchicalClusterDao) {
		this.hierarchicalClusterDao = hierarchicalClusterDao;
	}

	@Override
	public List<HierarchicalCluster> DoHierarchicalCluster(List<TurningPoint> list, int num) {
		System.out.println(num);
		List<HierarchicalCluster> resultList = new ArrayList<>();
		List<HierarchicalCluster> originalList = InitialCluster(list);
		resultList = originalList;
		CalculateCenter(resultList);
		while (resultList.size() > num) {
			double min = Double.MAX_VALUE;
			int mergeIndexA = 0;
			int mergeIndexB = 0;
			System.out.println("start cluster");
			for (int i = 0; i < resultList.size(); i++) {
				for (int j = 0; j < resultList.size(); j++) {
					if(i!=j){
						HierarchicalCluster clusterA = resultList.get(i);
						HierarchicalCluster clusterB = resultList.get(j);
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
			System.out.println(resultList.size());
			if(resultList.size()==300){
				CalculateCenter(resultList);
				for(int i = 0;i<300;i++)
				{
					int numberOfTurningPointSize = resultList.get(i).getTurningPoints().size();
					HierarchicalCluster cluster = resultList.get(i);
					resultList.get(i).setNumberOfCluster(300);
					resultList.get(i).setSize(numberOfTurningPointSize);
					resultList.get(i).setDivide("alllevel1");
					resultList.get(i).setMethod("HierarchicalCluster");
					hierarchicalClusterDao.save(cluster);
					for(int j = 0;j<numberOfTurningPointSize;j++){
						Cluster_Point cp = new Cluster_Point(cluster, cluster.getTurningPoints().get(j));
						//cp.setParent("101");
						clusterPointDao.save(cp);
					}
				}
			}
			if(resultList.size()==200){

				CalculateCenter(resultList);
				for(int i = 0;i<200;i++)
				{
					int numberOfTurningPointSize = resultList.get(i).getTurningPoints().size();
					HierarchicalCluster cluster = resultList.get(i);
					resultList.get(i).setNumberOfCluster(200);
					resultList.get(i).setSize(numberOfTurningPointSize);
					resultList.get(i).setDivide("alllevel1");
					resultList.get(i).setMethod("HierarchicalCluster");
					hierarchicalClusterDao.save(cluster);
					for(int j = 0;j<numberOfTurningPointSize;j++){
						Cluster_Point cp = new Cluster_Point(cluster, cluster.getTurningPoints().get(j));
						//cp.setParent("101");
						clusterPointDao.save(cp);
					}
				}
			
			}
		}
		//end while loop
		CalculateCenter(resultList);
		for(int i = 0;i<resultList.size();i++)
		{
			int numberOfTurningPointSize = resultList.get(i).getTurningPoints().size();
			HierarchicalCluster cluster = resultList.get(i);
			resultList.get(i).setNumberOfCluster(num);
			resultList.get(i).setSize(numberOfTurningPointSize);
			resultList.get(i).setDivide("alllevel1");
			resultList.get(i).setMethod("HierarchicalCluster");
			hierarchicalClusterDao.save(cluster);
			for(int j = 0;j<numberOfTurningPointSize;j++){
				Cluster_Point cp = new Cluster_Point(cluster, cluster.getTurningPoints().get(j));
				//cp.setParent("101");
				clusterPointDao.save(cp);
			}
		}
		return null;
	}

	@Override
	public List<HierarchicalCluster> InitialCluster(List<TurningPoint> list) {
		
		List<HierarchicalCluster> originalClusters=new ArrayList<HierarchicalCluster>();
        for(int i=0;i<list.size();i++){
            
            List<TurningPoint> tempDataPoints=new ArrayList<TurningPoint>();
            tempDataPoints.add(list.get(i));

            HierarchicalCluster tempCluster=new HierarchicalCluster();
            tempCluster.setTurningPoints(tempDataPoints);
            
            originalClusters.add(tempCluster);
        }

        return originalClusters;
	}

	@Override
	public double GetDistance(double aLat, double aLong, double bLat, double bLong) {
		double a, b, R;  
	    R = 6378.137; // µØÇò°ë¾¶  
	    aLat = aLat * Math.PI / 180.0;  
	    bLat = bLat * Math.PI / 180.0;  
		a = aLat - bLat;
		b = (aLong - bLong) * Math.PI / 180.0;
		double d;
		double sa2, sb2;
		sa2 = Math.sin(a / 2.0);
		sb2 = Math.sin(b / 2.0);
		d = 2 * R * Math.asin(Math.sqrt(sa2 * sa2 + Math.cos(aLat) * Math.cos(bLat) * sb2 * sb2));
	    return d;  
	}

	@Override
	public List<HierarchicalCluster> MergeCluster(List<HierarchicalCluster> list, int index1, int index2) {
		if(index1 !=index2){
			System.out.println("index1:"+index1+"  index2:"+index2);
			HierarchicalCluster cluster1 = list.get(index1);
			HierarchicalCluster cluster2 = list.get(index2);
			List<TurningPoint> dataSet1 = cluster1.getTurningPoints();
			List<TurningPoint> dataSet2 = cluster2.getTurningPoints();
			for(int i = 0;i<dataSet2.size();i++){
				dataSet1.add(dataSet2.get(i));
			}
			cluster1.setTurningPoints(dataSet1);
			list.remove(index2);
		}
		return list;
	}

	@Override
	public void CalculateCenter(List<HierarchicalCluster> list) {
		//get each cluster
		for(int i =0;i<list.size();i++){
			//get list of turningpoints
			List<TurningPoint> turningPoints = list.get(i).getTurningPoints();
			double totalLatitude=0, totalLongtitude=0;
			int SizeOfTurningPoints = turningPoints.size();
			//get each turning point
			for(int j = 0;j<SizeOfTurningPoints;j++){
				totalLatitude=totalLatitude+Double.parseDouble(turningPoints.get(j).getLatitude());
				totalLongtitude=totalLongtitude+Double.parseDouble(turningPoints.get(j).getLongtitude());
			}
			list.get(i).setCenterLatitude(totalLatitude/SizeOfTurningPoints);
			list.get(i).setCenterLongtitude(totalLongtitude/SizeOfTurningPoints);
		}
		
	}

	@Override
	public int Save(HierarchicalCluster cluster) {
		return hierarchicalClusterDao.save(cluster);
		
	}

	@Override
	public List<HierarchicalCluster> DoKMeans(List<TurningPoint> list, int numOfCluster, int iteratorTime) {
		int pointSize = list.size();
		List<HierarchicalCluster> clusters = KMeansInitial(list, numOfCluster);
		int iterator = 0;
		while(iterator<iteratorTime){
			//
			for (int k = 0; k < clusters.size(); k++)
				clusters.get(k).setTurningPoints(new ArrayList<TurningPoint>());

			for (int i=0;i<pointSize;i++){
				double minDistance=Double.MAX_VALUE;
				int indexOfCluster = 0;
				TurningPoint tempPoint = list.get(i);
				for (int j = 0; j < numOfCluster; j++) {
					HierarchicalCluster tempCluster = clusters.get(j);
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
			//System.out.println(iterator+"´Î");
		}
		CalculateCenter(clusters);
		//save
//		for(int i = 0;i<clusters.size();i++)
//		{
//			HierarchicalCluster cluster = clusters.get(i);
//			int numOfPointSize = cluster.getTurningPoints().size();
//			if(numOfPointSize <= 0) continue;
//			cluster.setNumberOfCluster(numOfCluster);
//			cluster.setSize(numOfPointSize);
//			cluster.setDivide("start10"+"iteration"+iteratorTime);
//			cluster.setMethod("kmeans");
//			cluster.setName(null);
//			hierarchicalClusterDao.save(cluster);
//			for(int j = 0;j<numOfPointSize;j++){
//				Cluster_Point cp = new Cluster_Point(cluster, cluster.getTurningPoints().get(j));
//				//cp.setParent("101");
//				clusterPointDao.save(cp);
//			}
//		}
		return clusters;
	}
	
	public List<HierarchicalCluster> KMeansInitial(List<TurningPoint> list, int num)
	{
		List<HierarchicalCluster> clusters = new ArrayList<>();
		for(int i =0;i<num;i++){
			HierarchicalCluster tempCluster = new HierarchicalCluster();
			tempCluster.setCenterLatitude(Double.parseDouble(list.get(i).getLatitude()));
			tempCluster.setCenterLongtitude(Double.parseDouble(list.get(i).getLongtitude()));
			clusters.add(tempCluster);
		}
		return clusters;
	}

	@Override
	public List<HierarchicalCluster> GetStartPointAtTen() {
		List<HierarchicalCluster> result = hierarchicalClusterDao.GetStartPointAtTen();
		Transform(result);
		return result;
	}

	@Override
	public void Update(List<HierarchicalCluster> list) {
		for(int i = 0;i<list.size();i++)
		{
			HierarchicalCluster tempCluster = list.get(i);
			int sizeOfTurningPoints = tempCluster.getTurningPoints().size();
			double centerLatitude = tempCluster.getCenterLatitude();
			double centerLongtitude = tempCluster.getCenterLongtitude();
			tempCluster.setCenterLatitude(centerLatitude*sizeOfTurningPoints);
			tempCluster.setCenterLongtitude(centerLongtitude*sizeOfTurningPoints);
			hierarchicalClusterDao.update(tempCluster);
		}
	}

	@Override
	public void Transform(List<HierarchicalCluster> list) {
		for(int i = 0;i<list.size();i++){
			double longtitude = list.get(i).getCenterLongtitude()+0.0045;
			list.get(i).setCenterLongtitude(longtitude);
			double latitude = list.get(i).getCenterLatitude()-0.002;
			list.get(i).setCenterLatitude(latitude);
		}
	}
	
	@Override
	public List<HierarchicalCluster> DBScan(List<TurningPoint> list, double e, int minP) {
		int clusterID = 0;
		boolean allVisited = false;
		while(allVisited!=true){
			Iterator<TurningPoint> points = list.iterator();
			while(points.hasNext()){
				TurningPoint temp = points.next();
				if(temp.isVisited()) continue;;
				allVisited = false;
				temp.setVisited(true);
				List<TurningPoint> neighbor = DBSCANGetNeighbor(temp, list, e, minP);
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
		
		List<HierarchicalCluster> result = new ArrayList<>();
		//save the cluster
		for(int i = 0;i<clusterID;i++){
			result.add(new HierarchicalCluster());
		}
		
		//add all the points to cluster
		for(int j = 0;j<list.size();j++){
			TurningPoint point = list.get(j);
			int clusterIndex = point.getCid();
			System.out.println("point¡ª¡ªcluseterid:"+point.getCid());
			result.get(clusterIndex-1).getTurningPoints().add(point);
		}
		
		System.out.println("number of cluster:"+result.size());
		System.out.println("begin calculate");
		//save cluster
		CalculateCenter(result);
		for(int k =0;k<result.size();k++){
			int numberOfTurningPointSize = result.get(k).getTurningPoints().size();
			HierarchicalCluster cluster = result.get(k);
			cluster.setSize(numberOfTurningPointSize);
			cluster.setMethod("dbscan");
			cluster.setDivide("alllevel1start10"+"e"+e+"minP"+minP);
			hierarchicalClusterDao.save(cluster);
			for(int j = 0;j<numberOfTurningPointSize;j++){
				Cluster_Point cp = new Cluster_Point(cluster, cluster.getTurningPoints().get(j));
				//cp.setParent("101");
				clusterPointDao.save(cp);
			}
		}
		return result;
	}
	
	public List<TurningPoint> DBSCANGetNeighbor(TurningPoint temp, List<TurningPoint> list, double e, int minP){
		List<TurningPoint> neighbor = new ArrayList<TurningPoint>();
		Iterator<TurningPoint> iter = list.iterator();
		while(iter.hasNext()){
			TurningPoint p = iter.next();
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

	public void DBSCANExpandCluster(TurningPoint temp, List<TurningPoint> neighbor, int clusterID, List<TurningPoint> list, double e, int minP){
		temp.setCid(clusterID);
		Iterator<TurningPoint> iterator = neighbor.iterator();
		while(iterator.hasNext()){
			TurningPoint p = iterator.next();
			if(!p.isVisited()){
				p.setVisited(true);
				List<TurningPoint> pNeighbor = DBSCANGetNeighbor(p, list, e, minP);
				if(pNeighbor.size()>=minP){
					Iterator<TurningPoint> pNeighborPoints = pNeighbor.iterator();
					while(pNeighborPoints.hasNext()){
						TurningPoint no = pNeighborPoints.next();
						if(no.getCid()<=0) no.setCid(clusterID);
					}
				}
			}
			if(p.getCid()<=0) p.setCid(clusterID);
		}
	}
	
	@Override
	public HierarchicalCluster GetClusterById(int id) {
		return hierarchicalClusterDao.GetClusterById(id);
	}

	@Override
	public List<HierarchicalCluster> GetClusterByParentId(int parentId) {
		return hierarchicalClusterDao.GetClusterByParentId(parentId);
	}

	@Override
	public List<HierarchicalCluster> GetStartPointAtTenByDBSCAN() {
		List<HierarchicalCluster> result = hierarchicalClusterDao.GetStartPointAtTenByDBSCAN();
		Transform(result);
		return result;
	}

	@Override
	public List<HierarchicalCluster> GetStartPointAtTenByAGNES() {
		List<HierarchicalCluster> result = hierarchicalClusterDao.GetStartPointAtTenByAGNES();
		Transform(result);
		return result;
	}

	@Override
	public List<HierarchicalCluster> GetStartPointAtTenByKMeans() {
		List<HierarchicalCluster> result = hierarchicalClusterDao.GetStartPointAtTenByKMeans();
		Transform(result);
		return result;
	}

	
}
