package com.cn.dao;

import javax.annotation.Resource;

import org.hibernate.SessionFactory;
import org.springframework.orm.hibernate3.support.HibernateDaoSupport;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import com.cn.entity.Cluster_Point;

@Repository("clusterPointDao")
public class ClusterPointDaoImpl extends HibernateDaoSupport implements ClusterPointDao{

	@Resource
	public void setSuperSessionFactory(SessionFactory sessionFactory) {
		this.setSessionFactory(sessionFactory);
	}
	
	@Override
	@Transactional
	public void save(Cluster_Point cp) {
		this.getHibernateTemplate().save(cp);
		
	}

}
