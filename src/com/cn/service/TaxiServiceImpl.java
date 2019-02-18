package com.cn.service;

import java.io.FileWriter;
import java.io.IOException;
import java.sql.SQLException;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import javax.annotation.Resource;

import org.springframework.stereotype.Service;

import com.cn.dao.CharacterMopsiDao;
import com.cn.dao.CharacterSGADao;
import com.cn.dao.CharacterTraclusDao;
import com.cn.dao.ClusterDao;
import com.cn.dao.CompressedDao;
import com.cn.dao.MopsiDao;
import com.cn.dao.RawDao;
import com.cn.dao.Taxi100TopDao;
import com.cn.dao.TaxiDao;
import com.cn.dao.TurningPointDao;
import com.cn.entity.CPointCluster;
import com.cn.entity.ClusterEntity;
import com.cn.entity.ClusterResultEntity;
import com.cn.entity.Fragment;
import com.cn.entity.InfoEntity;
import com.cn.entity.MopsiEntity;
import com.cn.entity.RawEntity;
import com.cn.entity.RawForVoronoi;
import com.cn.entity.RawRepre;
import com.cn.entity.SegClustering;
import com.cn.entity.Taxi;
import com.cn.entity.TraclusRepEntity;
import com.cn.entity.TurningPoint;
import com.cn.entity.char_SGAEntity;
import com.cn.entity.char_TraclusEntity;
import com.cn.entity.char_mopsiEntity;
import com.cn.entity.trajectoryCluster;

import CharacteristicPoints.Algorithms_SGA;
import CharacteristicPoints.Point;
import CharacteristicPoints.Trajectory;
import CpointsClustering.Clustering;
import DataMining.Distance;
import DataMining.GetData;
import DataMining.Line;
import DataMining.TraClus;
import DataMining.TrajectoryDBA;
import ParameterSelect.CalcBean;
import ParameterSelect.MyComparator;

@Service("taxiService")
public class TaxiServiceImpl implements TaxiService {
    public List<SegClustering> resultKmeans;
    public List<SegClustering> resultDbscan;
    public List<SegClustering> resultHierarchical;
    public List<MopsiEntity> Mopsidata=new ArrayList<>();
	private TaxiDao taxiDao;
	public int ClusterNumber;
	private CompressedDao compressDao;
	private TurningPointDao turningPointDao;
    private RawDao rawDao;
    private Taxi100TopDao taxi100Dao;
    private ClusterDao clusterDao;
    private CharacterTraclusDao char_traclusDao;
    private CharacterSGADao char_sgaDao;
    private MopsiDao mopsiDao;
    private CharacterMopsiDao char_mopsiDao;
  //  private CharacterDao characterDao;
    public CharacterSGADao getSGADao() {
		return this.char_sgaDao;
	}
    @Resource
	public void setCharMopsiDao(CharacterMopsiDao mop) {
		this.char_mopsiDao  = mop;
	}
	public CharacterMopsiDao getCharMopsiDao() {
		return this.char_mopsiDao;
	}
	@Resource
	public void setSGADao(CharacterSGADao sga) {
		this.char_sgaDao  = sga;
	}
	public RawDao getRawDao() {
		return this.rawDao;
	}
	@Resource
	public void setTaxi100Dao(Taxi100TopDao taxiTop) {
		this.taxi100Dao  = taxiTop;
	}

	@Resource
	public void setRawDao(RawDao rawDao) {
		this.rawDao  = rawDao;
	}
	public ClusterDao getClusterDao() {
		return this.clusterDao;
	}
	@Resource
	public void setMopsiDao(MopsiDao mopsiDao) {
		this.mopsiDao  = mopsiDao;
	}
	public MopsiDao getMopsiDao() {
		return this.mopsiDao;
	}
	@Resource
	public void setClusterDao(ClusterDao clusterDao) {
		this.clusterDao = clusterDao;
	}
	public CharacterTraclusDao getchar_TraclusDao() {
		return this.char_traclusDao;
	}
	@Resource
	public void setChar_TraclusDao(CharacterTraclusDao char_traclusDao) {
		this.char_traclusDao  = char_traclusDao;
	}
	

	public TurningPointDao getTurningPointDao() {
		return turningPointDao;
	}

	@Resource
	public void setTurningPointDao(TurningPointDao turningPointDao) {
		this.turningPointDao = turningPointDao;
	}

	public TaxiDao getTaxiDao() {
		return taxiDao;
	}

	@Resource
	public void setTaxiDao(TaxiDao taxiDao) {
		this.taxiDao = taxiDao;
	}
	@Resource
	public void setCompressionDao(CompressedDao compressedDao) {
		this.compressDao = compressedDao;
	}
	@Override
	public List<Taxi> search(String taxiId, String time) {
		List<Taxi> result = taxiDao.Search(taxiId, time);
		transform(result);
		return result;
	}
	@Override
	public List<Taxi> search_frequency(String taxiId, String time) {
		List<Taxi> result = taxiDao.SearchFrequency(taxiId, time);
		return result;
	}
	@Override
	public List<Taxi> getAll() {
		return taxiDao.getAll();
	}

	@Override
	public Set<String> getAllTaxiId( ) {
		return taxiDao.getAllTaxiId();
	}

	@Override
	public void transform(List<Taxi> list) {
		for(int i=0;i<list.size();i++)
		{
			double longtitude = Double.parseDouble(list.get(i).getLongtitude())+0.0045;
			list.get(i).setLongtitude(Double.toString(longtitude));
			double latitude = Double.parseDouble(list.get(i).getLatitude())-0.002;
			list.get(i).setLatitude(Double.toString(latitude));
		}
	}

	@Override
	public void GenerateTurningPoints(List<String> taxiIds) {
		List<TurningPoint> turningPoints = new ArrayList<TurningPoint>();
		System.out.println("ids:"+taxiIds.size());
		for(int i = 0; i<taxiIds.size();i++){
			List<Taxi> array = taxiDao.Search(taxiIds.get(i), null);
			//handle single trajectory of one car
			for(int j=1;j<array.size();j++){
				Taxi pre = array.get(j-1);
				Taxi aft = array.get(j);
				if (!pre.getState().equals(aft.getState())) {
					turningPoints.add(
							new TurningPoint(pre.getLongtitude(), pre.getLatitude(), pre.getState(), pre.getTime(), pre.getTaxi_Id(), pre.getSpeed(), null));
					turningPoints.add(
							new TurningPoint(aft.getLongtitude(), aft.getLatitude(), aft.getState(), aft.getTime(), aft.getTaxi_Id(), aft.getSpeed(), null));
				}
//				if (pre.getState().equals(aft.getState())&&pre.getLongtitude().equals(aft.getLongtitude()) && pre.getLatitude().equals(aft.getLatitude())
//						&& !pre.getTime().equals(aft.getTime()) && pre.getSpeed().equals("0")&& aft.getSpeed().equals("0")) {
//					turningPoints.add(
//							new TurningPoint(aft.getLongtitude(), aft.getLatitude(), aft.getState(), aft.getTime(), aft.getTaxi_Id(), aft.getSpeed()));
//					
//				}
			}
		}
		//save all turningpoints
		for(int i=0;i<turningPoints.size();i++){
			turningPointDao.save(turningPoints.get(i));
		}
	}
	@Override
    public List<List<Taxi>> GetFragmentAfterCompressed(List<String>list){
    	List<List<Taxi>> result = new ArrayList<List<Taxi>>();
        for(int i=0;i<list.size();i++){
        	List<Taxi> array = compressDao.SearchCompressed(list.get(i), null);//这里报错：nullPointerException
        	result.add(array);
        }
    	return result;
    }
	@Override
    public List<List<Taxi>> GetFragmentOriginal(List<String>list){
    	List<List<Taxi>> result = new ArrayList<List<Taxi>>();
       /* for(int i=0;i<list.size();i++){
        	List<Taxi> array = taxiDao.Search(list.get(i), null);//获取taxi_id对应的原始轨迹，未经过分割的轨迹
        	result.add(array);
        }*/
          result = taxiDao.SearchForList(list);
    	return result;
    }      
	@Override
    public List<List<MopsiEntity>> GetFragmentOriginalMopsi(List<String>list){
    	List<List<MopsiEntity>> result = new ArrayList<List<MopsiEntity>>();
       /* for(int i=0;i<list.size();i++){
        	List<Taxi> array = taxiDao.Search(list.get(i), null);//获取taxi_id对应的原始轨迹，未经过分割的轨迹
        	result.add(array);
        }*/
          result = mopsiDao.SearchForList(list);
    	return result;
    }      
	@Override
	public List<List<Taxi>> GetFragment(List<String> list) {
		List<List<Taxi>> result= new ArrayList<>();
		for(int i=0;i<list.size();i++)
		{
			List<Taxi> array = taxiDao.Search(list.get(i), null);
			//System.out.println(array);
			int trajectoryTotalSize = array.size();
			int fragmentSize = 0;
			List<Taxi> fragment = null;
			for(int j=0;j<trajectoryTotalSize;j++)
			{
				Taxi currentTaxi = array.get(j);
				double cos=1;
				if(j > 0 && j < trajectoryTotalSize - 1){
				double xA = Double.parseDouble(currentTaxi.getLatitude())-Double.parseDouble(array.get(j-1).getLatitude());
				double yA = Double.parseDouble(currentTaxi.getLongtitude())-Double.parseDouble(array.get(j-1).getLongtitude());
				double xB = Double.parseDouble(array.get(j+1).getLatitude())-Double.parseDouble(currentTaxi.getLatitude());
				double yB = Double.parseDouble(array.get(j+1).getLatitude())-Double.parseDouble(currentTaxi.getLatitude());
				cos = (xA*xB+yA*yB)/(Math.sqrt(xA*xA+yA*yA)*Math.sqrt(xB*xB+yB*yB));
				}
				//ignore no-load point
				if (currentTaxi.getState().equals("0"))
					continue;
				//start a new fragment
				else if ((j == 0 && currentTaxi.getState().equals("1"))
						|| (j > 0 && currentTaxi.getState().equals("1") && array.get(j - 1).getState().equals("0"))) {
					if (fragment!=null&&fragmentSize != 0&&fragment.size()>2)
					{
						transform(fragment);
						result.add(fragment);
					}
						
					fragment = new ArrayList<>();
					fragment.add(currentTaxi);
					fragmentSize++;
					continue;
				}
				//找到特征点
				else if (j > 0 && j < trajectoryTotalSize - 1 && currentTaxi.getState().equals("1")
						&& array.get(j - 1).getState().equals("1") && array.get(j + 1).getState().equals("1")
						&& cos <= 0) {
					fragment.add(currentTaxi);
					if(fragment!=null&&fragment.size()>2) result.add(fragment);
					fragment = new ArrayList<>();
					fragmentSize++;
					fragment.add(currentTaxi);
					continue;
				}
//				else if (j > 0
//						&& ((Integer.parseInt(currentTaxi.getAngle())
//								- Integer.parseInt(array.get(j - 1).getAngle())) >= 90)
//						&& currentTaxi.getState().equals("1") && array.get(j - 1).getState().equals("1")) {
//					fragment.add(currentTaxi);
//					if(fragment.size()>1) result.add(fragment);
//					fragment = new ArrayList<>();
//					fragmentSize++;
//					fragment.add(currentTaxi);
//					continue;
//				}
				else if (currentTaxi.getState().equals("1")) fragment.add(currentTaxi);
			}
			//add the last fragment
			if(fragment!=null&&fragment.size()>2) {
				transform(fragment);
				result.add(fragment);
			}
		}
		return result;
	}
	@Override
	public int getClusterNum(){
		return this.ClusterNumber;
	}
//针对展示所有轨迹
	public List<List<Taxi>> GetFragmentAll(List<String>list) {
		List<List<Taxi>> result= new ArrayList<>();
		for(int i=0;i<list.size();i++)
		{
			List<Taxi> array = taxiDao.SearchAll(list.get(i), null);
			//System.out.println(array);
			int trajectoryTotalSize = array.size();
			int fragmentSize = 0;
			List<Taxi> fragment = null;
			for(int j=0;j<trajectoryTotalSize;j++)
			{
				Taxi currentTaxi = array.get(j);
				double cos=1;
				if(j > 0 && j < trajectoryTotalSize - 1){
				double xA = Double.parseDouble(currentTaxi.getLatitude())-Double.parseDouble(array.get(j-1).getLatitude());
				double yA = Double.parseDouble(currentTaxi.getLongtitude())-Double.parseDouble(array.get(j-1).getLongtitude());
				double xB = Double.parseDouble(array.get(j+1).getLatitude())-Double.parseDouble(currentTaxi.getLatitude());
				double yB = Double.parseDouble(array.get(j+1).getLatitude())-Double.parseDouble(currentTaxi.getLatitude());
				cos = (xA*xB+yA*yB)/(Math.sqrt(xA*xA+yA*yA)*Math.sqrt(xB*xB+yB*yB));
				}
				//ignore no-load point
				if (currentTaxi.getState().equals("0"))
					continue;
				//start a new fragment
				else if ((j == 0 && currentTaxi.getState().equals("1"))
						|| (j > 0 && currentTaxi.getState().equals("1") && array.get(j - 1).getState().equals("0"))) {
					if (fragment!=null&&fragmentSize != 0&&fragment.size()>2)
					{
						transform(fragment);
						result.add(fragment);
					}
						
					fragment = new ArrayList<>();
					fragment.add(currentTaxi);
					fragmentSize++;
					continue;
				}
				//找到特征点
				else if (j > 0 && j < trajectoryTotalSize - 1 && currentTaxi.getState().equals("1")
						&& array.get(j - 1).getState().equals("1") && array.get(j + 1).getState().equals("1")
						&& cos <= 0) {
					fragment.add(currentTaxi);
					if(fragment!=null&&fragment.size()>2) result.add(fragment);
					fragment = new ArrayList<>();
					fragmentSize++;
					fragment.add(currentTaxi);
					continue;
				}
//				else if (j > 0
//						&& ((Integer.parseInt(currentTaxi.getAngle())
//								- Integer.parseInt(array.get(j - 1).getAngle())) >= 90)
//						&& currentTaxi.getState().equals("1") && array.get(j - 1).getState().equals("1")) {
//					fragment.add(currentTaxi);
//					if(fragment.size()>1) result.add(fragment);
//					fragment = new ArrayList<>();
//					fragmentSize++;
//					fragment.add(currentTaxi);
//					continue;
//				}
				else if (currentTaxi.getState().equals("1")) fragment.add(currentTaxi);
			}
			//add the last fragment
			if(fragment!=null&&fragment.size()>2) {
				transform(fragment);
				result.add(fragment);
			}
		}
		
		return result;
	}
	@Override
	public List<Fragment> fragmentIndex(List<List<Taxi>> result){
		//为每条fragment赋值一个索引
			List<Fragment> fragmentList = new ArrayList<Fragment>();
				for(int i=0;i<result.size();i++){
					 Fragment frag_withIndex = new Fragment();
					 frag_withIndex.setfragment(result.get(i));//给对应的线段轨迹进行轨迹的赋值
					 frag_withIndex.setID(i);//给对应生成的线段轨迹进行轨迹id的赋值方便后面的索引查找
				     fragmentList.add(frag_withIndex);
				}
				return fragmentList;
	}
	@Override
	public List<Fragment> fragmentIndexMopsi(List<List<MopsiEntity>> result){
		//为每条fragment赋值一个索引
			List<Fragment> fragmentList = new ArrayList<Fragment>();
				for(int i=0;i<result.size();i++){
					 Fragment frag_withIndex = new Fragment();
					 frag_withIndex.setfragmentMopsi(result.get(i));//给对应的线段轨迹进行轨迹的赋值
					 frag_withIndex.setID(i);//给对应生成的线段轨迹进行轨迹id的赋值方便后面的索引查找
				     fragmentList.add(frag_withIndex);
				}
				return fragmentList;
	}
	//以上：针对展示所有轨迹
	@Override
	public double[][] GetSimilarity(List<List<Taxi>> trajectory) {
		int size = trajectory.size();
		double[][] similarity = new double[size][size];
		for(int i=0;i<size;i++){
			for (int j = 0; j < size; j++) {
				similarity[i][j] = GetDirectionSensitiveHausdorffDistance(trajectory.get(i), trajectory.get(j));
				
			}
		}
		return similarity;
	}
	@Override
	public HashMap<Integer,ArrayList<Double>> GetSimilarityForShowAll(List<List<Taxi>> trajectory) {
		int size = trajectory.size();
		//double[][] similarity = new double[size][size];
		HashMap<Integer,ArrayList<Double>> similarity = new HashMap<Integer,ArrayList<Double>>();
		for(int i=0;i<size;i++){
			ArrayList<Double> sim_temp = new ArrayList<Double>();
			for (int j = 0; j < size; j++) {
				double sim_track = GetDirectionSensitiveHausdorffDistance(trajectory.get(i), trajectory.get(j));
				sim_temp.add(sim_track);
			}
			similarity.put(i, sim_temp);
		}
		return similarity;
	}
	@Override
	public double getHausdorffDistance(List<Taxi> t1, List<Taxi> t2) {
		double HD=0.0;
		double hAB=0.0;
		double hBA=0.0;
		int lenA=t1.size();
		int lenB=t2.size();
		double[]  minHA= new double[lenA];
		double[]  minHB = new double[lenB];
		
		for(int i=0;i<lenA;i++)
		{
			minHA[i] = GetDistance(Double.parseDouble(t1.get(i).getLongtitude()),
					Double.parseDouble(t1.get(i).getLatitude()), Double.parseDouble(t2.get(0).getLongtitude()),
					Double.parseDouble(t2.get(0).getLatitude()));

			for (int j = 1; j < lenB; j++)
			{
				double distance = GetDistance(Double.parseDouble(t1.get(i).getLongtitude()),
						Double.parseDouble(t1.get(i).getLatitude()), Double.parseDouble(t2.get(j).getLongtitude()),
						Double.parseDouble(t2.get(j).getLatitude()));
				if(distance<minHA[i])
					minHA[i]=distance;
			}
		}
		hAB=minHA[0];
		for(int i=1;i<minHA.length;i++)
		{
			if(minHA[i]>hAB)
				hAB=minHA[i];
				
		}
		
		for(int i=0;i<lenB;i++)
		{
			minHB[i]=GetDistance(Double.parseDouble(t2.get(i).getLongtitude()),
					Double.parseDouble(t2.get(i).getLatitude()), Double.parseDouble(t1.get(0).getLongtitude()),
					Double.parseDouble(t1.get(0).getLatitude()));
			for(int j=1;j<lenA;j++)
			{
				double distance = GetDistance(Double.parseDouble(t2.get(i).getLongtitude()),
						Double.parseDouble(t2.get(i).getLatitude()), Double.parseDouble(t1.get(j).getLongtitude()),
						Double.parseDouble(t1.get(j).getLatitude()));
				if(distance<minHB[i])
					minHB[i]=distance;
			}
		}
		hBA=minHB[0];
		for(int i=1;i<minHB.length;i++)
		{
			if(minHB[i]>hBA)
				hBA=minHB[i];
				
		}
		
		if(hAB<hBA)
			HD=hBA;
		else
			HD=hAB;
	
		return HD;
	}
    public double HausTurn(DataMining.Line line1,DataMining.Line line2){
     //首先将线段转化为List<Taxi>
    	List<Taxi> list1 = new ArrayList<>();
    	List<Taxi> list2 = new ArrayList<>();
    	DataMining.Point s1 = line1.getS();
    	DataMining.Point e1 = line1.getE();
    	DataMining.Point s2 = line2.getS();
    	DataMining.Point e2 = line2.getE();
    	Taxi TS1 = new Taxi();
    	TS1.setLatitude(String.valueOf(s1.x));
    	TS1.setLongtitude(String.valueOf(s1.y));
    	list1.add(TS1);
    	Taxi TE1= new Taxi();
    	TE1.setLatitude(String.valueOf(e1.x));
    	TE1.setLongtitude(String.valueOf(e1.y));
    	list1.add(TE1);
    	
    	Taxi TS2 = new Taxi();
    	TS2.setLatitude(String.valueOf(s2.x));
    	TS2.setLongtitude(String.valueOf(s2.y));
    	list1.add(TS2);
    	Taxi TE2= new Taxi();
    	TE2.setLatitude(String.valueOf(e2.x));
    	TE2.setLongtitude(String.valueOf(e2.y));
    	list2.add(TE2);
        double val  = GetDirectionSensitiveHausdorffDistance(list1,list2);
        return val;
    }
	public double GetDirectionSensitiveHausdorffDistance(List<Taxi> TA, List<Taxi> TB){
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
		for(int i = 0;i<numOfTB;i++){
			double closestDistance = Double.MAX_VALUE;
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
		
		return (scoreAB+scoreBA)/2;
	}
 
	//point to line shortest distance
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
          return space;
	}
	
