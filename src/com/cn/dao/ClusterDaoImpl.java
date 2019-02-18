package com.cn.dao;

import java.util.List;

import javax.annotation.Resource;

import org.hibernate.SessionFactory;
import org.springframework.orm.hibernate3.support.HibernateDaoSupport;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import com.cn.entity.CPointCluster;
import com.cn.entity.ClusterResultEntity;
import com.cn.entity.Taxi;
import com.cn.entity.TurningPoint;
@Repository("cluster_result")
public class ClusterDaoImpl extends HibernateDaoSupport implements ClusterDao{

	@Resource
	public void setSuperSessionFactory(SessionFactory sessionFactory) {
		this.setSessionFactory(sessionFactory);
	}
	@Transactional
	@Override
	public void Store(List<CPointCluster> result,String cluster_type,String cp_type) {
		// TODO Auto-generated method stub
		//将聚类得到的结果批量地存入cluster_result 表中
		for(int i=0;i<result.size();i++){
			CPointCluster temp = result.get(i);//temp为一个CPointCluster
			String clusterId = String.valueOf(temp.getId());
			String centerLatitude = String.valueOf(temp.getCenterLatitude());
			String centerLongtitude = String.valueOf(temp.getCenterLongtitude());
			ClusterResultEntity clusterEntity = new ClusterResultEntity();
			clusterEntity.setClusterId(clusterId);
			clusterEntity.setCenter_Latitude(centerLatitude);
			clusterEntity.setCenter_Longtitude(centerLongtitude);
			clusterEntity.setCluster_type(cluster_type);
			clusterEntity.setCp_type(cp_type);
			clusterEntity.setId(i);
			//设置好entity属性之后即可进行save操作
			save(clusterEntity);
		}
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<ClusterResultEntity> Search() {
			//org.hibernate.Session session = this.getSession();
			//Query query =session.createQuery("from Taxi where Taxi_Id='"+taxiId+"' order by Time") ;
		//	return this.getSession().createQuery("from Taxi where Taxi_Id=? order by Time").setParameter(0, taxiId).list();
			return this.getSession().createQuery("from ClusterResultEntity").list();
		 
	}
	@Transactional
	@Override
	public void Delete() {
	List<ClusterResultEntity> list   =   this.Search();//从cluster_result里面获取所有记录
        if(list.size()>0){  
           this.getHibernateTemplate().deleteAll(list);
         }
	}
	@Transactional
	@Override
	public void save(ClusterResultEntity clusterEntity) {
		this.getHibernateTemplate().save(clusterEntity);
	}
	@Transactional
	@Override
	public void merge(ClusterResultEntity clusterEntity) {
		this.Delete();
		this.save(clusterEntity);
		
	}
 
	@SuppressWarnings("unchecked")
	@Override
	public List<ClusterResultEntity> SearchForMSE() {
		return this.getSession().createQuery("from ClusterResultEntity").list();
		 
	}

}
