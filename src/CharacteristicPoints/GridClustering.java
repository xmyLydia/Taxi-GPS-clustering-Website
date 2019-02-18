package CharacteristicPoints;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

import com.cn.entity.CPointCluster;
import com.cn.entity.Taxi;

import DataMining.GetData;

/**
 *     R R R R           A           MMM         MMM    B B B B        O O O O      TTTTTTTTTTTTTTTT  
 *     R     R          A A          M  M       M  M    B      B      O       O            TT
 *     R     R         A   A         M   M     M   M    B      B     O         O           TT 
 *     R R  R         A     A        M    M   M    M    B B B B      O         O           TT
 *     R    R        A A A A A       M     MMM     M    B      B     O         O           TT
 *     R     R      A         A      M             M    B      B      O       O            TT
 *     R      R    A           A     M             M    B B B B        O O O O             TT  
 */

public class GridClustering {
	
	//four parameters 
	public ArrayList<Point>data;
	public  int m;
	public  int nx;
	public  int ny;
	
	//media data 
	public int[][] I;
	public int[][] seed;
	public int[][] J;
	
	public int clustersNumber = 0;  //the number of the clusters
	
	public GridClustering(ArrayList<Point> rawData, int m, int nx, int ny)
	{
		this.data = rawData;
		this.m    = m;
		this.nx   = nx;
		this.ny   = ny;
	}
	
	//compute the grid each point belongs to and get the I
	//according to the given 'data','nx','ny'
	public boolean dataGridImage()
	{
		if (data == null || m == 0 || nx <= 0 || ny<= 0)
		{
			System.err.println("Please initial the parameters first!");
			return false;
		}
		
		double x_min = 0, x_max = 0;
		double y_min = 0, y_max = 0;
		double point_x, point_y;
		
		x_min = data.get(0).getX();
		x_max = data.get(0).getX();
		y_min = data.get(0).getY();
		y_max = data.get(0).getY();
		System.out.println(data.size());
		for (int i=0; i<data.size(); i++)
		{
			Point temp_point = data.get(i);
			point_x = temp_point.getX();
			point_y = temp_point.getY();
			if (point_x <= x_min)
				x_min = point_x;
			if (point_x >= x_max)
				x_max = point_x;
			if (point_y <= y_min)
				y_min = point_y;
			if (point_y >= y_max)
				y_max = point_y;
		}
		
		double width  = x_max - x_min;
		double height = y_max - y_min;
		if (width <= 0 || height <= 0)
		{
			System.err.println("The width and height are wrong!");
			return false;
		}
		int temp_t, temp_s;
		for (int i=0; i<data.size(); i++)
		{
			Point temp = data.get(i);
			temp_t = (int)Math.ceil( (x_max - temp.getX()) / width * nx);
			temp_s = (int)Math.ceil( (y_max - temp.getY()) / height * ny);
			if (temp_t == 0)
				temp_t = 1;
			if (temp_s == 0)
				temp_s = 1;
			
			temp.t = temp_t;
			temp.s = temp_s;
			data.set(i,temp);
		}
		int xyBig = (nx>ny)? nx : ny;
		I = new int[xyBig+1][xyBig+1];
		for(int i=0; i< data.size(); i++)
		{
			Point temp = data.get(i);
			I[temp.t][temp.s] += 1;
		}
		
		return true;
	}
	
	//0 for random, 1 for maximal values, 2 for aliquots
	public void findSeeds(int option)
	{
		if (option<0 || option>2 || m<1)
		{
			System.err.println("No this option for finding seeds!");
			return;
		}
		
		seed = new int[m][2];
		if (option == 0)
		{
			//choose m values that range from 1 to nx*ny which represents 
			//the grid chosen to be one seed
			int[] seedGridNumbers = Brain.randomChoose(1, nx*ny, m);
			int temp_t, temp_s;
			for (int i=0; i<m; i++)
			{
				if(seedGridNumbers!=null){
				temp_t = (seedGridNumbers[i] - 1) / nx + 1;
				temp_s = seedGridNumbers[i] - (temp_t - 1) * nx;
				
				seed[i][0] = temp_t;
				seed[i][1] = temp_s;
				}
			}
			
		}else if(option == 1)     
		{
			seed = Brain.getSeedByMaxValues(m, I);
		}else           // option == 2
		{
			int[] seedGridNumbers = Brain.aliquotsChoose(nx*ny, m);
			int temp_t, temp_s;
			for (int i=0; i<m; i++)
			{
				temp_t = (seedGridNumbers[i] - 1) / nx + 1;
				temp_s = seedGridNumbers[i] - (temp_t - 1) * nx;
				
				seed[i][0] = temp_t;
				seed[i][1] = temp_s;
			}

		}
		
	}
	
