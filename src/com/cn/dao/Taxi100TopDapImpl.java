package com.cn.dao;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

import javax.annotation.Resource;

import org.hibernate.Query;
import org.hibernate.SessionFactory;
import org.hibernate.classic.Session;
import org.springframework.orm.hibernate3.support.HibernateDaoSupport;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import com.cn.entity.Taxi;

@Repository("taxi100top")
public class Taxi100TopDapImpl extends HibernateDaoSupport implements Taxi100TopDao{

	@Resource
	public void setSuperSessionFactory(SessionFactory sessionFactory) {
		this.setSessionFactory(sessionFactory);
	}
	
	@SuppressWarnings("unchecked")
	@Override
	public List<Taxi> Search(String taxiId, String time) {
		if (time==null)
		{
			//org.hibernate.Session session = this.getSession();
			//Query query =session.createQuery("from Taxi where Taxi_Id='"+taxiId+"' order by Time") ;
		//	return this.getSession().createQuery("from Taxi where Taxi_Id=? order by Time").setParameter(0, taxiId).list();
			return this.getSession().createQuery("from TaxiTop100Entity where Taxi_Id=? order by Time").setParameter(0, taxiId).list();
		}
		return null;
	}
	
	@SuppressWarnings("unchecked")
	@Override
	public List<Taxi> SearchFrequency(String taxiId, String time) {
		if (time==null)
		{
			//org.hibernate.Session session = this.getSession();
			//Query query =session.createQuery("from Taxi where Taxi_Id='"+taxiId+"' order by Time") ;
			//return this.getSession().createQuery("count(*) from Taxi where Taxi_Id=?").setParameter(0, taxiId).list();
			return this.getSession().createQuery("count(*) from TaxiTop100Entity where Taxi_Id=?").setParameter(0, taxiId).list();
		}
		return null;
	}
	@SuppressWarnings("unchecked")
	@Override
	public List<Taxi> SearchAll(String taxiId, String time) {
		if (time==null)
		{
			//org.hibernate.Session session = this.getSession();
			//Query query =session.createQuery("from Taxi where Taxi_Id='"+taxiId+"' order by Time") ;
			 return this.getSession().createQuery("from TaxiTop100Entity where Taxi_Id=? order by Time").setParameter(0, taxiId).list();
			// return this.getSession().createQuery("from Taxi where Taxi_Id=? order by Time").setParameter(0, taxiId).list();
		}
		return null;
	}
	@SuppressWarnings("unchecked")
	@Override
	public List<Taxi> getAll() {
		List<Taxi> resultAll =this.getSession().createQuery("from TaxiTop100Entity order by Time").list();
		System.out.println(resultAll);
		return resultAll;
	}

	@SuppressWarnings({ "unchecked", "rawtypes" })
	@Override
	public Set<String> getAllTaxiId() {
		List<Taxi> array = this.getSession().createQuery("from TaxiTop100Entity").list();
		Set<String> result = new HashSet();
		for(int i=0;i<array.size();i++){
			result.add(array.get(i).getTaxi_Id());
		}
		return result;
	}
	
	@SuppressWarnings({ "unchecked", "rawtypes" })
	@Override
	public Set<String> GetTaxiIdByFuzzy(String fuzzyId){
		String fuzzy = "%"+fuzzyId;
		List<Taxi> array = this.getSession().createQuery("from TaxiTop100Entity where Taxi_Id like ?").setParameter(0, fuzzy).list();
		Set<String> result = new HashSet();
		for(int i=0;i<array.size();i++){
			result.add(array.get(i).getTaxi_Id());
		}
		return result;
	}

	@Transactional
	@Override
	public void Delete(Taxi taxi) {
		this.getHibernateTemplate().delete(taxi);
	}
 


}

