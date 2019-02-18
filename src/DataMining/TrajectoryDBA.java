package DataMining;

import java.util.ArrayList;
import java.util.List;

import com.cn.entity.MopsiEntity;
public class TrajectoryDBA {
	static final long serialVersionUID = 1L;

	private final static int NIL = -1;
	private final static int DIAGONAL = 0;
	private final static int LEFT = 1;
	private final static int UP = 2;
	private static double EARTH_RADIUS = 6378.137;  
	/**
	 * This attribute is used in order to initialize only once the matrixes
	 */
	private final static int MAX_SEQ_LENGTH = 4000;
    
	/**
	 * store the cost of the alignment
	 */
	private static double[][] costMatrix = new double[MAX_SEQ_LENGTH][MAX_SEQ_LENGTH];
	
	/**
	 * store the warping path
	 */
	private static int[][] pathMatrix = new int[MAX_SEQ_LENGTH][MAX_SEQ_LENGTH];

	/**
	 * Store the length of the optimal path in each cell
	 */
	private static int[][] optimalPathLength = new int[MAX_SEQ_LENGTH][MAX_SEQ_LENGTH];


	/**
	 * Dtw Barycenter Averaging (DBA)
	 * @param C average sequence to update
	 * @param sequences set of sequences to average
	 */
	public static List<MopsiEntity> Trajectory_DBA(List<MopsiEntity> C, List<List<MopsiEntity>> sequences) {
         final ArrayList<MopsiEntity>[] tupleAssociation = new ArrayList[C.size()];//C.size个MopsiEntity数组
		//final List<List<MopsiEntity>> tupleAssociation = new ArrayList<List<MopsiEntity>>(C.size());//创建一个List，其中每个元素都是MopsiEntity的arraylist，也就是二元arraylist
	/*	for (int i = 0; i < tupleAssociation.size(); i++) {
			List<MopsiEntity> temp_association= new ArrayList<MopsiEntity>(sequences.size());//每个元素开辟一个长度为序列点个数的double的arrayList
		    MopsiEntity entity= new MopsiEntity();
		    entity.setCid(-1);
		    temp_association.add(entity);
			tupleAssociation.set(i, temp_association);
		    tupleAssociation.add(temp_association);
		}
	 */
         for (int i = 0; i < tupleAssociation.length; i++) {
 			tupleAssociation[i] = new ArrayList<MopsiEntity>(sequences.size());//每个元素都是开辟一个长度为序列点个数的MopsiEntity的ArrayList
 		}
		int nbTuplesAverageSeq, i, j, indiceRes;
		double res = 0.0;
		int centerLength = C.size();//平均轨迹有几个点
		int seqLength;

		for (List<MopsiEntity> T : sequences) {//对于每条轨迹
			seqLength = T.size();//seqLength是该轨迹的点数， 

			costMatrix[0][0] = distanceTo(C.get(0), T.get(0));//平均序列的0号点到对应T序列的0号点的distance^2
			pathMatrix[0][0] = TrajectoryDBA.NIL;//路径设置为-1
			optimalPathLength[0][0] = 0;//最优路径设置为0

			for (i = 1; i < centerLength; i++) {//对于平均序列的每个点，从1号点开始（0号点已经设置好了）
				costMatrix[i][0] = costMatrix[i - 1][0] + distanceTo(C.get(i), T.get(0));
				pathMatrix[i][0] = TrajectoryDBA.UP;
				optimalPathLength[i][0] = i;
			}
			for (j = 1; j < seqLength; j++) {//对于T序列的每个点，同样也是设置三个矩阵的值
				costMatrix[0][j] = costMatrix[0][j - 1] + distanceTo(T.get(j), C.get(0));
				pathMatrix[0][j] = TrajectoryDBA.LEFT;
				optimalPathLength[0][j] = j;
			}
			for (i = 1; i < centerLength; i++) {//对于平均矩阵的每个点
				for (j = 1; j < seqLength; j++) {//遍历T矩阵的每个点
					indiceRes = TrajectoryDBA.ArgMin3(costMatrix[i - 1][j - 1], costMatrix[i][j - 1], costMatrix[i - 1][j]);
					pathMatrix[i][j] = indiceRes;
					switch (indiceRes) {
						case DIAGONAL:
							res = costMatrix[i - 1][j - 1];
							optimalPathLength[i][j] = optimalPathLength[i - 1][j - 1] + 1;
							break;
						case LEFT:
							res = costMatrix[i][j - 1];
							optimalPathLength[i][j] = optimalPathLength[i][j - 1] + 1;
							break;
						case UP:
							res = costMatrix[i - 1][j];
							optimalPathLength[i][j] = optimalPathLength[i - 1][j] + 1;
							break;
					}
					costMatrix[i][j] = res + distanceTo(C.get(i), T.get(j));
				}
			}

			nbTuplesAverageSeq = optimalPathLength[centerLength - 1][seqLength - 1] + 1;

			i = centerLength - 1;
			j = seqLength - 1;

			for (int t = nbTuplesAverageSeq - 1; t >= 0; t--) {
				MopsiEntity entity_temp = T.get(j);
				tupleAssociation[i].add(entity_temp);//这里要加的点的类型是MopsiEntity，所以tupleAssociation[i]的类型应该设置成MopsiEntity
				switch (pathMatrix[i][j]) {
					case DIAGONAL:
						i = i - 1;
						j = j - 1;
						break;
					case LEFT:
						j = j - 1;
						break;
					case UP:
						i = i - 1;
						break;
				}

			}
         System.out.println("h");
		}

		for (int t = 0; t < centerLength; t++) {//对于average route的每个点
            MopsiEntity temp_avg = new MopsiEntity();
			temp_avg = barycenter(tupleAssociation[t]);
			C.set(t, temp_avg);
		}
		return C;
	}

