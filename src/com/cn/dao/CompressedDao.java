package com.cn.dao;

import java.util.List;

import com.cn.entity.Taxi;

public interface CompressedDao {
	public List<Taxi> SearchCompressed(String taxiId,String time);
}
