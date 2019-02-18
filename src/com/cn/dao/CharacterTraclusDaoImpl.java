package com.cn.dao;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.annotation.Resource;

import org.hibernate.SessionFactory;
import org.springframework.orm.hibernate3.support.HibernateDaoSupport;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import com.cn.entity.CPointCluster;
import com.cn.entity.char_TraclusEntity;
import com.cn.entity.ClusterResultEntity;
import com.cn.entity.Taxi;
@Repository("char_traclus")
public class CharacterTraclusDaoImpl extends HibernateDaoSupport implements CharacterTraclusDao{
	@Resource
	public void setSuperSessionFactory(SessionFactory sessionFactory) {
		this.setSessionFactory(sessionFactory);
	}
	@Override
	@Transactional
	public void save(char_TraclusEntity ce) {
		this.getHibernateTemplate().save(ce);
	}
 
	@SuppressWarnings("unchecked")
	@Override
	public List<char_TraclusEntity> Search() {
		return this.getSession().createQuery("from char_TraclusEntity order by Time").list();
	}
	@SuppressWarnings("unchecked")
	@Override
	public List<char_TraclusEntity> SearchById(String taxiId) {
		 return this.getSession().createQuery("from char_TraclusEntity where Taxi_Id=? order by Time").setParameter(0, taxiId).list();
	}
	@Override
	public void Delete() {
		List<char_TraclusEntity> list   =   this.Search();//从cluster_result里面获取所有记录
        if(list.size()>0){  
           this.getHibernateTemplate().deleteAll(list);
         }
		
	}
	@SuppressWarnings({ "unchecked", "rawtypes" })
	@Override
	public Set<String> getAllTaxiId() {
		List<char_TraclusEntity> array = this.getSession().createQuery("from char_TraclusEntity").list();
		Set<String> result = new HashSet();
		for(int i=0;i<array.size();i++){
			result.add(array.get(i).getTaxi_Id());
		}
		return result;
	}
	@Override
	public void Store(List<char_TraclusEntity> result) {
		for(int i=0;i<result.size();i++){
			char_TraclusEntity temp = result.get(i);//temp为一个CPointCluster
			String GPS_ID = temp.getGPS_Id();
			String taxi_id = temp.getTaxi_Id();
			String  Latitude = String.valueOf(temp.getLatitude());
			String  Longtitude = String.valueOf(temp.getLongtitude());
			String  speed = temp.getSpeed();
			String angle = temp.getAngle();
			String time = temp.getTime();
			String state = temp.getState();
			String Algor_type = temp.getAlgor_type();
			char_TraclusEntity ce = new char_TraclusEntity();
			ce.setGPS_Id(GPS_ID);
			ce.setTaxi_Id(taxi_id);
			ce.setLongtitude(Longtitude);
			ce.setLatitude(Latitude);
			ce.setSpeed(speed);
			ce.setAngle(angle);
			ce.setTime(time);
			ce.setState(state);
			ce.setAlgor_type(Algor_type);
		//	ce.setId(i);
			//设置好entity属性之后即可进行save操作
			save(ce);
		}
		
	}
	
}
