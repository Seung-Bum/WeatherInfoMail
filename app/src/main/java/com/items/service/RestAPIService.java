package com.items.service;

import java.util.HashMap;

public interface RestAPIService {
	public void insertAirInfo(HashMap<String, Object> param);

	public void insertDepartureData(HashMap<String, Object> param);
	
	public HashMap<String, Object> departureDataSeq();

	public void insertWeatherInfo(HashMap<String, Object> param);
	
	public HashMap<String, Object> selectWeatherInfo();
	
	public HashMap<String, Object> selectUserInfo();
}
