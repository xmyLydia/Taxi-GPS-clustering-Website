package com.cn.controller;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import com.cn.entity.CPointCluster;
import com.cn.entity.ClassForVoronoi;
import com.cn.entity.ClusterResultEntity;
import com.cn.entity.CompareEntity;
import com.cn.entity.Fragment;
import com.cn.entity.InfoEntity;
import com.cn.entity.MopsiEntity;
import com.cn.entity.RawEntity;
import com.cn.entity.RawForVoronoi;
import com.cn.entity.RawRepre;
import com.cn.entity.SearchEntity;
import com.cn.entity.Taxi;
import com.cn.entity.TraclusRepEntity;
import com.cn.entity.showAllEntity;
import com.cn.service.TaxiService;

import DataMining.GetData;
import DataMining.Line;
import DataMining.Trajectory;
import ParameterSelect.SimilarityTXT;

 
@Controller
@RequestMapping("/taxi")

public class TaxiController {

	private TaxiService taxiService;
	private GetData getData;//用于展示压缩后的数据
	private List<String> taxiIdList ;
	public List<CPointCluster> Kresult = new ArrayList<CPointCluster>();//聚类后的聚类结果，可以用于kmeans聚类展示以及voronoi图，因此设置为类内元素，多次可用

	public TaxiService getTaxiService() {
		return taxiService;
	}

	@Resource
	public void setTaxiService(TaxiService taxiService) {
		this.taxiService = taxiService;
	}
	@RequestMapping(value = "/frequency", method = RequestMethod.POST)
	public @ResponseBody List<Taxi> Frequency(@RequestBody SearchEntity searchEntity) {
		System.out.println("enter");
		System.out.println(searchEntity.getTaxiId());
		List<Taxi> result;
		if(searchEntity.getTaxiId()==null)result = taxiService.getAll();
		else result = taxiService.search_frequency(searchEntity.getTaxiId(), null);
		return result;
	}
	@RequestMapping(value = "/mopsi_jump", method = RequestMethod.POST)
	public ModelAndView Mopsi_jump(HttpServletRequest arg0,HttpServletResponse arg1) {
		System.out.println("jump mopsi");
		 return new ModelAndView("/jsp/mopsi_jump");
	}
	@RequestMapping(value = "/single", method = RequestMethod.POST)
	public @ResponseBody List<Taxi> ShowAllData(@RequestBody SearchEntity searchEntity) {
		System.out.println("enter");
		System.out.println(searchEntity.getTaxiId());
		List<Taxi> result;
		if(searchEntity.getTaxiId()==null)result = taxiService.getAll();
		else result = taxiService.search(searchEntity.getTaxiId(), null);
		return result;
	}
	@RequestMapping(value = "/traclus_rep", method = RequestMethod.POST)
	public @ResponseBody List<Trajectory> ShowTraclusRep(@RequestBody SearchEntity searchEntity) {
		//展示代表轨迹
	//	double eps = searchEntity.traclus_eps;
	//	double eps = searchEntity.eps;
	//	int minlns = searchEntity.traclus_minlns;
	//	int minlns = searchEntity.minpts;
		String cluster_type = searchEntity.cluster_type;
		String cp = searchEntity.partition_type;
		System.out.println("rep_track");
		List<Trajectory> result = new ArrayList<>();
		//if(cluster_type.equals("traclus_dbscan")){
	/*	if(cluster_type.equals("dbscan")){
			result = taxiService.TraclusRep(cp,eps,minlns) ;
		}else{
			//其他类型的聚类方法
			result = taxiService.GeneralRep(cluster_type,cp);
		}
		*/
		if(cluster_type.equals("kmeans")){
			int k = searchEntity.k;
			int iterator = searchEntity.iterator;
			result = taxiService.getReKmeansSeg(cp,k,iterator);
		}else if(cluster_type.equals("dbscan")){
			    double eps = searchEntity.eps;
			//	int minlns = searchEntity.traclus_minlns;
				int minlns = searchEntity.minpts;
				result = taxiService.getReDbscanSeg(cp,eps,minlns);
		}else{
			//hierarchical
			int n = searchEntity.n;
			result = taxiService.getReHierarchicalSeg(cp,n);
		}
		//System.out.println("in the traclus_rep:eps is:"+eps+"and minlns is:"+minlns);
		return result;
	}
	@RequestMapping(value = "/traclus_rep_raw", method = RequestMethod.POST)
	public @ResponseBody RawRepre ShowTraclusRepRaw(@RequestBody SearchEntity searchEntity) {
		int rep_order = searchEntity.rep_order;//代表轨迹号
		//展示代表轨迹
		String cluster_type = searchEntity.cluster_type;
		double eps = searchEntity.traclus_eps;
		int minlns = searchEntity.traclus_minlns;
		String cp_type = searchEntity.partition_type;
		RawRepre result = taxiService.TraclusRepRaw(cp_type,rep_order,eps,minlns,cluster_type) ;
	//	System.out.println("rep_order"+rep_order+"size:"+result.size());
		return result;
	}
	@RequestMapping(value = "/taxiid", method = RequestMethod.POST)
	public @ResponseBody Set<String> GetTaxiId(@RequestBody SearchEntity searchEntity) {
		System.out.println("enter id,index:"+searchEntity.getIndex());
		int index = searchEntity.getIndex();
		if(index==0)
		{
			taxiIdList = new ArrayList<String>(taxiService.getAllTaxiId());
			Set<String> result = new HashSet<>();
			for(int i = 0;i<10;i++)
			{
				result.add(taxiIdList.get(index*10+i));
			}
			return result;
		}
		else
		{
			Set<String> result = new HashSet<>();
			for(int i = 0;i<10;i++)
			{
				result.add(taxiIdList.get(index*10+i));
			}
			return result;
		}
	}
	