	public void seedGrowing()
	{
		if (seed == null)
		{
			System.out.println("The seed is empty, please use method 'findSeeds' first!");
			return;
		}
		int xyBig = (nx>ny)? nx : ny;
		J = new int[xyBig+1][xyBig+1];  
		
		int[] x_direction = {-1, 0, 1, 0,  1, -1, 1, -1};
		int[] y_direction = {0, 1, 0, -1, 1, 1, -1, -1};
		int label = 1;            
		int[] seedx = new int[(nx + 1) * (ny + 1)];
		int[] seedy = new int[(nx + 1) * (ny + 1)];
		
		for(int i=0; i<seed.length; i++)
		{
			int nStart = 1;
			int nEnd   = 1;
			seedx[1] = seed[i][0];
			seedy[1] = seed[i][1];
			boolean flag = false;
			
			if (J[seed[i][0]] [seed[i][1]] == 0)
			{
				J[seed[i][0]] [seed[i][1]] = label;
				while(nStart <= nEnd)
				{
					int current_x = seedx[nStart];
					int current_y = seedy[nStart];
					for (int k=0; k<8; k++)
					{
						int current_xx = current_x + x_direction[k];
						int current_yy = current_y + y_direction[k];
						if(current_xx > 0 && current_xx < (xyBig+1) 
						&& current_yy > 0 && current_yy < (xyBig+1))
						{
							if (I[current_xx][current_yy]>0 && J[current_xx][current_yy]==0)
							{
								flag = true;
								J[current_xx][current_yy] = label;
								nEnd ++;
								seedx[nEnd] = current_xx;
								seedy[nEnd] = current_yy;
							}
						}
					}
					nStart++;
				}
//				if(flag)    //only if label is assigned 
//				{
//					label++;
//					flag = false;
//				}
				//if(flag)
				label++;
				
			}
		}
		
		System.out.println("Label is : "+ label);
		if (label > 1)
		{
			this.clustersNumber = label - 1;
		}
		System.out.println("There are "+ this.clustersNumber + " clusters.");
	}
	
	public void assignClusterIDtoPoints()
	{
		for (int i=0; i<data.size(); i++)
		{
			Point temp = data.get(i);
			int gridT = temp.t;
			int gridS = temp.s;
			temp.clusterID = J[gridT][gridS];
			data.set(i, temp);
		}
	}
	
	public void printDataCluster()
	{
		int count  =0;
		for (int i=0; i<data.size(); i++)
		{
			Point temp = data.get(i);
			count++;
			System.out.println(temp.getX() + "," + temp.getY() + " clusterID: "+ temp.clusterID );
		}
		System.out.println("count:"+count);
	}
	
	public void printJ()
	{
		if(J != null && J.length>1 && J[0].length>1)
		{
			for(int i=1; i<J.length; i++)
			{
				for(int j=1; j<J[0].length; j++)
				{
					System.out.println("J["+i+"]["+j+"] : "+J[i][j]);
				}
			}
		}
	}
	public static void main(String args[]) throws ClassNotFoundException, SQLException{
		//获取数据
		GetData gt = new GetData();
		 String cp_type = "traclus";
		List<Taxi> Data = gt.getCharacterResult(cp_type);
		ArrayList<Point> rawData = transmit(Data);
		GridClustering grid = new GridClustering(rawData, 30, 12, 12);
		grid.dataGridImage();
		grid.findSeeds(1);
		grid.seedGrowing();
		grid.assignClusterIDtoPoints();
		grid.getClusterResult();
		List<CPointCluster> grid_cluster = grid.getClusterResult();
		//存入数据库
		
		 String cluster_type="grid";//聚类方法：有DBSCAN,Grid,等等
		 gt.DbStoreHelper();
		  gt.AddIntoCluster(grid_cluster,cluster_type,cp_type);//将聚类中心点加入cluster_result
	      gt.AddCluster_rawPoint(grid_cluster,cluster_type,cp_type);//将raw point加入cluster_raw
		for(int i=0;i<grid.data.size();i++){
			Point temp = grid.data.get(i);
			System.out.println("CLUSTER_ID"+temp.clusterID);
		}
		grid.printDataCluster();
		grid.printJ();
	}
	 private ArrayList<Integer> RemoveDuplicateNum(ArrayList<Point> list)
     {
		 ArrayList<Integer> clusterList = new ArrayList<>();
		  for(int i=0;i<list.size();i++){
			  clusterList.add(list.get(i).clusterID);
		  }
         for (int i = 0; i < clusterList.size() - 1; i++)
         {
             for (int j = i + 1; j < clusterList.size(); j++)
             {
                 if (clusterList.get(i).equals(clusterList.get(j)))
                 {
                	 clusterList.remove(j);
                     j--;
                 }
             }
         }
         return clusterList;

     }
    public ArrayList<CPointCluster> getClusterResult(){
    	ArrayList<CPointCluster> result = new ArrayList<CPointCluster>();
    
    	ArrayList<Integer> cList = RemoveDuplicateNum(this.data);
    	ArrayList<List<Taxi>> all = new ArrayList<>();
    	for(int i=0;i<cList.size();i++){
    		List<Taxi> temp = new ArrayList<Taxi>();//每个聚类对应一个点集
    		all.add(temp);
    		CPointCluster temp_CP = new CPointCluster();
    		result.add(temp_CP);
    	}
    	for(int j=0;j<data.size();j++){
    		//对于每个点
    		Point point = data.get(j);
    		for(int k=0;k<cList.size();k++){
    			  int cluster_num = cList.get(k);
    			  System.out.println("clusterid-:"+point.clusterID);
    			  if(point.clusterID==cluster_num){
    				  all.get(k).add(revert(point));
    				  break;
    			  }
    		}
    	}
    	for(int i=0;i<result.size();i++){
    		result.get(i).setTurningPoints(all.get(i));
    	}
    	CalculateCenter(result);
    	//计算中心点
    	for(int m=0;m<result.size();m++){
   		 result.get(m).setId(m);
   	 }
    	return result;
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
    public Taxi revert(Point p){
    	Taxi t = new Taxi();
    	 t.setGPS_Id(p.getGPS_ID());
    	 t.setTaxi_Id(p.getTaxi_id());
    	 t.setLatitude(String.valueOf(p.getX()));
    	 t.setLongtitude(String.valueOf(p.getY()));
    	 t.setSpeed(p.getSpeed());
    	 t.setAngle(p.getAngle());
    	 t.setTime(p.getTime());
    	 t.setState(p.getState());
    	 
    	 return t;
    }
	private static  ArrayList<Point> transmit(List<Taxi> data) {
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
}