	/** 
	 * 计算地球上任意两点(经纬度)距离 
	 *  
	 * @param long1 
	 *            第一点经度 
	 * @param lat1 
	 *            第一点纬度 
	 * @param long2 
	 *            第二点经度 
	 * @param lat2 
	 *            第二点纬度 
	 * @return 返回距离 单位：米 
	 */  
	public static double GetDistance(double long1, double lat1, double long2, double lat2) {
		double a, b, R;
		R = 6378.137; // 地球半径km
		lat1 = lat1 * Math.PI / 180.0;
		lat2 = lat2 * Math.PI / 180.0;
		a = lat1 - lat2;
		b = (long1 - long2) * Math.PI / 180.0;
		double d;
		double sa2, sb2;
		sa2 = Math.sin(a / 2.0);
		sb2 = Math.sin(b / 2.0);
		d = 2 * R * Math.asin(Math.sqrt(sa2 * sa2 + Math.cos(lat1) * Math.cos(lat2) * sb2 * sb2));
		return d;
	}

	@Override
	public void DeleteDuplicateTaxi(List<String> id) {
		for(int i = 0;i<id.size();i++){
			List<Taxi> array = taxiDao.Search(id.get(i), null);
			int trajectorySize = array.size();
			for(int j = 1;j<trajectorySize;j++){
				Taxi pre = array.get(j-1);
				Taxi aft = array.get(j);
				
				if(pre.getTaxi_Id().equals(aft.getTaxi_Id())
						&&pre.getTime().equals(aft.getTime())) taxiDao.Delete(pre);
				else if (pre.getTaxi_Id().equals(aft.getTaxi_Id())
						&&pre.getAngle().equals(aft.getAngle())
						&&pre.getLatitude().equals(aft.getLatitude())
						&&pre.getLongtitude().equals(aft.getLongtitude())
						&&pre.getSpeed().equals(aft.getSpeed())
						&&pre.getState().equals(aft.getState())) {
					taxiDao.Delete(pre);
				}
			}
			
		}
		for(int i = 0;i<id.size();i++){
			List<Taxi> array = taxiDao.Search(id.get(i), null);
			if(array.size()<3){
				for(int j = 0;j<array.size();j++) taxiDao.Delete(array.get(j));
			}
		}
	}
	
	@Override
	public List<trajectoryCluster> InitialCluster(List<List<Taxi>> list) {
		
		List<trajectoryCluster> originalClusters=new ArrayList<trajectoryCluster>();
         for(int i=0;i<list.size();i++){
           
        	//List<trajectoryCluster> tempDataTracks=new ArrayList<trajectoryCluster>();
        	//tempDataTracks.add((trajectoryCluster) list.get(i));
            List<List<Taxi>> tempTrackList = new ArrayList<List<Taxi>>();
            tempTrackList.add(list.get(i));
            trajectoryCluster tempCluster=new trajectoryCluster();
          //  tempCluster.setTurningPoints(tempDataTracks);
            tempCluster.setTrajectoryList(tempTrackList);
            originalClusters.add(tempCluster);
        }
            System.out.println("the original cluster is "+originalClusters.size());
        return originalClusters;
	}

	//对相似度进行轨迹聚类,使用的是TRACLUS聚类方法
	@Override
	public HashMap<Integer, ArrayList<Integer>>  TRACLUS(List<List<Taxi>> list, double[][] similarity){
 
	ArrayList<Integer> Queue = new ArrayList<Integer>();//队列Q
	ArrayList<Integer> Noise = new ArrayList<Integer>();//噪音
	double eps = 5 ; double minlns = 3;
	int clusterId = 0;
	ArrayList<Integer> TraClusterId = new ArrayList<Integer>();//记录每条轨迹的clusterId
	ArrayList<Integer> TraClassified = new ArrayList<Integer>();//记录每条轨迹是否被归类，0为没有被归类，1为被归类了
	int num_tra = list.size();//一共有几条轨迹
	//将所有的轨迹的聚类号先设置为-1
	for(int i=0;i<num_tra;i++){
		TraClusterId.add(-1);
	}
	for(int i = 0 ; i<num_tra;i++){
		TraClassified.add(0);//将所有轨迹全部标记为未被访问
	}
	for(int i = 0;i<num_tra;i++){//对于每条轨迹
		if(TraClassified.get(i)==0){
		//	TraClassified.set(i,2);//标记为被访问
			//如果轨迹i未被分类
			//首先计算每条轨迹里面的领域数量，即similarity里面小于等于eps的轨迹数量
			ArrayList<Integer> eps_col  = EPS_NUM(i, similarity, eps);//第i条轨迹的领域轨迹号
			int numOfEps = eps_col.size();//领域轨迹数
			if(numOfEps+1>=minlns){//如果是核心线段
				TraClusterId.set(i,clusterId);//核心线段加入到clustering中
				//将clusterId分配给领域内所有的轨迹
			     for(int j=0;j<eps_col.size();j++){
			    	 TraClusterId.set(j, clusterId);
			    	 TraClassified.set(j, 2);//邻域线段标记为被访问
			    	 //将领域轨迹除掉本身轨迹的集合插入到队列Q
			    	 if(i!=j){//除掉本身轨迹
			    		 Queue.add(eps_col.get(j));//邻域轨迹的行号
			    	 }
			     }
			    //STEP2:ExpandCluster
			 	while( Queue.size()>0){//queue非空
					int Track_col  = Queue.get(0);//Queue里面的第一条轨迹M
					//计算M对应的领域
					ArrayList<Integer> EPS_M =  EPS_NUM(Track_col, similarity, eps);//第M条轨迹的领域
					if(EPS_M.size()>=minlns){//直接密度可达
						for(int j = 0;j<EPS_M.size();j++){
							int col_num = EPS_M.get(j);//领域线段的行号
							if(TraClassified.get(col_num)==0||TraClassified.get(col_num)==1){
								TraClusterId.set(col_num, clusterId);
								TraClassified.set(col_num, 2);
							}
							int X = EPS_M.get(j);
							if(TraClassified.get(j)==0 ){
							    //在Queue里面查找是否有X号轨迹，若没有则加入
								boolean find = false;
								for(int k=0;k<Queue.size();k++){
									int tra_num=Queue.get(k);
									if(tra_num==X){
										find = true;//queue里面已经有了对应的轨迹
										break;
									}
								}if(find==false){
									//queue里面没有线段X
									Queue.add(X);
								}
							}
						}
					}
					Queue.remove(0);//将Queue中的第一条轨迹去掉
				}
			    clusterId++;
			}else{
				//将轨迹L标记为噪音
				Noise.add(i);
				TraClassified.set(i, 1);
			}
		}
		 
	}
	//STEP 3 : 将所有轨迹分配到属于它的聚类
	HashMap<Integer,ArrayList<Integer>> ClusterAll = new HashMap<Integer,ArrayList<Integer>>();//所有聚类的集合
	ArrayList<Integer> copyOfClusterid=new ArrayList<Integer>();
	copyOfClusterid.addAll(TraClusterId);
	ArrayList<Integer> Cluster;
	ArrayList<Integer> uniqueClusterId = uniqueClusterId(copyOfClusterid);
    for(int i = 0;i<uniqueClusterId.size();i++){ 
    	Cluster = new ArrayList<Integer>();//每条轨迹对应一个聚类
    	int clu_num = uniqueClusterId.get(i);
    	for(int j=0;j<TraClusterId.size();j++){//j对应的是轨迹的行号
    	if(TraClusterId.get(j)==clu_num){//第J条轨迹的聚类号是匹配的
    	Cluster.add(j);
    	}	
    	}
    	ClusterAll.put(i, Cluster);
    }
	//检查轨迹的基数
    for(int j = 0;j<ClusterAll.size();j++){
    	int numOfTracks = 0;
    	ArrayList<Integer> ClusterTemp = ClusterAll.get(j);//对应的聚类
    	numOfTracks = ClusterTemp.size();//聚类内的轨迹数
    	if(numOfTracks+1<minlns){//若聚类对应的轨迹数目小于Minlns
    		//将聚类从总聚类集合中去掉
    	    ClusterAll.remove(j);
             }
     }
    return ClusterAll;
	}
	public ArrayList<Integer> uniqueClusterId(ArrayList<Integer> list){//去重复
		ArrayList<Integer> Unique_list = list;
		for  ( int  i  =   0 ; i  <  Unique_list.size()  -   1 ; i ++ )   { 
		    for  ( int  j  =  Unique_list.size()  -   1 ; j  >  i; j -- )   { 
		      if  (Unique_list.get(j).equals(Unique_list.get(i)))   { 
		    	  Unique_list.remove(j); 
		      } 
		    } 
		  } 
		return Unique_list;
	}
	 
	 
	//计算每条轨迹的EPS内轨迹 
 	public ArrayList<Integer> EPS_NUM(int numTra,double[][] similarity,double eps){
		ArrayList<Integer> epsList = new ArrayList<Integer>();
		 
		for(int i=0;i<similarity[numTra].length;i++){
			if(similarity[numTra][i]<=eps){
				epsList.add(i);//将领域轨迹的行号记录下来
			}
		}
		return epsList ;// 领域轨迹数
	}
	//计算中心点,这里应为计算中心轨迹
	@Override
	public void CalculateCenter(List<trajectoryCluster> resultList) {
		//将每个轨迹的GPS点个数统一，少的加0，首先获取最长轨迹的GPS点数
		int  maxlength=0;
		List<trajectoryCluster> unifiedCluster = new ArrayList<trajectoryCluster>();
		for(int i=0;i<resultList.size();i++){
			maxlength=0;
			trajectoryCluster tempCluster = resultList.get(i);
			List<List<Taxi>> tracks = tempCluster.getTrajectoryList();
			maxlength=tracks.get(0).size();//第一个轨迹的点数
			for(int j =1;j<tracks.size();j++){
				if(maxlength<tracks.get(j).size()){
					maxlength=tracks.get(j).size();
				}
			}
			resultList.get(i).setMaxLength(maxlength);
			//System.out.println("maxlength is"+resultList.get(i).getMaxlength());
		}
		
		//获取最大长度也就是最大点数之后对其余参与计算中心点的轨迹进行补0
		for(int i=0;i<resultList.size();i++){
			trajectoryCluster tempCluster = resultList.get(i);
			List<List<Taxi>> tracks = tempCluster.getTrajectoryList();
			for(int j=0;j<tracks.size();j++){
				List<Taxi> tempTrack = tracks.get(j);//获取每条轨迹
				Taxi lastTaxi = tempTrack.get(tempTrack.size()-1);//获取每条轨迹的最后一个GPS点
				String lastLat = lastTaxi.getLatitude();//最后一个数据点的纬度
				String lastLon = lastTaxi.getLongtitude();//最后一个数据点的经度
				for(int k=0;k<(maxlength-tracks.size());k++){
					Taxi tempTaxi = new Taxi();
					//tempTaxi.setLatitude("0");
					//tempTaxi.setLongtitude("0");
					tempTaxi.setLatitude(lastLat);
					tempTaxi.setLongtitude(lastLon);
					tracks.get(j).add(tempTaxi);
				}
			}//对每个轨迹都补充完毕后设置set
			tempCluster.setTrajectoryList(tracks);
			unifiedCluster.add(tempCluster);
		}
		//get each cluster
	
		for(int i =0;i<unifiedCluster.size();i++){
			List<Taxi> averageTracks = new ArrayList<Taxi>();//用于存放每条轨迹计算得到的均值轨迹
			//get list of tracks
			List<List<Taxi>> Tracks = resultList.get(i).getTrajectoryList();
			double totalLatitude=0, totalLongtitude=0;
			int SizeOfTracks = Tracks.size();
			//get each tracks
			for(int k=0;k<maxlength;k++){
			for(int j = 0;j<SizeOfTracks;j++){
				//for(int k=0;k<Tracks.get(j).size();k++){
				double tempLat = Double.parseDouble(Tracks.get(j).get(k).getLatitude());
				double tempLon = Double.parseDouble(Tracks.get(j).get(k).getLongtitude());
				totalLatitude=totalLatitude+tempLat;
				totalLongtitude=totalLongtitude+tempLon;
				//}
				//counter++;
			}
			Taxi tempAvgTaxi = new Taxi();
			tempAvgTaxi.setLatitude(String.valueOf(totalLatitude/SizeOfTracks));
			tempAvgTaxi.setLongtitude(String.valueOf(totalLongtitude/SizeOfTracks));
			averageTracks.add(tempAvgTaxi);
		}
			unifiedCluster.get(i).setCenterTracks(averageTracks); 
			//unifiedCluster.get(i).setCenterLongtitude(totalLongtitude/SizeOfTracks);
		}
		resultList=unifiedCluster;
	}
	//以上计算中心轨迹
//合并
	@Override
	public List<trajectoryCluster> MergeCluster(List<trajectoryCluster> list, int index1, int index2) {
		if(index1 !=index2){
			System.out.println("index1:"+index1+"  index2:"+index2);
			trajectoryCluster cluster1 = list.get(index1);
			trajectoryCluster cluster2 = list.get(index2);
			List<List<Taxi>> dataSet1 = cluster1.getTrajectoryList();
			List<List<Taxi>> dataSet2 = cluster2.getTrajectoryList();
			for(int i = 0;i<dataSet2.size();i++){
				dataSet1.add(dataSet2.get(i));
			}
			cluster1.setTrajectoryList(dataSet1);
			
			list.set(index1, cluster1);
			list.remove(index2);
		}
		return list;
	}
	@Override
	public List<List<Taxi>> backToListTracks(List<trajectoryCluster> list) {
		List<List<Taxi>> tracks = new ArrayList<List<Taxi>>();
		if(list!=null){
		 for(int i=0;i<list.size();i++){
			 List<List<Taxi>> tempTracks = list.get(i).getTrajectoryList();
			 for(int j=0;j<tempTracks.size();j++){
				 tracks.add(tempTracks.get(j));
			 }
		 }}
		else{
			System.out.println("list is empty");
		}
		return tracks;
	}
//计算representative trajectory
	@SuppressWarnings("unchecked")
	public  List<Taxi>  Representative( ArrayList<Integer>  cluster, List<List<Taxi>> trajectory,double minlns){
		double smooth = 0.0002;
		List<List<Taxi>> Re_all = new ArrayList<List<Taxi>>();
		 List<Taxi>  representative = new ArrayList< Taxi >();//representative 轨迹
		List<List<Taxi>> clusterTracks = new ArrayList<List<Taxi>>();//聚类内所有轨迹
		List<List<Taxi>> clusterRotate = new ArrayList<List<Taxi>>();//聚类内所有旋转后轨迹
		//计算平均方向向量
		double x = 0;
		double y = 0;
		for(int i = 0 ;i<cluster.size();i++){
		 int track_number = cluster.get(i);//聚类里面的轨迹好
		 List<Taxi> tracks =  trajectory.get(track_number);//第track_number条轨迹
		 clusterTracks.add(tracks);
		 double start_x = Double.parseDouble(tracks.get(0).getLatitude());//第一个GPS点的经度作为坐标系的x
		 double start_y = Double.parseDouble(tracks.get(0).getLongtitude());//第一个GPS点的纬度作为坐标系的y
		 double end_x = Double.parseDouble(tracks.get(tracks.size()-1).getLatitude());//最后一个GPS点的经度作为坐标系的x
		 double end_y = Double.parseDouble(tracks.get(tracks.size()-1).getLongtitude());//最后一个GPS点的纬度作为坐标系的y
		 //计算向量即(x2-x1,y2-y1)
			double temp_x = end_x-start_x;
			double temp_y = end_y-start_y;
			x = x + temp_x;
			y = y + temp_y;
		}
		  
		double[] average_vector = {x/cluster.size(),y/cluster.size()};
		double x_average = average_vector[0];
		double y_average = average_vector[1];
	
		//旋转坐标轴使得x轴与平均向量平行
		//首先计算旋转角度，通过点积来求
		double average_length = Math.sqrt(x_average*x_average+y_average*y_average);
				
		double cos_angle =  x_average/average_length;
		double sin_angle =  y_average/average_length;
		 
		//计算出sin 和 cos之后计算x'和y'
		for(int i=0;i<clusterTracks.size();i++){
			List<Taxi> temp = clusterTracks.get(i);//原始轨迹
			List<Taxi> temp_rotate =  new ArrayList<Taxi>();//用于存放旋转后轨迹
			for(int j=0;j<temp.size();j++){
			Taxi point = temp.get(j);//原始点
			Taxi rotate_point = new Taxi();//用于存放旋转后的点
	         double	x_rotate =  cos_angle * (Double.parseDouble(point.getLatitude())) + sin_angle* (Double.parseDouble(point.getLongtitude()));//cos*x+sin*y
	         double	y_rotate =  (-1)*sin_angle* (Double.parseDouble(point.getLatitude())) + cos_angle * (Double.parseDouble(point.getLongtitude()));//-1*sin*x+cos*y
			 rotate_point.setLatitude(String.valueOf(x_rotate));//旋转点的经度
			 rotate_point.setLongtitude(String.valueOf(y_rotate));//旋转点的纬度
			 temp_rotate.add(rotate_point);
			}
		    clusterRotate.add(temp_rotate);
		}
		ArrayList<Taxi> P =  new ArrayList<Taxi>();//P是聚类的起点和终点的集合
		for(int i=0;i<clusterRotate.size();i++){
	    List<Taxi> tempTracks = clusterRotate.get(i); 
		Taxi p_start = new Taxi();
	    p_start = tempTracks.get(0);//起点
		Taxi p_end = new Taxi();
	    p_end = tempTracks.get(tempTracks.size()-1);//终点
		P.add(p_start);
		P.add(p_end);
		}
 
		MyComparator mc = new MyComparator();
		Collections.sort(P,mc);//对P内的点进行根据X'的值从小到大的排序
		//对旋转后的逐条线段进行排序：根据线段里面最小的X'值进行比较
		try{
			Collections.sort(clusterRotate, new Comparator<List<Taxi>>(){
				@Override
				public int compare(List<Taxi>l1, List<Taxi> l2) {
					double v1_start =Double.parseDouble(l1.get(0).getLatitude());
					double v1_end =Double.parseDouble(l1.get(l1.size()-1).getLatitude());
					double v2_start = Double.parseDouble(l2.get(0).getLatitude());
					double v2_end = Double.parseDouble(l2.get(l2.size()-1).getLatitude());
					double min_v1=0.0;
					double min_v2=0.0;
					if(v1_start<v1_end){
						min_v1 = v1_start;
					}else{
						min_v1 = v1_end;
					}
					if(v2_start<v2_end){
						min_v2 = v2_start;
					}else{
						min_v2 = v2_end;
					}
					if(min_v1 > min_v2)
						return 1;
					if(min_v1 < min_v2)
						return -1;
					return 0;
				}
			});
		}catch(Exception e){
			e.printStackTrace();
		}
 
		double preX = 0.0;
		boolean mark = false;
		for(int i=0;i<P.size();i++){
			Taxi p_sweep=P.get(i);
			CalcBean neighbor = getNeighbor(p_sweep, clusterRotate);
			if(!mark&&neighbor.getCount()>minlns){
				double avgy = neighbor.getSumy();
				double x_before = Double.parseDouble(p_sweep.getLatitude())*cos_angle-avgy*sin_angle;
				double y_before = (Double.parseDouble(p_sweep.getLatitude())+sin_angle*avgy*cos_angle-Double.parseDouble(p_sweep.getLatitude())*cos_angle*cos_angle)/sin_angle;
			    Taxi insert = new Taxi();
			    insert.setLatitude(String.valueOf(x_before));
			    insert.setLongtitude(String.valueOf(y_before));
				representative.add(insert);
				preX = Double.parseDouble(p_sweep.getLatitude());
				mark = true;
			}
			else if(neighbor.getCount() >minlns && mark){
				double p_x = Double.parseDouble(p_sweep.getLatitude());
				if(p_x - preX>=smooth){
					double avgy = neighbor.getSumy()/neighbor.getCount();
					double x_before_else = p_x *cos_angle - avgy*sin_angle;
					double y_before_else =(p_x+avgy*sin_angle*cos_angle-p_x*cos_angle*cos_angle)/sin_angle;
					Taxi insert_else = new Taxi();
					insert_else.setLatitude(String.valueOf(x_before_else));
					insert_else.setLongtitude(String.valueOf(y_before_else));
					representative.add(insert_else);
					preX = p_x;
				}
			}
		}
       Re_all.add(representative);
 
		return representative;
	}
	 