	@RequestMapping(value = "/alltaxiid", method = RequestMethod.POST)
	public @ResponseBody Set<String> GetAllTaxiId() {
		return taxiService.getAllTaxiId();
	}
	
	@RequestMapping(value = "/all", method = RequestMethod.POST)
	public @ResponseBody List<Taxi> GetAllTaxi()
	{
		System.out.println("enter all");
		return taxiService.getAll();
	}
	
	@RequestMapping(value="/compare", method = RequestMethod.POST)
	public @ResponseBody CompareEntity ComparePath(@RequestBody SearchEntity searchEntity){
		System.out.println("enter compare");
		CompareEntity result = new CompareEntity();
		  List<List<Taxi>> trajectory = taxiService.GetFragment(searchEntity.getComparedTaxiIds());//方程的分割方法获得的轨迹，也是项目最开始的方法
	     result.setFragment(trajectory);
		result.setSimilarity(taxiService.GetSimilarity(trajectory));
		return result;
		
	}
	@RequestMapping(value="/MSE", method = RequestMethod.POST)
	public @ResponseBody int MSE(@RequestBody SearchEntity searchEntity){
		String cluster_type = searchEntity.cluster_type;//聚类的类型
		String cp_type = searchEntity.partition_type;
	//	double result = taxiService.MSE(cluster_type,cp_type);
	//	BigDecimal   b   =   new   BigDecimal(result); 
		//double   resultNew   =   b.setScale(2,   BigDecimal.ROUND_HALF_UP).doubleValue(); //四舍五入两位小数 
		int number = taxiService.getClusterNum();
		return number;
		
	}
	@RequestMapping(value="/sga", method = RequestMethod.POST)
	public @ResponseBody boolean SGA(@RequestBody SearchEntity searchEntity) throws IOException{
		double minAngle = searchEntity.minAngle;
		double minStopDuration = searchEntity.minStopDuration;
		double minDis = searchEntity.minDistance;
		double maxDis = searchEntity.maxDistance;
		taxiService.SGA(minAngle,minStopDuration,minDis,maxDis);
		return false;
		
	}
	//展示traclus结果
	@RequestMapping(value="/traclus", method = RequestMethod.POST)
	public @ResponseBody CompareEntity TraclusPath(@RequestBody SearchEntity searchEntity){
		System.out.println("enter traclus");
		CompareEntity result = new CompareEntity();
		// List<List<Taxi>> trajectory = taxiService.GetFragmentO(searchEntity.getComparedTaxiIds());方程的分割方法获得的轨迹，也是项目最开始的方法
		//List<List<Taxi>> trajectory = taxiService.GetFragmentOriginal(searchEntity.getComparedTaxiIds());//未经过分割的原始轨迹展示
		getData = new GetData();
		getData.DbStoreHelper();
		 //List<List<Taxi>> trajectory = getData.getFragment(searchEntity.getComparedTaxiIds());//使用DP算法后的简化轨迹
	 	//List<List<Taxi>> trajectory = getData.getFragmentGTC(searchEntity.getComparedTaxiIds());//使用GTC算法后的简化轨迹
		List<List<Taxi>> trajectory = getData.getFragmentTraclus(searchEntity.getComparedTaxiIds());//使用GTC算法后的简化轨迹
		result.setFragment(trajectory);
		result.setSimilarity(taxiService.GetSimilarity(trajectory));
		return result;
		
	}
	//用于展示所有轨迹
	@RequestMapping(value="/showAll", method = RequestMethod.POST)
	public @ResponseBody showAllEntity showAllEntity(@RequestBody SearchEntity searchEntity){
		System.out.println("show all");
		showAllEntity result = new showAllEntity();
		List<String> idList = new ArrayList<String>();//set转化为list
		 
		List<String> testList = new ArrayList<String>(taxiService.getAllTaxiId());
		for(int i=0;i<testList.size();i++){
			System.out.println(testList.get(i));
		}System.out.println("length:"+testList.size());
	   for(int i=0;i<100;i++){//将20改为1从而查看数据点是否出现了问题
		 idList.add(testList.get(i));
		 System.out.println("the id used for check is"+testList.get(i));
		 }
	  
	 //  List<List<Taxi>> trajectory = taxiService.GetFragmentAll(testList);
	  List<List<Taxi>> trajectory = taxiService.GetFragmentOriginal(testList);//未经过分割的原始轨迹展示
	   //List<List<Taxi>> trajectoryForAll = taxiService.GetFragmentAll(testList);
		 //获取轨迹列表之后进行聚类
	//	  List<List<Taxi>> resultTracks = taxiService.clusterTrajectory(trajectory,taxiService.GetSimilarity(trajectory));
		  //修改是以上
		  result.setFragment(trajectory);
	//	 result.setSimilarity(taxiService.GetSimilarityForShowAll(trajectory));
		  //  result.setFragment(resultTracks );
		 // result.setSimilarity(taxiService.GetSimilarity(resultTracks ));
		//  double[][] outputSimilarity = result.getSimilarity();
		 
		  //将相似度写入txt文件用于metlab的参数选择
		    SimilarityTXT similar = new SimilarityTXT();
		 //  similar.writeTXT(outputSimilarity);
		  //对获得的轨迹进行索引赋值并写入txt
		  List<Fragment> listFrag= taxiService.fragmentIndex(trajectory);
     
      //   System.out.println(outputSimilarity.length+":"+outputSimilarity[0].length);
		 //给定不同EPS求ENTROPY的值并写入txt
	 
		return result;
		
	}
	@RequestMapping(value="/showAllMopsi", method = RequestMethod.POST)
	public @ResponseBody showAllEntity showAllMopsi(@RequestBody SearchEntity searchEntity){
		System.out.println("show all");
		showAllEntity result = new showAllEntity();
		List<String> idList = new ArrayList<String>();//set转化为list
		 
		List<String> testList = new ArrayList<String>();
		
		for(int i=0;i<5;i++){
			String temp = String.valueOf(i+1);
			testList.add(temp);
			System.out.println(testList.get(i));
		}System.out.println("length:"+testList.size()); 
	     List<List<MopsiEntity>> trajectory = taxiService.GetFragmentOriginalMopsi(testList);//未经过分割的原始轨迹展示
	   
		  result.setFragmentMopsi(trajectory); 
		  //将相似度写入txt文件用于metlab的参数选择
		    SimilarityTXT similar = new SimilarityTXT(); 
		  //对获得的轨迹进行索引赋值并写入txt
		  List<Fragment> listFrag= taxiService.fragmentIndexMopsi(trajectory); 
		 //给定不同EPS求ENTROPY的值并写入txt 
		return result; 
	}
	//以上用于展示所有轨迹
	//从taxi获取数据之后进行层次聚类结果得到聚类后的轨迹
	/*
	@RequestMapping(value="/trajectoryCluster", method = RequestMethod.POST)
	public @ResponseBody showAllEntity trajectoryCluster(@RequestBody SearchEntity searchEntity){
		System.out.println("enter all");
		showAllEntity result = new showAllEntity();
		List<String> idList = new ArrayList<String>();//set转化为list
		//taxiService
		List<String> testList = new ArrayList<String>(taxiService.getAllTaxiId());
		for(int i=0;i<testList.size();i++){
			System.out.println(testList.get(i));
		}System.out.println("length:"+testList.size());
	  for(int i=0;i<20;i++){//将20改为1从而查看数据点是否出现了问题
		 idList.add(testList.get(i));
		 System.out.println("the id used for check is"+testList.get(i));
		 }
	 
		//List<List<Taxi>> trajectory = taxiService.GetFragment(searchEntity.getComparedTaxiIds());
		List<List<Taxi>> trajectory = taxiService.GetFragmentAll(idList);
	 //以上数据是20个taxi得到的所有轨迹数据，下面进行聚类
		  List<List<Taxi>> afterCluster_trajectory = taxiService.clusterTrajectory(trajectory);
		// result.setFragment(trajectory);
		result.setFragment(afterCluster_trajectory);
		// result.setSimilarity(taxiService.GetSimilarity(trajectory));
		  result.setSimilarity(taxiService.GetSimilarity(afterCluster_trajectory));
		return result;
		
	}
	*/
	//修改是以上
	@RequestMapping(value="/test/generate")
	public @ResponseBody String Test()
	{
		taxiService.GenerateTurningPoints(taxiIdList);  
		//taxiService.deleteDuplicate();
		return "hello";
	}
	
