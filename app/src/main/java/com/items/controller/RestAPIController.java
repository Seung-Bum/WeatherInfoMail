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
	 * �װ�������� DB����(Aviation weather information restAPI, XMLtoJson)
	 * @param model
	 * @return String @ author yang @ version 1.0
	 */
	@Scheduled(cron = "0 30 5 * * ?") // �� �� �� �� �� ����
	@ResponseBody
	@GetMapping("/aviationWeatherAPI")
	public void aviationWeatherAPI() throws Exception {

		// * ICAO �����ڵ带 ����� ���� ������ ��ȿ�� METAR/SPECI ������ ��ȸ
		// String url = "http://amoapi.kma.go.kr/amoApi/metar?icao=RKSI";
		
		// * ICAO �����ڵ带 ����� ���� ������ ��ȿ�� TAF ������ ��ȸ (RKSI ��õ����)
		//   (6~30 �ð��� ��ȿ �ð��� ���� ������ 1�� 4ȸ �����Ѵ�.)
		String url_TAF = "http://amoapi.kma.go.kr/amoApi/taf?icao=RKSI";
		
		String resXml ="";
		try {
			// API RESULT
			resXml = httpGetApiCall(url_TAF);
			log.info("API CALL RESULT : " + resXml);
		}catch(Exception e) {
			log.error("API CALL ERROR : " + e.getMessage());
		}

		// xml ���¸� json���� ����
		JSONObject jsonObject = XML.toJSONObject(resXml);
		JSONObject responseJson = (JSONObject) jsonObject.get("response");
		JSONObject body = (JSONObject) responseJson.get("body");
		JSONObject items = (JSONObject) body.get("items");
		JSONObject item = (JSONObject) items.get("item");
		
		// TAF ����
		String tafMsg = (String) item.get("tafMsg");
		
		// TAF Line
		String[] tafMsg_str = tafMsg.split("\n");
		
		// ���� �ʱ�ȭ
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
		
		// ���ʿ���
		String line0Taf2 = line0[2].toString();
		if( (line0Taf2.indexOf("Z") == -1) ) { 
			result0 += "createTime : " + "<br>";
		}			
		else {
			String createDay = line0Taf2.substring(0,2); // day
			String createTime = line0Taf2.substring(2,4); // time
			String createMinute = line0Taf2.substring(4,6); // minute
			//result0.put("createTime", createDay + "�� " + createTime + ":" + createMinute);
			result0 += createDay + "�� " + createTime + ":" + createMinute + "<br>";
		}
		
		// ���� ��ȿ�ð� - 30�ð� ��ȿ
		String line0Taf3 = line0[3].toString();
		if(line0Taf3.indexOf("/") != -1) {
			String[] available_day = line0Taf3.toString().split("/");
			result0 += "availableDay : " +  available_day[0].substring(0, 2) + "�� " + available_day[0].substring(2, 4) + "�� ~ " + 
							available_day[1].substring(0, 2) + "�� " + available_day[1].substring(2, 4) + "��" + "<br>";
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
		// line3������ ������ ������ ��������� line4�� ���� ��쵵 ����
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
	 * String ������ url�� ������ ApiGet ȣ���� ����, ���� Ÿ���� XML������ String
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
	 * ���ڸ� �ִ��� Ȯ��
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
	 * ����ڰ� �Է��� �ⱹ������ DB�� insert
	 * @param String email, String departureDate
	 * @return 
     * @throws ParseException 
	 */
	@RequestMapping("/departureData")
	public String insertDepartureData(String email, String departureDate, 
								HttpServletRequest request, HttpServletResponse response) throws ParseException {
		
		// email ���� Ȯ���ϱ� (html input���� �ɷ��ֱ���)
		EmailValidator emailValidator = new EmailValidator(); 
		if (!emailValidator.isValidEmail(email)) {
			log.info("- email ������ �ƴմϴ�. ");
			return "redirect:/aviationWeather.html";
		}
		
		// date format ex) 20230920
		log.info("- USER �Է����� : email - " + email + " / departureDate - " + departureDate.replace("-",""));	
		HashMap<String, Object> param = new HashMap<String, Object>();
		param.put("departureDate", departureDate.replace("-",""));
		param.put("email", email);
		
        // ���� ��¥ ���ϱ�
        LocalDate today = LocalDate.now();		
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");         
        Date dDay = formatter.parse(departureDate);
        Date tDate = formatter.parse(today.toString());
		
		// ���� �ֱ��� seq�� ���� seq�� ������� ����
		HashMap<String, Object> maxSeq = restAPIService.departureDataSeq();
		param.put("seq", Integer.valueOf(maxSeq.get("SEQ").toString()) + 1);
		
		if (!param.get("email").equals("") && !param.get("departureDate").equals("")) {	
	        
			// before check
	        if (dDay.before(tDate)) {
	        	log.info("- �ⱹ������ ���� �̻����� �������ּ���.");
	        	return "redirect:/aviationWeather.html";
	        }			
			
			// �ⱹ���� insert
			restAPIService.insertDepartureData(param);
			log.info("- �ⱹ���� Upload �Ϸ�");
			return "redirect:/aviationWeather.html";
		} else {
			log.info("- �ⱹ���� ���Է�");
			// �ⱹ���� �Է� �ȳ� �޽��� ������
			return "redirect:/aviationWeather.html";
		}
		
	}
	
    /**
	 * aviationWeatherInfoStr
	 * ForeCastDay, ������, ǳ��, ���ðŸ�, �� �ణ, �ڹ�, ������ȣ, NSC
	 * mail ������ ���� String���� ����
	 * @param String[] strArray
	 * @return String
	 */
    public String aviationWeatherInfoStr(String[] strArray) {
    	
    	HashMap<String, Object> param = new HashMap<>();
    	String resultStr = "";
    	
    	int loopCnt = strArray.length;
    	if (loopCnt != 0) {
	    	for (int i=0; i<loopCnt; i++) {
	
		    	// Array�� str �� �ܾ parse
				String taf = strArray[i].toString();
				//log.info("taf: " + taf );
				
				// �������
				if(taf.indexOf("TN") != -1) { 
					resultStr = "minimumTemper : " + taf.substring(2, 4) + "��C" + "<br>";
					log.info("minimumTemper : " + taf.substring(2, 4) + "��C");
				}
				
				// �ְ���
				if(taf.indexOf("TX") != -1) { 
					resultStr = "highestTemper : " + taf.substring(2, 4) + "��C" + "<br>";
					log.info("highestTemper : " + taf.substring(2, 4) + "��C");  
				} 
				
				// airport
				if(taf.indexOf("RKSI") != -1) { 
					resultStr = "airport : " + "��õ����" + "<br>";
					log.info("airport : " + "��õ����");
				}
				
				// ForeCastDay
				if((taf.indexOf("/") != -1) && (taf.indexOf("Z") == -1)) { 
					String forecastDay = taf.substring(0, 2);
					String forecastTime = taf.substring(2, 4);
					String forecastUntilDay = taf.substring(5, 7);
					String forecastUntilTime = taf.substring(7, 9);
					String foreCastDay = forecastDay + "�� " + forecastTime + "�� ~ " + forecastUntilDay + "�� " + forecastUntilTime + "��";
					resultStr = foreCastDay + "<br>";
					log.info("forecastDay : " + foreCastDay); 
				}
				
				// ������, ǳ��
				if(taf.indexOf("KT") != -1) {
					// ������
					resultStr = "azimuth : " + taf.substring(0, 3) + "<br>";
					log.info("azimuth : " + taf.substring(0, 3));
					
					// Knots
					resultStr = "Knots : " + taf.substring(3, 5) + "<br>";
					log.info("Knots : " + taf.substring(3, 5));
				}
				
				// ���ðŸ� - ���ڸ� ���� ��� ���ðŸ��� �Ǵ�
				if (isNumeric(taf)) { 
					resultStr = "sight : " + taf + "<br>";
					log.info("sight : " + taf);
				}
	
				// �� �ణ
				if(taf.indexOf("-RA") != -1) { 
					resultStr = "littleRA : " + "�� �ణ" + "<br>";
					log.info("littleRA : " + "�� �ణ"); 
				}
				
				// �� ����
				if(taf.indexOf("RA") != -1) { 
					resultStr = "RA : " + "�� ����" + "<br>";
					log.info("RA : " + "�� ����");
				}
	
				// �ڹ� : �Ȱ����� ������ ���� ����
				if(taf.indexOf("BR") != -1) { 
					resultStr = "lightRai : " + "�ڹ�" + "<br>";
					log.info("lightRai : " + "�ڹ�");
				}
	
				// ������ȣ : ������ ���쵵 ���� ��Ÿ Ư���� �ϱ��Ȳ�� ���� ��
				if(taf.indexOf("CAVOK") != -1) { 
					resultStr = "CAVOK : " + "������ȣ" + "<br>";
					log.info("CAVOK : " + "������ȣ");
				}
	
				// ��� �߿��� ���� ����, ���������� ���� ����
				if(taf.indexOf("NSC") != -1) {
					resultStr = "NSC : " + "��� �߿��� ���� ����, ���������� ���� ����" + "<br>";
					log.info("NSC : " + "��� �߿��� ���� ����, ���������� ���� ����");
				}
				
				// ��, ����
				if(taf.indexOf("FEW") != -1) { 
					resultStr = "FEW : " + taf.substring(4, 6) + "00ft" + " ���� ����" + "<br>";
					log.info("FEW : " + taf.substring(4, 6) + "00ft" + " ���� ����");
				}
				
				if(taf.indexOf("SCT") != -1) { 
					resultStr = "SCT : " + taf.substring(4, 6) + "00ft" + " ���� ����" + "<br>";
					log.info("SCT : " + taf.substring(4, 6) + "00ft" + " ���� ����");
				}			
	    	}
    	}
    	
    	return resultStr;
    }

}
