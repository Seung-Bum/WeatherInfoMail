package com.items.controller;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.util.Date;
import java.util.HashMap;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.util.EntityUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.json.JSONObject;
import org.json.XML;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.items.Util.EmailValidator;
import com.items.domain.Airport;
import com.items.service.RestAPIService;

@Controller
public class RestAPIController {
	
	static final Logger log = LogManager.getLogger(RestAPIController.class);

	@Value("${restapi.Service.call}")
	private String SERVICE_CALL;
	
	@Value("${restapi.token}")
	private String TOKEN;

	@Autowired
	RestAPIService restAPIService;
	
	Airport airport;
	
	/**
	 * aviationWeatherAPI
	 * 항공기상정보 DB적재(Aviation weather information restAPI, XMLtoJson)
	 * @param model
	 * @return String @ author yang @ version 1.0
	 */
	@Scheduled(cron = "0 30 5 * * ?") // 초 분 시 일 월 요일
	@ResponseBody
	@GetMapping("/aviationWeatherAPI")
	public void aviationWeatherAPI() throws Exception {

		// * ICAO 공항코드를 사용해 국내 공항의 유효한 METAR/SPECI 전문을 조회
		// String url = "http://amoapi.kma.go.kr/amoApi/metar?icao=RKSI";
		
		// * ICAO 공항코드를 사용해 국내 공항의 유효한 TAF 전문을 조회 (RKSI 인천공항)
		//   (6~30 시간의 유효 시간을 갖고 있으며 1일 4회 보고한다.)
		String url_TAF = "http://amoapi.kma.go.kr/amoApi/taf?icao=RKSI";
		
		String resXml ="";
		try {
			// API RESULT
			resXml = httpGetApiCall(url_TAF);
			log.info("API CALL RESULT : " + resXml);
		}catch(Exception e) {
			log.error("API CALL ERROR : " + e.getMessage());
		}

		// xml 형태를 json으로 변경
		JSONObject jsonObject = XML.toJSONObject(resXml);
		JSONObject responseJson = (JSONObject) jsonObject.get("response");
		JSONObject body = (JSONObject) responseJson.get("body");
		JSONObject items = (JSONObject) body.get("items");
		JSONObject item = (JSONObject) items.get("item");
		
		// TAF 전문
		String tafMsg = (String) item.get("tafMsg");
		
		// TAF Line
		String[] tafMsg_str = tafMsg.split("\n");
		
		// 변수 초기화
		String[] line0;
		String[] line1;
		String[] line2;
		String[] line3;
		String[] line4;

		// TAF Line Loop
		log.info("TAF Line Loop Start");
		HashMap<String, Object> rsltParam = new HashMap<>();	
		
		// Line0
		log.info("Line0 Start");
		line0 = tafMsg_str[0].trim().split(" ");			
		String result0 = aviationWeatherInfoStr(line0);
		
		// 최초예보
		String line0Taf2 = line0[2].toString();
		if( (line0Taf2.indexOf("Z") == -1) ) { 
			result0 += "createTime : " + "<br>";
		}			
		else {
			String createDay = line0Taf2.substring(0,2); // day
			String createTime = line0Taf2.substring(2,4); // time
			String createMinute = line0Taf2.substring(4,6); // minute
			//result0.put("createTime", createDay + "일 " + createTime + ":" + createMinute);
			result0 += createDay + "일 " + createTime + ":" + createMinute + "<br>";
		}
		
		// 예보 유효시간 - 30시간 유효
		String line0Taf3 = line0[3].toString();
		if(line0Taf3.indexOf("/") != -1) {
			String[] available_day = line0Taf3.toString().split("/");
			result0 += "availableDay : " +  available_day[0].substring(0, 2) + "일 " + available_day[0].substring(2, 4) + "시 ~ " + 
							available_day[1].substring(0, 2) + "일 " + available_day[1].substring(2, 4) + "시" + "<br>";
			}
		else { 
			result0 += "availableDay : " + "<br>";
		}
		
		log.info("line0 : result0 - " + result0.toString());
		rsltParam.put("resMap0", result0);
		
		// line1
		log.info("Line1 Start");
		line1 = tafMsg_str[1].trim().split(" ");
		String result1 = aviationWeatherInfoStr(line1);
		log.info("line1 : result1 - " + result1.toString());
		rsltParam.put("resMap1", result1);
		
		// line2
		log.info("Line2 Start");
		line2 = tafMsg_str[2].trim().split(" ");
		String result2 = aviationWeatherInfoStr(line2);
		log.info("line2 : result2 - " + result2.toString());
		rsltParam.put("resMap2", result2);
		
		// line3
		log.info("Line3 Start");	
		line3 = tafMsg_str[3].trim().split(" ");
		String result3 = aviationWeatherInfoStr(line3);
		log.info("line3 : result3 - " + result3.toString());
		rsltParam.put("resMap3", result3);
		
		// line4
		// line3까지는 정보가 무조건 들어있지만 line4는 없을 경우도 있음
		try {
			log.info("Line4 Start");
			line4 = tafMsg_str[5].trim().split(" ");
			String result4 = aviationWeatherInfoStr(line4);
			log.info("line4 : result4 - " + result4.toString());
			rsltParam.put("resMap4", result4);
		} catch(Exception e) {
			String[] none = {};
			String result4 = aviationWeatherInfoStr(none);
			log.info("line4 : result4 - " + result4.toString());
			rsltParam.put("resMap4", result4);
		}

    	restAPIService.insertWeatherInfo(rsltParam);
		log.info("TAF Line Loop End");
		//return "aviationWeather";
	}
		
