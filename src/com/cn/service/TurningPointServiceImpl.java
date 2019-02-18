package com.cn.service;

import java.util.List;

import javax.annotation.Resource;

import org.springframework.stereotype.Service;

import com.cn.dao.TurningPointDao;
import com.cn.entity.TurningPoint;

@Service("turningPointService")
public class TurningPointServiceImpl implements TurningPointService {

	private TurningPointDao turningPointDao;
	
	public TurningPointDao getTurningPointDao() {
		return turningPointDao;
	}

	@Resource
	public void setTurningPointDao(TurningPointDao turningPointDao) {
		this.turningPointDao = turningPointDao;
	}

	@Override
	public List<TurningPoint> GetAllTurningPoint() {
		List<TurningPoint> list = turningPointDao.GetAllByTime();
		//Transform(list);
		return list;
	}

	@Override
	public void Transform(List<TurningPoint> list) {
		for(int i=0;i<list.size();i++)
		{
			double longtitude = Double.parseDouble(list.get(i).getLongtitude())+0.0045;
			list.get(i).setLongtitude(Double.toString(longtitude));
			double latitude = Double.parseDouble(list.get(i).getLatitude())-0.002;
			list.get(i).setLatitude(Double.toString(latitude));
		}
	}
	
	public void deleteDuplicate()
	{
		List<String> taxiIds = turningPointDao.GetAllIds();
		for(int j = 0;j<taxiIds.size();j++)
		{
			List<TurningPoint> turningPoints = turningPointDao.GetById(taxiIds.get(j));
			for(int n=1;n<turningPoints.size();n=n+2)
			{
				TurningPoint pre = turningPoints.get(n-1);
				TurningPoint aft = turningPoints.get(n);
				if(!pre.getState().equals(aft.getState())
						&&pre.getTaxiId().equals(aft.getTaxiId())
						&&!pre.getTime().equals(aft.getTime())){
					TurningPoint temp = new TurningPoint();
					temp.setTaxiId(taxiIds.get(j));
					temp.setState("0");
					temp.setSpeed("0");
					temp.setTime(pre.getTime());
					double longtitude = 0,latitude = 0;
					longtitude = (Double.parseDouble(pre.getLongtitude())+Double.parseDouble(aft.getLongtitude()))/2;
					latitude = (Double.parseDouble(pre.getLatitude())+Double.parseDouble(aft.getLatitude()))/2;
					temp.setLatitude(Double.toString(latitude));
					temp.setLongtitude(Double.toString(longtitude));
					if (pre.getState().equals("0")&&aft.getState().equals("1")) {
						temp.setType("start");
					}
					else if(pre.getState().equals("1")&&aft.getState().equals("0")) temp.setType("stop");
					
					
					turningPointDao.merge(temp, pre, aft);
				}
				
			}
		}
	}
	
	@Override
	public void SmallRegion(){
		List<TurningPoint> list= turningPointDao.GetAllByTaxiId();
		double latitudeLimitUp = 31.332270, latitudeLimitDown = 31.108052, longtitudeLimitDown = 121.359578, longtitudeLimitUp = 121.640700;
		for(int i = 0;i<list.size();i++){
			TurningPoint temp = list.get(i);
			double tempLatitude = Double.parseDouble(temp.getLatitude());
			double tempLongtitude = Double.parseDouble(temp.getLongtitude());
			if (tempLatitude < latitudeLimitDown || tempLatitude > latitudeLimitUp
					|| tempLongtitude < longtitudeLimitDown || tempLongtitude > longtitudeLimitUp || temp.getType()==null)
				turningPointDao.delete(temp);
		}
	}

	@Override
	public List<TurningPoint> GetAllStartPoint() {
		return turningPointDao.GetStartPoint();
	}

	@Override
	public List<TurningPoint> GetAllDropPoint() {
		return turningPointDao.GetDropPoint();
	}

	@Override
	public List<TurningPoint> GetAllStartPointAtTen() {
		return turningPointDao.GetStartPointAtTen();
	}

	@Override
	public List<TurningPoint> GetByCluster(int clusterId) {
		return turningPointDao.GetByCluster(clusterId);
	}

}
