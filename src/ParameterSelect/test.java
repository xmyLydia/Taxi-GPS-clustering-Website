package ParameterSelect;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Set;
import java.util.HashMap;
import javax.annotation.Resource;
import com.cn.entity.Taxi;
import org.springframework.stereotype.Service;

import com.cn.dao.TaxiDao;
import com.cn.dao.TurningPointDao;
import com.cn.entity.Cluster_Point;
import com.cn.entity.Fragment;
import ParameterSelect.*;
import com.cn.entity.Taxi;
import com.cn.entity.TurningPoint;
import com.cn.entity.trajectoryCluster;
public class test{
	 //针对展示所有轨迹
		
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
		
		//对相似度进行轨迹聚类,使用的是TRACLUS聚类方法
	
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
	    public  List<List<Taxi>> Partition(List<Taxi> TR){//将一条原始轨迹划分成线段
	    ArrayList<Integer> CP = new ArrayList<Integer>();
	    List<List<Taxi>> Line = new ArrayList<List<Taxi>>();//存放切割后的几条线段
	    int startIndex = 0;
	    int length = 1;
	    int len_i = TR.size();//TR里面一共有几个点
	    int count = 0;//CP的计数起点
	    Taxi p1= TR.get(0);//轨迹的第一个点
	    CP.add(0);//将第一个点加入CP中，0代表点在原始轨迹的行号
	    int currIndex;
	    while(startIndex+length<=len_i-1){
	    	currIndex = startIndex + length;
	        Taxi p_startIndex = TR.get(startIndex);
	        Taxi p_currIndex = TR.get(currIndex);
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
	    return null;
	    }
		public List<Taxi> CutTrajectory(int start,int end,List<Taxi> single_trajectory,ArrayList<Integer>CP){
			List<Taxi> cutTra = new ArrayList<Taxi>();
			if(start != CP.get(0)){
				start = start + 1;
			}
			for(int i=start;i<=end;i++){
				cutTra.add(single_trajectory.get(i));
			}
			return cutTra;
		}
	    public double distance(Taxi p1,Taxi p2){
	    	double distance = 0.0;
	    	 double p1_x = Double.parseDouble(p1.getLatitude());
	         double p1_y = Double.parseDouble(p1.getLongtitude());
	         double p2_x = Double.parseDouble(p2.getLatitude());
	         double p2_y = Double.parseDouble(p2.getLongtitude());
	         
	         distance  =  Math.sqrt((p1_x-p2_x)*(p1_x-p2_x)+(p1_y-p2_y)*(p1_y-p2_y));
	         return distance;
	    }
	    public double distance(List<Taxi> trajectory){
	    	double distance = 0.0;
	    	double start_x = Double.parseDouble(trajectory.get(0).getLatitude());
	        double start_y = Double.parseDouble(trajectory.get(0).getLongtitude());
	        double end_x = Double.parseDouble(trajectory.get(trajectory.size()-1).getLatitude());
	        double end_y = Double.parseDouble(trajectory.get(trajectory.size()-1).getLongtitude());
	    	
	        distance = Math.sqrt((start_x- end_x)*(start_x - end_x)+(start_y - end_y)*(start_y - end_y));
	    	return distance ;
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
	    private double distance_perpen(Taxi si,Taxi ei,Taxi sj,Taxi ej) {
	        double sj_x = Double.parseDouble(sj.getLatitude());
	        double si_x = Double.parseDouble(si.getLatitude());
	        double ei_x = Double.parseDouble(ei.getLatitude());
	        double ej_x = Double.parseDouble(ej.getLatitude());
	        
	        double sj_y = Double.parseDouble(sj.getLongtitude());
	        double si_y = Double.parseDouble(si.getLongtitude());
	        double ei_y = Double.parseDouble(ei.getLongtitude());
	        double ej_y = Double.parseDouble(ej.getLongtitude());
	    	double x1=sj_x-si_x;
			double y1=sj_y-si_y;
			
			double x2=ei_x-si_x;
			double y2=ei_y-si_y;
			
			double x3=ej_x-si_x;
			double y3=ej_y-si_y;
			
			double x=Math.pow(x2,2)+Math.pow(y2,2);
			
			double u1=(x1*x2+y1*y2)/x;
			double u2=(x3*x2+y3*y2)/x;
		    
		    double psx=si_x+u1*x2;
			double psy=si_y+u1*y2;
			
			double pex=si_x+u2*x2;
			double pey=si_y+u2*y2;
			
			double Lper1=Math.sqrt(Math.pow(psx-sj_x,2)+Math.pow(psy-sj_y,2));
			double Lper2=Math.sqrt(Math.pow(pex-ej_x,2)+Math.pow(pey-ej_y,2));
			double d_perpen;
			
			if(Lper1==0&&Lper2==0)
			{
				d_perpen=0;
			}
			else
			{
				d_perpen=(Math.pow(Lper1,2)+Math.pow(Lper2,2))/(Lper1+Lper2);
			}
	      return d_perpen;
		}

		public List<Taxi> newLine(List<Taxi> trajectory,int start, int end){
	    	List<Taxi> newLine = new ArrayList<Taxi>();
	    	for(int i=start;i<=end;i++){
	    		newLine.add(trajectory.get(start));
	    	}
	    	return newLine;
	    }
		private double distance_angle(List<Taxi> l1,List<Taxi> l2) {
			//角度距离
		    double len = distance(l2);
		    double cos = cos_angle(l1,l2);
		    double sin = Math.sqrt(1-cos*cos);
		    double angle = len*sin;
		    return angle;
		}
	    public double cos_angle(List<Taxi> l1,List<Taxi> l2){
	    	double start1_x = Double.parseDouble(l1.get(0) .getLatitude());
			double start1_y = Double.parseDouble(l1.get(0).getLongtitude());
			double end1_x = Double.parseDouble(l1.get(l1.size()-1).getLatitude());
			double end1_y = Double.parseDouble(l1.get(l1.size()-1).getLongtitude());
			double start2_x = Double.parseDouble(l2.get(0).getLatitude());
	        double start2_y = Double.parseDouble(l2.get(0) .getLongtitude());
	        double end2_x = Double.parseDouble(l2.get(l2.size()-1) .getLatitude());
	        double end2_y = Double.parseDouble(l2.get(l2.size()-1) .getLongtitude());
	        double x1= end1_x - start1_x;
	        double y1= end1_y - start1_y;
	        double x2= end2_x - start2_x;
	        double y2= end2_y - start2_y;
	        double length1 = Math.sqrt(x1*x1+y1*y1);
	        double length2 = Math.sqrt(x2*x2+y2*y2);
	        if(length1 ==0 || length2 ==0){
	        	return 0;
	        }
	        else{
	        	double cos = (x1*x2+y1*y2)/(length1*length2);
	        	return cos;
	        }
	    }
		private double PerDistance(Taxi start, Taxi end, Taxi p1, Taxi p2) {
			// TODO Auto-generated method stub
			//垂直距离,start点和end点是底部的线段的两端点,公式：L=|(x0-x1)(y1-y2)-(x1-x2)(y0-y1)|/√[(x1-x2)^2+(y1-y2)^2]
			double perDistance = 0.0;
			double start_x = Double.parseDouble(start .getLatitude());
			double start_y = Double.parseDouble(start.getLongtitude());
			double end_x = Double.parseDouble(end.getLatitude());
			double end_y = Double.parseDouble(end.getLongtitude());
			double p1_x = Double.parseDouble(p1 .getLatitude());
	        double p1_y = Double.parseDouble(p1 .getLongtitude());
	        double p2_x = Double.parseDouble(p2 .getLatitude());
	        double p2_y = Double.parseDouble(p2 .getLongtitude());
	        if(start_x!=end_x || start_y!=end_y){
	        double dis1 = Math.abs((p1_x-start_x)*(start_y-end_y)-(start_x - end_x)*(p1_y - start_y))/Math.sqrt((start_x-end_x)*(start_x-end_x)+(start_y-end_y)*(start_y-end_y));
			double dis2 = Math.abs((p2_x-start_x)*(start_y-end_y)-(start_x - end_x)*(p2_y - start_y))/Math.sqrt((start_x-end_x)*(start_x-end_x)+(start_y-end_y)*(start_y-end_y));
			if((dis1+dis2)!=0)
			{
				perDistance = (dis1*dis1+dis2*dis2)/(dis1+dis2);
			}
			else{
				perDistance = 0.0;
			}
	        }
			return perDistance;
		}

		//controller调用的聚类方法
		
		public List<List<Taxi>> clusterTrajectory(List<List<Taxi>> trajectory,double[][] similarity) {
			double minlns = 2;
			List<List<Taxi>> partition= new ArrayList<List<Taxi>>();
			//for(int i=0;i<trajectory.size();i++){
				//List<List<Taxi>> temp = Partition(trajectory.get(i));//拆分得到的几条线段
			    //partition.addAll(temp);
			//}
			
			//tra.setParameter(5, 3);
			//tra.setMDL_COST_ADVANTAGE(10);
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

	public static  void main(String[] args){
		Taxi a1 = new Taxi();
		a1.setLongtitude("4");
		a1.setLatitude("5");
		Taxi a2 = new Taxi();
		a2.setLongtitude("2");
		a2.setLatitude("4");
		Taxi a3 = new Taxi();
		a3.setLongtitude("3");
		a3.setLatitude("5");
		Taxi a4 = new Taxi();
		a4.setLongtitude("1");
		a4.setLatitude("40");
		List<Taxi> A = new ArrayList<>();
	    A.add(a1);
	    A.add(a2);
	    A.add(a3);
	    A.add(a4);
	    //B
	    Taxi b1 = new Taxi();
		b1.setLongtitude("40");
		b1.setLatitude("5");
		Taxi b2 = new Taxi();
		b2.setLongtitude("24");
		b2.setLatitude("4");
		Taxi b3 = new Taxi();
		b3.setLongtitude("39");
		b3.setLatitude("5");
		Taxi b4 = new Taxi();
		b4.setLongtitude("10");
		b4.setLatitude("40");
		Taxi b5= new Taxi();
		b5.setLongtitude("15");
		b5.setLatitude("40");
		Taxi b6 = new Taxi();
		b6.setLongtitude("80");
		b6.setLatitude("44");
		List<Taxi> 	B = new ArrayList<>();
	    B.add(b1);
	    B.add(b2);
	    B.add(b3);
	    B.add(b4);
	    B.add(b5);
	    B.add(b6);
	    //C
	    //B
	    Taxi c1 = new Taxi();
		c1.setLongtitude("14");
		c1.setLatitude("15");
		Taxi c2 = new Taxi();
		c2.setLongtitude("22");
		c2.setLatitude("42");
		Taxi c3 = new Taxi();
		c3.setLongtitude("43");
		c3.setLatitude("55");
		Taxi c4 = new Taxi();
		c4.setLongtitude("12");
		c4.setLatitude("40");
		Taxi c5= new Taxi();
		c5.setLongtitude("51");
		c5.setLatitude("40");
		Taxi c6 = new Taxi();
		c6.setLongtitude("40");
		c6.setLatitude("84");
		List<Taxi> 	C = new ArrayList<>();
	    C.add(c1);
	    C.add(c2);
	    C.add(c3);
	    C.add(c4);
	    C.add(c5);
	    C.add(c6);
		//MyComparator mc = new MyComparator();
		//Collections.sort(list,mc);
		//for(int i=0;i<list.size();i++){
		//	System.out.println(list.get(i).getLongtitude());
		//}
	    List<List<Taxi>> list = new ArrayList<List<Taxi>>();
	    list.add(A);
	    list.add(B);
	    list.add(C);
	    List<List<Taxi>> partition = new ArrayList<List<Taxi>>();
	    test t = new test();
	    	partition = 	t.Partition(C);
	     System.out.println(partition.size());
	}
	
}