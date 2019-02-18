package com.cn.controller;

import java.util.List;
import javax.annotation.Resource;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import com.cn.entity.HierarchicalCluster;
import com.cn.entity.SearchEntity;
import com.cn.entity.TurningPoint;
import com.cn.service.HierarchicalClusterService;
import com.cn.service.TurningPointService;

@Controller
@RequestMapping("/cluster")
public class ClusterController {
	private HierarchicalClusterService hierarchicalClusterService;
	
	private TurningPointService turningPointService;

	public TurningPointService getTurningPointService() {
		return turningPointService;
	}

	@Resource
	public void setTurningPointService(TurningPointService turningPointService) {
		this.turningPointService = turningPointService;
	}

	public HierarchicalClusterService getHierarchicalClusterService() {
		return hierarchicalClusterService;
	}

	@Resource
	public void setHierarchicalClusterService(HierarchicalClusterService hierarchicalClusterService) {
		this.hierarchicalClusterService = hierarchicalClusterService;
	}
	
	@RequestMapping(value="/pointofinterest", method=RequestMethod.POST)
	public @ResponseBody List<HierarchicalCluster> GetPointOfInterest(){
		return hierarchicalClusterService.GetStartPointAtTen();
	}
	
	
	
	@RequestMapping(value="/nextlayer", method=RequestMethod.POST)
	public @ResponseBody List<HierarchicalCluster> GetNextLayer(@RequestBody SearchEntity searchEntity){
		System.out.println("enter nextlayer");
		System.out.println("cluster_id:"+searchEntity.getClusterId());
		List<TurningPoint> turningPoints = turningPointService.GetByCluster(searchEntity.getClusterId());
		List<HierarchicalCluster> result = hierarchicalClusterService.DoKMeans(turningPoints, 10, 100);
		for(int i=0;i<result.size();i++){
			result.get(i).setSize(result.get(i).getTurningPoints().size());
		}
		return result;
	}

	@RequestMapping(value="/test/get", method=RequestMethod.POST)
	public @ResponseBody List<HierarchicalCluster> GetTestCluster(){
		return null;
	}
	
	@RequestMapping(value="/show/agnes", method=RequestMethod.POST)
	public @ResponseBody List<HierarchicalCluster> ShowAGNES(){
		return hierarchicalClusterService.GetStartPointAtTenByAGNES();
	}
	
	@RequestMapping(value="/show/kmeans", method=RequestMethod.POST)
	public @ResponseBody List<HierarchicalCluster> ShowKMeans(){
		return hierarchicalClusterService.GetStartPointAtTenByKMeans();
	}
	
	@RequestMapping(value="/show/dbscan", method=RequestMethod.POST)
	public @ResponseBody List<HierarchicalCluster> ShowDBSCAN(){
		return hierarchicalClusterService.GetStartPointAtTenByDBSCAN();
	}
}
