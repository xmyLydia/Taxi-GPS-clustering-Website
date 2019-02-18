package com.cn.dao;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.annotation.Resource;

import org.hibernate.SessionFactory;
import org.springframework.orm.hibernate3.support.HibernateDaoSupport;
import org.springframework.stereotype.Repository;

import com.cn.entity.MopsiEntity;
import com.cn.entity.Taxi;

@Repository("sample_mopsi")
public class MopsiDaoImpl extends HibernateDaoSupport implements MopsiDao{
	@Resource
	public void setSuperSessionFactory(SessionFactory sessionFactory) {
		this.setSessionFactory(sessionFactory);
	}
	
	@Override
	public List<List<MopsiEntity>> SearchForList(List<String> taxiList){
		List<List<MopsiEntity>> all = new ArrayList<List<MopsiEntity>>();
		for(int i=0;i<taxiList.size();i++){
			String taxiId = taxiList.get(i);
			@SuppressWarnings("unchecked")
			List<MopsiEntity> temp = this.getSession().createQuery("from MopsiEntity where Taxi_Id=? order by Time").setParameter(0, taxiId).list();
		    all.add(temp);
		}
		return all;
	}
	
	@SuppressWarnings("unchecked")
	@Override
	public List<MopsiEntity> Search(String taxiId, String time) {
		if (time==null)
		{
			//org.hibernate.Session session = this.getSession();
			//Query query =session.createQuery("from Taxi where Taxi_Id='"+taxiId+"' order by Time") ;
		//	return this.getSession().createQuery("from Taxi where Taxi_Id=? order by Time").setParameter(0, taxiId).list();
			return this.getSession().createQuery("from MopsiEntity where Taxi_Id=? order by Time").setParameter(0, taxiId).list();
		}
		return null;
	}
	@SuppressWarnings({ "unchecked", "rawtypes" })
	@Override
	public Set<String> getAllRouteId() {
		List<MopsiEntity> array = this.getSession().createQuery("from MopsiEntity").list();
		Set<String> result = new HashSet();
		for(int i=0;i<array.size();i++){
			result.add(array.get(i).getTaxi_Id());
		}
		return result;
	}
}
