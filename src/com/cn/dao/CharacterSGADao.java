package com.cn.dao;

import java.util.List;

import com.cn.entity.char_SGAEntity;

public interface CharacterSGADao {
    void save(char_SGAEntity ce);
	List<char_SGAEntity> Search();
	void Delete();
	void Store(List<char_SGAEntity> result);
	void saveAll(List<char_SGAEntity> ce);
}
