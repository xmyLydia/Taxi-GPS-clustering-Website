package ParameterSelect;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.lang.Math;

import com.cn.entity.Fragment;

public class OptimalEps {
 public HashMap<Double,Double> CalculateEPS(double[][]similarity,List<Fragment> list){
	 //计算信息熵
	 //首先计算p(xi)即每条轨迹线段领域的出现概率
	 //第一步计算每条轨迹在不同的EPS下邻域内线段数量
	 //List<HashMap<Double,Integer>> listForAll= new ArrayList<HashMap<Double,Integer>>();
	 //声明一个hashmap<EPS,hashmap<id_Track,num_EPS>>用于存放所有结果
	 //HashMap<Double,HashMap<Integer,Integer>> EPS_AllNum = new HashMap<Double,HashMap<Integer,Integer>>();
	 HashMap<Double,Double> EPS_HX = new HashMap<Double,Double>();//<eps,h(x)>
	 for(double eps=0;eps<45;eps=eps+0.01){
		// HashMap<Integer,Integer> id_num = new HashMap<Integer,Integer>();//用于存放在不同的id下的track内邻域轨迹数量
		 int All_length=1;//计算对应所有线段的邻域内轨迹线段总数,起点为1条，即线段本身
	 for(int i=0;i<list.size();i++){//遍历每条轨迹测算邻域内轨迹数目
		// HashMap<Double,Integer> EPS_NUM = new HashMap<Double,Integer>();//存放不同EPS下每条轨迹对应的邻域内轨迹数量
		 int indexI = list.get(i).getID();//对象轨迹（主轨迹）
		 ArrayList<Integer> indexEps = new ArrayList<Integer>();//用于记录在邻域之内的轨迹ID
		 for(int j=0;j<similarity[0].length;j++){
			 double distance= similarity[indexI][j];//对应的每条轨迹与主轨迹之间的距离
			 if(distance<eps){
				 //若距离在eps之内
				 if(j!=indexI){
					 //需要记下j即邻域轨迹的id号
					 indexEps.add(j);
				 }
			 }
		 }//该循环结束后可以确定领域的轨迹数目
		 
		//判断完一条轨迹的所有邻域内轨迹后，将其赋值index list，indexEps的长度即为每条轨迹的邻域轨迹数目
		list.get(i).setIndexEps(indexEps);
		All_length=All_length+indexEps.size();//每个主轨迹确定邻域后，对总线段数目累加
		//EPS_NUM.put(eps, indexEps.size());
		//id_num.put(i, indexEps.size());//<每条主轨迹的id号,对应的邻域内轨迹数目>
		//list.get(i).setNumEps(EPS_NUM);
		//计算P(Xi)
	 }
	// HashMap<Integer,Double> id_hx = new HashMap<Integer,Double>();//<id,h(x)>
	 double Reverse_hx =0;//对于每个eps,hx的初始值为0;
	 for(int k=0;k<list.size();k++){
		 //计算p(Xi)=每条轨迹的邻域数/所有轨迹的领域数目之和
		 int each_TrackNum = list.get(k).GetindexEPS().size()+1;
		 double proportion = each_TrackNum/All_length;//即p(Xk)
		 Reverse_hx = Reverse_hx+ proportion*(Math.log(proportion)/Math.log((double)2));//累加
		// id_hx.put(k, hx);//<id,h(x)>
	 }
	 EPS_HX.put(eps, -Reverse_hx);//<eps,h(x)>
	 //EPS_AllNum.put(eps, id_num);//<eps,hashmap<id,num>
	 }
	 return EPS_HX;
 }
 public HashMap<Double, ArrayList<Integer>> testEPS(double[][]similarity,List<Fragment> list){
	 //计算信息熵
	 //首先计算p(xi)即每条轨迹线段领域的出现概率
	 //第一步计算每条轨迹在不同的EPS下邻域内线段数量
	 //List<HashMap<Double,Integer>> listForAll= new ArrayList<HashMap<Double,Integer>>();
	 //声明一个hashmap<EPS,hashmap<id_Track,num_EPS>>用于存放所有结果
	 //HashMap<Double,HashMap<Integer,Integer>> EPS_AllNum = new HashMap<Double,HashMap<Integer,Integer>>();
	 HashMap<Double,ArrayList<Integer>> EPS_HX = new HashMap<Double,ArrayList<Integer>>();//<eps,h(x)>
	 for(double eps=0;eps<45;eps=eps+0.01){
		// HashMap<Integer,Integer> id_num = new HashMap<Integer,Integer>();//用于存放在不同的id下的track内邻域轨迹数量
		 int All_length=1;//计算对应所有线段的邻域内轨迹线段总数,起点为1条，即线段本身
		 ArrayList<Integer> num_length= new ArrayList<Integer>();//index为轨迹id号,对应的元素为邻域线段数
	 for(int i=0;i<list.size();i++){//遍历每条轨迹测算邻域内轨迹数目
		// HashMap<Double,Integer> EPS_NUM = new HashMap<Double,Integer>();//存放不同EPS下每条轨迹对应的邻域内轨迹数量
		 int indexI = list.get(i).getID();//对象轨迹（主轨迹）
		 ArrayList<Integer> indexEps = new ArrayList<Integer>();//用于记录在邻域之内的轨迹ID
		 for(int j=0;j<similarity[0].length;j++){
			 double distance= similarity[indexI][j];//对应的每条轨迹与主轨迹之间的距离
			 if(distance<eps){
				 //若距离在eps之内
				 if(j!=indexI){
					 //需要记下j即邻域轨迹的id号
					 indexEps.add(j);
				 }
			 }
		 }//该循环结束后可以确定领域的轨迹数目
		 
		//判断完一条轨迹的所有邻域内轨迹后，将其赋值index list，indexEps的长度即为每条轨迹的邻域轨迹数目
		list.get(i).setIndexEps(indexEps);
		All_length=All_length+indexEps.size();//每个主轨迹确定邻域后，对总线段数目累加
		num_length.add(indexEps.size());
		//EPS_NUM.put(eps, indexEps.size());
		//id_num.put(i, indexEps.size());//<每条主轨迹的id号,对应的邻域内轨迹数目>
		//list.get(i).setNumEps(EPS_NUM);
		//计算P(Xi)
	 }
	// HashMap<Integer,Double> id_hx = new HashMap<Integer,Double>();//<id,h(x)>
	 double Reverse_hx =0;//对于每个eps,hx的初始值为0;
	 for(int k=0;k<list.size();k++){
		 //计算p(Xi)=每条轨迹的邻域数/所有轨迹的领域数目之和
		 int each_TrackNum = list.get(k).GetindexEPS().size()+1;
		 double proportion = each_TrackNum/All_length;//即p(Xk)
		 Reverse_hx = Reverse_hx+ proportion*(Math.log(proportion)/Math.log((double)2));//累加
		// id_hx.put(k, hx);//<id,h(x)>
	 }
	 EPS_HX.put(eps, num_length);//<eps,h(x)>
	 //EPS_AllNum.put(eps, id_num);//<eps,hashmap<id,num>
	 }
	 return EPS_HX;
 }
  
}
