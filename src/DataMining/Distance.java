package DataMining;

import java.util.ArrayList;
import java.util.List;

import com.cn.entity.Taxi;

public class Distance {

	/**
	 * @description 涓や釜鐐逛箣闂寸殑娆у紡璺濈
	 * @param p1 绗竴涓偣
	 * @param p2 绗簩涓偣
	 * @return 娆у紡璺濈
	 */
	public static double distance(Point p1 , Point p2){
		double x = p1.x - p2.x;
		double y = p1.y - p2.y;
		return Math.sqrt(x * x  + y * y);
	}
	/**
	 * @description 璁＄畻绾挎鐨勯暱搴�
	 * @param l 绾挎
	 * @return 绾挎鐨勯暱搴�
	 */
	public static double distance(Line l){
		double x = l.getE().x - l.getS().x;
		double y = l.getE().y - l.getS().y;
		return Math.sqrt(x * x  + y * y);
	}
	/**
	 * @description 璁＄畻涓ゆ潯绾挎涔嬮棿鐨勫瀭鐩磋窛绂�
	 * @param l1 绗竴鏉＄嚎娈�
	 * @param l2 绗簩鏉＄嚎娈�
	 * @return 绾挎涔嬮棿鐨勫瀭鐩磋窛绂�
	 */
	public static double distance_perpen(Line l1 , Line l2){
		double dxy1 = distance(l1.getS(), l2.getS());
		double cos1 = cos_angle(l2.getS(), l1.getS() , l2.getS() , l2.getE());
		double dy1 = dxy1 * Math.sqrt(1 - cos1 * cos1);
		
		double dxy2 = distance(l1.getE(), l2.getE());
		double cos2 = cos_angle(l2.getE(), l1.getE() , l2.getE() , l2.getS());
		double dy2 = dxy2 * Math.sqrt(1 -cos2 * cos2);
		System.out.printf("cos1%f , cos2%f \n",cos1 , cos2);
		if(dy1 == 0 && dy2 == 0)
			return 0;
		else
			return Math.sqrt(dy1 * dy1 + dy2 * dy2) / (dy1 + dy2);
	}
	
	/**
	 * @description 杞ㄨ抗鍒掑垎鏃剁殑 鏈�灏忔弿杩伴暱搴�
	 * @param points 杞ㄨ抗闆嗗悎
	 * @param start 寮�濮嬬偣涓嬫爣锛堝寘鍚級
	 * @param end 缁撴潫鐐逛笅鏍囷紙鍖呭惈锛�
	 * @return
	 */
	public double MDL_VAL(ArrayList<Point> raw_points,List<Point> ch_points){
		//去重复
		for(int i=0;i<ch_points.size();i++){
			Point point = ch_points.get(i);
			for(int j=i+1;j<ch_points.size();j++){
				Point point_compare = ch_points.get(j);
				if(point.x==point_compare.x&&point.y==point_compare.y){
					ch_points.remove(j);
					j--;
				}
			}
		}
		//track为原始轨迹
		double result = 0;
		int seg_size = ch_points.size()-1;//划分好的线段数
		for(int i=0;i<ch_points.size()-1;i++){//对于每条线段
			int start = ch_points.get(i).order;
			int end = ch_points.get(i+1).order;
			double mdl = distance_mdl_par(raw_points,start,end);
			result+=mdl;
			System.out.println("seg "+i+" mdl is:"+mdl);
		}
		return result;
	}
 
	public static double distance_mdl_par(List<Point> points , int start , int end){
		double LH = Math.log10(distance(points.get(start) , points.get(end))) / Math.log10(2);
		double LDH = 0;
		Line l = new Line(points.get(start), points.get(end));
		double angleTemp = 0;
		double perpenTemp = 0;
		for(int i = start ; i < end ; i++){
			int next = i + 1;
			Line lTemp = new Line(points.get(i),points.get(next));  
			if (distance(l) >= distance(lTemp)) {
				angleTemp += distance_angle(l, lTemp);
				perpenTemp += distance_perpen2(l, lTemp);
			}else{
				angleTemp += distance_angle(lTemp, l);
				perpenTemp += distance_perpen2(lTemp, l);
			}
		}
		if(angleTemp == 0 || perpenTemp == 0){
			LDH = 0;
		}else{
			LDH=(Math.log10(perpenTemp)+Math.log10(angleTemp))/Math.log10(2);
		}
		
		return LH + LDH;
	}
	
	public static double distance_mdl_nopar(List<Point> points , int start , int end){
		
		double LHTemp = 0.0;
		for(int i = start ; i < end ; i++){
			LHTemp += distance(points.get(i) , points.get(i+1));
		}
		return Math.log10(LHTemp)/Math.log10(2);
	}
	
