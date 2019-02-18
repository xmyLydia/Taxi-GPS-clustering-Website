package com.cn.dao;

import java.util.List;

import javax.annotation.Resource;

import org.hibernate.Query;
import org.hibernate.SessionFactory;
import org.springframework.orm.hibernate3.support.HibernateDaoSupport;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import com.cn.entity.HierarchicalCluster;

@Repository("hierarchicalClusterDao")
public class HierarchicalClusterDaoImpl extends HibernateDaoSupport implements HierarchicalClusterDao{

	@Resource
	public void setSuperSessionFactory(SessionFactory sessionFactory) {
		this.setSessionFactory(sessionFactory);
	}
	
	@Transactional
	@Override
	public int save(HierarchicalCluster cluster) {
		this.getHibernateTemplate().save(cluster);
		return cluster.getId();
	}
	
	@Transactional
	@Override
	public void update(HierarchicalCluster cluster) {
		this.getHibernateTemplate().update(cluster);
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<HierarchicalCluster> GetStartPointAtTen() {
		String start="alllevel1";
		int num =100;
		String method = "HierarchicalCluster";
		return this.getSession().createQuery("from HierarchicalCluster where divide=? and numberOfCluster=? and method=?")
				.setParameter(0, start).setParameter(1, num).setParameter(2, method).list();
	}

	@SuppressWarnings("unchecked")
	@Override
	public HierarchicalCluster GetClusterById(int id) {
		List<HierarchicalCluster> list = this.getSession().createQuery("from HierarchicalCluster where id=?").setParameter(0, id)
				.list();
		if(list.size()!=0)return list.get(0);
		return null;
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<HierarchicalCluster> GetClusterByParentId(int parentId) {
		Query query=this.getSession().createQuery("from HierarchicalCluster as h"
				+ " where h.id in"
				+" (select cp.cp_hierarchicalCluster.id from Cluster_Point as cp"
				+ " where cp.parent=?)");
		query.setParameter(0, String.valueOf(parentId));
		List<HierarchicalCluster> list = query.list();
		return list;
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<HierarchicalCluster> GetStartPointAtTenByKMeans() {
		String method = "kmeans";
		String divide = "start10iteration1000";
		return this.getSession().createQuery("from HierarchicalCluster where method=? and divide=?")
				.setParameter(0, method).setParameter(1, divide).list();
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<HierarchicalCluster> GetStartPointAtTenByAGNES() {
		String start="alllevel1";
		int num =100;
		String method = "HierarchicalCluster";
		return this.getSession().createQuery("from HierarchicalCluster where divide=? and numberOfCluster=? and method=?")
				.setParameter(0, start).setParameter(1, num).setParameter(2, method).list();
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<HierarchicalCluster> GetStartPointAtTenByDBSCAN() {
		String method ="dbscan";
		String divide ="alllevel1start10e0.2minP3";
		return this.getSession().createQuery("from HierarchicalCluster where method=? and divide=?")
				.setParameter(0, method).setParameter(1, divide).list();
	}

}
