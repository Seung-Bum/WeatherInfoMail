package com.items.service;

import java.util.HashMap;
import java.util.List;

public interface RestAPIService {
	public void insertAirInfo(HashMap<String, Object> param);

	public void insertDepartureData(HashMap<String, Object> param);
	
	public HashMap<String, Object> departureDataSeq();

	public void insertWeatherInfo(HashMap<String, Object> param);
	
	public HashMap<String, Object> selectWeatherInfo();
	
	public List<HashMap<String, Object>> targetUserInfo();
}