	/**
	 * @description 姹備袱鏉＄嚎娈甸棿鐨勫す瑙掍綑寮�
	 * @param 杈冮暱鐨勭嚎娈�
	 * @param 杈冪煭鐨勭嚎娈�
	 * @return 澶硅浣欏鸡
	 */
	public static  double distance_angle(Line l1 , Line l2){
		
		double len = distance(l2);
		
		double cos = cos_angle(l1, l2);
		
		double sin = Math.sqrt(1 - cos * cos);
		
		double angle = len * sin;
		
		return angle;
	}
	/**
	 * @description 璁＄畻涓ゆ潯绾挎涔嬮棿鐨勫す瑙掍綑寮�
	 * @param si 绾挎i鐨勮捣鐐�
	 * @param ei 绾挎i鐨勭粓鐐�
	 * @param sj 绾挎j鐨勮捣鐐�
	 * @param ej 绾挎j鐨勭粓鐐�
	 * @return
	 */
	static double cos_angle(Point si,Point ei,Point sj,Point ej){
		//鎶婁袱鏉＄嚎娈靛钩绉诲埌鍘熺偣锛堢嚎娈靛紑濮嬨�佺粨鏉熷潎鍑忓幓寮�濮嬬偣鐨勫潗鏍囷級
		double x1=ej.x-sj.x;
		double y1=ej.y-sj.y;
		
		double x2=ei.x-si.x;
		double y2=ei.y-si.y;
		
		double xi=Math.sqrt(Math.pow(x1,2)+Math.pow(y1,2));
		double yi=Math.sqrt(Math.pow(x2,2)+Math.pow(y2,2));
		if(xi == 0 || yi ==0){
			return 0;
		}
		double cos=(x1*x2+y1*y2)/(xi*yi);
		return cos;
	}
	
	/**
	 * @description 璁＄畻涓ゆ潯绾挎涔嬮棿鐨勫す瑙掍綑寮�
	 * @param l1 绾挎1
	 * @param l2 绾挎2
	 * @return 澶硅浣欏鸡
	 */
	static double  cos_angle(Line l1 , Line l2){
		return cos_angle(l1.getS() , l1.getE(), l2.getS() , l2.getE());
	}
	public static double caculate_angle(Point si,Point ei,Point sj,Point ej)
	{
		double x1=ej.x-sj.x;
		double y1=ej.y-sj.y;
		double cos = cos_angle(si, ei, sj, ej);
		float sin=(float) Math.sqrt(1-cos * cos);
		double angle=Math.sqrt(Math.pow(x1,2)+Math.pow(y1,2))*sin;
		return angle;
		
	}
	
	/**
	 * @description 璁＄畻涓ゆ潯绾挎涔嬮棿鐨勫瀭鐩磋窛绂�
	 * @param 杈冮暱鐨勭嚎娈�
	 * @param 杈冪煭鐨勭嚎娈�
	 * @return 绾挎涔嬮棿鐨勫瀭鐩磋窛绂�
	 */
	public static double distance_perpen2(Line l1 , Line l2){
		return caculate_perpen(l1.getS(), l1.getE(), l2.getS(), l2.getE());
	}
	