	/**
	 * httpGetApiCall
	 * String 형태의 url을 가지고 ApiGet 호출을 실행, 리턴 타입은 XML형태의 String
	 * @param String url
	 * @return String Xml @ author yang @ version 1.0
	 */
	public String httpGetApiCall(String url) throws Exception {

		CloseableHttpClient httpClient = HttpClientBuilder.create().build();
		HttpGet httpGet = new HttpGet(url);
		httpGet.setHeader("content-type", "application/json; charset=utf-8");

		CloseableHttpResponse response = httpClient.execute(httpGet);
		String resXml = EntityUtils.toString(response.getEntity(), "UTF-8");

		return resXml;
	}
	
	/**
	 * isNumeric
	 * 숫자만 있는지 확인
	 * @param String url
	 * @return String Xml @ author yang @ version 1.0
	 */
    public static boolean isNumeric(String s)
    {
        try {
        	Integer.parseInt(s);
        } catch (NumberFormatException ex) {
        	return false;
        }
    	return true;
    }
	
    /**
	 * insertDepartureData
	 * 사용자가 입력한 출국정보를 DB에 insert
	 * @param String email, String departureDate
	 * @return 
     * @throws ParseException 
	 */
	@RequestMapping("/departureData")
	public String insertDepartureData(String email, String departureDate, 
								HttpServletRequest request, HttpServletResponse response) throws ParseException {
		
		// email 형태 확인하기 (html input에서 걸러주긴함)
		EmailValidator emailValidator = new EmailValidator(); 
		if (!emailValidator.isValidEmail(email)) {
			log.info("- email 형식이 아닙니다. ");
			return "redirect:/aviationWeather.html";
		}
		
		// date format ex) 20230920
		log.info("- USER 입력정보 : email - " + email + " / departureDate - " + departureDate.replace("-",""));	
		HashMap<String, Object> param = new HashMap<String, Object>();
		param.put("departureDate", departureDate.replace("-",""));
		param.put("email", email);
		
        // 현재 날짜 구하기
        LocalDate today = LocalDate.now();		
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");         
        Date dDay = formatter.parse(departureDate);
        Date tDate = formatter.parse(today.toString());
		
		// 가장 최근의 seq의 다음 seq로 순서대로 적재
		HashMap<String, Object> maxSeq = restAPIService.departureDataSeq();
		param.put("seq", Integer.valueOf(maxSeq.get("SEQ").toString()) + 1);
		
		if (!param.get("email").equals("") && !param.get("departureDate").equals("")) {	
	        
			// before check
	        if (dDay.before(tDate)) {
	        	log.info("- 출국정보는 오늘 이상으로 설정해주세요.");
	        	return "redirect:/aviationWeather.html";
	        }			
			
			// 출국정보 insert
			restAPIService.insertDepartureData(param);
			log.info("- 출국정보 Upload 완료");
			return "redirect:/aviationWeather.html";
		} else {
			log.info("- 출국정보 미입력");
			// 출국정보 입력 안내 메시지 보내기
			return "redirect:/aviationWeather.html";
		}
		
	}
	
