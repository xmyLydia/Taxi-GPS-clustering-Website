package ParameterSelect;

import java.math.BigDecimal;
import java.util.Comparator;

import com.cn.entity.Taxi;

   
public class MyComparator implements Comparator<Taxi>{
@Override
public int compare(Taxi t1, Taxi t2) {
	// TODO Auto-generated method stub
	 
	Double value1 =Double.parseDouble(t1.getLatitude());
	Double value2 =Double.parseDouble(t2.getLatitude());
	 
	 return value1.compareTo(value2);
} 
}