	@RequestMapping(value="/test/delete", method = RequestMethod.POST)
	public @ResponseBody String DeleteDuplicateTaxi(){
		taxiService.DeleteDuplicateTaxi(taxiIdList);
		return "hello";
	} 
	@RequestMapping(value="/kmeans_cluster", method=RequestMethod.POST)
	public @ResponseBody List<CPointCluster> ShowTraclus_Characteristics(@RequestBody SearchEntity searchEntity){
		int k = searchEntity.k;
		int iterator = searchEntity.iterator;
		String cp = searchEntity.partition_type;
		System.out.println(k+";"+iterator+":"+"cp:"+cp);
		if(cp!=null){
		 if(cp.equals("traclus")){
			 List<CPointCluster> Kresult = taxiService.Kmeans("traclus","kmeans",k,iterator);
		return Kresult;
		 }
		else
			return taxiService.Kmeans("SGA","kmeans",k,iterator);
		} 
		return null;
	}
	@RequestMapping(value="/kmeansMopsi_cluster", method=RequestMethod.POST)
	public @ResponseBody List<CPointCluster> ShowMopsi_CharacteristicsCluster(@RequestBody SearchEntity searchEntity){
		int k = searchEntity.k;
		int iterator = searchEntity.iterator;
		String user = searchEntity.user;
		//List<CPointCluster> Kresult = new ArrayList<CPointCluster>();
	    Kresult = taxiService.KmeansMopsi(user,k,iterator);
		return Kresult;
	  
	}
	@RequestMapping(value="/dbscan_cluster", method=RequestMethod.POST)
	public @ResponseBody List<CPointCluster> ShowTraclus_CharacteristicsDBscan(@RequestBody SearchEntity searchEntity){
		double eps = searchEntity.eps;
		int minpts = searchEntity.minpts;
		String cp = searchEntity.partition_type;
		System.out.println(eps+";"+minpts+":");
		if(cp!=null){
		if(cp.equals("traclus"))
		return taxiService.Dbscan("traclus","dbscan",eps,minpts);
		else
			return taxiService.Dbscan("SGA","dbscan",eps,minpts);}
		return null;
	}
	 @RequestMapping(value="/raw_data_seg", method=RequestMethod.POST)
	public @ResponseBody CompareEntity ShowCluster_rawDataSeg(@RequestBody SearchEntity searchEntity){//return type使用CompareEntity因为里面有fragment类型，不做改变即可以使用该类型
		 //展示三条原始轨迹的生成
		 String cp = searchEntity.partition_type;
		  String type = searchEntity.cluster_type;
		  System.out.println("in the raw_data controller : the partition_type is:"+ cp);
		  CompareEntity result = new CompareEntity();
		  ArrayList<Line> trajectory = taxiService.getTrajectoryForRepreSentative();
		  System.out.println("the legnth of rep lines is:"+trajectory.size());
	      result.setLineList(trajectory);
		 // 考虑到之后要使用Vornori,所以需要根据获得的result生成JSON文件
		  return result;
	} 
	 @RequestMapping(value="/mopsi_seg", method=RequestMethod.POST)
		public @ResponseBody void Mopsi_seg(){//return type使用CompareEntity因为里面有fragment类型，不做改变即可以使用该类型
		 taxiService.MopsiPartition();
		}
	 @RequestMapping(value="/mopsi_avg", method=RequestMethod.POST)
		public @ResponseBody showAllEntity Mopsi_Avg(@RequestBody SearchEntity searchEntity){//return type使用CompareEntity因为里面有fragment类型，不做改变即可以使用该类型
			  showAllEntity result = new showAllEntity();
		      String user = searchEntity.user;
			  int routes = searchEntity.routes;
			  int method = searchEntity.method;//traclus or DBA
			  List<String> rawList = new ArrayList<String>();//原始ID的list
			  List<Trajectory> list = new ArrayList<>();
			  //这里需要设置用来计算代表轨迹的原始轨迹
			  //根据user和routes选择来获取原始routes的ID
			  rawList = taxiService.GetStringList_Mopsi(user,routes);
			 //从而获取原始轨迹展示
			  List<List<MopsiEntity>> trajectory = taxiService.GetFragmentOriginalMopsi(rawList);
		     //设置原始轨迹
			  result.setFragmentMopsi(trajectory);
			  for(int i=0;i<trajectory.size();i++) {
				  System.out.println("size:"+trajectory.get(i).size());
			  }
			  System.out.println("hi");
			  if(method==1) {
			  list = taxiService.MopsiRep(user,routes);//对已存入table的数据进行averaging，使用方法为TRACLUS
			  }else if(method==2) {
				  list = taxiService.MopsiRep_DBA(trajectory,routes);//对已存入table的数据进行averaging，方法为DBA
			  }
			  
			  result.setRep(list);//设置计算得到的代表轨迹
			
			  return result;
		}
	 @RequestMapping(value="/mopsi_DBA", method=RequestMethod.POST)
		public @ResponseBody showAllEntity Mopsi_Avg_DBA(@RequestBody SearchEntity searchEntity){//return type使用CompareEntity因为里面有fragment类型，不做改变即可以使用该类型
			  showAllEntity result = new showAllEntity();
		      String user = searchEntity.user;
			  int routes = searchEntity.routes;
			  List<String> rawList = new ArrayList<String>();//原始ID的list
			  List<Trajectory> list = new ArrayList<>();
			  
			  //这里需要设置用来计算代表轨迹的原始轨迹
			  //根据user和routes选择来获取原始routes的ID
			  rawList = taxiService.GetStringList_Mopsi(user,routes);
			 //从而获取原始轨迹展示
			  List<List<MopsiEntity>> trajectory = taxiService.GetFragmentOriginalMopsi(rawList);
		     //设置原始轨迹
			  result.setFragmentMopsi(trajectory);
			  //averaging
			  list = taxiService.MopsiRep_DBA(trajectory,routes);//对已存入table的数据进行averaging
			  
			  result.setRep(list);//设置计算得到的代表轨迹
			 
			  return result;
		}
	 @RequestMapping(value="/mopsi_rep", method=RequestMethod.POST)
		public @ResponseBody List<Trajectory> Mopsi_Rep(){//return type使用CompareEntity因为里面有fragment类型，不做改变即可以使用该类型
			 
			  
			  List<Trajectory> list = new ArrayList<>();
			  list = taxiService.MopsiRep("kurrna",2);//对已存入table的数据进行averaging
			 
			  
			//  int clusterNum = taxiService.getClusterNum();
			//  result.setNumber(clusterNum);
		    //  result.setLineList(list);
			  return list;
		}
	@RequestMapping(value="/exper", method=RequestMethod.POST)
	public @ResponseBody CompareEntity ShowCluster_rawDataSegCluster(@RequestBody SearchEntity searchEntity){//return type使用CompareEntity因为里面有fragment类型，不做改变即可以使用该类型
		  String cp = searchEntity.partition_type;
		  String type = searchEntity.cluster_type;
		  System.out.println("in the raw_data controller : the partition_type is:"+ cp);
		  CompareEntity result = new CompareEntity();
		  ArrayList<DataMining.Line> list = new ArrayList<>();
		  if(type.equals("kmeans")){
			  int k = searchEntity.k;
			  int iterator = searchEntity.iterator;
			  list = taxiService.ShowSeg_clusterKmeans(cp,k,iterator);//对Kmeans算法实验使用的方法
		  }
		  if(type.equals("dbscan")){
			  double eps = searchEntity.eps;
			  int minpts = searchEntity.minpts;
			  list = taxiService.ShowSeg_cluster(cp,eps,minpts);//0.1001,3
		  }
		  if(type.equals("hierarchical")){
			  int n = searchEntity.n;
			  list  = taxiService.ShowSeg_clusterHierarchical_Agnes(cp,n);//对层次聚类算法实验使用的方法
		  }
		 //  list = taxiService.ShowSeg_cluster("sga",0.1001,3);//0.1001,3
	   //     list = taxiService.ShowSeg_cluster("traclus",0.02 ,24);//对traclus聚类算法使用的方法0.023,4
		  // list = taxiService.ShowSeg_clusterKmeans("traclus",4,2000);//对Kmeans算法实验使用的方法
		  //  list  = taxiService.ShowSeg_clusterHierarchical_Agnes("traclus",4 );//对层次聚类算法实验使用的方法
		  int clusterNum = taxiService.getClusterNum();
		  result.setNumber(clusterNum);
	      result.setLineList(list);
		  return result;
	}
	@RequestMapping(value="/rawData", method=RequestMethod.POST)
	public @ResponseBody CompareEntity ShowCluster_rawData(@RequestBody SearchEntity searchEntity){//return type使用CompareEntity因为里面有fragment类型，不做改变即可以使用该类型
		  String cp = searchEntity.partition_type;
		  String type = searchEntity.cluster_type;
		  System.out.println("in the raw_data controller : the partition_type is:"+ cp+"and cluster type is:"+type);
		  CompareEntity result = new CompareEntity();
		  List<List<RawEntity>> trajectory = taxiService.ClusterRawDataToLine(cp,type);
		  System.out.println("the legnth of raw tracks is:"+trajectory.size());
	      result.setFragmentRaw(trajectory);
		 // 考虑到之后要使用Vornori,所以需要根据获得的result生成JSON文件
		  return result;
	}
	@RequestMapping(value="/D3_info", method=RequestMethod.POST)
	public @ResponseBody InfoEntity ShowTrack_Info(@RequestBody SearchEntity searchEntity){//return type使用CompareEntity因为里面有fragment类型，不做改变即可以使用该类型
		  String cp = searchEntity.partition_type;
		  String type = searchEntity.cluster_type;
		  int order= searchEntity.taxi_order;
		  System.out.println("in the raw_data controller : the partition_type is:"+ cp);
		  InfoEntity result = new InfoEntity();//用于传回js的信息展示
		  List<List<RawEntity>> trajectory = taxiService.ClusterRawDataToLine(cp,type);
		  List<RawEntity> track = trajectory.get(order);
		  result = taxiService.SetInfo(track);
		 // System.out.println("the legnth of raw tracks is:"+trajectory.size());
	    //  result.setFragmentRaw(trajectory);
	      
		  return result;
	}
	@RequestMapping(value="/raw_Cluster", method=RequestMethod.POST)
	public @ResponseBody ClassForVoronoi ShowCluster_rawCluster(){//return type使用CompareEntity因为里面有fragment类型，不做改变即可以使用该类型
		  ClassForVoronoi vo_data = new ClassForVoronoi();
		  List<ClusterResultEntity> cluster_points = taxiService.GetClusterResult();//从cp_result里面获得所有的点
	      List<CPointCluster> result = taxiService.ConvertResultEntityToCP(cluster_points);
	      //从rawdata里面获取原始用于聚类的点
	      List<RawForVoronoi> rawPoints = taxiService.GetRawData();
	      
	      System.out.println("voronoi raw:"+ rawPoints.size());
	      vo_data.setCPointCluster(result);
	      vo_data.setRaw(rawPoints);
		  return vo_data;
	}
	@RequestMapping(value="/vor_Mopsi", method=RequestMethod.POST)
	public @ResponseBody ClassForVoronoi ShowVor_Mopsi(@RequestBody SearchEntity searchEntity){//return type使用CompareEntity因为里面有fragment类型，不做改变即可以使用该类型
		  String user = searchEntity.user;//根据user来获取聚类和原始点
		  ClassForVoronoi vo_data = new ClassForVoronoi();
		 // List<ClusterResultEntity> cluster_points = taxiService.GetClusterResult();//从cp_result里面获得所有的点
	      //List<CPointCluster> result = taxiService.ConvertResultEntityToCP(cluster_points);
		  List<CPointCluster> result = Kresult;//该结果来源于对Mopsi点的聚类
	      //从rawdata里面获取原始用于聚类的点
	      List<RawForVoronoi> rawPoints = taxiService.GetRawDataForMopsiVor();
	      
	      System.out.println("voronoi raw:"+ rawPoints.size());
	      vo_data.setCPointCluster(result);
	      vo_data.setRaw(rawPoints);
		  return vo_data;
	}
	@RequestMapping(value="/agnes_cluster", method=RequestMethod.POST)
	public @ResponseBody List<CPointCluster> ShowTraclus_CharacteristicsAgnes(@RequestBody SearchEntity searchEntity){
		int n = searchEntity.n;
		 
		String cp = searchEntity.partition_type;
		System.out.println(n+";" +"cp:"+cp);
		if(cp!=null){
		 if(cp.equals("traclus"))
		return taxiService.Hierarchical_Agnes("traclus","agnes",n);
		else
			return taxiService.Hierarchical_Agnes("SGA","agnes",n);
		} 
		return null;
	}
	@RequestMapping(value="/grid_cluster", method=RequestMethod.POST)
	public @ResponseBody List<CPointCluster> ShowTraclus_CharacteristicsGrid(@RequestBody SearchEntity searchEntity){
		int m = searchEntity.m;
		int nx = searchEntity.nx;
		int ny = searchEntity.ny;
		String cp = searchEntity.partition_type;
		System.out.println(m+";" +nx+";"+ny+"cp:"+cp);
		if(cp!=null){
		 if(cp.equals("traclus"))
		return taxiService.Hierarchical_Grid("traclus","grid",m,nx,ny);
		else
			return taxiService.Hierarchical_Grid("SGA","grid",m,nx,ny);
		} 
		return null;
	}
	@RequestMapping(value="/traclus_dbscan_cluster", method=RequestMethod.POST)
	public @ResponseBody List<CPointCluster> ShowTraclus_CharacteristicsDBscanMinlns(@RequestBody SearchEntity searchEntity){
		double eps = searchEntity.traclus_eps;
		int minlns = searchEntity.traclus_minlns;
		
		String cp = searchEntity.partition_type;
		System.out.println(eps+";"+minlns+":");
		if(cp!=null){
		if(cp.equals("traclus"))
		return taxiService.Traclus_Dbscan("traclus","traclus_dbscan",eps,minlns);
		else
			return taxiService.Traclus_Dbscan("SGA","traclus_dbscan",eps,minlns);}
		return null;
	}
}

