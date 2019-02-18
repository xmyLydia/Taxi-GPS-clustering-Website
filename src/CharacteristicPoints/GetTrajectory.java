package CharacteristicPoints;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.TreeSet;

import DataMining.GetData;
public class GetTrajectory {
      /* public List<List<Point>> getTracksAll(){
    	   GetData gt = new GetData();
    	   List<List<Point>> list_point = new ArrayList<List<Point>>();
    	  List<List<Taxi>> list = gt.getPointForCharacter();//得到100条轨迹
    	  //为了重用，将taxi的数据转化为point
    	  
       }*/
	public ArrayList<Point> getTracks(String taxi_id){
		GetData gt = new GetData();
		return gt.getTracks(taxi_id);
	}
     public static void main(String args[]){
    	 List<Integer> list = new ArrayList<Integer>();
    	 for(int i=0;i<100;i++){
    		 int temp = i;
    		 list.add(temp);
    	 }
    	 for(int i=0;i<100;i++){
    		 list.add(i);
    	 }
    	 TreeSet<String> oldIds = new TreeSet<String>();
         if(list !=null){// 数据库已经存在的数据
             for (int i = 0; i < list.size(); i++){
                  oldIds.add(list.get(i).toString());
             }
         }
        // Collections.sort(oldIds);
         System.out.println(oldIds.size());
     }
	
}
