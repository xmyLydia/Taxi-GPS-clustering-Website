package com.cn.dao;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import com.cn.entity.Taxi;

public interface Taxi100TopDao {

	public List<Taxi> Search(String taxiId, String time);
	public List<Taxi> getAll();
	public Set<String> getAllTaxiId();
	public void Delete(Taxi taxi);
	public Set<String> GetTaxiIdByFuzzy(String fuzzyId);
	public List<Taxi> SearchAll(String taxiId, String time);
	List<Taxi> SearchFrequency(String taxiId, String time);
}
