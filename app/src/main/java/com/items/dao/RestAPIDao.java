package com.items.dao;

import java.util.HashMap;

import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface RestAPIDao {
	void insertAirInfo(HashMap<String, Object> param);
	
	void insertDepartureData(HashMap<String, Object> param);
	
	void insertWeatherInfo(HashMap<String, Object> param);
	
	HashMap<String, Object> departureDataSeq();

	HashMap<String, Object> selectWeatherInfo();
	
	HashMap<String, Object> selectUserInfo();
}