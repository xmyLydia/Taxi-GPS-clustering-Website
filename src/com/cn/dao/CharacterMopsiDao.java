package com.cn.dao;

import java.util.List;
import java.util.Set;

import com.cn.entity.char_TraclusEntity;
import com.cn.entity.char_mopsiEntity;

public interface CharacterMopsiDao {
	 
	public List<char_mopsiEntity> Search();
	public void Delete();
	public void Store(List<char_mopsiEntity> result);
	public Set<String> getAllTaxiId();
	public List<char_mopsiEntity> SearchById(String taxiId);
	public void saveAll(List<char_mopsiEntity> ce);
	public void save(char_mopsiEntity ce);
}
