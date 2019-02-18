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
	private GetData getData;//����չʾѹ���������
	private List<String> taxiIdList ;
	public List<CPointCluster> Kresult = new ArrayList<CPointCluster>();//�����ľ���������������kmeans����չʾ�Լ�voronoiͼ���������Ϊ����Ԫ�أ���ο���

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
		//չʾ����켣
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
			//�������͵ľ��෽��
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
		int rep_order = searchEntity.rep_order;//����켣��
		//չʾ����켣
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
		  List<List<Taxi>> trajectory = taxiService.GetFragment(searchEntity.getComparedTaxiIds());//���̵ķָ����õĹ켣��Ҳ����Ŀ�ʼ�ķ���
	     result.setFragment(trajectory);
		result.setSimilarity(taxiService.GetSimilarity(trajectory));
		return result;
		
	}
	@RequestMapping(value="/MSE", method = RequestMethod.POST)
	public @ResponseBody int MSE(@RequestBody SearchEntity searchEntity){
		String cluster_type = searchEntity.cluster_type;//���������
		String cp_type = searchEntity.partition_type;
	//	double result = taxiService.MSE(cluster_type,cp_type);
	//	BigDecimal   b   =   new   BigDecimal(result); 
		//double   resultNew   =   b.setScale(2,   BigDecimal.ROUND_HALF_UP).doubleValue(); //����������λС�� 
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
	//չʾtraclus���
	@RequestMapping(value="/traclus", method = RequestMethod.POST)
	public @ResponseBody CompareEntity TraclusPath(@RequestBody SearchEntity searchEntity){
		System.out.println("enter traclus");
		CompareEntity result = new CompareEntity();
		// List<List<Taxi>> trajectory = taxiService.GetFragmentO(searchEntity.getComparedTaxiIds());���̵ķָ����õĹ켣��Ҳ����Ŀ�ʼ�ķ���
		//List<List<Taxi>> trajectory = taxiService.GetFragmentOriginal(searchEntity.getComparedTaxiIds());//δ�����ָ��ԭʼ�켣չʾ
		getData = new GetData();
		getData.DbStoreHelper();
		 //List<List<Taxi>> trajectory = getData.getFragment(searchEntity.getComparedTaxiIds());//ʹ��DP�㷨��ļ򻯹켣
	 	//List<List<Taxi>> trajectory = getData.getFragmentGTC(searchEntity.getComparedTaxiIds());//ʹ��GTC�㷨��ļ򻯹켣
		List<List<Taxi>> trajectory = getData.getFragmentTraclus(searchEntity.getComparedTaxiIds());//ʹ��GTC�㷨��ļ򻯹켣
		result.setFragment(trajectory);
		result.setSimilarity(taxiService.GetSimilarity(trajectory));
		return result;
		
	}
	//����չʾ���й켣
	@RequestMapping(value="/showAll", method = RequestMethod.POST)
	public @ResponseBody showAllEntity showAllEntity(@RequestBody SearchEntity searchEntity){
		System.out.println("show all");
		showAllEntity result = new showAllEntity();
		List<String> idList = new ArrayList<String>();//setת��Ϊlist
		 
		List<String> testList = new ArrayList<String>(taxiService.getAllTaxiId());
		for(int i=0;i<testList.size();i++){
			System.out.println(testList.get(i));
		}System.out.println("length:"+testList.size());
	   for(int i=0;i<100;i++){//��20��Ϊ1�Ӷ��鿴���ݵ��Ƿ����������
		 idList.add(testList.get(i));
		 System.out.println("the id used for check is"+testList.get(i));
		 }
	  
	 //  List<List<Taxi>> trajectory = taxiService.GetFragmentAll(testList);
	  List<List<Taxi>> trajectory = taxiService.GetFragmentOriginal(testList);//δ�����ָ��ԭʼ�켣չʾ
	   //List<List<Taxi>> trajectoryForAll = taxiService.GetFragmentAll(testList);
		 //��ȡ�켣�б�֮����о���
	//	  List<List<Taxi>> resultTracks = taxiService.clusterTrajectory(trajectory,taxiService.GetSimilarity(trajectory));
		  //�޸�������
		  result.setFragment(trajectory);
	//	 result.setSimilarity(taxiService.GetSimilarityForShowAll(trajectory));
		  //  result.setFragment(resultTracks );
		 // result.setSimilarity(taxiService.GetSimilarity(resultTracks ));
		//  double[][] outputSimilarity = result.getSimilarity();
		 
		  //�����ƶ�д��txt�ļ�����metlab�Ĳ���ѡ��
		    SimilarityTXT similar = new SimilarityTXT();
		 //  similar.writeTXT(outputSimilarity);
		  //�Ի�õĹ켣����������ֵ��д��txt
		  List<Fragment> listFrag= taxiService.fragmentIndex(trajectory);
     
      //   System.out.println(outputSimilarity.length+":"+outputSimilarity[0].length);
		 //������ͬEPS��ENTROPY��ֵ��д��txt
	 
		return result;
		
	}
	@RequestMapping(value="/showAllMopsi", method = RequestMethod.POST)
	public @ResponseBody showAllEntity showAllMopsi(@RequestBody SearchEntity searchEntity){
		System.out.println("show all");
		showAllEntity result = new showAllEntity();
		List<String> idList = new ArrayList<String>();//setת��Ϊlist
		 
		List<String> testList = new ArrayList<String>();
		
		for(int i=0;i<5;i++){
			String temp = String.valueOf(i+1);
			testList.add(temp);
			System.out.println(testList.get(i));
		}System.out.println("length:"+testList.size()); 
	     List<List<MopsiEntity>> trajectory = taxiService.GetFragmentOriginalMopsi(testList);//δ�����ָ��ԭʼ�켣չʾ
	   
		  result.setFragmentMopsi(trajectory); 
		  //�����ƶ�д��txt�ļ�����metlab�Ĳ���ѡ��
		    SimilarityTXT similar = new SimilarityTXT(); 
		  //�Ի�õĹ켣����������ֵ��д��txt
		  List<Fragment> listFrag= taxiService.fragmentIndexMopsi(trajectory); 
		 //������ͬEPS��ENTROPY��ֵ��д��txt 
		return result; 
	}
	//��������չʾ���й켣
	//��taxi��ȡ����֮����в�ξ������õ������Ĺ켣
	/*
	@RequestMapping(value="/trajectoryCluster", method = RequestMethod.POST)
	public @ResponseBody showAllEntity trajectoryCluster(@RequestBody SearchEntity searchEntity){
		System.out.println("enter all");
		showAllEntity result = new showAllEntity();
		List<String> idList = new ArrayList<String>();//setת��Ϊlist
		//taxiService
		List<String> testList = new ArrayList<String>(taxiService.getAllTaxiId());
		for(int i=0;i<testList.size();i++){
			System.out.println(testList.get(i));
		}System.out.println("length:"+testList.size());
	  for(int i=0;i<20;i++){//��20��Ϊ1�Ӷ��鿴���ݵ��Ƿ����������
		 idList.add(testList.get(i));
		 System.out.println("the id used for check is"+testList.get(i));
		 }
	 
		//List<List<Taxi>> trajectory = taxiService.GetFragment(searchEntity.getComparedTaxiIds());
		List<List<Taxi>> trajectory = taxiService.GetFragmentAll(idList);
	 //����������20��taxi�õ������й켣���ݣ�������о���
		  List<List<Taxi>> afterCluster_trajectory = taxiService.clusterTrajectory(trajectory);
		// result.setFragment(trajectory);
		result.setFragment(afterCluster_trajectory);
		// result.setSimilarity(taxiService.GetSimilarity(trajectory));
		  result.setSimilarity(taxiService.GetSimilarity(afterCluster_trajectory));
		return result;
		
	}
	*/
	//�޸�������
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
	public @ResponseBody CompareEntity ShowCluster_rawDataSeg(@RequestBody SearchEntity searchEntity){//return typeʹ��CompareEntity��Ϊ������fragment���ͣ������ı伴����ʹ�ø�����
		 //չʾ����ԭʼ�켣������
		 String cp = searchEntity.partition_type;
		  String type = searchEntity.cluster_type;
		  System.out.println("in the raw_data controller : the partition_type is:"+ cp);
		  CompareEntity result = new CompareEntity();
		  ArrayList<Line> trajectory = taxiService.getTrajectoryForRepreSentative();
		  System.out.println("the legnth of rep lines is:"+trajectory.size());
	      result.setLineList(trajectory);
		 // ���ǵ�֮��Ҫʹ��Vornori,������Ҫ���ݻ�õ�result����JSON�ļ�
		  return result;
	} 
	 @RequestMapping(value="/mopsi_seg", method=RequestMethod.POST)
		public @ResponseBody void Mopsi_seg(){//return typeʹ��CompareEntity��Ϊ������fragment���ͣ������ı伴����ʹ�ø�����
		 taxiService.MopsiPartition();
		}
	 @RequestMapping(value="/mopsi_avg", method=RequestMethod.POST)
		public @ResponseBody showAllEntity Mopsi_Avg(@RequestBody SearchEntity searchEntity){//return typeʹ��CompareEntity��Ϊ������fragment���ͣ������ı伴����ʹ�ø�����
			  showAllEntity result = new showAllEntity();
		      String user = searchEntity.user;
			  int routes = searchEntity.routes;
			  int method = searchEntity.method;//traclus or DBA
			  List<String> rawList = new ArrayList<String>();//ԭʼID��list
			  List<Trajectory> list = new ArrayList<>();
			  //������Ҫ���������������켣��ԭʼ�켣
			  //����user��routesѡ������ȡԭʼroutes��ID
			  rawList = taxiService.GetStringList_Mopsi(user,routes);
			 //�Ӷ���ȡԭʼ�켣չʾ
			  List<List<MopsiEntity>> trajectory = taxiService.GetFragmentOriginalMopsi(rawList);
		     //����ԭʼ�켣
			  result.setFragmentMopsi(trajectory);
			  for(int i=0;i<trajectory.size();i++) {
				  System.out.println("size:"+trajectory.get(i).size());
			  }
			  System.out.println("hi");
			  if(method==1) {
			  list = taxiService.MopsiRep(user,routes);//���Ѵ���table�����ݽ���averaging��ʹ�÷���ΪTRACLUS
			  }else if(method==2) {
				  list = taxiService.MopsiRep_DBA(trajectory,routes);//���Ѵ���table�����ݽ���averaging������ΪDBA
			  }
			  
			  result.setRep(list);//���ü���õ��Ĵ���켣
			
			  return result;
		}
	 @RequestMapping(value="/mopsi_DBA", method=RequestMethod.POST)
		public @ResponseBody showAllEntity Mopsi_Avg_DBA(@RequestBody SearchEntity searchEntity){//return typeʹ��CompareEntity��Ϊ������fragment���ͣ������ı伴����ʹ�ø�����
			  showAllEntity result = new showAllEntity();
		      String user = searchEntity.user;
			  int routes = searchEntity.routes;
			  List<String> rawList = new ArrayList<String>();//ԭʼID��list
			  List<Trajectory> list = new ArrayList<>();
			  
			  //������Ҫ���������������켣��ԭʼ�켣
			  //����user��routesѡ������ȡԭʼroutes��ID
			  rawList = taxiService.GetStringList_Mopsi(user,routes);
			 //�Ӷ���ȡԭʼ�켣չʾ
			  List<List<MopsiEntity>> trajectory = taxiService.GetFragmentOriginalMopsi(rawList);
		     //����ԭʼ�켣
			  result.setFragmentMopsi(trajectory);
			  //averaging
			  list = taxiService.MopsiRep_DBA(trajectory,routes);//���Ѵ���table�����ݽ���averaging
			  
			  result.setRep(list);//���ü���õ��Ĵ���켣
			 
			  return result;
		}
	 @RequestMapping(value="/mopsi_rep", method=RequestMethod.POST)
		public @ResponseBody List<Trajectory> Mopsi_Rep(){//return typeʹ��CompareEntity��Ϊ������fragment���ͣ������ı伴����ʹ�ø�����
			 
			  
			  List<Trajectory> list = new ArrayList<>();
			  list = taxiService.MopsiRep("kurrna",2);//���Ѵ���table�����ݽ���averaging
			 
			  
			//  int clusterNum = taxiService.getClusterNum();
			//  result.setNumber(clusterNum);
		    //  result.setLineList(list);
			  return list;
		}
	@RequestMapping(value="/exper", method=RequestMethod.POST)
	public @ResponseBody CompareEntity ShowCluster_rawDataSegCluster(@RequestBody SearchEntity searchEntity){//return typeʹ��CompareEntity��Ϊ������fragment���ͣ������ı伴����ʹ�ø�����
		  String cp = searchEntity.partition_type;
		  String type = searchEntity.cluster_type;
		  System.out.println("in the raw_data controller : the partition_type is:"+ cp);
		  CompareEntity result = new CompareEntity();
		  ArrayList<DataMining.Line> list = new ArrayList<>();
		  if(type.equals("kmeans")){
			  int k = searchEntity.k;
			  int iterator = searchEntity.iterator;
			  list = taxiService.ShowSeg_clusterKmeans(cp,k,iterator);//��Kmeans�㷨ʵ��ʹ�õķ���
		  }
		  if(type.equals("dbscan")){
			  double eps = searchEntity.eps;
			  int minpts = searchEntity.minpts;
			  list = taxiService.ShowSeg_cluster(cp,eps,minpts);//0.1001,3
		  }
		  if(type.equals("hierarchical")){
			  int n = searchEntity.n;
			  list  = taxiService.ShowSeg_clusterHierarchical_Agnes(cp,n);//�Բ�ξ����㷨ʵ��ʹ�õķ���
		  }
		 //  list = taxiService.ShowSeg_cluster("sga",0.1001,3);//0.1001,3
	   //     list = taxiService.ShowSeg_cluster("traclus",0.02 ,24);//��traclus�����㷨ʹ�õķ���0.023,4
		  // list = taxiService.ShowSeg_clusterKmeans("traclus",4,2000);//��Kmeans�㷨ʵ��ʹ�õķ���
		  //  list  = taxiService.ShowSeg_clusterHierarchical_Agnes("traclus",4 );//�Բ�ξ����㷨ʵ��ʹ�õķ���
		  int clusterNum = taxiService.getClusterNum();
		  result.setNumber(clusterNum);
	      result.setLineList(list);
		  return result;
	}
	@RequestMapping(value="/rawData", method=RequestMethod.POST)
	public @ResponseBody CompareEntity ShowCluster_rawData(@RequestBody SearchEntity searchEntity){//return typeʹ��CompareEntity��Ϊ������fragment���ͣ������ı伴����ʹ�ø�����
		  String cp = searchEntity.partition_type;
		  String type = searchEntity.cluster_type;
		  System.out.println("in the raw_data controller : the partition_type is:"+ cp+"and cluster type is:"+type);
		  CompareEntity result = new CompareEntity();
		  List<List<RawEntity>> trajectory = taxiService.ClusterRawDataToLine(cp,type);
		  System.out.println("the legnth of raw tracks is:"+trajectory.size());
	      result.setFragmentRaw(trajectory);
		 // ���ǵ�֮��Ҫʹ��Vornori,������Ҫ���ݻ�õ�result����JSON�ļ�
		  return result;
	}
	@RequestMapping(value="/D3_info", method=RequestMethod.POST)
	public @ResponseBody InfoEntity ShowTrack_Info(@RequestBody SearchEntity searchEntity){//return typeʹ��CompareEntity��Ϊ������fragment���ͣ������ı伴����ʹ�ø�����
		  String cp = searchEntity.partition_type;
		  String type = searchEntity.cluster_type;
		  int order= searchEntity.taxi_order;
		  System.out.println("in the raw_data controller : the partition_type is:"+ cp);
		  InfoEntity result = new InfoEntity();//���ڴ���js����Ϣչʾ
		  List<List<RawEntity>> trajectory = taxiService.ClusterRawDataToLine(cp,type);
		  List<RawEntity> track = trajectory.get(order);
		  result = taxiService.SetInfo(track);
		 // System.out.println("the legnth of raw tracks is:"+trajectory.size());
	    //  result.setFragmentRaw(trajectory);
	      
		  return result;
	}
	@RequestMapping(value="/raw_Cluster", method=RequestMethod.POST)
	public @ResponseBody ClassForVoronoi ShowCluster_rawCluster(){//return typeʹ��CompareEntity��Ϊ������fragment���ͣ������ı伴����ʹ�ø�����
		  ClassForVoronoi vo_data = new ClassForVoronoi();
		  List<ClusterResultEntity> cluster_points = taxiService.GetClusterResult();//��cp_result���������еĵ�
	      List<CPointCluster> result = taxiService.ConvertResultEntityToCP(cluster_points);
	      //��rawdata�����ȡԭʼ���ھ���ĵ�
	      List<RawForVoronoi> rawPoints = taxiService.GetRawData();
	      
	      System.out.println("voronoi raw:"+ rawPoints.size());
	      vo_data.setCPointCluster(result);
	      vo_data.setRaw(rawPoints);
		  return vo_data;
	}
	@RequestMapping(value="/vor_Mopsi", method=RequestMethod.POST)
	public @ResponseBody ClassForVoronoi ShowVor_Mopsi(@RequestBody SearchEntity searchEntity){//return typeʹ��CompareEntity��Ϊ������fragment���ͣ������ı伴����ʹ�ø�����
		  String user = searchEntity.user;//����user����ȡ�����ԭʼ��
		  ClassForVoronoi vo_data = new ClassForVoronoi();
		 // List<ClusterResultEntity> cluster_points = taxiService.GetClusterResult();//��cp_result���������еĵ�
	      //List<CPointCluster> result = taxiService.ConvertResultEntityToCP(cluster_points);
		  List<CPointCluster> result = Kresult;//�ý����Դ�ڶ�Mopsi��ľ���
	      //��rawdata�����ȡԭʼ���ھ���ĵ�
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