	private CalcBean getNeighbor(Taxi p,List<List<Taxi>>tracluster){
		CalcBean bean = new CalcBean();
		for(int i = 0 ; i < tracluster.size() ; i++){
			List<Taxi> l = tracluster.get(i);
			double max = Double.parseDouble(l.get(0).getLatitude());
			double min = Double.parseDouble(l.get(l.size()-1).getLatitude());
			if(max < min){
				double temp = max ;
				max = min;
				min = temp;
			}
			if(min > Double.parseDouble(p.getLatitude()))
				break;
			if(min < Double.parseDouble(p.getLatitude()) && max > Double.parseDouble(p.getLatitude())){
				bean.incrementCount();
				bean.incrementSumY( getY(l , p));
			}
		}
		return bean;
}	
	private double getY(List<Taxi> l , Taxi p){
		double start_x = Double.parseDouble( l.get(0).getLatitude());
		double start_y = Double.parseDouble( l.get(0).getLongtitude());
		double end_x = Double.parseDouble( l.get(l.size()-1).getLatitude());
		double end_y = Double.parseDouble( l.get(l.size()-1).getLongtitude());
		double p_x = Double.parseDouble(p.getLatitude());
		double disX = (start_x- end_x);
		if(disX == 0){
			return (end_y - start_y) / 2;
		}
		//斜率
		double k = (start_y - end_y) / disX;
		return (start_y + k * (p_x - start_x));
}
	@Override
	public void MopsiPartition() {
		 //List<List<Taxi>> allTrack = new ArrayList<List<Taxi>>(); 
		//get data from mopsi
		  char_mopsiDao.Delete();
		  ArrayList<String> idList = new ArrayList<String>();
		  List<List<MopsiEntity>> partitioned = new ArrayList<List<MopsiEntity>>();
		 for(int i=0;i<10;i++) {
			 String id = String.valueOf(i+1);
			 idList.add(id);
		 }
		//get route from table mopsi according to the route id
		  int count = 0;
		for(int i=0;i<idList.size();i++) {
			String id = idList.get(i);
			List<MopsiEntity> track = mopsiDao.Search(id, null);
			
		    partitioned = Partition_mopsiSeg(track);//进行MDL划分,对应的Partitioned可以有1条到多条线段，区分标准是id
		    for(int j=0;j<partitioned.size();j++) {
		    	//对于J0到jn每条线段的里面的点的taxiid需要进行修改从而实现用taxi id进行区分的目的
		    	List<MopsiEntity> temp = partitioned.get(j);//一条轨迹
		    	for(int k =0;k<temp.size();k++) {
		    		//进行taxi_id的修改
		    	//	String taxi_id = id+"sub"+j;//新的taxi_id
		    		String taxi_id = id;
		    		temp.get(k).setTaxi_Id(taxi_id);
		    	}
		    	  //存入数据库
		        List<char_mopsiEntity> ce = convertToCharMopsi(temp);
		    	char_mopsiDao.saveAll(ce);
		    	//partitioned.set(j, temp);
		    }
		    //
		     count = count + partitioned.size();
		  
	    
	      //  char_mopsiDao.saveAll(ce);
		}
		//store data into database;
	   System.out.println("the size of tracks:"+count);
	}
	public List<char_mopsiEntity> convertToCharMopsi(List<MopsiEntity> list){
		List<char_mopsiEntity> list_ce = new ArrayList<char_mopsiEntity>();
    	for(int i=0;i<list.size();i++){
    		  MopsiEntity temp_point =  list.get(i);
    		  char_mopsiEntity ce = new char_mopsiEntity();
    		   //start点
    		   String latitudeS = temp_point.getLatitude();
    		   String longtitudeS = temp_point.getLongtitude();
    	      String gps_idS = temp_point.getGPS_Id();
    	      String taxi_idS = temp_point.getTaxi_Id();
    	      String angleS = temp_point.getAngle();
    	      String speedS = temp_point.getSpeed();
    	      String stateS = temp_point.getState();
    	      String timeS = temp_point.getTime();
    	      String angle = temp_point.getAngle();
    	      
    	      ce.setGPS_Id(gps_idS);
    	      ce.setTaxi_Id(taxi_idS);
    	      ce.setAlgor_type("traclus");
    	      ce.setLatitude(latitudeS);
    	      ce.setLongtitude(longtitudeS);
    	      ce.setSpeed(speedS);
    	      ce.setTime(timeS);
    	      ce.setState(stateS);
    	      ce.setAngle(angle);
    	      list_ce.add(ce);
    		}
    	for(int i=0;i<list_ce.size();i++){
    		 char_mopsiEntity ce_first = list_ce.get(i);
    		 String gps_id = ce_first.getGPS_Id();
    		for(int j=i+1;j<list_ce.size();j++){
    			 char_mopsiEntity ce_compare = list_ce.get(j);
        		 String gps_compare = ce_compare.getGPS_Id();
        		 if(gps_id.equals(gps_compare)){
        			 list_ce.remove(j);
        			 j--;
        		 }
    		}
    	}
    	return list_ce;
	}
	public  List<List<MopsiEntity>> Partition_mopsiSeg(List<MopsiEntity> TR){//将一条原始轨迹划分成线段
	    ArrayList<Integer> CP = new ArrayList<Integer>();
	    int startIndex = 0;
	    int length = 1;
	    int len_i = TR.size();//TR里面一共有几个点
	    int count = 0;//CP的计数起点
	    MopsiEntity p1= TR.get(0);//轨迹的第一个点
	    CP.add(0);//将第一个点加入CP中，0代表点在原始轨迹的行号
	    int currIndex;
	    while(startIndex+length<=len_i-1){
	    	currIndex = startIndex + length;
	        double Cost_par = MDL_Mopsi(1,TR,startIndex,currIndex);
	        double Cost_nopar = MDL_Mopsi(0,TR,startIndex,currIndex);
	        
	        if(Cost_par>Cost_nopar){
	        	count = count + 1;
	        	CP.add(currIndex-1);//将当前点的前一个点加入CP
	        	startIndex = currIndex - 1;
	        	length = 1;
	        }else{
	        	length = length + 1;
	        }
	    }
	    count = count + 1;
	    CP.add(TR.size()-1);//将轨迹的最后一个点的行号加入到CP
	   //获得CP之后进行线段的截取
	    CP = uniqueClusterId(CP);//去重复
	    System.out.println("cp"+CP.size());
	    List<List<MopsiEntity>> result =  CutTrajectory_Mopsi(CP,TR);
	    return result;
	    
	    }
	public  List<List<Taxi>> Partition(List<Taxi> TR){//将一条原始轨迹划分成线段
	    ArrayList<Integer> CP = new ArrayList<Integer>();
	    int startIndex = 0;
	    int length = 1;
	    int len_i = TR.size();//TR里面一共有几个点
	    int count = 0;//CP的计数起点
	    Taxi p1= TR.get(0);//轨迹的第一个点
	    CP.add(0);//将第一个点加入CP中，0代表点在原始轨迹的行号
	    int currIndex;
	    while(startIndex+length<=len_i-1){
	    	currIndex = startIndex + length;
	        double Cost_par = MDL(1,TR,startIndex,currIndex);
	        double Cost_nopar = MDL(0,TR,startIndex,currIndex);
	        
	        if(Cost_par>Cost_nopar){
	        	count = count + 1;
	        	CP.add(currIndex-1);//将当前点的前一个点加入CP
	        	startIndex = currIndex - 1;
	        	length = 1;
	        }else{
	        	length = length + 1;
	        }
	    }
	    count = count + 1;
	    CP.add(TR.size()-1);//将轨迹的最后一个点的行号加入到CP
	   //获得CP之后进行线段的截取
	    CP = uniqueClusterId(CP);//去重复
	    List<List<Taxi>> result =  CutTrajectory(CP,TR);
	    return result;
	    
	    }
	public List<List<MopsiEntity>> CutTrajectory_Mopsi(ArrayList<Integer>CP,List<MopsiEntity> trajectory){
		List<List<MopsiEntity>> cutTra = new ArrayList<List<MopsiEntity>>();
		for(int i=0;i<CP.size()-1;i++){
		   int number = CP.get(i);
		   int next = CP.get(i+1);
		   List<MopsiEntity> temp = new ArrayList<MopsiEntity>();
		   if(i!=CP.size()-2){
		   for(int j = number;j<next;j++){
			   MopsiEntity point = trajectory.get(j);
			 temp.add(point);
		   }
		   }else{
			   for(int j = number;j<=next;j++){
				 MopsiEntity point = trajectory.get(j);
				 temp.add(point);
			   }
		   }
		   cutTra.add(temp);
		}
		return cutTra;
	}
	public List<List<Taxi>> CutTrajectory(ArrayList<Integer>CP,List<Taxi> trajectory){
		List<List<Taxi>> cutTra = new ArrayList<List<Taxi>>();
		for(int i=0;i<CP.size()-1;i++){
		   int number = CP.get(i);
		   int next = CP.get(i+1);
		   List<Taxi> temp = new ArrayList<Taxi>();
		   if(i!=CP.size()-2){
		   for(int j = number;j<next;j++){
			 Taxi point = trajectory.get(j);
			 temp.add(point);
		   }
		   }else{
			   for(int j = number;j<=next;j++){
				 Taxi point = trajectory.get(j);
				 temp.add(point);
			   }
		   }
		   cutTra.add(temp);
		}
		return cutTra;
	}
	private double MDL_Mopsi(int mdl,List<MopsiEntity> trajectory,int startIndex,int end) {
        //LH是算两个点的欧几里得距离
		double LH = 0.0;
		double LDH = 0.0;
		//根据行号对TR进行截取
		List<MopsiEntity> partition = new ArrayList<MopsiEntity>();
			for(int i = startIndex;i<=end;i++){
				MopsiEntity temp = trajectory.get(i);
				partition.add(temp);
			}
		if(mdl ==1){//partition
		MopsiEntity start = trajectory.get(startIndex);
		double x_start = Double.parseDouble(start.getLatitude());
		double y_start = Double.parseDouble(start.getLongtitude());
		MopsiEntity endTra = trajectory.get(end);
		double x_end = Double.parseDouble(endTra.getLatitude());
		double y_end = Double.parseDouble(endTra.getLongtitude());
		double x = x_start - x_end;
		double y = y_start - y_end;
		LH = Math.sqrt(x*x+y*y);
		
		
		//计算LDH
		MopsiEntity startPoint = partition.get(0);
		MopsiEntity endPoint =partition.get(partition.size()-1);
		double Perpen_distance = 0.0;
		double angle_distance = 0.0;
		double x_par_start = Double.parseDouble(startPoint.getLatitude());
		double y_par_start = Double.parseDouble(startPoint.getLongtitude());
		double x_par_end = Double.parseDouble(endPoint.getLatitude());
		double y_par_end = Double.parseDouble(endPoint.getLongtitude());
		if(partition.size()>2){
			//如果p1和p2不是相邻的两个点
			for(int i=1;i<partition.size()-1;i++){//i从partition第2个到倒数第2个点
				MopsiEntity point_TR = partition.get(i);
				MopsiEntity point_next = partition.get(i+1);
				double x_S = Double.parseDouble(point_TR.getLatitude());
				double y_S = Double.parseDouble(point_TR.getLongtitude());
				double x_E = Double.parseDouble(point_next.getLatitude());
				double y_E = Double.parseDouble(point_next.getLongtitude());
				if(x_par_start==x_par_end&&y_par_start==y_par_end){
					LDH = 0.0;
				}else{
					double distanceSE = Math.abs((x_S - x_par_start)*(y_par_start - y_par_end)-(x_par_start-x_par_end)*(y_S - y_par_start))/Math.sqrt((x_par_start - x_par_end)*(x_par_start - x_par_end) + (y_par_start -y_par_end)*(y_par_start -y_par_end));                                                
					double distance_par = Math.abs((x_E - x_par_start)*(y_par_start - y_par_end)-(x_par_start-x_par_end)*(y_E - y_par_start))/Math.sqrt((x_par_start - x_par_end)*(x_par_start - x_par_end) + (y_par_start -y_par_end)*(y_par_start -y_par_end));                                       
				    //垂直距离
					Perpen_distance = Perpen_distance + (distanceSE*distanceSE + distance_par*distance_par)/(distanceSE + distance_par);
					//角度距离
					double Li_length = Math.sqrt((x_par_end - x_par_start)*(x_par_end - x_par_start)+(y_par_end - y_par_start)*(y_par_end - y_par_start));
				    double Pi_length = Math.sqrt((x_E -x_S)*(x_E - x_S) + (y_E - y_S)* (y_E - y_S));
				    double cosValue =( (x_par_end - x_par_start) * (x_E - x_S) + (y_par_end - y_par_start) * (y_E - y_S))/(Li_length * Pi_length);
				    angle_distance = angle_distance + Math.sqrt((1-cosValue*cosValue)*((x_par_end - x_par_start)* (x_par_end - x_par_start)+(y_par_end - y_par_start)* (y_par_end - y_par_start)));
				    
				}
			}
			LDH = (Math.log(Perpen_distance)+Math.log(angle_distance))/Math.log(2);
		}else{
			LDH = 0.0;
		}
		}
		else{
			//nopar
			if(partition.size()>1){
			for(int i = 0;i<partition.size()-1;i++){
				double x1= Double.parseDouble(partition.get(i).getLatitude());
				double y1= Double.parseDouble(partition.get(i).getLongtitude());
				double x2= Double.parseDouble(partition.get(i+1).getLatitude());
				double y2= Double.parseDouble(partition.get(i+1).getLongtitude());
				LH  = LH + Math.sqrt((x1-x2)*(x1-x2)+(y1-y2)*(y1-y2));
			}}
			else{
				LH = 0.0;
			}
		}
       double result  = LH+LDH;
		return result;
	}
	private double MDL( int mdl,List<Taxi> trajectory,int startIndex,int end) {
        //LH是算两个点的欧几里得距离
		double LH = 0.0;
		double LDH = 0.0;
		//根据行号对TR进行截取
		List<Taxi> partition = new ArrayList<Taxi>();
			for(int i = startIndex;i<=end;i++){
				Taxi temp = trajectory.get(i);
				partition.add(temp);
			}
		if(mdl ==1){//partition
		Taxi start = trajectory.get(startIndex);
		double x_start = Double.parseDouble(start.getLatitude());
		double y_start = Double.parseDouble(start.getLongtitude());
		Taxi endTra = trajectory.get(end);
		double x_end = Double.parseDouble(endTra.getLatitude());
		double y_end = Double.parseDouble(endTra.getLongtitude());
		double x = x_start - x_end;
		double y = y_start - y_end;
		LH = Math.sqrt(x*x+y*y);
		
		
		//计算LDH
		Taxi startPoint = partition.get(0);
		Taxi endPoint =partition.get(partition.size()-1);
		double Perpen_distance = 0.0;
		double angle_distance = 0.0;
		double x_par_start = Double.parseDouble(startPoint.getLatitude());
		double y_par_start = Double.parseDouble(startPoint.getLongtitude());
		double x_par_end = Double.parseDouble(endPoint.getLatitude());
		double y_par_end = Double.parseDouble(endPoint.getLongtitude());
		if(partition.size()>2){
			//如果p1和p2不是相邻的两个点
			for(int i=1;i<partition.size()-1;i++){//i从partition第2个到倒数第2个点
				Taxi point_TR = partition.get(i);
				Taxi point_next = partition.get(i+1);
				double x_S = Double.parseDouble(point_TR.getLatitude());
				double y_S = Double.parseDouble(point_TR.getLongtitude());
				double x_E = Double.parseDouble(point_next.getLatitude());
				double y_E = Double.parseDouble(point_next.getLongtitude());
				if(x_par_start==x_par_end&&y_par_start==y_par_end){
					LDH = 0.0;
				}else{
					double distanceSE = Math.abs((x_S - x_par_start)*(y_par_start - y_par_end)-(x_par_start-x_par_end)*(y_S - y_par_start))/Math.sqrt((x_par_start - x_par_end)*(x_par_start - x_par_end) + (y_par_start -y_par_end)*(y_par_start -y_par_end));                                                
					double distance_par = Math.abs((x_E - x_par_start)*(y_par_start - y_par_end)-(x_par_start-x_par_end)*(y_E - y_par_start))/Math.sqrt((x_par_start - x_par_end)*(x_par_start - x_par_end) + (y_par_start -y_par_end)*(y_par_start -y_par_end));                                       
				    //垂直距离
					Perpen_distance = Perpen_distance + (distanceSE*distanceSE + distance_par*distance_par)/(distanceSE + distance_par);
					//角度距离
					double Li_length = Math.sqrt((x_par_end - x_par_start)*(x_par_end - x_par_start)+(y_par_end - y_par_start)*(y_par_end - y_par_start));
				    double Pi_length = Math.sqrt((x_E -x_S)*(x_E - x_S) + (y_E - y_S)* (y_E - y_S));
				    double cosValue =( (x_par_end - x_par_start) * (x_E - x_S) + (y_par_end - y_par_start) * (y_E - y_S))/(Li_length * Pi_length);
				    angle_distance = angle_distance + Math.sqrt((1-cosValue*cosValue)*((x_par_end - x_par_start)* (x_par_end - x_par_start)+(y_par_end - y_par_start)* (y_par_end - y_par_start)));
				    
				}
			}
			LDH = (Math.log(Perpen_distance)+Math.log(angle_distance))/Math.log(2);
		}else{
			LDH = 0.0;
		}
		}
		else{
			//nopar
			if(partition.size()>1){
			for(int i = 0;i<partition.size()-1;i++){
				double x1= Double.parseDouble(partition.get(i).getLatitude());
				double y1= Double.parseDouble(partition.get(i).getLongtitude());
				double x2= Double.parseDouble(partition.get(i+1).getLatitude());
				double y2= Double.parseDouble(partition.get(i+1).getLongtitude());
				LH  = LH + Math.sqrt((x1-x2)*(x1-x2)+(y1-y2)*(y1-y2));
			}}
			else{
				LH = 0.0;
			}
		}
       double result  = LH+LDH;
		return result;
	}
   
 //controller调用的聚类方法
	@Override
	public List<List<Taxi>> clusterTrajectory(List<List<Taxi>> trajectory,double[][] similarity) {
		double minlns = 2;
	/*	List<List<Taxi>>partition= new ArrayList<List<Taxi>>();
		for(int i=0;i<trajectory.size();i++){
			List<List<Taxi>> temp = Partition(trajectory.get(i));//拆分得到的几条线段
		    partition.addAll(temp);
		}
		*/
		 
		 
		 HashMap<Integer, ArrayList<Integer>> resultCluster = TRACLUS(trajectory,similarity);
         List<List<Taxi>> resultRepre = new ArrayList<List<Taxi>>();
	   for(int i=0;i<resultCluster.size();i++){
	    //	 for(int i=1;i<2;i++){
	    	List<Taxi> temp = Representative(resultCluster.get(i), trajectory, minlns);
	    	for(int j=0;j<temp.size();j++){
	    		double longi= Double.parseDouble(temp.get(j).getLongtitude());
	    		double lati = Double.parseDouble(temp.get(j).getLatitude());
	    		if(longi>180||longi<(-1)*180){
	    			temp.remove(j);
	    		}
	    		if(lati>90||lati<(-1)*90){
	    			temp.remove(j);
	    		}
	    	}
	    	resultRepre.add(temp);
	    } 
	  
		return resultRepre;
	}
	//使用DBSCAN变种的方法进行聚类，eps=1.6,minlns=11

