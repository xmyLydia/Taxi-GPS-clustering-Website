package com.cn.dao;

import java.util.List;
import java.util.Set;

import com.cn.entity.Taxi;
import com.cn.entity.char_TraclusEntity;

public interface CharacterTraclusDao {
    void save(char_TraclusEntity ce);
	List<char_TraclusEntity> Search();
	void Delete();
	void Store(List<char_TraclusEntity> result);
	Set<String> getAllTaxiId();
	List<char_TraclusEntity> SearchById(String taxiId);
}
