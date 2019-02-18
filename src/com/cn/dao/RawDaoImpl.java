package com.cn.dao;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.annotation.Resource;

import org.hibernate.SessionFactory;
import org.springframework.orm.hibernate3.support.HibernateDaoSupport;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import com.cn.entity.CPointCluster;
import com.cn.entity.RawEntity;
import com.cn.entity.Taxi;
@Repository("cp_raw")
public class RawDaoImpl extends HibernateDaoSupport implements RawDao{

	@Resource
	public void setSuperSessionFactory(SessionFactory sessionFactory) {
		this.setSessionFactory(sessionFactory);
	}
     @Transactional
     @Override
     public void Traclus_DbscanStore(List<List<Taxi>>data,String cluster_type,String cp_type){
    	 List<Taxi>all = new ArrayList<Taxi>();
    	 for(int i=0;i<data.size();i++){
    		 all.addAll(data.get(i));
    	 }
    	 for(int j=0;j<all.size();j++){ 
			    RawEntity raw = new RawEntity();
		   		Taxi temp_taxi = all.get(j);//taxi点
		   		String gpsid = temp_taxi.getGPS_Id();
		   		String taxi_id = temp_taxi.getTaxi_Id();
		   		String latitude = temp_taxi.getLatitude();
		   		String longtitude= temp_taxi.getLongtitude();
		   		String speed = temp_taxi.getSpeed();
		   		String angle = temp_taxi.getAngle();
		   		String state = temp_taxi.getState();
		   		String time = temp_taxi.getTime();
		   		String cluster_id = String.valueOf(j+1);//一个for里面的cluster_id都是一样的
		   		String id = cluster_id+j;
		   	     raw.setGPS_Id(gpsid);
		         raw.setTaxi_Id(taxi_id);  
		         raw.setLongtitude(longtitude); 
		         raw.setLatitude(latitude); 
		         raw.setSpeed(speed);
		         raw.setAngle(angle);
		         raw.setTime(time);
		         raw.setState(state);
		         raw.setCluster_id(cluster_id);
		         raw.setCluster_type(cluster_type);
		         raw.setCp_type(cp_type);
		         raw.setId(id);
		         
		         saveRaw(raw);
		   	}
     }
     @Transactional
 	@Override
    public Set getClusterId(){
		List<RawEntity> array = this.getSession().createQuery("from RawEntity").list();
		Set<String> result = new HashSet();
		for(int i=0;i<array.size();i++){
			result.add(array.get(i).getCluster_id());
		}
		return result;
    }
    @Transactional
	@Override
	public void Store(List<CPointCluster> result,String cluster_type,String cp_type) {
		// TODO Auto-generated method stub
		//将聚类得到的结果批量地存入cp_raw表中
		 int count =0;
		for(int i=0;i<result.size();i++){
			CPointCluster temp = result.get(i);//temp为一个CPointCluster
			List<Taxi> points = temp.getTurningPoints();
			
			 for(int j=0;j<points.size();j++){ 
				    RawEntity raw = new RawEntity();
			   		Taxi temp_taxi = points.get(j);//taxi点
			   		String gpsid = temp_taxi.getGPS_Id();
			   		String taxi_id = temp_taxi.getTaxi_Id();
			   		String latitude = temp_taxi.getLatitude();
			   		String longtitude= temp_taxi.getLongtitude();
			   		String speed = temp_taxi.getSpeed();
			   		String angle = temp_taxi.getAngle();
			   		String state = temp_taxi.getState();
			   		String time = temp_taxi.getTime();
			   		String cluster_id = String.valueOf(temp.getId());//一个for里面的cluster_id都是一样的
			   		String id = cluster_id+j;
			   	     raw.setGPS_Id(gpsid);
			         raw.setTaxi_Id(taxi_id);  
			         raw.setLongtitude(longtitude); 
			         raw.setLatitude(latitude); 
			         raw.setSpeed(speed);
			         raw.setAngle(angle);
			         raw.setTime(time);
			         raw.setState(state);
			         raw.setCluster_id(cluster_id);
			         raw.setCluster_type(cluster_type);
			         raw.setCp_type(cp_type);
			         raw.setId(id);
			         count++;
			         saveRaw(raw);
			   	}
			
		}
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<RawEntity> Search(String cluster_type,String cp_type) {
			//org.hibernate.Session session = this.getSession();
			//Query query =session.createQuery("from Taxi where Taxi_Id='"+taxiId+"' order by Time") ;
		//	return this.getSession().createQuery("from Taxi where Taxi_Id=? order by Time").setParameter(0, taxiId).list();
			return this.getSession().createQuery("from RawEntity where cluster_type=? and cp_type = ? order by Time").setParameter(0, cluster_type).setParameter(1,cp_type).list();
		 
	}
	@SuppressWarnings("unchecked")
	@Override
	public List<RawEntity> SearchAll() {
			//org.hibernate.Session session = this.getSession();
			//Query query =session.createQuery("from Taxi where Taxi_Id='"+taxiId+"' order by Time") ;
		//	return this.getSession().createQuery("from Taxi where Taxi_Id=? order by Time").setParameter(0, taxiId).list();
			return this.getSession().createQuery("from RawEntity order by Time").list();
		 
	}
	@Transactional
	@Override
	public void DeleteRaw() {
	List<RawEntity> list   =   this.SearchAll();//从cluster_result里面获取所有记录
        if(list.size()>0){  
           this.getHibernateTemplate().deleteAll(list);//truncate
         }
	}
	@Transactional
	@Override
	public void saveRaw(RawEntity raw) {
		this.getHibernateTemplate().merge(raw);
	}
	@Transactional
	@Override
	public void merge(RawEntity rawEntity) {
		this.DeleteRaw();
		this.saveRaw(rawEntity);
		
	}
	
}