	private static double rad(double d)   
	{   
	     return d * Math.PI / 180.0;   
	}  
	/**   
	 * 根据两点间经纬度坐标（double值），计算两点间距离，单位为米   
	 */   
	public static double GetDistance(double lat1, double lng1, double lat2, double lng2)   
	{   
	    double radLat1 = rad(lat1);   
	    double radLat2 = rad(lat2);   
	    double a = radLat1 - radLat2;   
	    double b = rad(lng1) - rad(lng2);   
	    double s = 2 * Math.asin(Math.sqrt(Math.pow(Math.sin(a/2),2) +   
	    Math.cos(radLat1)*Math.cos(radLat2)*Math.pow(Math.sin(b/2),2)));   
	    s = s * EARTH_RADIUS;   
	    s = Math.round(s * 10000) / 10000;   
	    return s;   
	}   
	public static double Min3(final double a, final double b, final double c) {
		if (a < b) {
			if (a < c) {
				return a;
			} else {
				return c;
			}
		} else {
			if (b < c) {
				return b;
			} else {
				return c;
			}
		}
	}

	public static int ArgMin3(final double a, final double b, final double c) {
		if (a < b) {
			if (a < c) {
				return 0;
			} else {
				return 2;
			}
		} else {
			if (b < c) {
				return 1;
			} else {
				return 2;
			}
		}
	}

	public static double distanceTo(MopsiEntity a, MopsiEntity b) {
		//计算两个GPS点的距离，这里打算用球面距离
		double lat1 = Double.valueOf(a.getLatitude());
		double lon1 = Double.valueOf(a.getLongtitude());
		double lat2 = Double.valueOf(b.getLatitude());
		double lon2 = Double.valueOf(b.getLongtitude());
		double dis = GetDistance(lat1, lon1, lat2, lon2);
		return dis;
	}


	public static MopsiEntity barycenter(List<MopsiEntity> list) {//把一系列的点的latitude和longitude都求个平均值
		MopsiEntity result = new MopsiEntity();
		if (list.size() < 1) {
			throw new RuntimeException("empty double tab");
		}
		double sum_lat = 0.0;
		double sum_lon = 0.0;
		for (MopsiEntity o : list) {
			
			sum_lat += Double.valueOf(o.getLatitude());
			sum_lon += Double.valueOf(o.getLongtitude());
			
		}
		double avg_lat = sum_lat/list.size();
		double avg_lon = sum_lon/list.size();
		
		result.setLatitude(String.valueOf(avg_lat));
		result.setLongtitude(String.valueOf(avg_lon));
	//	return sum / tab.length;
		return result;
	}
	public List<List<Integer>> Normalization_Tras(List<List<Integer>> list){
		//这里首先把每条轨迹的长度统一，采取的方法就是把最长的那条轨迹多出来的点加在短的轨迹后面，
	 	List<List<Integer>> result = new ArrayList<List<Integer>>();
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
					int temp = 0;
					temp = list.get(index).get(k);//我希望这里是复制而不是同一个地址
					list.get(j).add(temp);
				}
			}
		}
		return list;
	}
   public List<Trajectory> MopsiToLine(List<MopsiEntity> list){
	   //把List<MopsiEntity>格式转为List<Trajectory>
	   List<Trajectory> result = new ArrayList<>();
	   Trajectory tra = new Trajectory();
	   List<Point> points_list = new ArrayList<Point>();
	   //MopsiEntity to Points
	   for(int i=0;i<list.size();i++) {
		   MopsiEntity mop_temp = list.get(i);
		   Point point = new Point();
		   point.setGPS_ID(mop_temp.getGPS_Id());
		   point.setSpeed(mop_temp.getSpeed());
		   point.setState(mop_temp.getState());
		   point.setTaxi_id(mop_temp.getTaxi_Id());
		   point.setX(Double.valueOf(mop_temp.getLatitude()));
		   point.setY(Double.valueOf(mop_temp.getLongtitude()));
		   point.setTime(mop_temp.getTime());
		   points_list.add(point);
	   }
	   tra.setPoints(points_list);
	   result.add(tra);
	   return result;
   }
}
