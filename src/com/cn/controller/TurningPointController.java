package com.cn.controller;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.Resource;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import com.cn.entity.HierarchicalCluster;
import com.cn.entity.TurningPoint;
import com.cn.service.HierarchicalClusterService;
import com.cn.service.TurningPointService;

@Controller
@RequestMapping("/turning")
public class TurningPointController {

	private TurningPointService turningPointService;
	
	private HierarchicalClusterService hierarchicalClusterService;
	
	private List<TurningPoint> turningPoints = new ArrayList<>();
	
	public HierarchicalClusterService getHierarchicalClusterService() {
		return hierarchicalClusterService;
	}

	@Resource
	public void setHierarchicalClusterService(HierarchicalClusterService hierarchicalClusterService) {
		this.hierarchicalClusterService = hierarchicalClusterService;
	}
	
	public List<TurningPoint> getTurningPoints() {
		return turningPoints;
	}

	public void setTurningPoints(List<TurningPoint> turningPoints) {
		this.turningPoints = turningPoints;
	}

	public TurningPointService getTurningPointService() {
		return turningPointService;
	}

	@Resource
	public void setTurningPointService(TurningPointService turningPointService) {
		this.turningPointService = turningPointService;
	}


	@RequestMapping(value="/all", method = RequestMethod.POST)
	public @ResponseBody List<TurningPoint> GetAllTurningPoints()
	{
		turningPoints = turningPointService.GetAllStartPointAtTen();
		return turningPoints;
	}
	
	@RequestMapping(value="/cluster", method = RequestMethod.POST)
	public @ResponseBody List<HierarchicalCluster> test()
	{
		System.out.println("enter hierarchical");
		return hierarchicalClusterService.DoKMeans(turningPointService.GetAllStartPointAtTen(), 100, 1000);
		//return hierarchicalClusterService.DBScan(turningPointService.GetAllStartPointAtTen(), 0.05, 3);
		//return hierarchicalClusterService.DoHierarchicalCluster(turningPointService.GetAllStartPointAtTen(), 100);
	}
	
	@RequestMapping(value="/delete", method = RequestMethod.POST)
	public @ResponseBody String Reduce()
	{
		turningPointService.deleteDuplicate();
		return "hello";
	}
	
	@RequestMapping(value="/smaller", method = RequestMethod.POST)
	public @ResponseBody String Small(){
		System.out.println("enter small");
		turningPointService.SmallRegion();
		return "hello";
	}
	
	@RequestMapping(value="/test/save", method = RequestMethod.POST)
	public @ResponseBody int TestSave(){
		HierarchicalCluster cluster = new HierarchicalCluster();
		cluster.setCenterLatitude(0.0);
		cluster.setCenterLongtitude(0.0);
		cluster.setDivide("test");
		cluster.setNumberOfCluster(300);
		cluster.setSize(900);
		cluster.setMethod("kmeans");
		
		return hierarchicalClusterService.Save(cluster);
	}
	
	@RequestMapping(value="/test/update", method = RequestMethod.POST)
	public @ResponseBody String TestUpdate(){
		hierarchicalClusterService.Update(hierarchicalClusterService.GetStartPointAtTen());
		return "hello";
	}
	
	@RequestMapping(value="/test/get", method = RequestMethod.POST)
	public @ResponseBody List<TurningPoint> TestGet(){
		System.err.println("enter get turningpoint");
		return turningPointService.GetAllTurningPoint();
	}
}