	public static double caculate_perpen(Point si,Point ei,Point sj,Point ej)
	{
		
		double x1=sj.x-si.x;
		double y1=sj.y-si.y;
		
		double x2=ei.x-si.x;
		double y2=ei.y-si.y;
		
		double x3=ej.x-si.x;
		double y3=ej.y-si.y;
		
		double x=Math.pow(x2,2)+Math.pow(y2,2);
		
		double u1=(x1*x2+y1*y2)/x;
		double u2=(x3*x2+y3*y2)/x;
	    
	    double psx=si.x+u1*x2;
		double psy=si.y+u1*y2;
		
		double pex=si.x+u2*x2;
		double pey=si.y+u2*y2;
		
		double Lper1=Math.sqrt(Math.pow(psx-sj.x,2)+Math.pow(psy-sj.y,2));
		double Lper2=Math.sqrt(Math.pow(pex-ej.x,2)+Math.pow(pey-ej.y,2));
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
	public static double dist(Line lp , Line tp)
	{
		
		Point si;
		Point ei;
		Point sj;
		Point ej;
		
		si=lp.getS();
		ei=lp.getE();
		sj=tp.getS();
		ej=tp.getE();
		
		double x1=sj.x-si.x;
		double y1=sj.y-si.y;
		
		double x2=ei.x-si.x;
		double y2=ei.y-si.y;
		
		double x3=ej.x-si.x;
		double y3=ej.y-si.y;
		
		double x=Math.pow(x2,2)+Math.pow(y2,2);
		
		double u1=(x1*x2+y1*y2)/x;
		double u2=(x3*x2+y3*y2)/x;
	    
	    double psx=si.x+u1*x2;
		double psy=si.y+u1*y2;
		
		double pex=si.x+u2*x2;
		double pey=si.y+u2*y2;
		
		double Lper1=Math.sqrt(Math.pow(psx-sj.x,2)+Math.pow(psy-sj.y,2));
		double Lper2=Math.sqrt(Math.pow(pex-ej.x,2)+Math.pow(pey-ej.y,2));
		double d_perpen;
		
		if(Lper1==0&&Lper2==0)
		{
			d_perpen=0;
		}
		else
		{
			d_perpen=(Math.pow(Lper1,2)+Math.pow(Lper2,2))/(Lper1+Lper2);
		}
		
		double x4=ej.x-sj.x;
		double y4=ej.y-sj.y;
		
		double xi=Math.sqrt(Math.pow(x4,2)+Math.pow(y4,2));
		double yi=Math.sqrt(Math.pow(x2,2)+Math.pow(y2,2));
		
		double cos=(x4*x2+y4*y2)/(xi*yi);
		if(cos>1){
			cos = 1;
		}
		if(cos<-1){
			cos = -1;
		}
		if(xi == 0 || yi == 0)
			cos = 0;
		double t=Math.sqrt(1 - cos * cos);
		
		double angle=(Math.sqrt(Math.pow(x4,2)+Math.pow(y4,2)))*t;
		
	    double Lpar1=Math.sqrt(Math.pow(psx-si.x,2)+Math.pow(psy-si.y,2));
		double Lpar2=Math.sqrt(Math.pow(pex-ei.x,2)+Math.pow(pey-ei.y,2));
		
		double d_parallel;
		
		if(Lpar1>Lpar2)	
			d_parallel=Lpar2;
		else
			d_parallel=Lpar1;
		return (d_parallel+d_perpen+angle);
	}
	public double disForTest(Line lp , Line tp){
		Point si;
		Point ei;
		Point sj;
		Point ej;
		
		si=lp.getS();
		ei=lp.getE();
		sj=tp.getS();
		ej=tp.getE();
		
		double x1=sj.x-si.x;
		double y1=sj.y-si.y;
		
		double x2=ei.x-si.x;
		double y2=ei.y-si.y;
		
		double x3=ej.x-si.x;
		double y3=ej.y-si.y;
		
		double x=Math.pow(x2,2)+Math.pow(y2,2);
		
		double u1=(x1*x2+y1*y2)/x;
		double u2=(x3*x2+y3*y2)/x;
	    
	    double psx=si.x+u1*x2;
		double psy=si.y+u1*y2;
		
		double pex=si.x+u2*x2;
		double pey=si.y+u2*y2;
		
		double Lper1=Math.sqrt(Math.pow(psx-sj.x,2)+Math.pow(psy-sj.y,2));
		double Lper2=Math.sqrt(Math.pow(pex-ej.x,2)+Math.pow(pey-ej.y,2));
		double d_perpen;
		
		if(Lper1==0&&Lper2==0)
		{
			d_perpen=0;
		}
		else
		{
			d_perpen=(Math.pow(Lper1,2)+Math.pow(Lper2,2))/(Lper1+Lper2);
		}
		
		double x4=ej.x-sj.x;
		double y4=ej.y-sj.y;
		
		double xi=Math.sqrt(Math.pow(x4,2)+Math.pow(y4,2));
		double yi=Math.sqrt(Math.pow(x2,2)+Math.pow(y2,2));
		
		double cos=(x4*x2+y4*y2)/(xi*yi);
		if(cos>1){
			cos =1;
		}
		if(cos<-1){
			cos = -1;
		}
		if(xi == 0 || yi == 0)
			cos = 0;
		double t=Math.sqrt(1 - cos * cos);
		
		double angle=(Math.sqrt(Math.pow(x4,2)+Math.pow(y4,2)))*t;
		
	    double Lpar1=Math.sqrt(Math.pow(psx-si.x,2)+Math.pow(psy-si.y,2));
		double Lpar2=Math.sqrt(Math.pow(pex-ei.x,2)+Math.pow(pey-ei.y,2));
		
		double d_parallel;
		
		if(Lpar1>Lpar2)	
			d_parallel=Lpar2;
		else
			d_parallel=Lpar1;
		return (d_parallel+d_perpen+angle);
	}
//	public static double dist(Line l1 , Line l2){
//		double angle = 0.0;
//		double minPerpen = 0.0;
//		double parallel = 0.0;
//		if(distance(l1) > distance(l2)){
//			angle = distance_angle(l1, l2);
//			parallel = distance_perpen2(l1, l2);
//		}else{
//			angle = dist(l2, l1);
//			parallel = distance_perpen2(l2, l1);
//		}
//	}

}