    /**
	 * aviationWeatherInfoStr
	 * ForeCastDay, 방위각, 풍속, 가시거리, 비 약간, 박무, 시정양호, NSC
	 * mail 정보를 만들어서 String으로 리턴
	 * @param String[] strArray
	 * @return String
	 */
    public String aviationWeatherInfoStr(String[] strArray) {
    	
    	HashMap<String, Object> param = new HashMap<>();
    	String resultStr = "";
    	
    	int loopCnt = strArray.length;
    	if (loopCnt != 0) {
	    	for (int i=0; i<loopCnt; i++) {
	
		    	// Array의 str 한 단어씩 parse
				String taf = strArray[i].toString();
				//log.info("taf: " + taf );
				
				// 최저기온
				if(taf.indexOf("TN") != -1) { 
					resultStr = "minimumTemper : " + taf.substring(2, 4) + "°C" + "<br>";
					log.info("minimumTemper : " + taf.substring(2, 4) + "°C");
				}
				
				// 최고기온
				if(taf.indexOf("TX") != -1) { 
					resultStr = "highestTemper : " + taf.substring(2, 4) + "°C" + "<br>";
					log.info("highestTemper : " + taf.substring(2, 4) + "°C");  
				} 
				
				// airport
				if(taf.indexOf("RKSI") != -1) { 
					resultStr = "airport : " + "인천공항" + "<br>";
					log.info("airport : " + "인천공항");
				}
				
				// ForeCastDay
				if((taf.indexOf("/") != -1) && (taf.indexOf("Z") == -1)) { 
					String forecastDay = taf.substring(0, 2);
					String forecastTime = taf.substring(2, 4);
					String forecastUntilDay = taf.substring(5, 7);
					String forecastUntilTime = taf.substring(7, 9);
					String foreCastDay = forecastDay + "일 " + forecastTime + "시 ~ " + forecastUntilDay + "일 " + forecastUntilTime + "시";
					resultStr = foreCastDay + "<br>";
					log.info("forecastDay : " + foreCastDay); 
				}
				
				// 방위각, 풍속
				if(taf.indexOf("KT") != -1) {
					// 방위각
					resultStr = "azimuth : " + taf.substring(0, 3) + "<br>";
					log.info("azimuth : " + taf.substring(0, 3));
					
					// Knots
					resultStr = "Knots : " + taf.substring(3, 5) + "<br>";
					log.info("Knots : " + taf.substring(3, 5));
				}
				
				// 가시거리 - 숫자만 있을 경우 가시거리로 판단
				if (isNumeric(taf)) { 
					resultStr = "sight : " + taf + "<br>";
					log.info("sight : " + taf);
				}
	
				// 비 약간
				if(taf.indexOf("-RA") != -1) { 
					resultStr = "littleRA : " + "비 약간" + "<br>";
					log.info("littleRA : " + "비 약간"); 
				}
				
				// 비 보통
				if(taf.indexOf("RA") != -1) { 
					resultStr = "RA : " + "비 보통" + "<br>";
					log.info("RA : " + "비 보통");
				}
	
				// 박무 : 안개보다 시정이 좋은 상태
				if(taf.indexOf("BR") != -1) { 
					resultStr = "lightRai : " + "박무" + "<br>";
					log.info("lightRai : " + "박무");
				}
	
				// 시정양호 : 강수나 뇌우도 없고 기타 특별한 일기상황이 없을 때
				if(taf.indexOf("CAVOK") != -1) { 
					resultStr = "CAVOK : " + "시정양호" + "<br>";
					log.info("CAVOK : " + "시정양호");
				}
	
				// 운영상 중요한 구름 없고, 수직시정에 제한 없음
				if(taf.indexOf("NSC") != -1) {
					resultStr = "NSC : " + "운영상 중요한 구름 없고, 수직시정에 제한 없음" + "<br>";
					log.info("NSC : " + "운영상 중요한 구름 없고, 수직시정에 제한 없음");
				}
				
				// 고도, 구름
				if(taf.indexOf("FEW") != -1) { 
					resultStr = "FEW : " + taf.substring(4, 6) + "00ft" + " 구름 조금" + "<br>";
					log.info("FEW : " + taf.substring(4, 6) + "00ft" + " 구름 조금");
				}
				
				if(taf.indexOf("SCT") != -1) { 
					resultStr = "SCT : " + taf.substring(4, 6) + "00ft" + " 구름 보통" + "<br>";
					log.info("SCT : " + taf.substring(4, 6) + "00ft" + " 구름 보통");
				}			
	    	}
    	}
    	
    	return resultStr;
    }

}
