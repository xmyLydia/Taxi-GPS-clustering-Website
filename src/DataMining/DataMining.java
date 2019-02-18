package DataMining;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.cn.entity.Taxi;
import com.cn.service.TaxiServiceImpl;

public class DataMining {
//分成三部分：partition，clustering，representative
public List<List<Taxi>> Partition_trajectory(List<Taxi> trajectory) throws ParseException{
	List<List<Taxi>> partition = new ArrayList<List<Taxi>>();
	//首先考虑到点与点时间间隔16秒，从而太过密集无法进行分割，因此先进行压缩
	List<Taxi> compress = getCompressed(Compression(trajectory,trajectory.size()), trajectory);
	TaxiServiceImpl impl = new TaxiServiceImpl();
	partition = impl.Partition(compress);//进行分割
	return partition;
 }
public Date convertTime(String str) throws ParseException{
	Date date = null;
	SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
	date = sdf.parse(str); 
	return date;
}
public HashMap<Integer,Double> getMaxDistance(List<Taxi>taxi){
	HashMap<Integer,Double> map = new HashMap<Integer,Double>();
	double max = 0 ;
	int max_num = 0;;
	Taxi first = taxi.get(0);
	double x_first = Double.parseDouble(first.getLatitude());
	double y_first = Double.parseDouble(first.getLongtitude());
	Taxi last = taxi.get(taxi.size()-1);
	double x_last = Double.parseDouble(last.getLatitude());
	double y_last = Double.parseDouble(last.getLongtitude());
	for(int i=1;i<taxi.size()-1;i++){
		//首先计算每个点到起点和终点连线的垂直距离
	Taxi temp = taxi.get(i);
	double x = Double.parseDouble(temp.getLatitude());
	double y = Double.parseDouble(temp.getLongtitude());
    double distance_Per = Math.abs((x - x_first)*(y_first - y_last)-(x_first-x_last)*(y - y_first))/Math.sqrt((x_first - x_last)*(x_first - x_last) + (y_first -y_last)*(y_first -y_last));                                                
    if(distance_Per > max){
    	max = distance_Per;
    	max_num = i;
    }
	}
	map.put(max_num,max);
	return map;
}
public Double getMaxDistance_max(List<Taxi>taxi){
	double max = 0 ;
	int max_num = 0;;
	Taxi first = taxi.get(0);
	double x_first = Double.parseDouble(first.getLatitude());
	double y_first = Double.parseDouble(first.getLongtitude());
	Taxi last = taxi.get(taxi.size()-1);
	double x_last = Double.parseDouble(last.getLatitude());
	double y_last = Double.parseDouble(last.getLongtitude());
	for(int i=1;i<taxi.size()-1;i++){
		//首先计算每个点到起点和终点连线的垂直距离
	Taxi temp = taxi.get(i);
	double x = Double.parseDouble(temp.getLatitude());
	double y = Double.parseDouble(temp.getLongtitude());
    double distance_Per = Math.abs((x - x_first)*(y_first - y_last)-(x_first-x_last)*(y - y_first))/Math.sqrt((x_first - x_last)*(x_first - x_last) + (y_first -y_last)*(y_first -y_last));                                                
    if(distance_Per > max){
    	max = distance_Per;
    	max_num = i;
    }
	}
	return max;
}
public List<Taxi> getCompressed(ArrayList<Integer>breakPoint,List<Taxi> trajectory){
	List<Taxi> taxi = new ArrayList<Taxi>();
	for(int i= 0;i<breakPoint.size();i++){
		int col= breakPoint.get(i);
		Taxi temp = trajectory.get(col);
		taxi.add(temp);
	}
	return taxi;
}
public double getMaxDistance_amongSub(List<Taxi>taxi,ArrayList<Integer> breakPoint){//寻找子轨迹中最大的欧氏距离
	double max= 0.0;
	List<List<Taxi>> sub = subTrajectory(breakPoint, taxi);//获得子轨迹
	for(int i=0;i<sub.size();i++){
		double temp = getMaxDistance_max(sub.get(i));//第i条子轨迹中的最大欧氏距离
		if(temp>max){
			max = temp;
		}
	}
	return max;
}
public double getThreshold(List<Taxi> trajectory) throws ParseException{
	//获得阈值
	Taxi first = trajectory.get(0);
	double x_first = Double.parseDouble(first.getLatitude());
	double y_first = Double.parseDouble(first.getLongtitude());
	Date first_time =  convertTime(first.getTime());
	
	Taxi last = trajectory.get(trajectory.size()-1);
	double x_last = Double.parseDouble(last.getLatitude());
	double y_last = Double.parseDouble(last.getLongtitude());
	Date last_time =  convertTime(last.getTime());
	Taxi second = trajectory.get(1);
	double x_second = Double.parseDouble(second.getLatitude());
    double y_second = Double.parseDouble(second.getLongtitude());
	Date second_time = convertTime(second.getTime());
	long deta_e =Math.abs(last_time.getTime()-first_time.getTime());
		long data_i = Math.abs(second_time.getTime()-first_time.getTime());
		double latiApprox_i = x_second + (data_i/deta_e)*(x_last - x_first);
		double longtiApprox_i = y_second + (data_i/deta_e)*(y_last - y_first);
		//分割阈值
		double threshold = Math.sqrt((latiApprox_i - x_second)*(latiApprox_i)+(longtiApprox_i - y_second)*(longtiApprox_i - y_second));
		return threshold;
	
}
List<List<Taxi>> subTrajectory(ArrayList<Integer> num_track, List<Taxi> taxi){
	List<List<Taxi>> subTracks = new ArrayList<List<Taxi>>();
	for(int i=0;i<num_track.size()-1;i++){
		List<Taxi> sub = new ArrayList<Taxi>();
		int start = num_track.get(i);
		int end = num_track.get(i+1);
		if(i!= num_track.size()-2){
		for(int j = start;j<end;j++){
			sub.add(taxi.get(j));
		}}else{
			for(int j = start;j<=end;j++){
				sub.add(taxi.get(j));
			}
		}
		subTracks.add(sub);//将子轨迹加入到list当中
	}
	return subTracks;
}
private ArrayList<Integer> Compression(List<Taxi> trajectory,int original_size) throws ParseException {
	// 使用TD-TR 算法进行轨迹压缩，降低密度
	ArrayList<Integer> breakPoint = new ArrayList<Integer>();//记录在哪个点进行分割
	HashMap<Integer,Double>map = getMaxDistance(trajectory);//轨迹为trajectory时的中间点到端点连线的最大值点号和最大距离
	double max=0.0;//最大距离
	int max_col = -1;//最大距离对应的点号
	 Set<Map.Entry<Integer, Double>> set=map.entrySet();    
     for (Iterator <Map.Entry<Integer, Double>> iterator = set.iterator(); iterator.hasNext();) {  
         Map.Entry<Integer, Double> entry = (Map.Entry<Integer, Double>) iterator.next();  
         max_col=entry.getKey();  
         max=entry.getValue();  
     }  
	double max_sub = 0.0;
	double threshold = getThreshold(trajectory);//轨迹为trajectory时的阈值：这里是采用第二个点的相似点到本身点的距离作为阈值
	//首先判断max>threshold?如果是的话加入breakpoint，分割后获得两条轨迹，分别迭代
	if(max > threshold){
		max_sub = getMaxDistance_amongSub(trajectory,breakPoint);
		if((original_size - breakPoint.size())!=2&&(max_sub>=threshold)){
		  breakPoint.add(max_col);
		  List<List<Taxi>> subTracks = subTrajectory(breakPoint, trajectory);//分割后获取子轨迹
		  for(int i=0;i<subTracks.size();i++){
		   ArrayList<Integer> temp = Compression(subTracks.get(i),original_size);
		   breakPoint.addAll(temp);
		}
	}
	}
	return breakPoint;
}

public void transform(List<Taxi> list) {
	for(int i=0;i<list.size();i++)
	{
		double longtitude = Double.parseDouble(list.get(i).getLongtitude())+0.0045;
		list.get(i).setLongtitude(Double.toString(longtitude));
		double latitude = Double.parseDouble(list.get(i).getLatitude())-0.002;
		list.get(i).setLatitude(Double.toString(latitude));
	}
}
public List<List<Taxi>> GetFragment(List<String>list) {
	List<List<Taxi>> result= new ArrayList<>();
	for(int i=0;i<list.size();i++)
	{
		GetData getData = new GetData();
		List<Taxi> array = getData.GetById(list.get(i));
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
public HashMap<Integer,ArrayList<Double>> GetSimilarity_data(List<List<Taxi>> trajectory) {
	HashMap<Integer,ArrayList<Double>> similarity = new HashMap<Integer,ArrayList<Double>>();//key对应的是第几条轨迹，value对应的是存放和每条线段的相似度的list
	int size = trajectory.size();//一共几条轨迹
	for(int i=0;i<size;i++){
		ArrayList<Double> list = new ArrayList<Double>();//每条轨迹对应的相似度List
		for (int j = 0; j < size; j++) {
			double temp_similarity =  GetDirectionSensitiveHausdorffDistance(trajectory.get(i), trajectory.get(j));
			 list.add(temp_similarity);
		}
		similarity.put(i, list);
	}
	return similarity;
}

public HashMap<Integer, ArrayList<Integer>>  TRACLUS_data(List<List<Taxi>> list, HashMap<Integer,ArrayList<Double>> similarity){

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
	 System.out.println("第"+i+"条轨迹");
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
Date d = new Date();
System.out.println(d);
return ClusterAll;
}
public ArrayList<Integer> EPS_NUM(int numTra,HashMap<Integer,ArrayList<Double>> similarity,double eps){
	ArrayList<Integer> epsList = new ArrayList<Integer>();
	for(int i=0;i<similarity.get(numTra).size();i++){
		double sim = similarity.get(numTra).get(i);
		if(sim <= eps){
			epsList.add(i);//将领域轨迹的行号记录下来
		}
	}
	return epsList ;// 领域轨迹数
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
public void writeObject(HashMap<Integer,ArrayList<Integer>>map){
    try {
        List<HashMap<Integer, ArrayList<Integer>>> list=new ArrayList<HashMap<Integer, ArrayList<Integer>>>();
        list.add(map);
        
        FileOutputStream outStream = new FileOutputStream("G:/data/1.txt");
        ObjectOutputStream objectOutputStream = new ObjectOutputStream(
                outStream);

        objectOutputStream.writeObject(list);
        outStream.close();
        System.out.println("successful");
    } catch (Exception e) {
        e.printStackTrace();
    }
}

public void readObject(){
    FileInputStream freader;
    try {
        freader = new FileInputStream("G:/data/1.txt");
        ObjectInputStream objectInputStream = new ObjectInputStream(freader);
        HashMap<String,Object> map = new HashMap<String,Object>();
        
        List<Map<String, Object>> list=(List<Map<String, Object>>)objectInputStream.readObject();
        for (Map<String, Object> map2 : list) {
            System.out.println(map2.toString());
        }
    } catch (FileNotFoundException e) {
        // TODO Auto-generated catch block
        e.printStackTrace();
    } catch (IOException e) {
        // TODO Auto-generated catch block
        e.printStackTrace();
    } catch (ClassNotFoundException e) {
        // TODO Auto-generated catch block
        e.printStackTrace();
    }
}
public List<Taxi> washTrajectory(List<Taxi> taxi){
	//对原始数据进行清洗，然后返回清洗后的值
	List<Taxi> result = new ArrayList<Taxi>();
	if(taxi.size()>2){
		return null;
	}
	for(int i=0;i<taxi.size();i++){
	    //对轨迹里面的每一个点进行判断，如果
		return result;
	}
	return result;
}
public static void main(String args[]) throws ParseException{
	List<List<Taxi>> list_tra = new ArrayList<List<Taxi>>();
	GetData getData = new GetData();
	List<Taxi> taxi = getData.GetById("81421");//出租车牌号为87553的出租车在10月12号的轨迹
	DataMining datamining = new DataMining();
	ArrayList<Integer> breakPoint = datamining.Compression(taxi,taxi.size());//轨迹压缩
	System.out.println(breakPoint.size());
	//调用partition_trajectory函数
//	List<String> id = getData.AllTaxiId();
//	list_tra = datamining.GetFragment(id);
	//调用traclus函数（在impl里面）
//	System.out.println(list_tra.size());
	//调用representation函数（在Impl里面）
//	HashMap<Integer, ArrayList<Integer>> map = datamining.TRACLUS_data(list_tra, datamining.GetSimilarity_data(list_tra));
	//写入文件
//	datamining.writeObject(map);
//	System.out.println(map.size());
	//输出当前时间
//	Date date = new Date();
//	System.out.println(date);
}
}
