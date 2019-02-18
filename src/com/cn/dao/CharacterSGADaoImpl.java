package com.cn.dao;

import java.util.List;

import javax.annotation.Resource;

import org.hibernate.SessionFactory;
import org.springframework.orm.hibernate3.support.HibernateDaoSupport;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import com.cn.entity.char_SGAEntity;
@Repository("char_sga")
public class CharacterSGADaoImpl extends HibernateDaoSupport implements CharacterSGADao{
	@Resource
	public void setSuperSessionFactory(SessionFactory sessionFactory) {
		this.setSessionFactory(sessionFactory);
	}
	@Override
	@Transactional
	public void save(char_SGAEntity ce) {
		this.getHibernateTemplate().save(ce);
	}
	@Override
	public void saveAll(List<char_SGAEntity> ce) {
		 for(int i=0;i<ce.size();i++){
			 char_SGAEntity Ce = ce.get(i);
			 this.getHibernateTemplate().save(Ce);
		 }
		
	}
	@SuppressWarnings("unchecked")
	@Override
	public List<char_SGAEntity> Search() {
		return this.getSession().createQuery("from char_SGAEntity order by Time").list();
	}
	@Override
	public void Delete() {
		List<char_SGAEntity> list   =   this.Search();//从sga表里面获取所有记录
        if(list.size()>0){  
           this.getHibernateTemplate().deleteAll(list);//全部删除
         }
		
	}
	@Override
	public void Store(List<char_SGAEntity> result) {
		for(int i=0;i<result.size();i++){
			char_SGAEntity temp = result.get(i);//temp为一个CPointCluster
			String GPS_ID = temp.getGPS_Id();
			String taxi_id = temp.getTaxi_Id();
			String  Latitude = String.valueOf(temp.getLatitude());
			String  Longtitude = String.valueOf(temp.getLongtitude());
			String  speed = temp.getSpeed();
			String angle = temp.getAngle();
			String time = temp.getTime();
			String state = temp.getState();
			String Algor_type = temp.getAlgor_type();
			char_SGAEntity ce = new char_SGAEntity();
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