	@Override
	public List<Taxi> GetTraclusCharacteristic() {
		// 直接访问数据库获取由traclus生成的characteristic点，表格为新建的表格：Characteristics
		List<Taxi> result = new ArrayList<Taxi>();
		DataMining.GetData gt = new GetData();
		gt.DbStoreHelper();
		result = gt.getTraclusCharacters();
		return result;
	}
	@Override
	public List<CPointCluster> TEST() {
		List<CPointCluster> result = new ArrayList<CPointCluster>();
		clusterDao.Delete();
		ClusterResultEntity testEntity = new ClusterResultEntity();
		testEntity.setClusterId("101");
		testEntity.setCenter_Latitude("5");
		testEntity.setCenter_Longtitude("5");
		testEntity.setCluster_type("grid");
		testEntity.setCp_type("SGA");
		clusterDao.save(testEntity);
		return result;
	}
	@Override
	public List<CPointCluster> KmeansMopsi(String user, int k, int iterator) {
		//从table：char_mopsi中获取点并进行聚类
		//kmeans,参数为：partition_type, clustering_type,k,iterator
		CpointsClustering.Clustering clusterMethod = new Clustering();
		//使用Dao进行结果的存储和获取 
		//step1：获得聚类的结果
	 	List<CPointCluster> result = new ArrayList<>();
	 	List<String> idList = new ArrayList<String>();//根据User获取route的id,比如kuurna的就是1,2,3,4,5.  Pasi的就是6,7,8,9,10
	 	if(user.equals("Kurrna")) {
	 		for(int i=0;i<5;i++) {
	 			idList.add(String.valueOf(i+1));
	 		}
	 	}else if(user.equals("Pasi")) {
	 		for(int i=5;i<10;i++) {
	 			idList.add(String.valueOf(i+1));
	 		}
	 	}
		try {
			//从char_mopsi获取数据
		     
		       List<List<MopsiEntity>> data_routes = new ArrayList<List<MopsiEntity>>();
		       data_routes =	 mopsiDao.SearchForList(idList);
		       //然后把多个List定义的多条route的点放到同一个List里面来聚类
		       List<MopsiEntity> data = clusterMethod.PutAllRoutesToList(data_routes);
		       setMopsiRawPoint(data);
		       System.out.println("size of all:"+data.size());
				try {
				 	result = clusterMethod.Final_KmeansMopsi(data, k, iterator);
				} catch (IOException e) {
					e.printStackTrace();
				}  
			 
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		} catch (SQLException e) {
			e.printStackTrace();
		}
		//step2:存入cluster_result(使用ClusterDao)
	/*	clusterDao.Delete();//Truncate操作
		clusterDao.Store(result,string2,string);//加上2个参数:cluster_type,cp_type
		//存入cp_raw
		rawDao.DeleteRaw();
	    rawDao.Store(result,string2,string);
	    
	 */
		
		return result;
	}
	public void setMopsiRawPoint(List<MopsiEntity>data) {
		this.Mopsidata = data;
	}
	public List<MopsiEntity> getMopsiRawPoint(){
		return Mopsidata;
	}
	@Override
	public List<CPointCluster> Kmeans(String string, String string2, int i, int j) {
		//kmeans,参数为：partition_type, clustering_type,k,iterator
		CpointsClustering.Clustering clusterMethod = new Clustering();
		//使用Dao进行结果的存储和获取 
		//step1：获得聚类的结果
	 	List<CPointCluster> result = new ArrayList<>();
		try {
			//从char_traclus或char_SGA中获取数据
			if(string.equals("traclus")){
		     List<char_TraclusEntity> data_traclus = char_traclusDao.Search();
		      List<Taxi> data =ConvertTypeToTaxiTraclus(data_traclus);
		     /*实验部分
		      将data得到data_part
		      */
		  //   List<Taxi> data_part = taxiPartial(data);
		     ArrayList<Line> segments = pointsToLine(data);
				try {
				 	result = clusterMethod.Final_Kmeans(segments,data,string, string2, i, j);
				//	result = clusterMethod.Final_Kmeans(segments,data_part,string, string2, i, j);
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}  
			}else{
				  List<char_SGAEntity> data_sga = char_sgaDao.Search();
				     List<Taxi> data =ConvertTypeToTaxiSGA(data_sga);
				     ArrayList<Line> segments = pointsToLine(data);
					try {
						result = clusterMethod.Final_Kmeans(segments,data,string, string2, i, j);
					} catch (IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
			}
		} catch (ClassNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		//step2:存入cluster_result(使用ClusterDao)
		clusterDao.Delete();//Truncate操作
		clusterDao.Store(result,string2,string);//加上2个参数:cluster_type,cp_type
		//存入cp_raw
		rawDao.DeleteRaw();
	    rawDao.Store(result,string2,string);
		return result;
	}
	public List<Taxi> taxiPartial(List<Taxi>data){
		List<Taxi> result = new ArrayList<>();
		List<String> taxiIdList = new ArrayList<>();
		for(int i=0;i<data.size();i++){
			//线段的划分是基于原始轨迹的，而原始轨迹以taxiid作为划分依据
			String taxiId = data.get(i).getTaxi_Id();
			if(!taxiIdList.contains(taxiId)){
				taxiIdList.add(taxiId);
			}
		}
		 // for(int i=0;i<taxiIdList.size();i++){
		   for(int i=0;i<10;i++){
			String id = taxiIdList.get(i);
			for(int j=0;j<data.size();j++){
				Taxi entity = data.get(j);
				if(entity.getTaxi_Id().equals(id)){
					result.add(entity);
				}
			}
		}    
		return result;
	}
	public ArrayList<Line> pointsToLine(List<Taxi>data){
		        // 把List<RawEntity>转化为ArrayList<Line>
				ArrayList<Line> result = new ArrayList<>();
				List<String> taxiIdList = new ArrayList<>();
				for(int i=0;i<data.size();i++){
					//线段的划分是基于原始轨迹的，而原始轨迹以taxiid作为划分依据
					String taxiId = data.get(i).getTaxi_Id();
					if(!taxiIdList.contains(taxiId)){
						taxiIdList.add(taxiId);
					}
				}
				    for(int i=0;i<taxiIdList.size();i++){
				   //for(int i=0;i<10;i++){
					String id = taxiIdList.get(i);
					List<Taxi> rawList = new ArrayList<>();
					for(int j=0;j<data.size();j++){
						Taxi entity = data.get(j);
						if(entity.getTaxi_Id().equals(id)){
							rawList.add(entity);
						}
					}
					//连成Line,例如三个点连成两条线
					for(int k=0;k<rawList.size()-1;k++){
						Taxi raw = rawList.get(k);
						Taxi rawNext = rawList.get(k+1);
						Line line = new Line();
						line =TaxiToLine(raw,rawNext);
						result.add(line);
					}
				}
				return result;
	}
	public List<String> NumberOfClusterMopsi(List<MopsiEntity> data) {
		List<String> taxiIdList = new ArrayList<>();
		for(int i=0;i<data.size();i++){
			//线段的划分是基于原始轨迹的，而原始轨迹以taxiid作为划分依据
			String taxiId = data.get(i).getTaxi_Id();
			if(!taxiIdList.contains(taxiId)){
				taxiIdList.add(taxiId);
			}
		}
		return taxiIdList ; 
	}
	public ArrayList<Line> pointsToLineMopsi(List<MopsiEntity>data){
        // 把List<RawEntity>转化为ArrayList<Line>
		ArrayList<Line> result = new ArrayList<>();
		List<String> taxiIdList = new ArrayList<>();
		for(int i=0;i<data.size();i++){
			//线段的划分是基于原始轨迹的，而原始轨迹以taxiid作为划分依据
			String taxiId = data.get(i).getTaxi_Id();
			if(!taxiIdList.contains(taxiId)){
				taxiIdList.add(taxiId);
			}
		}
		    for(int i=0;i<taxiIdList.size();i++){
		   //for(int i=0;i<10;i++){
			String id = taxiIdList.get(i);
			List<MopsiEntity> rawList = new ArrayList<>();
			for(int j=0;j<data.size();j++){
				MopsiEntity entity = data.get(j);
				if(entity.getTaxi_Id().equals(id)){
					rawList.add(entity);
				}
			}
			//连成Line,例如三个点连成两条线
			for(int k=0;k<rawList.size()-1;k++){
				MopsiEntity raw = rawList.get(k);
				MopsiEntity rawNext = rawList.get(k+1);
				Line line = new Line();
				line =TaxiToLineMopsi(raw,rawNext);
				String clusterid = raw.getTaxi_Id();
				line.setMopsiId(clusterid);
				result.add(line);
			}
		}
		return result;
}
	private Line TaxiToLine(Taxi raw, Taxi rawNext) {
		Line line = new Line();
		//将两个entity转化成为Point，然后连成line
		DataMining.Point s = new DataMining.Point();
		DataMining.Point e = new DataMining.Point();
		double s_x = Double.valueOf(raw.getLatitude());
		double s_y = Double.valueOf(raw.getLongtitude());
		double e_x = Double.valueOf(rawNext.getLatitude());
		double e_y = Double.valueOf(rawNext.getLongtitude());
		s.setX(s_x);
		s.setTaxi_id(raw.getTaxi_Id());
		s.setY(s_y);
		e.setX(e_x);
		e.setY(e_y);
		e.setTaxi_id(raw.getTaxi_Id());
		line.setS(s);
		line.setE(e);
		return line;
	}
	private Line TaxiToLineMopsi(MopsiEntity raw, MopsiEntity rawNext) {
		Line line = new Line();
		//将两个entity转化成为Point，然后连成line
		DataMining.Point s = new DataMining.Point();
		DataMining.Point e = new DataMining.Point();
		double s_x = Double.valueOf(raw.getLatitude());
		double s_y = Double.valueOf(raw.getLongtitude());
		double e_x = Double.valueOf(rawNext.getLatitude());
		double e_y = Double.valueOf(rawNext.getLongtitude());
		s.setX(s_x);
		s.setTaxi_id(raw.getTaxi_Id());
		s.setY(s_y);
		e.setX(e_x);
		e.setY(e_y);
		e.setTaxi_id(raw.getTaxi_Id());
		line.setS(s);
		line.setE(e);
		return line;
	}
	private List<Taxi> ConvertTypeToTaxiSGA(List<char_SGAEntity> search) {
		 List<Taxi> list = new ArrayList<Taxi>();
		  //将char_KmeansEntity 改变成taxi
		  for(int i=0;i<search.size();i++){
			  char_SGAEntity kE = search.get(i);
			  String gpsid = kE.getGPS_Id();
			  String taxiId = kE.getTaxi_Id();
			  String lon = kE.getLongtitude();
			  String lat = kE.getLatitude();
			  String speed = kE.getSpeed();
			  String angle = kE.getAngle();
			  String state = kE.getState();
			  String time = kE.getTime();
		//	  String algor_type = kE.getAlgor_type();
			  Taxi taxi = new Taxi();
			  taxi.setGPS_Id(gpsid);
			  taxi.setTaxi_Id(taxiId);
			  taxi.setLongtitude(lon);
			  taxi.setLatitude(lat);
			  taxi.setSpeed(speed);
			  taxi.setAngle(angle);
			  taxi.setTime(time);
			  taxi.setState(state);
			  list.add(taxi);
		  }
			return list; 
	}
    private List<char_mopsiEntity> convertMopsiEntity(List<List<Taxi>> partitioned){
    	//考虑如果说partition之后会存在有一些子轨迹，那么是不是要让column增加一列，叫做subID,根据这些subId,
    	//可以按照时间排序得到一些子轨迹，并给这些子轨迹加上clusterID,在Mopsi当中就是route id,然后再计算代表轨迹
    	
    	List<char_mopsiEntity> list = new ArrayList<char_mopsiEntity>();
    	/* for(int i=0;i<partitioned.size();i++){
   		  char_mopsiEntity mE = new char_mopsiEntity();//需要转化成为的类型
   		  Taxi temp = partitioned.get(i);//原来类型
   		  String gpsid = temp.get
   		  String taxiId = mE.getTaxi_Id();
   		  String lon = mE.getLongtitude();
   		  String lat = mE.getLatitude();
   		  String speed = kE.getSpeed();
   		  String angle = kE.getAngle();
   		  String state = kE.getState();
   		  String time = kE.getTime();
   	//	  String algor_type = kE.getAlgor_type();
   		  Taxi taxi = new Taxi();
   		  taxi.setGPS_Id(gpsid);
   		  taxi.setTaxi_Id(taxiId);
   		  taxi.setLongtitude(lon);
   		  taxi.setLatitude(lat);
   		  taxi.setSpeed(speed);
   		  taxi.setAngle(angle);
   		  taxi.setTime(time);
   		  taxi.setState(state);
   		  list.add(mE);
   	  }*/
    	return list;
    	 
    }
	private List<Taxi> ConvertTypeToTaxiTraclus(List<char_TraclusEntity> search) {
	  List<Taxi> list = new ArrayList<Taxi>();
	  //将char_KmeansEntity 改变成taxi
	  for(int i=0;i<search.size();i++){
		  char_TraclusEntity kE = search.get(i);
		  String gpsid = kE.getGPS_Id();
		  String taxiId = kE.getTaxi_Id();
		  String lon = kE.getLongtitude();
		  String lat = kE.getLatitude();
		  String speed = kE.getSpeed();
		  String angle = kE.getAngle();
		  String state = kE.getState();
		  String time = kE.getTime();
	//	  String algor_type = kE.getAlgor_type();
		  Taxi taxi = new Taxi();
		  taxi.setGPS_Id(gpsid);
		  taxi.setTaxi_Id(taxiId);
		  taxi.setLongtitude(lon);
		  taxi.setLatitude(lat);
		  taxi.setSpeed(speed);
		  taxi.setAngle(angle);
		  taxi.setTime(time);
		  taxi.setState(state);
		  list.add(taxi);
	  }
		return list; 
	}
	private List<MopsiEntity> ConvertTypeToMopsiEntity(List<char_mopsiEntity> search) {
		  List<MopsiEntity> list = new ArrayList<MopsiEntity>();
		  //将char_KmeansEntity 改变成taxi
		  for(int i=0;i<search.size();i++){
			  char_mopsiEntity kE = search.get(i);
			  String gpsid = kE.getGPS_Id();
			  String taxiId = kE.getTaxi_Id();
			  String lon = kE.getLongtitude();
			  String lat = kE.getLatitude();
			  String speed = kE.getSpeed();
			  String angle = kE.getAngle();
			  String state = kE.getState();
			  String time = kE.getTime();
		//	  String algor_type = kE.getAlgor_type();
			  MopsiEntity taxi = new MopsiEntity();
			  taxi.setGPS_Id(gpsid);
			  taxi.setTaxi_Id(taxiId);
			  taxi.setLongtitude(lon);
			  taxi.setLatitude(lat);
			  taxi.setSpeed(speed);
			  taxi.setAngle(angle);
			  taxi.setTime(time);
			  taxi.setState(state);
			  list.add(taxi);
		  }
			return list; 
		}
	private List<List<Taxi>> ConvertTypeToTaxiTraclus_dbscan(List<char_TraclusEntity> search) {
		//变成List<List<Taxi>>
		  List<List<Taxi>> list = new ArrayList<List<Taxi>>();
		  //首先获取来自search的所有的taxi_id
		  ArrayList<String> idListArray = new ArrayList<String>();
		  Set<String> idList = new HashSet();
			for(int i=0;i<search.size();i++){
				idList.add(search.get(i).getTaxi_Id());
			}
			Iterator iter = idList.iterator();
			while(iter.hasNext()){
				//将set里面的东西放到ArrayList里面
				idListArray.add((String)iter.next());
			}
		  //将char_KmeansEntity 改变成taxi
		   for(int i=0;i<idListArray.size();i++){
			 //for(int i=0;i<100;i++){
			  String id = idListArray.get(i);
			  List<Taxi> tempList = new ArrayList<Taxi>();
			  for(int j=0;j<search.size();j++){
			  //这里需要根据idList里面的东西将轨迹分为List<List<Taxi>>
			  char_TraclusEntity kE = search.get(j);
			  if(kE.getTaxi_Id().equals(id)){
			  String gpsid = kE.getGPS_Id();
			  String taxiId = kE.getTaxi_Id();
			  String lon = kE.getLongtitude();
			  String lat = kE.getLatitude();
			  String speed = kE.getSpeed();
			  String angle = kE.getAngle();
			  String state = kE.getState();
			  String time = kE.getTime();
		//	  String algor_type = kE.getAlgor_type();
			  Taxi taxi = new Taxi();
			  taxi.setGPS_Id(gpsid);
			  taxi.setTaxi_Id(taxiId);
			  taxi.setLongtitude(lon);
			  taxi.setLatitude(lat);
			  taxi.setSpeed(speed);
			  taxi.setAngle(angle);
			  taxi.setTime(time);
			  taxi.setState(state);
			  tempList.add(taxi);
			  }
			  }
			 list.add(tempList);
		  }
			return list; 
		}
	public List<List<Taxi>> ConvertTypeToTaxiTraclus_dbscan_sga(List<char_SGAEntity> search){
		  List<List<Taxi>> list = new ArrayList<List<Taxi>>();
		  //首先获取来自search的所有的taxi_id
		  ArrayList<String> idListArray = new ArrayList<String>();
		  Set<String> idList = new HashSet();
			for(int i=0;i<search.size();i++){
				idList.add(search.get(i).getTaxi_Id());
			}
			Iterator iter = idList.iterator();
			while(iter.hasNext()){
				//将set里面的东西放到ArrayList里面
				idListArray.add((String)iter.next());
			}
		  //将char_KmeansEntity 改变成taxi
		  for(int i=0;i<idListArray.size();i++){
			  String id = idListArray.get(i);
			  List<Taxi> tempList = new ArrayList<Taxi>();
			  for(int j=0;j<search.size();j++){
			  //这里需要根据idList里面的东西将轨迹分为List<List<Taxi>>
			  char_SGAEntity kE = search.get(j);
			  if(kE.getTaxi_Id().equals(id)){
			  String gpsid = kE.getGPS_Id();
			  String taxiId = kE.getTaxi_Id();
			  String lon = kE.getLongtitude();
			  String lat = kE.getLatitude();
			  String speed = kE.getSpeed();
			  String angle = kE.getAngle();
			  String state = kE.getState();
			  String time = kE.getTime();
		//	  String algor_type = kE.getAlgor_type();
			  Taxi taxi = new Taxi();
			  taxi.setGPS_Id(gpsid);
			  taxi.setTaxi_Id(taxiId);
			  taxi.setLongtitude(lon);
			  taxi.setLatitude(lat);
			  taxi.setSpeed(speed);
			  taxi.setAngle(angle);
			  taxi.setTime(time);
			  taxi.setState(state);
			  tempList.add(taxi);
			  }
			  }
			 list.add(tempList);
		  }
			return list; 
	}
	@Override
	public List<CPointCluster> Dbscan(String string, String string2, double eps, int minlns) {
		
		CpointsClustering.Clustering clusterMethod = new Clustering();
		List<CPointCluster> result = new ArrayList<>();
		try {
			if(string.equals("traclus")){
			     List<char_TraclusEntity> data_traclus = char_traclusDao.Search();
			     List<Taxi> data =ConvertTypeToTaxiTraclus(data_traclus);
			   //  List<Taxi> data_part = taxiPartial(data);
				//	result = clusterMethod.Final_Dbscan(data,string, string2, eps, minlns);
			     try {
					//result = clusterMethod.Final_Dbscan(data_part,string, string2, eps, minlns);
					result = clusterMethod.Final_Dbscan(data,string, string2, eps, minlns);
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				}else{
					  List<char_SGAEntity> data_sga = char_sgaDao.Search();
					     List<Taxi> data =ConvertTypeToTaxiSGA(data_sga);
						try {
							result = clusterMethod.Final_Dbscan(data,string, string2, eps, minlns);
						} catch (IOException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
				}
		} catch (ClassNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		//step2:存入cluster_result(使用ClusterDao)
		clusterDao.Delete();//Truncate操作
		clusterDao.Store(result,string2,string);//加上2个参数:cluster_type,cp_type
		//存入cp_raw
		rawDao.DeleteRaw();
	    rawDao.Store(result,string2,string);
		return result;
	}
	@Override
	public List<Taxi> ClusterRawData() {
		//展示raw data
	 	GetData gt = new GetData();
		gt.DbStoreHelper();
		List<Taxi> result = new ArrayList<Taxi>();
		try {
			result = gt.getRawData("kmeans","traclus");
		} catch (ClassNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return result;
		 
	}
    public boolean Exist(String id,ArrayList<String> list){
         //判断id是否在idList当中，不在则返回false    	
    	boolean flag = false;
    	for(int i=0;i<list.size();i++){
    		String temp = list.get(i);
    		if(temp.equals(id)){
    			flag = true;
    			break;//若id在list当中就直接返回true
    		}
    	}
    	
    	return flag;
    }
    
	public List<List<RawEntity>> MiningTypeClusterRawDataToLineSeg(String cp,String cluster_type){
           //从cp_raw里面获取数据用于展示
		List<List<RawEntity>> result = new ArrayList<List<RawEntity>>();
		List<List<RawEntity>> seg_result = new ArrayList<List<RawEntity>>();
		 List<RawEntity>  All_point = new ArrayList< RawEntity >() ;//获取所有的点
		if(cp!=null){
		if(cp.equals("traclus")){
		 All_point = rawDao.Search(cluster_type,"traclus");
		}else{
			All_point = rawDao.Search(cluster_type,"SGA");
		}
		} 
		//获取了关于kmeans和cp_type的所有点之后，进行轨迹的划分，划分的依据是taxi_id,即有100条由特征点连起来的第二代轨迹
		//首先获取unique taxi_id
		ArrayList<String> idList = new ArrayList<>();
		for(int i=0;i<All_point.size();i++){
			RawEntity temp = All_point.get(i);
			String taxiId = temp.getTaxi_Id();
			if(!Exist(taxiId,idList)){
				idList.add(taxiId);
				//与此同时生成存放该id对应的List<Taxi>
				List<RawEntity> track = new ArrayList<RawEntity>();
				result.add(track);//加入到线段集合中
			}
		}
		int countSegment =0;
	   //获取了idList之后需要根据id进行分配
		for(int i=0;i<idList.size();i++){
			List<RawEntity> temp_track = result.get(i);
			String temp_id = idList.get(i);
			for(int j=0;j<All_point.size();j++){
				RawEntity temp_point = All_point.get(j);
				String id = temp_point.getTaxi_Id();
				if(id.equals(temp_id)){
					//则把该点加入到对应的track里面
					temp_track.add(temp_point);
					All_point.remove(j);//把第j个点挪去
					j--;
				}
			}//该循环结束后即可获知某Id对应几个特征点
			if(temp_track.size()>1){
			countSegment+= temp_track.size()-1;//
			}
			//将tempSeg划分成线段
			for(int k=0;k<temp_track.size()-1;k++){
				List<RawEntity> segment = new ArrayList<RawEntity>();
				RawEntity seg_track = temp_track.get(k);
				RawEntity next_track = temp_track.get(k+1);
				segment.add(seg_track);
				segment.add(next_track);
				seg_result.add(segment);
			}
			//对每个id查询一遍之后就set到结果里面
			//result.set(i,temp_track);
			
		}
		System.out.println("there are "+ countSegment+" segments");
		return seg_result;
	}

	@Override
	public List<List<RawEntity>> ClusterRawDataToLine(String cp,String cluster_type){
           //从cp_raw里面获取数据用于展示
		List<List<RawEntity>> result = new ArrayList<List<RawEntity>>();
		//展示raw data
		
		//GetData gt = new GetData();
		//gt.DbStoreHelper();
		 List<RawEntity>  All_point = new ArrayList< RawEntity >() ;//获取所有的点
		if(cp!=null){
		if(cp.equals("traclus")){
		 All_point = rawDao.Search(cluster_type,"traclus");
		}else{
			All_point = rawDao.Search(cluster_type,"SGA");
		}
		} 
		//获取了关于kmeans和cp_type的所有点之后，进行轨迹的划分，划分的依据是taxi_id,即有100条由特征点连起来的第二代轨迹
		//首先获取unique taxi_id
		ArrayList<String> idList = new ArrayList<>();
		for(int i=0;i<All_point.size();i++){
			RawEntity temp = All_point.get(i);
			String taxiId = temp.getTaxi_Id();
			if(!Exist(taxiId,idList)){
				idList.add(taxiId);
				//与此同时生成存放该id对应的List<Taxi>
				List<RawEntity> track = new ArrayList<RawEntity>();
				result.add(track);//加入到线段集合中
			}
		}
		int countSegment =0;
	   //获取了idList之后需要根据id进行分配
	 	for(int i=0;i<idList.size();i++){
		//	for(int i=0;i<10;i++){
			List<RawEntity> temp_track = result.get(i);
			String temp_id = idList.get(i);
			for(int j=0;j<All_point.size();j++){
				RawEntity temp_point = All_point.get(j);
				String id = temp_point.getTaxi_Id();
				if(id.equals(temp_id)){
					//则把该点加入到对应的track里面
					temp_track.add(temp_point);
					All_point.remove(j);//把第j个点挪去
					j--;
				}
			}//该循环结束后即可获知某Id对应几个特征点
			if(temp_track.size()>1){
			countSegment+= temp_track.size()-1;//
			}
			//对每个id查询一遍之后就set到结果里面
			result.set(i,temp_track);
		}
		System.out.println("there are "+ countSegment+" segments");
		return result;
	}
	@Override
	public List<CPointCluster> Hierarchical_Agnes(String string, String string2, int n) {
		//参数为：partition_type, clustering_type,n
		CpointsClustering.Clustering clusterMethod = new Clustering();
		List<CPointCluster> result = new ArrayList<>();
		if(string.equals("traclus")){
		     List<char_TraclusEntity> data_traclus = char_traclusDao.Search();
		     List<Taxi> data =ConvertTypeToTaxiTraclus(data_traclus);
			     // List<char_SGAEntity> data_sga = char_sgaDao.Search();
		     // List<Taxi> data =ConvertTypeToTaxiSGA(data_sga);
		         ArrayList<Line> lineList = pointsToLine(data);
		       //   List<Taxi> data_part = taxiPartial(data);
				try {
					  result = clusterMethod.Final_Hierarchical(lineList,data,string, string2, n);
					//result = clusterMethod.Final_Hierarchical(lineList,data_part,string, string2, n);
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}else{          
				  List<char_SGAEntity> data_sga = char_sgaDao.Search();
				     List<Taxi> data =ConvertTypeToTaxiSGA(data_sga);
				     ArrayList<Line> lineList = pointsToLine(data);
					try {
						result = clusterMethod.Final_Hierarchical(lineList,data,string, string2, n);
					} catch (IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
			}
		//result = clusterMethod.Final_Hierarchical(string, string2, n);
		//step2:存入cluster_result(使用ClusterDao)
		clusterDao.Delete();//Truncate操作
		clusterDao.Store(result,string2,string);//加上2个参数:cluster_type,cp_type
		//存入cp_raw
		rawDao.DeleteRaw();
	    rawDao.Store(result,string2,string);
		return result;
	}

	@Override
	public List<CPointCluster> Hierarchical_Grid(String string, String string2, int m, int nx, int ny) {
		//参数为：partition_type, clustering_type,n
		CpointsClustering.Clustering clusterMethod = new Clustering();
		List<CPointCluster> result = new ArrayList<>();
		if(string.equals("traclus")){
		     List<char_TraclusEntity> data_traclus = char_traclusDao.Search();
		     List<Taxi> data =ConvertTypeToTaxiTraclus(data_traclus);
		     ArrayList<Line> lineList = pointsToLine(data);
		   //  List<Taxi> data_part = taxiPartial(data);
				try {
				 	result = clusterMethod.Final_Grid(lineList,data,string, string2, m,nx,ny);
					//result = clusterMethod.Final_Grid(lineList,data_part,string, string2, m,nx,ny);
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}else{
				  List<char_SGAEntity> data_sga = char_sgaDao.Search();
				     List<Taxi> data =ConvertTypeToTaxiSGA(data_sga);
				     ArrayList<Line> lineList = pointsToLine(data);
					try {
						result = clusterMethod.Final_Grid(lineList,data,string, string2, m,nx,ny);
					} catch (IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
			}
			
		//result = clusterMethod.Final_Grid(string, string2, m,nx,ny);
		//step2:存入cluster_result(使用ClusterDao)
		clusterDao.Delete();//Truncate操作
		clusterDao.Store(result,string2,string);//加上2个参数:cluster_type,cp_type
		//存入cp_raw
		rawDao.DeleteRaw();
	    rawDao.Store(result,string2,string);
		return result;
	}

	@Override
	public double MSE(String cluster_type,String cp_type) {
		 if(cluster_type!=null){
			 List<ClusterResultEntity> cluster = clusterDao.SearchForMSE();
			 List<RawEntity> raw = rawDao.Search(cluster_type, cp_type);
			 //计算MSE
			 double distance =0;
			 for(int i =0;i<cluster.size();i++){
				 ClusterResultEntity cluster_point = cluster.get(i);
				 double central_x = Double.parseDouble(cluster_point.getCenter_Latitude());
				 double central_y = Double.parseDouble(cluster_point.getCenter_Latitude());
				 String clusterId = String.valueOf(cluster_point.getClusterId());
				
				 for(int j=0;j<raw.size();j++){
					 RawEntity tempRaw = raw.get(j);
					 if(tempRaw.getCluster_id().equals(clusterId)){
					 double x = Double.parseDouble(tempRaw.getLatitude());
					 double y = Double.parseDouble(tempRaw.getLongtitude());
					 distance +=Math.sqrt(Math.pow(central_x - x,2)+ Math.pow((central_y-y), 2));//原始点和聚类点的间距
					 }
				 }
			 }
			 return distance;
		 }else{
			 return -1;
		 }
	}

	@Override
	public List<List<RawEntity>> GetPointsByClusterId() {
		//根据获得的轨迹集合中的点的cluster_id来划分点集
		List<List<RawEntity>> result = new ArrayList<List<RawEntity>>();
		List<ClusterEntity> clusterResult = new ArrayList<ClusterEntity>();
        //从数据库表格cp_raw获取所有点
		List<RawEntity>  All_point = new ArrayList< RawEntity >() ;//获取所有的点
		All_point = rawDao.SearchAll();
		//从所有点获取unique的cluster_id
		for(int i=0;i<All_point.size();i++){
			RawEntity temp_point = All_point.get(i);
			String cluster_id = temp_point.getCluster_id();
			if(!ExistClusterId(clusterResult,cluster_id)){
				//若不存在，则加入
				ClusterEntity cluster = new ClusterEntity();
				cluster.setCluster_id(cluster_id);
				clusterResult.add(cluster);
			}
		}
		//获取了Unique的点之后，对所有点根据id进行划分
		for(int i=0;i<clusterResult.size();i++){
			ClusterEntity tempCluster = clusterResult.get(i);
			String cluster_id = tempCluster.getCluster_id();
			List<RawEntity> points = tempCluster.getPointList();
			for(int j=0;j<All_point.size();j++){
				RawEntity tempPoint = All_point.get(j);
				String point_clusterId = tempPoint.getCluster_id();
				if(cluster_id.equals(point_clusterId)){
				      points.add(tempPoint);
				      All_point.remove(j);
				      j--;
				}
			}
			tempCluster.setPointList(points);
			clusterResult.set(i,tempCluster);
		}
		for(int i=0;i<clusterResult.size();i++){
			List<RawEntity> list = new ArrayList<RawEntity>();
			list.addAll(clusterResult.get(i).getPointList());
			result.add(list);
		}
		return result;
	}

	private boolean ExistClusterId(List<ClusterEntity> clusterResult, String cluster_id) {
		for(int i=0;i<clusterResult.size();i++){
			String str = clusterResult.get(i).getCluster_id();
			if(cluster_id.equals(str)){
				return true;
			}
		}
		return false;
	}
	public ArrayList<CharacteristicPoints.Point> getTracks(List<Taxi>track_Taxi){
		ArrayList<CharacteristicPoints.Point> Track = new ArrayList<>();
		for(int i=0;i<track_Taxi.size();i++){
    		Taxi taxiPoint = track_Taxi.get(i);
            String x = taxiPoint.getLatitude();
            String y = taxiPoint.getLongtitude();
            String gpsid= taxiPoint.getGPS_Id();
            String taxiid = taxiPoint.getTaxi_Id();
            String speed = taxiPoint.getSpeed();
            String state = taxiPoint.getState();
            String time_taxi = taxiPoint.getTime();
            String angle = taxiPoint.getAngle();
    		CharacteristicPoints.Point temp= new CharacteristicPoints.Point(x,y,gpsid,taxiid,speed,state,angle,time_taxi);
    	    Track.add(temp);
    	}
		return Track;
	}
	public void CopyOfSGA_Experiment(double minAngle, double minStopDuration, double minDis, double maxDis) throws IOException{

		//	char_sgaDao.Delete();
			//获得四个参数后调用Dao层获取100条轨迹记录以及进行SGA算法计算并且存入
		   //首先获取所有的taxi_id
		    List<List<Taxi>> listGetFromDao = new ArrayList<List<Taxi>>();
			Algorithms_SGA am =  new Algorithms_SGA();
			Set<String> idList = taxiDao.getAllTaxiId();
			List<String> id_list = new ArrayList<String>();
			for(String str:idList){
				id_list.add(str);
			}
			for(int i=0;i<100;i++){
				String id = id_list.get(i);
				List<Taxi> tempList = taxiDao.Search(id, null);
				listGetFromDao.add(tempList);
			}
			double avg_similarity=0;
			double maxDisForSGA = 0;
			double minDisForSGA = 0;
			 FileWriter fw = null;
		     fw = new FileWriter("G://data/Experiments/sga_new_time.txt",true);
		      minAngle = 1.631 ;
		    // minStopDuration = 300 ;
		       minDis = 0.01;//当MinDis作为变量时，范围在0.0001到0.04都有值6.0048，往上为0
		      maxDis = 2.0241;
		    //  for(double m=0.0001;m<0.1;m=m+0.01){//这里对实验条件进行改变当maxDis达到0.047,有值得范围0.0001到0.047，都是6.0048
		      for(double m=0.001;m<=3000;m=m+1){
		    	  int countPoint = 0;
		    	 double sum_all = 0;
		    	 minStopDuration  = m;
		    	//  double val_single = -1;
		    	  double val = 1;
			for(int i=0;i<100;i++){
				
				//List<Taxi> track = taxiDao.Search(id, null);
				List<Taxi> track = listGetFromDao.get(i);
				 ArrayList<Point> Points = getTracks(track);
				 Trajectory track_new = new Trajectory(Points);
				 ArrayList<Point> Cluster = new ArrayList<>();
			        try {
					 Cluster = am.algorithm(track_new,minAngle,minStopDuration,minDis,maxDis);
				     //下面是实验部分
					  val =  CalculateSGA(track_new,minAngle,minStopDuration,minDis,maxDis);
				   // WriteIntoTXT(fw,i,minAngle,minStopDuration,minDis,maxDis,val_single);
			        } catch (ParseException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
			        //存入数据库
			        List<char_SGAEntity> ce = convertToSGA(Cluster);
			   //     char_sgaDao.saveAll(ce);
			    	sum_all+=val;
			    	countPoint +=ce.size();
			}
			//System.out.println(sum_all); 
		 	System.out.println("m=:"+m+"and val is:"+sum_all);
		 	WriteIntoTXT(0,countPoint, fw,minAngle,minStopDuration,minDis,maxDis,sum_all);
	   }
             fw.close();
	}
	public void copy2SGA(double minAngle, double minStopDuration, double minDis, double maxDis) throws IOException{

//		char_sgaDao.Delete();
				//获得四个参数后调用Dao层获取100条轨迹记录以及进行SGA算法计算并且存入
			   //首先获取所有的taxi_id
			    List<List<Taxi>> listGetFromDao = new ArrayList<List<Taxi>>();
				Algorithms_SGA am =  new Algorithms_SGA();
				Set<String> idList = taxiDao.getAllTaxiId();
				List<String> id_list = new ArrayList<String>();
				for(String str:idList){
					id_list.add(str);
				}
				for(int i=0;i<100;i++){
					String id = id_list.get(i);
					List<Taxi> tempList = taxiDao.Search(id, null);
					listGetFromDao.add(tempList);
				}
				 FileWriter fw = null;
			     fw = new FileWriter("G://data/Experiments/sga_new_mdl_maxDis_3D_max100_new.txt",true);
			     minStopDuration = 300 ;//不变
			     minDis = 0.01;//当MinDis作为变量时，范围在0.0001到0.04都有值6.0048，往上为0不变
			     
			      minAngle = 30 ;
			    
			      maxDis = 0.1;
			     //  for(double max=0.0001;max<0.1175;max=max+0.01){
			    	//  maxDis = max;
			    	   for(double m=0.0001;m<4;m=m+0.001){//这里对实验条件进行改变当maxDis达到0.047,有值得范围0.0001到0.047，都是6.0048
			     //  for(double m=0.001;m<=3000;m=m+1){
			    	 double sum_all = 0;
			    	  maxDis = m;
			    	//  double val_single = -1;
			    	  double val = 1;
			    	  int countPoint = 0;
				for(int i=0;i<100;i++){//拿一条轨迹进行实验
					//List<Taxi> track = taxiDao.Search(id, null);
					List<Taxi> track = listGetFromDao.get(i);
					 ArrayList<Point> Points = getTracks(track);
					 Trajectory track_new = new Trajectory(Points);
					 ArrayList<Point> Cluster = new ArrayList<>();
				        try {
						 Cluster = am.algorithm(track_new,minAngle,minStopDuration,minDis,maxDis);
					     //下面是实验部分
						//  val =  CalculateSGA(track_new,minAngle,minStopDuration,minDis,maxDis);
					        val = CalculateSGA_MDL(track,track_new,minAngle,minStopDuration,minDis,maxDis);
				        } catch (ParseException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
				        //存入数据库
				        List<char_SGAEntity> ce = convertToSGA(Cluster);
				   //     char_sgaDao.saveAll(ce);
				    	sum_all+=val;
				    	countPoint+=ce.size();
				    	WriteIntoTXT(i,countPoint,fw,minAngle,minStopDuration,minDis,maxDis,val);
				}
				//System.out.println(sum_all); 
			 	System.out.println("m=:"+m+"and val is:"+val+"and p is:"+countPoint);
			 	//WriteIntoTXT(countPoint,fw,minAngle,minStopDuration,minDis,maxDis,val);
		   }
	//}
	             fw.close();
	}
	@Override
	public void SGA(double minAngle, double minStopDuration, double minDis, double maxDis) throws IOException {
	 
                /*
				//获得四个参数后调用Dao层获取100条轨迹记录以及进行SGA算法计算并且存入
			   //首先获取所有的taxi_id
			    List<List<Taxi>> listGetFromDao = new ArrayList<List<Taxi>>();
				Algorithms_SGA am =  new Algorithms_SGA();
				Set<String> idList = taxiDao.getAllTaxiId();
				List<String> id_list = new ArrayList<String>();
				for(String str:idList){
					id_list.add(str);
				}
				for(int i=0;i<100;i++){
					String id = id_list.get(i);
					List<Taxi> tempList = taxiDao.Search(id, null);
					listGetFromDao.add(tempList);
				}
				 FileWriter fw = null;
			     fw = new FileWriter("G://data/Experiments/sga_new_mdl_maxDis_3D_max100_new.txt",true);
			     minStopDuration = 300 ;//不变
			     minDis = 0.01;//当MinDis作为变量时，范围在0.0001到0.04都有值6.0048，往上为0不变
			     
			      minAngle = 30 ;
			    
			      maxDis = 0.1;
			     //  for(double max=0.0001;max<0.1175;max=max+0.01){
			    	//  maxDis = max;
			    	   for(double m=0.0001;m<4;m=m+0.001){//这里对实验条件进行改变当maxDis达到0.047,有值得范围0.0001到0.047，都是6.0048
			     //  for(double m=0.001;m<=3000;m=m+1){
			    	 double sum_all = 0;
			    	  maxDis = m;
			    	//  double val_single = -1;
			    	  double val = 1;
			    	  int countPoint = 0;
				for(int i=0;i<100;i++){//拿一条轨迹进行实验
					//List<Taxi> track = taxiDao.Search(id, null);
					List<Taxi> track = listGetFromDao.get(i);
					 ArrayList<Point> Points = getTracks(track);
					 Trajectory track_new = new Trajectory(Points);
					 ArrayList<Point> Cluster = new ArrayList<>();
				        try {
						 Cluster = am.algorithm(track_new,minAngle,minStopDuration,minDis,maxDis);
					     //下面是实验部分
						//  val =  CalculateSGA(track_new,minAngle,minStopDuration,minDis,maxDis);
					        val = CalculateSGA_MDL(track,track_new,minAngle,minStopDuration,minDis,maxDis);
				        } catch (ParseException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
				        //存入数据库
				        List<char_SGAEntity> ce = convertToSGA(Cluster);
				   //     char_sgaDao.saveAll(ce);
				    	sum_all+=val;
				    	countPoint+=ce.size();
				    	WriteIntoTXT(i,countPoint,fw,minAngle,minStopDuration,minDis,maxDis,val);
				}
				//System.out.println(sum_all); 
			 	System.out.println("m=:"+m+"and val is:"+val+"and p is:"+countPoint);
			 	//WriteIntoTXT(countPoint,fw,minAngle,minStopDuration,minDis,maxDis,val);
		   }
	//}
	             fw.close();
	             */
		 //备份SGA函数
		   //  minStopDuration = 300 ;//不变
		  //   minDis = 0.01;//当MinDis作为变量时，范围在0.0001到0.04都有值6.0048，往上为0不变
		  //   minAngle = 30 ;
		  //    maxDis = 0.3419;//10:0.3922  100:0.3419
			char_sgaDao.Delete();
			//获得四个参数后调用Dao层获取100条轨迹记录以及进行SGA算法计算并且存入
		   //首先获取所有的taxi_id
			Algorithms_SGA am =  new Algorithms_SGA();
			Set<String> idList = taxiDao.getAllTaxiId();
			List<String> id_list = new ArrayList<String>();
			for(String str:idList){
				id_list.add(str);
			}
			double avg_similarity=0;
			double maxDisForSGA = 0;
			double minDisForSGA = 0;
			int lineNum = 0;
			List<List<Taxi>> allTrack = new ArrayList<>();
			for(int i=0;i<100;i++){
				String id = id_list.get(i);
				List<Taxi> track = taxiDao.Search(id, null);
				allTrack.add(track);
				
			}for(int i=0;i<allTrack.size();i++){
				List<Taxi> track = allTrack.get(i);
				 ArrayList<Point> Points = getTracks(track);
				 Trajectory track_new = new Trajectory(Points);
				 ArrayList<Point> Cluster = new ArrayList<>();
			        try {
					 Cluster = am.algorithm(track_new,minAngle,minStopDuration,minDis,maxDis);
					 if(maxDisForSGA<am.maxDis){
						 maxDisForSGA = am.maxDis;
					 }
					 if(minDisForSGA>am.minDis){
						 minDisForSGA = am.minDis;
					 }
					} catch (ParseException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
			        //存入数据库
			        List<char_SGAEntity> ce = convertToSGA(Cluster);
			        lineNum+=ce.size()-1;
			        char_sgaDao.saveAll(ce);
			        //实验中用于计算相似度：豪斯多夫距离测算特征点连接后的距离和原始轨迹的距离
			        List<Taxi> cpoint_track = PointToTaxi(Cluster);
			        avg_similarity += GetDirectionSensitiveHausdorffDistance(track , cpoint_track);
			}
			 System.out.println("haus similarity is "+avg_similarity/100);
			 System.out.println("max distance is "+maxDisForSGA);
			 System.out.println("min distance is "+minDisForSGA);
			 System.out.println("line num is:"+lineNum);
	}
 
  public void copySGA1(double minAngle, double minStopDuration, double minDis, double maxDis) throws IOException{
		 //备份SGA函数
		char_sgaDao.Delete();
		//获得四个参数后调用Dao层获取100条轨迹记录以及进行SGA算法计算并且存入
	   //首先获取所有的taxi_id
		Algorithms_SGA am =  new Algorithms_SGA();
		Set<String> idList = taxiDao.getAllTaxiId();
		List<String> id_list = new ArrayList<String>();
		for(String str:idList){
			id_list.add(str);
		}
		double avg_similarity=0;
		double maxDisForSGA = 0;
		double minDisForSGA = 0;
		int lineNum = 0;
		for(int i=0;i<100;i++){
			String id = id_list.get(i);
			List<Taxi> track = taxiDao.Search(id, null);
			 ArrayList<Point> Points = getTracks(track);
			 Trajectory track_new = new Trajectory(Points);
			 ArrayList<Point> Cluster = new ArrayList<>();
		        try {
				 Cluster = am.algorithm(track_new,minAngle,minStopDuration,minDis,maxDis);
				 if(maxDisForSGA<am.maxDis){
					 maxDisForSGA = am.maxDis;
				 }
				 if(minDisForSGA>am.minDis){
					 minDisForSGA = am.minDis;
				 }
				} catch (ParseException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
		        //存入数据库
		        List<char_SGAEntity> ce = convertToSGA(Cluster);
		        lineNum+=ce.size()-1;
		        char_sgaDao.saveAll(ce);
		        //实验中用于计算相似度：豪斯多夫距离测算特征点连接后的距离和原始轨迹的距离
		        List<Taxi> cpoint_track = PointToTaxi(Cluster);
		        avg_similarity += GetDirectionSensitiveHausdorffDistance(track , cpoint_track);
		}
		 System.out.println("haus similarity is "+avg_similarity/100);
		 System.out.println("max distance is "+maxDisForSGA);
		 System.out.println("min distance is "+minDisForSGA);
		 System.out.println("line num is:"+lineNum);
  }
	private void WriteIntoTXT(int order, int countPoint , FileWriter fw, double minAngle, double minStopDuration, double minDis, double maxDis,
			double val_single) throws IOException {
		//将结果写入文件 
	     try {
	            String c = order+" "+countPoint+" "+minAngle +" "+minStopDuration+" "+minDis+" "+maxDis+" "+val_single+   "\r\n";
	            fw.write(c);
	         } catch (IOException e1) {
	            e1.printStackTrace();
	             System.out.println("写入失败");
	            System.exit(-1);
	        }    
	}
    public double CalculateSGA_MDL(List<Taxi> track, Trajectory track_new, double minAngle, double minStopDuration, double minDis,
			double maxDis) throws ParseException, IOException{
    	double result =0;
		double sum_dis = 0;
		DataMining.Distance disMeasure = new Distance();
	//track_new为原始轨迹，需要对它进行SGA划分之后，计算各线段和它的距离求平均值，方便进行对比，并且写入文件
		Algorithms_SGA am_sga =  new Algorithms_SGA();
		 @SuppressWarnings("unused")
		 //STEP1:划分后获得特征点
		 ArrayList<Point> Cluster_sga =  am_sga.algorithm(track_new, minAngle, minStopDuration, minDis, maxDis);//获取特征点
		 ArrayList<DataMining.Point> CH_points = TurningPointsToDataMining(Cluster_sga);
		 ArrayList<DataMining.Point> raw_points =TurningTaxiToDataMining(track);//原始轨迹
		 result = disMeasure.MDL_VAL(raw_points,  CH_points);
		 return result;
    }
	private ArrayList<DataMining.Point> TurningTaxiToDataMining(List<Taxi> list) {
	  	 ArrayList<DataMining.Point> result = new ArrayList<>();
    	 for(int i=0;i<list.size();i++){
    		 double lat = Double.valueOf(list.get(i).getLatitude());
    		 double lon = Double.valueOf(list.get(i).getLongtitude());
    		 DataMining.Point point = new DataMining.Point();
    		 point.x = lat;
    		 point.y = lon;
    		 result.add(point);
    	 }
    	 
		return result;
	}

	@SuppressWarnings("static-access")
	private double CalculateSGA(Trajectory track_new, double minAngle, double minStopDuration, double minDis,
			double maxDis) throws ParseException, IOException {
		double result =0;
		double sum_dis = 0;
		DataMining.Distance disMeasure = new Distance();
	//track_new为原始轨迹，需要对它进行SGA划分之后，计算各线段和它的距离求平均值，方便进行对比，并且写入文件
		Algorithms_SGA am_sga =  new Algorithms_SGA();
		 @SuppressWarnings("unused")
		 //STEP1:划分后获得特征点
		ArrayList<Point> Cluster_sga =  am_sga.algorithm(track_new, minAngle, minStopDuration, minDis, maxDis);//获取特征点
		 
		  //STEP2：将点变成线段集合
		 ArrayList<Line> lines = new ArrayList<>();
		 ArrayList<DataMining.Point> points = TurningPointsToDataMining(Cluster_sga);
		 lines = CPointToLine(points);
		 //并且：为了求距离，需要把track_new转化为line
		Line rawLine = TurnTrackToLine(track_new);
		//STEP3:将lines里面S点和E点相同的去掉
		for(int i=0;i<lines.size();i++){
			Line temp = lines.get(i);
			if(temp.getE()==null||temp.getS()==null){
				System.out.println("the line appear null"+i);
			}
			if(temp.getE().x==temp.getS().x&&temp.getE().y==temp.getS().y){
				lines.remove(i);//如果线段出现起点和终点一样的情况就丢掉
				i--;
			}
		}
		//STEP4:求线段集和原始线段之间的距离
		if(lines.size()>0){
		 for(int i=0;i<lines.size();i++){
			  sum_dis+=disMeasure.dist(rawLine, lines.get(i));
			// sum_dis+=HausTurn(rawLine, lines.get(i));
		 }
		 result = sum_dis/Double.valueOf(lines.size());
		}
	  return result;
	//	return Cluster_sga.size();
	}
     private Line TurnTrackToLine(Trajectory track_new) {
		//将CharacteristicsPoint中的Trajectory转化为DataMinig的Line
    	 DataMining.Line line = new Line();
		 DataMining.Point s = new DataMining.Point();
		 DataMining.Point e = new DataMining.Point();
		 s.x = track_new.getStart().x;
		 s.y = track_new.getStart().y;
		 e.x = track_new.getEnd().x;
		 e.y = track_new.getEnd().y;
		 line.setS(s);
		 line.setE(e);
		 return line;
	}

	private ArrayList<DataMining.Point> TurningPointsToDataMining(ArrayList<Point> cluster_sga) {
		// 将CharacteristicPoints.Point转化为DataMining.Point
    	 ArrayList<DataMining.Point> result = new ArrayList<>();
    	 for(int i=0;i<cluster_sga.size();i++){
    		 double lat = cluster_sga.get(i).getX();
    		 double lon = cluster_sga.get(i).getY();
    		 DataMining.Point point = new DataMining.Point();
    		 point.x = lat;
    		 point.y = lon;
    		 point.setOrder(cluster_sga.get(i).getOrder());
    		 result.add(point);
    	 }
    	 
		return result;
	}

	public ArrayList<Line> CPointToLine(ArrayList<DataMining.Point> points){
    	 ArrayList<Line> list = new ArrayList<>();
    	 for(int i=0;i<points.size()-1;i++){
    		DataMining.Point s = points.get(i);//起点
    		 DataMining.Point e = points.get(i+1);//终点
    		 Line temp_line = new Line();
    		 temp_line.setS(s);
    		 temp_line.setE(e);
    		 list.add(temp_line);
    	 }
    	 return list;
     }
	public void CopySGA(double minAngle, double minStopDuration, double minDis, double maxDis) throws IOException {
		 //备份SGA函数
		char_sgaDao.Delete();
		//获得四个参数后调用Dao层获取100条轨迹记录以及进行SGA算法计算并且存入
	   //首先获取所有的taxi_id
		Algorithms_SGA am =  new Algorithms_SGA();
		Set<String> idList = taxiDao.getAllTaxiId();
		List<String> id_list = new ArrayList<String>();
		for(String str:idList){
			id_list.add(str);
		}
		double avg_similarity=0;
		double maxDisForSGA = 0;
		double minDisForSGA = 0;
		for(int i=0;i<100;i++){
			String id = id_list.get(i);
			List<Taxi> track = taxiDao.Search(id, null);
			 ArrayList<Point> Points = getTracks(track);
			 Trajectory track_new = new Trajectory(Points);
			 ArrayList<Point> Cluster = new ArrayList<>();
		        try {
				 Cluster = am.algorithm(track_new,minAngle,minStopDuration,minDis,maxDis);
				 if(maxDisForSGA<am.maxDis){
					 maxDisForSGA = am.maxDis;
				 }
				 if(minDisForSGA>am.minDis){
					 minDisForSGA = am.minDis;
				 }
				} catch (ParseException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
		        //存入数据库
		        List<char_SGAEntity> ce = convertToSGA(Cluster);
		        
		        char_sgaDao.saveAll(ce);
		        //实验中用于计算相似度：豪斯多夫距离测算特征点连接后的距离和原始轨迹的距离
		        List<Taxi> cpoint_track = PointToTaxi(Cluster);
		        avg_similarity += GetDirectionSensitiveHausdorffDistance(track , cpoint_track);
		}
		 System.out.println("haus similarity is "+avg_similarity/100);
		 System.out.println("max distance is "+maxDisForSGA);
		 System.out.println("min distance is "+minDisForSGA);
	}
    private List<Taxi> PointToTaxi(ArrayList<Point> cluster) {
		List<Taxi> track = new ArrayList<>();
		for(int i=0;i<cluster.size();i++){
			Taxi taxi = new Taxi();
			Point tempPoint = cluster.get(i);
			taxi.setGPS_Id(tempPoint.getGPS_ID());
			taxi.setTaxi_Id(tempPoint.getTaxi_id());
			taxi.setLatitude(String.valueOf(tempPoint.getX()));
			taxi.setLongtitude(String.valueOf(tempPoint.getY()));
			taxi.setTime(tempPoint.getTime());
			taxi.setAngle(tempPoint.getAngle());
			taxi.setSpeed(tempPoint.getSpeed());
			taxi.setState(tempPoint.getState());
			track.add(taxi);
		}
		return track;
	}

	public List<char_SGAEntity> convertToSGA(ArrayList<Point> cluster){
    	List<char_SGAEntity> list_ce = new ArrayList<char_SGAEntity>();
    	for(int i=0;i<cluster.size();i++){
    		  CharacteristicPoints.Point temp_point =  cluster.get(i);
    		  char_SGAEntity ce = new char_SGAEntity();
    		   //start点
    		   String latitudeS = String.valueOf(temp_point.getX()-0.002);
    		   String longtitudeS = String.valueOf(temp_point.getY()+0.0045);
    	      String gps_idS = temp_point.getGPS_ID();
    	      String taxi_idS = temp_point.getTaxi_id();
    	      String angleS = temp_point.getAngle();
    	      String speedS = temp_point.getSpeed();
    	      String stateS = temp_point.getState();
    	      String timeS = temp_point.getTime();
    	      String angle = temp_point.getAngle();
    	      
    	      ce.setGPS_Id(gps_idS);
    	      ce.setTaxi_Id(taxi_idS);
    	      ce.setAlgor_type("SGA");
    	      ce.setLatitude(latitudeS);
    	      ce.setLongtitude(longtitudeS);
    	      ce.setSpeed(speedS);
    	      ce.setTime(timeS);
    	      ce.setState(stateS);
    	      ce.setAngle(angle);
    	      list_ce.add(ce);
    		}
    	for(int i=0;i<list_ce.size();i++){
    		 char_SGAEntity ce_first = list_ce.get(i);
    		 String gps_id = ce_first.getGPS_Id();
    		for(int j=i+1;j<list_ce.size();j++){
    			 char_SGAEntity ce_compare = list_ce.get(j);
        		 String gps_compare = ce_compare.getGPS_Id();
        		 if(gps_id.equals(gps_compare)){
        			 list_ce.remove(j);
        			 j--;
        		 }
    		}
    	}
    	return list_ce;
    }

	@Override
	public InfoEntity SetInfo(List<RawEntity> track) {
	InfoEntity info = new InfoEntity();
	String taxiId = track.get(0).getTaxi_Id();//track里面所有点的taxiid是同一个，因为是一条轨迹
    ArrayList<Double> speedList = new ArrayList<>(); 
    double countSpeed = 0.0;
	for(int i=0;i<track.size();i++){
    	 RawEntity temp = track.get(i);
    	 speedList.add(Double.valueOf(temp.getSpeed()));
    	 countSpeed+=Double.valueOf(temp.getSpeed());//速度总和
     }
	double totalDis = 0.0;
	for(int i=0;i<track.size()-1;i++){
		RawEntity p1= track.get(i);
		RawEntity p2 = track.get(i+1);
		double lat1 = Double.parseDouble(p1.getLatitude());
		double lng1 = Double.parseDouble(p1.getLongtitude());
		double lat2 = Double.parseDouble(p2.getLatitude());
		double lng2 = Double.parseDouble(p2.getLongtitude());
		double dis = getFlatternDistance(lat1, lng1, lat2, lng2);
		totalDis+=dis;
	}
     DateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");  
     Date start = null;
	try {
		start = df.parse(track.get(0).getTime());
	} catch (ParseException e) {
		// TODO Auto-generated catch block
		e.printStackTrace();
	}
     Date end = null;
	try {
		end = df.parse(track.get(track.size()-1).getTime());
	} catch (ParseException e) {
		// TODO Auto-generated catch block
		e.printStackTrace();
	}
     
     double totalTime = end.getTime() - start.getTime();
     double maxSpeed = Collections.max(speedList);
     double minSpeed = Collections.min(speedList);
     double avgSpeed = countSpeed/track.size();
     int CpointNum = track.size();
      
     info.setTaxiId(taxiId);
     info.setTotalTime(totalTime);
     info.setAvgSpeed(avgSpeed);
     info.setCpointNum(CpointNum);
     info.setMaxSpeed(maxSpeed);
     info.setMinSpeed(minSpeed);
     info.setTotalDis(totalDis);
     
	return info;
	}
	public double getRad(double d){
        return d*Math.PI/180.0;
    }
	public double  getFlatternDistance(double lat1,double lng1,double lat2,double lng2){ 
		double EARTH_RADIUS = 6378.137;    //单位KM
		double f = getRad((lat1 + lat2)/2); 
		double g = getRad((lat1 - lat2)/2); 
		double l = getRad((lng1 - lng2)/2); 

		double sg = Math.sin(g); 
		double sl = Math.sin(l); 
		double sf = Math.sin(f); 

		double s,c,w,r,d,h1,h2; 
		double a = EARTH_RADIUS; 
		double fl = 1/298.257; 

		sg = sg*sg; 
		sl = sl*sl; 
		sf = sf*sf; 

		s = sg*(1-sl) + (1-sf)*sl; 
		c = (1-sg)*(1-sl) + sf*sl; 

		w = Math.atan(Math.sqrt(s/c)); 
		r = Math.sqrt(s*c)/w; 
		d = 2*w*a; 
		h1 = (3*r -1)/2/c; 
		h2 = (3*r +1)/2/s; 
		return d*(1 + fl*(h1*sf*(1-sg) - h2*(1-sf)*sg)); 
		}

	@Override
	public List<ClusterResultEntity> GetClusterResult() {
	//从cp_result表格里面获得所有点用于voronoi的展示 
		 List<ClusterResultEntity> data =  clusterDao.Search();
		return data;
	}

	@Override
	public List<CPointCluster> ConvertResultEntityToCP(List<ClusterResultEntity> cluster_points) {
		List<CPointCluster> list = new ArrayList<CPointCluster>();
		for(int i=0;i<cluster_points.size();i++){
			CPointCluster cp = new CPointCluster();
			ClusterResultEntity temp_point = cluster_points.get(i);
		  int id = temp_point.getId();
			 String Cluster_id = temp_point.getClusterId();
			 double center_longtitude = Double.parseDouble(temp_point.getCenter_Longtitude());
			 double center_latitude = Double.parseDouble(temp_point.getCenter_Latitude());
			  String cluster_type  = temp_point.getCluster_type();
	      
	       cp.setId(id);
	       cp.setCenterLatitude(center_latitude);
	       cp.setCenterLongtitude(center_longtitude);
	       cp.setCluster_type(cluster_type);
	       list.add(cp);
		}
		return list;
	}

	@Override
	public List<RawForVoronoi> GetRawData() {
		//get all points in table raw_data for voronoi show
		List<RawEntity> All_points = rawDao.SearchAll();
		List<RawForVoronoi> result = new ArrayList<>();
		for(int i=0;i<All_points.size();i++){
			 RawForVoronoi vo = new RawForVoronoi();
			 double lat = Double.parseDouble(All_points.get(i).getLatitude().trim());
			 double lon = Double.parseDouble(All_points.get(i).getLongtitude().trim());
			 vo.setLatitude(lat);
			 vo.setLongtitude(lon);
		/*	 vo.setAngle(All_points.get(i).getAngle());
			 vo.setCluster_id(All_points.get(i).getCluster_id());
			 vo.setCluster_type(All_points.get(i).getCluster_type());
			 vo.setCp_type(All_points.get(i).getCp_type());
			 vo.setGPS_Id(All_points.get(i).getGPS_Id());
			 vo.setSpeed(All_points.get(i).getSpeed());
			 vo.setState(All_points.get(i).getSpeed());
			 vo.setTaxi_Id(All_points.get(i).getTaxi_Id());
			 vo.setTime(All_points.get(i).getTime());
			 vo.setId(All_points.get(i).getId());
			 */
			 result.add(vo);
		}
		return result;
	}
	@Override
	public List<RawForVoronoi> GetRawDataForMopsiVor() {
		//get all points in table mopsiEntity for voronoi show
		//List<RawEntity> All_points = rawDao.SearchAll();//这里要改，根据User从mopsiEntity中获取原始点
		List<MopsiEntity> All_points = getMopsiRawPoint();
		List<RawForVoronoi> result = new ArrayList<>();
		for(int i=0;i<All_points.size();i++){
			 RawForVoronoi vo = new RawForVoronoi();
			 double lat = Double.parseDouble(All_points.get(i).getLatitude().trim());
			 double lon = Double.parseDouble(All_points.get(i).getLongtitude().trim());
			 vo.setLatitude(lat);
			 vo.setLongtitude(lon);
			 result.add(vo);
		}
		return result;
	}
	@Override
   public HashMap<Integer,ArrayList<Line>> Traclus_Experiment(TraClus tra, List<List<Taxi>> data ,double eps,int minlns) {
	   //用在milns的实验部分
		 TraClus tra_exp  = new DataMining.TraClus();
		 ArrayList<DataMining.Trajectory> rTrajectory = new ArrayList<>();
		//rTrajectory = 	tra.getRtrajectory(data,eps,minlns);
		 HashMap<Integer,ArrayList<Line>> cluster = tra.getCluster();
	 	return cluster;
   }
	@Override
	public ArrayList<DataMining.Trajectory> getReKmeansSeg(String partition_type,int k, int iterator) {
		CpointsClustering.Clustering clusterMethod = new Clustering();
		ArrayList<Line>re_segs = new ArrayList<>();
		List<SegClustering> resultAll = new ArrayList<>();
		TraClus traclus = new TraClus();
		if(resultKmeans==null||resultKmeans.size()==0){
		
		// 对kmeans的聚类结果进行获取代表轨迹
		if(partition_type.equals("traclus")){
			 List<char_TraclusEntity> data_traclus = char_traclusDao.Search();
			 List<Taxi> data =ConvertTypeToTaxiTraclus(data_traclus);
			 ArrayList<Line> segments = pointsToLine(data);
				  resultAll = clusterMethod.DoKmeansSeg(segments, k , iterator );  
			}else{
				  List<char_SGAEntity> data_sga = char_sgaDao.Search();
				  List<Taxi> data =ConvertTypeToTaxiSGA(data_sga);
				  ArrayList<Line> segments = pointsToLine(data);
				  resultAll = clusterMethod.DoKmeansSeg(segments, k , iterator );
			}      
		}else{
			resultAll= resultKmeans;
		}
		HashMap<Integer,List<Line>> map = new HashMap<>();
		for(int i=0;i<resultAll.size();i++){
			SegClustering segTemp = resultAll.get(i);
			List<Line> lines = segTemp.getSegments();
			int clusterId = segTemp.getId();
			map.put(clusterId, lines);
		}  
		ClusterNumber = map.size();
		ArrayList<DataMining.Trajectory> reLine = traclus.getRTrajectoryGeneral(map);
		return reLine;
	}
                                  
	@Override
	public ArrayList<DataMining.Trajectory> getReDbscanSeg(String partition_type,double eps, int minlns) {
		System.out.println("hello,we are in traclus rep");     
		TraClus tra  = new DataMining.TraClus();
		
	List<List<Taxi>> data = new ArrayList<List<Taxi>>();
	List<Taxi> dataAll= new ArrayList<>();
	ArrayList<Line> segments  = new ArrayList<>();
		if(partition_type.equals("traclus")){
	        List<char_TraclusEntity> data_traclus = char_traclusDao.Search();
	        data =ConvertTypeToTaxiTraclus_dbscan(data_traclus);
	         dataAll =ConvertTypeToTaxiTraclus(data_traclus); 
	         segments = pointsToLine( dataAll);
		}
		else{
			List<char_SGAEntity> data_sga = char_sgaDao.Search();
		    data =ConvertTypeToTaxiTraclus_dbscan_sga(data_sga);
		     dataAll =ConvertTypeToTaxiSGA(data_sga);
		     segments = pointsToLine(dataAll);
		}
		ArrayList<DataMining.Trajectory> rTrajectory = new ArrayList<>();
		 rTrajectory = 	tra.getRtrajectoryNew(tra,segments,eps,minlns);
		 HashMap<Integer,  ArrayList<Line>> clusterMap = tra.getCluster();
		 HashMap<Integer,List<Line>> map = new HashMap<>();
		 Set<Integer> key = clusterMap.keySet(); 
		 Iterator it = key.iterator();
		 while(it.hasNext()){
			 int id = (int)(it.next());
			 List<Line>lines = clusterMap.get(id);
			 map.put(id,lines);
		 }    
		 ClusterNumber = map.size();
			ArrayList<DataMining.Trajectory> reLine = tra.getRTrajectoryGeneral(map);
			return reLine;
	}

	@Override
	public ArrayList<DataMining.Trajectory> getReHierarchicalSeg(String partition_type,int n) {
		CpointsClustering.Clustering clusterMethod = new Clustering();
		List<SegClustering> resultAll = new ArrayList<>();
		TraClus traclus = new TraClus();
		// 对kmeans的聚类结果进行获取代表轨迹
		if(resultHierarchical==null||resultHierarchical.size()==0){
		if(partition_type.equals("traclus")){
			 List<char_TraclusEntity> data_traclus = char_traclusDao.Search();
			 List<Taxi> data =ConvertTypeToTaxiTraclus(data_traclus);
			 ArrayList<Line> segments = pointsToLine(data);
				  resultAll = clusterMethod.DoHierarchicalSeg(segments, n );  
			}else{
				  List<char_SGAEntity> data_sga = char_sgaDao.Search();
				  List<Taxi> data =ConvertTypeToTaxiSGA(data_sga);
				  ArrayList<Line> segments = pointsToLine(data);
				  resultAll = clusterMethod.DoHierarchicalSeg(segments, n );
			}
		}else{
			resultAll = resultHierarchical;
		}
		HashMap<Integer,List<Line>> map = new HashMap<>();
		for(int i=0;i<resultAll.size();i++){
			SegClustering segTemp = resultAll.get(i);
			List<Line> lines = segTemp.getSegments();
			int clusterId = segTemp.getId();
			map.put(clusterId, lines);
		}
		ClusterNumber = map.size();
		ArrayList<DataMining.Trajectory> reLine = traclus.getRTrajectoryGeneral(map);
		return reLine;
	}
	@SuppressWarnings("static-access")
	@Override
	public  List<DataMining.Trajectory> MopsiRep_DBA(List<List<MopsiEntity>> trajectories,int routes){
	 
		TrajectoryDBA dba = new TrajectoryDBA();
		List<MopsiEntity> Mopsi_avg = new ArrayList<>();//用来存放格式为List<MopsiEntity>的平均轨迹结果
		// 这里需要从Mopsi的表里面得到的多条轨迹
		// 然后传入TrajectoryDBA当中，最后参考DBA.java的main函数获取List<MopsiEntity>
		//averageSequence是一条平均轨迹，点的个数？
		List<MopsiEntity> averageSequence = new ArrayList<MopsiEntity>();//平均轨迹，即代表轨迹
		//这里首先把每条轨迹的长度统一，采取的方法就是把最长的那条轨迹多出来的点加在短的轨迹后面，
		//List<List<MopsiEntity>> commonTra = Normalization_Tras(trajectories);
		int choice = (int) Math.random()*trajectories.size();//1到n条轨迹随意选一条
		//从第choice条轨迹的所有点赋值到平均轨迹
		for(int j=0;j<trajectories.get(choice).size();j++){
			//averageSequence.add(commonTra.get(choice).get(j)) ;
			averageSequence.add(trajectories.get(choice).get(j)) ;
		}
		//求平均
		//Mopsi_avg = dba.Trajectory_DBA(averageSequence, commonTra);
		Mopsi_avg = dba.Trajectory_DBA(averageSequence, trajectories);
		// 得到list之后把里面的点转为Points最后把points放到trajectory即可
		 
		List<DataMining.Trajectory> reLine = dba.MopsiToLine(Mopsi_avg);
		
		return reLine;
	}
	public List<List<MopsiEntity>> Normalization_Tras(List<List<MopsiEntity>> list){
		//这里首先把每条轨迹的长度统一，采取的方法就是把最长的那条轨迹多出来的点加在短的轨迹后面，
		int index = 0;
		//获取最长的trajectory的index
		for(int i=0;i<list.size();i++) {
			if(list.get(index).size()<list.get(i).size()) {
				index = i;
			}
		}
		//以该轨迹为基准，进行统一化长度
		for(int j=0;j<list.size();j++) {
			if(j!=index) {
				for(int k=list.get(j).size();k<list.get(index).size();k++) {
					MopsiEntity temp = new MopsiEntity();
					temp = list.get(index).get(k);//我希望这里是复制而不是同一个地址
					list.get(j).add(temp);
				}
			}
		}
		return list;
	 
	}
	@Override
	public  List<DataMining.Trajectory> MopsiRep(String user,int routes){
	 
		CpointsClustering.Clustering clusterMethod = new Clustering();
		TraClus traclus = new TraClus();
		//使用Dao进行结果的存储和获取 
		//step1：获得聚类的结果
		long startTime = 0;   
		long endTime = 0;
		List<SegClustering> result = new ArrayList<>();
		//从char_mopsi中获取数据      
		 
		 List<char_mopsiEntity> data_mopsi = char_mopsiDao.Search();
		 List<MopsiEntity> data =ConvertTypeToMopsiEntity(data_mopsi);
		 List<String> idList = NumberOfClusterMopsi(data);//获取有几个taxi_id就是几个cluster
		 ArrayList<Line> segments = pointsToLineMopsi(data);
		 //获取有几个cluster
			  startTime = System.currentTimeMillis();    //获取开始时间 
			 // result = clusterMethod.DoKmeansSeg(segments, k , iterator );  
			  result = clusterMethod.SetMopsiCluster(segments,idList);   
			  endTime = System.currentTimeMillis();    //获取结束时间
		//	  resultKmeans = result;
		 
	//	  ClusterNumber = result.size();      
		 
		//System.out.println("time cost is:"+(endTime - startTime) + "ms");
		ArrayList<Integer> idLineList = new ArrayList<Integer>();
		//这里需要计算result.get(i)里面的i是谁
        idLineList = GetRawList_Mopsi(user,routes);
			  
			  
		//System.out.println("there are:"+result.size()+" clusters");
		int clusterId = 1;//这里由于只需要计算一条averaging，所以只有一个cluster，统一设置为1
		List<Line> lines = new ArrayList<Line>();
		HashMap<Integer,List<Line>> map = new HashMap<>();
		for(int i=0;i<idLineList.size();i++){
			int index = idLineList.get(i);
			SegClustering segTemp = result.get(index);
			lines.addAll(segTemp.getSegments());
			//int clusterId = segTemp.getId();
			
		}  
		map.put(clusterId, lines);
	//	ClusterNumber = map.size();
		List<DataMining.Trajectory> reLine = traclus.getRTrajectoryGeneral(map);
		
		return reLine;
	}
	public ArrayList<Integer> GetRawList_Mopsi(String user,int routes){
		ArrayList<Integer> idLineList = new ArrayList<Integer>();
		if(user.equals("Kurrna")) {
			switch (routes) 
			{
			case 2:{
			 for(int i=0;i<2;i++) {
				 idLineList.add(i);
			 }
			 break;
			}
			case 3:{
				for(int i=0;i<3;i++) {
					 idLineList.add(i);
				 }
				 break;
			}
			case 4:{
				for(int i=0;i<4;i++) {
					 idLineList.add(i);
				 }
				 break;
			}
			case 5:{
				for(int i=0;i<5;i++) {
					 idLineList.add(i);
				 }
				 break;
			}
			}
		}else if(user.equals("Pasi")) {
			switch (routes) 
			{
			case 2:{
			 for(int i=5;i<7;i++) {
				 idLineList.add(i);
			 }
			 break;
			}
			case 3:{
				for(int i=5;i<8;i++) {
					 idLineList.add(i);
				 }
				 break;
			}
			case 4:{
				for(int i=5;i<9;i++) {
					 idLineList.add(i);
				 }
				 break;
			}
			case 5:{
				for(int i=5;i<10;i++) {
					 idLineList.add(i);
				 }
				 break;
			}
			}
		}
		return idLineList;
	}
	@Override
	public List<String> GetStringList_Mopsi(String user,int routes){
		List<String> idLineList = new ArrayList<String>();
		if(user.equals("Kurrna")) {
			switch (routes) 
			{
			case 2:{
			 for(int i=0;i<2;i++) {
				 idLineList.add(String.valueOf(i+1));
			 }
			 break;
			}
			case 3:{
				for(int i=0;i<3;i++) {
					 idLineList.add(String.valueOf(i+1));
				 }
				 break;
			}
			case 4:{
				for(int i=0;i<4;i++) {
					 idLineList.add(String.valueOf(i+1));
				 }
				 break;
			}
			case 5:{
				for(int i=0;i<5;i++) {
					 idLineList.add(String.valueOf(i+1));
				 }
				 break;
			}
			}
		}else if(user.equals("Pasi")) {
			switch (routes) 
			{
			case 2:{
			 for(int i=5;i<7;i++) {
				 idLineList.add(String.valueOf(i+1));
			 }
			 break;
			}
			case 3:{
				for(int i=5;i<8;i++) {
					 idLineList.add(String.valueOf(i+1));
				 }
				 break;
			}
			case 4:{
				for(int i=5;i<9;i++) {
					 idLineList.add(String.valueOf(i+1));
				 }
				 break;
			}
			case 5:{
				for(int i=5;i<10;i++) {
					 idLineList.add(String.valueOf(i+1));
				 }
				 break;
			}
			}
		}
		return idLineList;
	}
	@Override
	public ArrayList<Line> ShowSeg_clusterKmeans(String partition_type,int k, int iterator){
		System.out.println("kmeans, k:"+k+"iterator:"+iterator);
		ArrayList<Line>result_segs = new ArrayList<>();
		CpointsClustering.Clustering clusterMethod = new Clustering();
		//使用Dao进行结果的存储和获取 
		//step1：获得聚类的结果
		long startTime = 0;   
		long endTime = 0;
		List<SegClustering> result = new ArrayList<>();
		//从char_traclus或char_SGA中获取数据      
		if(partition_type.equals("traclus")){
		 List<char_TraclusEntity> data_traclus = char_traclusDao.Search();
		 List<Taxi> data =ConvertTypeToTaxiTraclus(data_traclus);
		 ArrayList<Line> segments = pointsToLine(data);
			  startTime = System.currentTimeMillis();    //获取开始时间 
			  result = clusterMethod.DoKmeansSeg(segments, k , iterator );  
			  endTime = System.currentTimeMillis();    //获取结束时间
			  resultKmeans = result;
		}else{
			  List<char_SGAEntity> data_sga = char_sgaDao.Search();
			  List<Taxi> data =ConvertTypeToTaxiSGA(data_sga);
			  ArrayList<Line> segments = pointsToLine(data);
			  startTime = System.currentTimeMillis();    //获取开始时间 
			  result = clusterMethod.DoKmeansSeg(segments, k , iterator );
			  endTime = System.currentTimeMillis();    //获取结束时间
			  resultKmeans = result;
		}
		  ClusterNumber = result.size();      
		for(int i=0;i<result.size();i++){
			result_segs.addAll(result.get(i).getSegments());
		}
		System.out.println("time cost is:"+(endTime - startTime) + "ms");
		System.out.println("there are:"+result.size()+" clusters");
		return result_segs;
	}
	@Override       
	public ArrayList<Line> ShowSeg_clusterHierarchical_Agnes(String partition_type,int n){
		System.out.println("hierarchical clustering,n:"+n);    
		ArrayList<Line>result_segs = new ArrayList<>();
		CpointsClustering.Clustering clusterMethod = new Clustering();
		//使用Dao进行结果的存储和获取 
		//step1：获得聚类的结果
		long startTime = 0;
		long endTime = 0;
		List<SegClustering> result = new ArrayList<>();
		//从char_traclus或char_SGA中获取数据
		if(partition_type.equals("traclus")){
		 List<char_TraclusEntity> data_traclus = char_traclusDao.Search();
		 List<Taxi> data =ConvertTypeToTaxiTraclus(data_traclus);
		 ArrayList<Line> segments = pointsToLine(data);
			  startTime = System.currentTimeMillis();    //获取开始时间 
			result = clusterMethod.DoHierarchicalSeg(segments, n); 
			  endTime = System.currentTimeMillis();    //获取结束时间
			  resultHierarchical = result;
		}else{
			  List<char_SGAEntity> data_sga = char_sgaDao.Search();
			     List<Taxi> data =ConvertTypeToTaxiSGA(data_sga);
			     ArrayList<Line> segments = pointsToLine(data);
			     startTime = System.currentTimeMillis();    //获取开始时间 
			 	 result = clusterMethod.DoHierarchicalSeg(segments, n); 
				 endTime = System.currentTimeMillis();    //获取结束时间
				 resultHierarchical = result;
		}
		  ClusterNumber = result.size();
		for(int i=0;i<result.size();i++){
			result_segs.addAll(result.get(i).getSegments());
		}
		System.out.println("time cost is:"+(endTime - startTime) + "ms");
		System.out.println("there are:"+result.size()+" clusters");
		return result_segs;
	}
	@Override
	public ArrayList<Line> ShowSeg_cluster(String partition_type,double eps,int minlns){
        System.out.println("dbscan, eps:"+eps+"minlins:"+minlns);
		TraClus tra  = new DataMining.TraClus();
		List<List<Taxi>> data = new ArrayList<List<Taxi>>();
		List<Taxi> dataAll= new ArrayList<>();
		ArrayList<Line> segments  = new ArrayList<>();
		if(partition_type.equals("traclus")){
	        List<char_TraclusEntity> data_traclus = char_traclusDao.Search();
	        data =ConvertTypeToTaxiTraclus_dbscan(data_traclus);
	         dataAll =ConvertTypeToTaxiTraclus(data_traclus); 
	         segments = pointsToLine(dataAll);
		}
		else{
			List<char_SGAEntity> data_sga = char_sgaDao.Search();
		    data =ConvertTypeToTaxiTraclus_dbscan_sga(data_sga);
		     dataAll =ConvertTypeToTaxiSGA(data_sga);
		     segments = pointsToLine(dataAll);
		}
		/*List<List<Taxi>> data_part = new ArrayList<>();
		for(int i=0;i<10;i++){
			data_part.add(data.get(i));
		}*/
		ArrayList<DataMining.Trajectory> rTrajectory = new ArrayList<>();
		long startTime = System.currentTimeMillis();    //获取开始时间 
 	 	rTrajectory = 	tra.getRtrajectoryNew(tra,segments,eps,minlns);
 	//	rTrajectory = 	tra.getRtrajectory(tra,data,eps,minlns);
		HashMap<Integer, ArrayList<Line>> clusterMap = tra.getCluster();
		long endTime = System.currentTimeMillis();    //获取结束时间
		System.out.println("time cost is:"+(endTime - startTime) + "ms");
		//double CH_val = getCH_traclus(clusterMap);
		//System.out.println("the ch cost for traclus is:"+CH_val);
		  ClusterNumber = clusterMap.size();
		ArrayList<Line> result = new ArrayList<>();
		Set set = clusterMap.keySet();
		System.out.println("there are:"+set.size()+" clusters");
		Iterator it = set.iterator();
		ArrayList<Integer> clusterIdlist = new ArrayList<>();
		int count=0;
		while(it.hasNext()){
			int id = (int) it.next();
			clusterIdlist.add(id);
			for(int i=0;i<clusterMap.get(id).size();i++){
				 clusterMap.get(id).get(i).setClusterId(id);
			}
			count+=clusterMap.get(id).size();
		 	result.addAll(clusterMap.get(id));
		}
      /* for(int i=0;i<segments.size();i++){
    	   Line seg = segments.get(i);
			for(int j=0;j<clusterIdlist.size();j++){
				int id = clusterIdlist.get(j);
				ArrayList<Line> lines = clusterMap.get(id);
				if(lines.contains(seg)){
					segments.get(i).setClusterId(id);
				}
			}
		}*/
		System.out.println("check size is:"+count);
		 return result;
		//return segments;
	}
	public Line getCenterSeg_traclus(ArrayList<Line> segments){ 
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
		        return centerLine;
			}else{
				return null;
			}
	}
	private double getCH_traclus(HashMap<Integer, ArrayList<Line>> clusterMap) {
		Clustering clusterMethod = new Clustering();
		// 针对traclus聚类算法计算CH值，用于和其他聚类算法进行比较
		double numerator_above = 0;
		double numerator =0;
		double denominator = 0;
		Set id = clusterMap.keySet();
		Iterator it = id.iterator();
		ArrayList<Line> listAll = new ArrayList<>();
		ArrayList<Integer> idList = new ArrayList<>();
		ArrayList<Line> centerLinelist = new ArrayList<>();
		while(it.hasNext()){//获取所有线段
			int id_temp = (int)it.next();
			idList.add(id_temp);
			ArrayList<Line> temp_lines = clusterMap.get(id_temp);
			//计算每个聚类的中心线段
			Line center_tempLine  = getCenterSeg_traclus(temp_lines);
			centerLinelist.add(center_tempLine);
			listAll.addAll(temp_lines);
		}
		 
		for(int i=0;i<id.size();i++){
			int id_temp = idList.get(i);
			int nk =clusterMap.get(id_temp).size() ;//聚类的轨迹个数
			Line center_line_cluster = centerLinelist.get(i);//聚类的中心线段
			Line center_all = clusterMethod.getCenterAllSegs(listAll);//所有线段的中心线段
			double dis = clusterMethod.GetDistanceLines(i,center_line_cluster,center_all);//上面两条线段的距离：豪斯多夫
			numerator_above+=dis*dis*nk;
		}
		numerator = numerator_above/(id.size());//分子
		double denominator_above = 0;
		for(int i=0;i<id.size();i++){
			int id_temp = idList.get(i);
		//	SegClustering clusterTemp = kmeansSegCluster.get(i);
			Line centerClusterLine = centerLinelist.get(i);//每个聚类内的中心线段
			List<Line> segs = clusterMap.get(id_temp);
			double clusterDis = 0 ;
			for(int j=0;j<segs.size();j++){
				Line line = segs.get(j);
				double dis = clusterMethod.GetDistanceLines(i,centerClusterLine,line);
				clusterDis+= dis*dis;
			}
			denominator_above +=clusterDis;
		}
	    denominator = denominator_above/(listAll.size()-id.size());
	    double CH = numerator/denominator;
		return CH;
	}
	@Override
	public List<DataMining.Trajectory> TraclusRep(String partition_type,double eps,int minlns) {
		//生成代表轨迹并返回，从cp_raw里面获取所有点，由于它们是分割得到的特征点，所以可以继续traclus的步骤继续进行group步骤并返回结果
		//List<TraclusRepEntity> result = new ArrayList<TraclusRepEntity>();
		System.out.println("hello,we are in traclus rep");     
		TraClus tra  = new DataMining.TraClus();
	List<List<Taxi>> data = new ArrayList<List<Taxi>>();
	List<Taxi> dataAll= new ArrayList<>();
	ArrayList<Line> segments  = new ArrayList<>();
		if(partition_type.equals("traclus")){
	        List<char_TraclusEntity> data_traclus = char_traclusDao.Search();
	        data =ConvertTypeToTaxiTraclus_dbscan(data_traclus);
	         dataAll =ConvertTypeToTaxiTraclus(data_traclus); 
	         segments = pointsToLine( dataAll);
		}
		else{
			List<char_SGAEntity> data_sga = char_sgaDao.Search();
		    data =ConvertTypeToTaxiTraclus_dbscan_sga(data_sga);
		     dataAll =ConvertTypeToTaxiSGA(data_sga);
		     segments = pointsToLine(dataAll);
		}
		ArrayList<DataMining.Trajectory> rTrajectory = new ArrayList<>();
		 rTrajectory = 	tra.getRtrajectoryNew(tra,segments,eps,minlns);
		 HashMap<Integer, ArrayList<Line>> clusterMap = tra.getCluster();
		System.out.println("Hi,the size of lines is:"+segments.size());
		System.out.println("cluster size is:"+tra.getCluster().size());
         //  long startTime = System.currentTimeMillis();    //获取开始时间

        //  CalculateHX(segments);
       //  CalculateQMeasure(tra,segments,data);
	//	long endTime = System.currentTimeMillis();    //获取结束时间
    //  System.out.println("process time is: "+String.valueOf(endTime-startTime)+ "ms");     
		return rTrajectory;
	}
	public void CalculateQMeasure(TraClus tra, ArrayList<Line> lines, List<List<Taxi>> data) throws IOException{
	
	   	 FileWriter fw = null;
	     fw = new FileWriter("G://data/Experiments/traclus_qmeasure_10_sga10_new5.txt",true);
	     for(int i=0;i<lines.size();i++){
	 		Line temp = lines.get(i);
	 		if(temp.getE()==null||temp.getS()==null){
	 			System.out.println("the line appear null"+i);
	 		}
	 		if(temp.getE().x==temp.getS().x&&temp.getE().y==temp.getS().y){
	 			lines.remove(i);//如果线段出现起点和终点一样的情况就丢掉
	 			i--;
	 		}
	 	}
	     ArrayList<DataMining.Trajectory> rTrajectory = new ArrayList<>();
    	// for(double eps =0.1001;eps<0.4 ;eps=eps+0.01){
	      double eps = 0.1001;
    		System.out.println("each start: eps is:"+eps);
    		double qmeasure= 0;
    		 int minlns =10;//8,9,10
    	//	for(int minlns =2;minlns<12;minlns++){//8,9,10
    	    rTrajectory = 	tra.getRtrajectoryNew(tra,lines,eps,minlns);
    		HashMap<Integer,ArrayList<Line>> cluster =  tra.getCluster();
    		double SSE = getSSE_val(lines,cluster);
    		double penalty = getPenalty(lines,cluster);
    		qmeasure = SSE+penalty;
    		    try {
    	            String c =  eps+" "+minlns+" "+qmeasure+" "+cluster.size()+"\r\n";
    	            fw.write(c);
    	         } catch (IOException e1) {
    	            e1.printStackTrace();
    	             System.out.println("写入失败");
    	            System.exit(-1);
    	        } 
    		    System.out.println("qmeasure: "+qmeasure);
    		    System.out.println("eps is:"+eps);
    	//	}
     // 	}
    	  fw.close();
	}
    private double getPenalty(ArrayList<Line> lines, HashMap<Integer, ArrayList<Line>> cluster_map) {
		// 计算噪音线段
    	double penalty = 0;
    	//step1:把所有类内线段放入list
    	ArrayList<Line> penaltyList = new ArrayList<>();
    	ArrayList<Line> all_clusterLines = new ArrayList<>();
     	Distance disMeasure = new Distance();
    	Set<Integer> set =  cluster_map.keySet();
    	Iterator it = set.iterator();
    	while(it.hasNext()){//对clusterid进行遍历
    		 all_clusterLines.addAll( cluster_map.get(it.next()));
    	}
    	//step2:判断每条线段是否属于类内，否则放入噪音list
    	for(int i=0;i<lines.size();i++){
    		Line line = lines.get(i);
    		if(!all_clusterLines.contains(line)){
    			penaltyList.add(line);
    		}
    	}
    	//step3：计算噪音内各线段距离
    	double sum_dis = 0;
    	for(int i=0;i<penaltyList.size();i++){
			for(int j=i;j<penaltyList.size();j++){
				double dis = disMeasure.dist(penaltyList.get(i), penaltyList.get(j));
				sum_dis+=dis*dis;
			}
		}
    	penalty = sum_dis/(2*penaltyList.size());
		return penalty;
	}
    
	private double getSSE_val(ArrayList<Line> lines, HashMap<Integer, ArrayList<Line>> cluster_map) {
		 
		//计算SSE值
    	Distance disMeasure = new Distance();
    	ArrayList<Integer> clusterIdlist = new ArrayList<>();
    	Set<Integer> set =  cluster_map.keySet();
    	Iterator it = set.iterator();
    	double sum_cluster = 0;
    	int countCluster = 0;
    	while(it.hasNext()){//对clusterid进行遍历
    		double sum_dis = 0; 
    		ArrayList<Line> temp_lines = cluster_map.get(it.next());
    		for(int i=0;i<temp_lines.size();i++){
    			Line temp = temp_lines.get(i);
    			if(temp.getE()==null||temp.getS()==null){
    				System.out.println("the line appear null"+i);
    			}
    			if(temp.getE().x==temp.getS().x&&temp.getE().y==temp.getS().y){
    				temp_lines.remove(i);//如果线段出现起点和终点一样的情况就丢掉
    				i--;
    			}
    		}
    		if(temp_lines.size()>0){
    		for(int i=0;i<temp_lines.size();i++){
    			for(int j=i;j<temp_lines.size();j++){
    				double dis = disMeasure.dist(temp_lines.get(i), temp_lines.get(j));
    				sum_dis+=dis*dis;
    				if(Double.isNaN(dis)){
    					System.out.println("dis is Nan");
    				}
    			}
    		}
    		//一个类内各线段距离求出来之后开始计算sse
    		sum_cluster+=sum_dis/Double.valueOf((2*temp_lines.size()));
    		if(Double.isNaN(sum_cluster)){
    			System.out.println("NAN");
    		}
    		}else{
    			System.out.println("why? the temp_line is :"+temp_lines.size()+" ehhh,the order for i is:"+countCluster);
    		}
    		countCluster++;
    	}
		return sum_cluster;
	}

	public void CalculateHX(ArrayList<Line> lines) throws IOException{
    	 FileWriter fw = null;
	     fw = new FileWriter("G://data/Experiments/traclus_eps_100traclusHere.txt",true);
	     for(int i=0;i<lines.size();i++){
	 		Line temp = lines.get(i);
	 		if(temp.getE()==null||temp.getS()==null){
	 			System.out.println("the line appear null"+i);
	 		}
	 		if(temp.getE().x==temp.getS().x&&temp.getE().y==temp.getS().y){
	 			lines.remove(i);//如果线段出现起点和终点一样的情况就丢掉
	 			i--;
	 		}
	 	}
	   
    	// for(double eps_it =0.0001;eps_it<0.5 ;eps_it=eps_it+0.01){
	      double eps = 0.02;
    		System.out.println("each start: eps is:"+ eps);
    		double hx_val = 0;
    		for(int i=0;i<lines.size();i++){
    			Line line = lines.get(i);
    			double px = getPX_val(eps,lines,i);
    			if(px ==0){
    				System.out.println("we found the i:"+i+" eps:"+eps);
    			}
    			hx_val+=px*(Math.log(px)/Math.log(Double.valueOf(2)));
    		}
    		hx_val = (-1)*hx_val;
    		    try {
    	            String c =  eps+" "+hx_val+"\r\n";
    	            fw.write(c);
    	         } catch (IOException e1) {
    	            e1.printStackTrace();
    	             System.out.println("写入失败");
    	            System.exit(-1);
    	        } 
    		    System.out.println("eps is:"+eps+"and ");
     	//}
    	  fw.close();
    }
	private double getPX_val(double eps, ArrayList<Line> lines,int order) {
		double px_val = 0;
		int sumSegs = 0;
		DataMining.Distance disMeasure = new Distance();
		ArrayList<Integer> NeighborNum= new ArrayList<>();
		for(int i=0;i<lines.size();i++){
			Line line = lines.get(i);
			int countNeighbor = 0;//每条线段的领域内线段条数
			for(int j=0;j<lines.size();j++){
				Line line_compare = lines.get(j);
					double dis = disMeasure.dist(line, line_compare);
					if(dis<=eps){
						//j线段在i线段的领域内
						countNeighbor++;
				}
			}
			NeighborNum.add(countNeighbor);
			sumSegs+=countNeighbor;
		}
		//计算Px
		int num_order = NeighborNum.get(order);
		px_val = Double.valueOf(num_order)/Double.valueOf(sumSegs);
		double avg_num = Double.valueOf(sumSegs)/Double.valueOf(lines.size());//当eps确定下来之后的所有线段的领域数均值
	 	System.out.println("we get the avg is :"+avg_num);
		return px_val;
	}
    public double getMinlns(ArrayList<Line>lines,double eps){
    	double minlns = 0;
		double sumSegs = 0;
		DataMining.Distance disMeasure = new Distance();
		ArrayList<Integer> NeighborNum= new ArrayList<>();
		for(int i=0;i<lines.size();i++){
			Line line = lines.get(i);
			int countNeighbor = 0;//每条线段的领域内线段条数
			for(int j=0;j<lines.size();j++){
				Line line_compare = lines.get(j);
					double dis = disMeasure.dist(line, line_compare);
					if(dis<=eps){
						//j线段在i线段的领域内
						countNeighbor++;
				}
			}
			NeighborNum.add(countNeighbor);
			sumSegs+=countNeighbor;//每条线段的领域数相加，目的是最后求平均领域
		}
		double avg = Double.valueOf(sumSegs)/Double.valueOf(lines.size());
    	return avg;
    }
	@Override
	public List<DataMining.Trajectory> GeneralRep(String cluster_type, String partition_type) {
		TraClus traclus = new TraClus();
		List<DataMining.Trajectory> result = new ArrayList<>();
		 
		    List<RawEntity> data = rawDao.SearchAll();
		    Set<String> clusterIdList = rawDao.getClusterId();
		   //计算代表轨迹仅需要cluster即可
		   result= traclus.getGeneralRTrajectory(clusterIdList,data,cluster_type,partition_type);
		     
		return result;
	}
	@Override
    public RawRepre TraclusRepRaw(String partition_type,int order,double eps,int lns,String cluster_type){
		RawRepre rawRepre = new RawRepre();
		TraClus tra  = new DataMining.TraClus();
		if(cluster_type.equals("traclus_dbscan")){
		
	  //  ArrayList<Line> rTrajectory_raw = new ArrayList<>();
	    List<List<Taxi>> track = new ArrayList<>();
	    if(partition_type.equals("traclus")){
	        List<char_TraclusEntity> data_traclus = char_traclusDao.Search();
	       track =ConvertTypeToTaxiTraclus_dbscan(data_traclus);
		}
		else{
			List<char_SGAEntity> data_sga = char_sgaDao.Search();
		    track =ConvertTypeToTaxiTraclus_dbscan_sga(data_sga);
		}
	  
		rawRepre = 	tra.getSingleRtrajectory(track,order,eps,lns);
		//rawRepre.setCluster(rTrajectory_raw);
		//获取对应Order的代表轨迹
		}
		else{
		    List<RawEntity> data = rawDao.SearchAll();
		    Set<String> clusterIdList = rawDao.getClusterId();
			//general方法获取线段
			rawRepre = tra.getGeneralSingleRtrajectory(clusterIdList,data,  cluster_type,  partition_type,order);
		}
		return rawRepre;
    }
	private List<List<Taxi>> getTaxiForTraclus() {
		//  从taxi表中获取数据用于traclus计算
		Set<String> idList= char_traclusDao.getAllTaxiId();
		ArrayList<String> partList = new ArrayList<String>();
		int countId = 0;
		for(String str:idList){
			if(countId<100){
			partList.add(str);//将100个taxi id加入到partlist当中
	         countId++;	
			}else{
				break;
			}
	}
		List<List<Taxi>> result = new ArrayList<List<Taxi>>();
		int count =0;
		for(int j=0;j<partList.size();j++){
			String id = partList.get(j);
			List<Taxi> listTaxi = taxiDao.Search(id, null);
			count +=listTaxi.size();
			result.add(listTaxi);
		}
		System.out.println("100taxis has :"+count+" points");
		return result;
	}

	private List<List<char_TraclusEntity>> ConvertAllPointsToListTrack(List<char_TraclusEntity> all) {
		// 参数为所有点，需要将其转化为list<list>>的格式
		List<List<char_TraclusEntity>> list = new ArrayList<List<char_TraclusEntity>>();
		ArrayList<String> idList = new ArrayList<>();
		for(int i=0;i<all.size();i++){
			//以taxi_id作为一条轨迹的主键
			String taxiId = all.get(i).getTaxi_Id();
			if(!idList.contains(taxiId)){
				idList.add(taxiId);
			}
		}
		//获得所有的taxi id之后进行分派
		for(int j=0;j<idList.size();j++){
			String taxi = idList.get(j);
			List<char_TraclusEntity> track = new ArrayList<char_TraclusEntity>();
			for(int k=0;k<all.size();k++){
				char_TraclusEntity temp = all.get(k);
				if(taxi.equals(temp.getTaxi_Id())){
					//加入到该List中
					track.add(temp);
					all.remove(k);//第k个点加入到该List当中
					k--;
				}
			}
			list.add(track);
		}
		System.out.println("the idList has"+ idList.size()+" taxiid");
		return list;
	}

	private List<TraclusRepEntity> convertToRepFormat(ArrayList<DataMining.Trajectory> rTrajectory) {
		//将格式转化成TraclusRepEntity用于存储
		List<TraclusRepEntity> list = new ArrayList<TraclusRepEntity>();
		int countPrimaryKey = 0;
		for(int i=0;i<rTrajectory.size();i++){
	       List<DataMining.Point> points  = rTrajectory.get(i).getPoints();//第i条轨迹的所有点
			   int line_num = i;
	       for(int j=0;j<points.size();j++){
	    	   TraclusRepEntity entity = new TraclusRepEntity();
	    	    DataMining.Point temp = points.get(j);
				int line_order = j;
				String lat = String.valueOf(temp.getX());
				String lon = String.valueOf(temp.getY());
				
				//set
				entity.setId(String.valueOf(countPrimaryKey));
				entity.setLatitude(lat);
				entity.setLongtitude(lon);
				entity.setLine_num(line_num);
				entity.setLine_order(line_order);
				list.add(entity);
				countPrimaryKey++;
			}
		}
		return list;
	}

	@Override
	public List<CPointCluster> Traclus_Dbscan(String partition_type, String cluster_type, double eps, int minlns) {
		// traclus对特征点聚类
        TraClus traclus = new TraClus();
		CpointsClustering.Clustering clusterMethod = new Clustering();
		List<CPointCluster> result = new ArrayList<>();
		List<List<Taxi>> data = new ArrayList<List<Taxi>>();
		if(partition_type.equals("traclus")){
		    List<char_TraclusEntity> data_traclus = char_traclusDao.Search();
		     data =ConvertTypeToTaxiTraclus_dbscan(data_traclus);
		     //获取result根据traclus里面提供的方法获得中心点
		              result = traclus.getCenterResult(data, eps, minlns);
			}else{
				   List<char_SGAEntity> data_sga = char_sgaDao.Search();
				  data =ConvertTypeToTaxiTraclus_dbscan_sga(data_sga);
				     result = traclus.getCenterResult(data, eps, minlns);
			}
		//step2:存入cluster_result(使用ClusterDao)
		clusterDao.Delete();//Truncate操作
		clusterDao.Store(result,cluster_type,partition_type);//加上2个参数:cluster_type,cp_type
		//存入cp_raw
		rawDao.DeleteRaw();
	    rawDao.Store(result, cluster_type, partition_type);
	    System.out.println("minlns in dbscan is :"+minlns+"and eps is:"+eps);
		return result;
	}

	@Override
	public ArrayList<Line> getTrajectoryForRepreSentative() {
		List<List<Taxi>> data = new ArrayList<>();
		List<char_TraclusEntity> data_traclus = char_traclusDao.Search();
	        data =ConvertTypeToTaxiTraclus_dbscan(data_traclus);
	        List<List<Taxi>> data_part = new ArrayList<>();
		 for(int i=0;i<5;i++){
			 data_part.add(data.get(i));
		 }
		 ArrayList<Line> result = new ArrayList<>();
		 for(int j=0;j<5;j++){
			 List<Taxi> tempList = data_part.get(j);
			 ArrayList<Line> temp_lines = pointsToLine(tempList);
			 Line line = temp_lines.get(0);
			 result.add(line);
		 }
		 return result;
	}

	 


}
