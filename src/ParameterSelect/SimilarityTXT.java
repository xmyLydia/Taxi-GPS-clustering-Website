package ParameterSelect;

import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;

public class SimilarityTXT {
	  public void writeTXT(double[][] similarity){
	        FileWriter fw = null;
	        try {
	            fw = new FileWriter("d://testAll.txt",true);
	            //String c = "abs"+"\r\n";
	            for(int i=0;i<similarity.length;i++){
	            	for(int j=0;j<similarity[0].length;j++){
	            		String c = String.valueOf(similarity[i][j])+" ";
	            fw.write(c);
	            	}
	            	fw.write("\r\n");
	            }
	            fw.close();
	        } catch (IOException e1) {
	            e1.printStackTrace();
	            System.out.println("写入失败");
	            System.exit(-1);
	        }
	    }
	  @SuppressWarnings("rawtypes")
	public void writeTrackHx(HashMap<Double,Double> map){
	        FileWriter fw = null;
	        try {
	            fw = new FileWriter("d://hx.txt",true);
	            //String c = "abs"+"\r\n";
	          //  for(int i=0;i<list.size();i++){
	            Iterator<Entry<Double, Double>> iter = map.entrySet().iterator();
	            while(iter.hasNext()){
	            Map.Entry entry = (Map.Entry)iter.next();
	           // String c = String.valueOf(list.get(i).getID()+":  "+list.get(i).getFragment())+"\r\n";
	           double eps =Double.parseDouble(entry.getKey().toString());//EPS
	           String Hx = entry.getValue().toString();//每个EPS对应的H(X)
	           String c = eps+" "+Hx+"\r\n";
	            fw.write(c);
	            }
	            fw.close();
	        } catch (IOException e1) {
	            e1.printStackTrace();
	            System.out.println("写入失败");
	            System.exit(-1);
	        }
	    }  
	  
		public void testwriteTrackHx(HashMap<Double,ArrayList<Integer>> map){
	        FileWriter fw = null;
	        try {
	            fw = new FileWriter("d://hxtest.txt",true);
	            //String c = "abs"+"\r\n";
	          //  for(int i=0;i<list.size();i++){
	            Iterator<Entry<Double, ArrayList<Integer>>> iter = map.entrySet().iterator();
	            while(iter.hasNext()){
	            Map.Entry entry = (Map.Entry)iter.next();
	           // String c = String.valueOf(list.get(i).getID()+":  "+list.get(i).getFragment())+"\r\n";
	           double eps =Double.parseDouble(entry.getKey().toString());//EPS
	           String Hx = entry.getValue().toString();//每个EPS对应的H(X)
	           String c = eps+" "+Hx+"\r\n";
	            fw.write(c);
	            }
	            fw.close();
	        } catch (IOException e1) {
	            e1.printStackTrace();
	            System.out.println("写入失败");
	            System.exit(-1);
	        }
	    }
}
