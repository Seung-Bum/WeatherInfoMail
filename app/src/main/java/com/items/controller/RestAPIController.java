package com.items.controller;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.StringReader;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.util.Date;
import java.util.HashMap;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.util.EntityUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.json.JSONArray;
import org.json.JSONObject;
import org.json.XML;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.HttpServerErrorException;
import org.springframework.web.client.RestTemplate;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;

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
	
	// ������
	/**
	 * ���� ���� �̷����� API ȣ��, Domestic airport take-off forecast api call�� curl�� ���� ���� ��
	 * ����� �޴´�.
	 * 
	 * @param model
	 * @return String @ author yang @ version 1.0
	 */
	@GetMapping("/takeOffForecast/runTimeCall")
	public String TakeOffForecastRunTimeCall(Model model) {

		
		//HashMap<String, Object> result = new HashMap<String, Object>();
		//String jsonInString = "";

		try {
			// Curl ���μ��� ����
			StringBuffer output = new StringBuffer();
			Process p = Runtime.getRuntime().exec(SERVICE_CALL); // curl ������
			BufferedReader reader = new BufferedReader(new InputStreamReader(p.getInputStream()));
			String line = "";

			while ((line = reader.readLine()) != null) {
				output.append(line + "\n");
				System.out.println(line.toString());
			}
			// p.waitFor();
			String outString = output.toString();

			// XML ������ String ������ parsing
			// StringBuffer ���� ��� �� ���̱⿡ StringBuffer ����
			StringBuffer sb = new StringBuffer();

			// ������ StringBuffer�ȿ� xml���� String ������ ����
			sb.append(outString);

			// Document�� �Ľ��Ͽ� ��� �� ���̱⿡ DocumentBuilderFactory ����
			DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();

			// DocumentBuilderFactory�� DocumentBuilder
			DocumentBuilder builder = factory.newDocumentBuilder();

			// sb.toString�� Document �������� ����
			Document document = builder.parse(new InputSource(new StringReader(sb.toString())));

			// document �ȿ��� ã���� �ϴ� �±װ��� ���� �ͼ� NodeList�� ����
			NodeList taglist = document.getElementsByTagName("response");
			NodeList response = (NodeList) taglist.item(0); // response
			NodeList body = (NodeList) response.item(1); // body
			NodeList items = (NodeList) body.item(1); // items

			String nodeName;
			String nodeValue;

			HashMap<String, Object> map = new HashMap<String, Object>();
			for (int i = 0; i < items.getLength(); i++) {
				NodeList item = (NodeList) items.item(i);

				for (int j = 0; j < item.getLength(); j++) {
					Node node = item.item(j);
					nodeName = node.getNodeName();
					nodeValue = node.getTextContent();
					// log.info(node.getNodeName() + " " + node.getTextContent());
					// log.info(nodeName + " " + nodeValue);

					if ((nodeName != null || nodeName != "") && (nodeValue != null || nodeValue != "")) {
						map.put(nodeName, nodeValue);
						// log.info(nodeName + " " + nodeValue);
					} else {
						map.put(nodeName, "null");
						// restAPIService.insertAirInfo(map);
						// log.info(nodeName + " " + "null");
					}
				}
				log.info(map.get("airportName") + " ");
				log.info(map.get("icaoCode") + " ");
				restAPIService.insertAirInfo(map);
			}

		} catch (HttpClientErrorException | HttpServerErrorException e) {
			log.info(e.getRawStatusCode());
			log.info(e.getStatusText());
		} catch (Exception e) {
			log.info(e.toString());
		}
		return "successPage";
	}
	
	
	// ������
	/**
	 * restTemplate ����, �����߻�
	 * 
	 * @param model
	 * @return String @ author yang @ version 1.0
	 */
	@GetMapping("/restTemplateAPI")
	public String restTemplateAPI(Model model) throws Exception {

		String url = "http://apis.data.go.kr/1360000/AirInfoService/getAirInfo?pageNo=1&numOfRows=10&fctm=202306300000&icaoCode=RKSI";
		// String url =
		// "http://apis.data.go.kr/1360000/AirInfoService/getAirInfo?serviceKey=kry52Qun7PJGODw51SGulaC5UitRsf1%2Bhts8gSWXpb7zYRfruRDZIB%2F5cXiWZk0oGSClTajuFU9bOul9kuYP5g%3D%3D&pageNo=1&numOfRows=10&fctm=202306290000&icaoCode=RKSI";
		RestTemplate rt = new RestTemplate();

		// �ش� �����
		HttpHeaders headers = new HttpHeaders();
		headers.add("Content-Type", "application/json; charset=utf-8");
		// headers.add("Content-type", "application/x-www-form-urlencoded;
		// charset=utf-8");
		// headers.add("Content-type", "text/plain; charset=utf-8");

		MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
		params.add("serviceKey",
				"kry52Qun7PJGODw51SGulaC5UitRsf1%2Bhts8gSWXpb7zYRfruRDZIB%2F5cXiWZk0oGSClTajuFU9bOul9kuYP5g%3D%3D");

		// �ش��� �ٵ� �ϳ��� ������Ʈ�� �����
		HttpEntity<MultiValueMap<String, String>> TokenRequest = new HttpEntity<>(params, headers);

		// Http ��û�ϰ� ���ϰ��� response ������ �ޱ�
		ResponseEntity<String> response = rt.exchange(url, // Host
				HttpMethod.POST, // Request Method
				TokenRequest, // RequestBody
				String.class); // return Object

		// HTTP POST ��û�� ���� ���� Ȯ��
		System.out.println("status : " + response.getStatusCode());
		System.out.println("body : " + response.getBody());

		return "successPage";
	}
	
	
	// ������
	/**
	 * ���� ���� �̷�����, Domestic airport take-off forecast restAPI, XMLtoJson
	 * 
	 * @param model
	 * @return String @ author yang @ version 1.0
	 */
	@GetMapping("/TakeOffForecast")
	public String TakeOffForecast(Model model) throws Exception {

		String url = "https://apis.data.go.kr/1360000/AirInfoService/getAirInfo?serviceKey=" + TOKEN
				+ "&pageNo=1&numOfRows=10&fctm=202306290000&icaoCode=RKSI";
		CloseableHttpClient httpClient = HttpClientBuilder.create().build();
		HttpGet httpGet = new HttpGet(url);
		httpGet.setHeader("content-type", "application/json; charset=utf-8");

		CloseableHttpResponse response = httpClient.execute(httpGet);
		String resXml = EntityUtils.toString(response.getEntity(), "UTF-8");
		// System.out.println(resXml);

		// xml ���¸� json���� �����ؼ� �ٷ�°��� ���ϴٰ��� (json simple �ƴ�)
		JSONObject jsonObject = XML.toJSONObject(resXml);
		JSONObject responseJson = (JSONObject) jsonObject.get("response");
		JSONObject body = (JSONObject) responseJson.get("body");
		JSONObject items = (JSONObject) body.get("items");
		JSONArray item = (JSONArray) items.get("item");

		HashMap<String, Object> map = new HashMap<String, Object>();
		for (int i = 0; i < item.length(); i++) {
			JSONObject jsonItem = (JSONObject) item.get(i);
			// System.out.println(jsonItem.toString());

			map.put("tmFc", jsonItem.get("tmFc"));

			System.out.println(jsonItem.get("airportName"));
			System.out.println(jsonItem.get("icaoCode"));
			System.out.println(jsonItem.get("qnh"));
			System.out.println(jsonItem.get("ws"));
			System.out.println(jsonItem.get("wd"));
			System.out.println(jsonItem.get("ta"));

		}
		return "successPage";
	}
	
	
	/**
	 * �װ��������, Aviation weather information restAPI, XMLtoJson
	 * 
	 * @param model
	 * @return String @ author yang @ version 1.0
	 */
	@ResponseBody
	@Scheduled(cron = "0 58 22 * * ?") // �� �� �� �� �� ����
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
		String[] line5;

		// TAF Line Loop
		log.info("TAF Line Loop Start");
		HashMap<String, Object> rsltParam = new HashMap<>();	
		
		// Line0
		log.info("Line0 Start");
		line0 = tafMsg_str[0].trim().split(" ");			
		//HashMap<String, Object> result0 = aviationWeatherInfo(line0);
		String result0 = aviationWeatherInfoStr(line0);
		
		// ���ʿ���
		String line0Taf2 = line0[2].toString();
		if( (line0Taf2.indexOf("Z") == -1) ) { 
			//result0.put("createTime", " ");
			result0 += "createTime : " + "/n";
		}			
		else {
			String createDay = line0Taf2.substring(0,2); // day
			String createTime = line0Taf2.substring(2,4); // time
			String createMinute = line0Taf2.substring(4,6); // minute
			//result0.put("createTime", createDay + "�� " + createTime + ":" + createMinute);
			result0 += createDay + "�� " + createTime + ":" + createMinute + "/n";
		}
		
		// ���� ��ȿ�ð� - 30�ð� ��ȿ
		String line0Taf3 = line0[3].toString();
		if(line0Taf3.indexOf("/") != -1) {
			String[] available_day = line0Taf3.toString().split("/");
			//result0.put("availableDay", available_day[0].substring(0, 2) + "�� " + available_day[0].substring(2, 4) + "�� ~ " + 
			//		available_day[1].substring(0, 2) + "�� " + available_day[1].substring(2, 4) + "��");
			result0 += "availableDay : " +  available_day[0].substring(0, 2) + "�� " + available_day[0].substring(2, 4) + "�� ~ " + 
							available_day[1].substring(0, 2) + "�� " + available_day[1].substring(2, 4) + "��" + "n/";
			}
		else { 
			//result0.put("availableDay", " ");
			result0 += "availableDay : " + "/n";
		}
		
		log.info("line0 : result0 - " + result0.toString());
		//model.addAttribute("resMap0", result0);
		rsltParam.put("resMap0", result0);
		
		// line1
		log.info("Line1 Start");
		line1 = tafMsg_str[1].trim().split(" ");
		String result1 = aviationWeatherInfoStr(line1);
		log.info("line1 : result1 - " + result1.toString());
		//model.addAttribute("resMap1", result1);
		rsltParam.put("resMap1", result1);
		
		// line2
		log.info("Line2 Start");
		line2 = tafMsg_str[2].trim().split(" ");
		String result2 = aviationWeatherInfoStr(line2);
		log.info("line2 : result2 - " + result2.toString());
		//model.addAttribute("resMap2", result2);
		rsltParam.put("resMap2", result2);
		
		// line3
		log.info("Line3 Start");	
		line3 = tafMsg_str[3].trim().split(" ");
		String result3 = aviationWeatherInfoStr(line3);
		log.info("line3 : result3 - " + result3.toString());
		//model.addAttribute("resMap3", result3);
		rsltParam.put("resMap3", result3);
		
		// line4
		// line3������ ������ ������ ��������� line4�� ���� ��쵵 ����
		try {
			log.info("Line4 Start");
			line4 = tafMsg_str[5].trim().split(" ");
			String result4 = aviationWeatherInfoStr(line4);
			log.info("line4 : result4 - " + result4.toString());
			//model.addAttribute("resMap4", result4);
			rsltParam.put("resMap4", result4);
		} catch(Exception e) {
			String[] none = {};
			String result4 = aviationWeatherInfoStr(none);
			log.info("line4 : result4 - " + result4.toString());
			//model.addAttribute("resMap4", result4);
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
	 * aviationWeatherInfo
	 * ForeCastDay, ������, ǳ��, ���ðŸ�, �� �ణ, �ڹ�, ������ȣ, NSC
	 * @param String[] strArray
	 * @return HashMap<String, Object>
	 */
    public HashMap<String, Object> aviationWeatherInfo(String[] strArray) {
    	
		// ���� �ʱ�ȭ
		HashMap<String, Object> map = new HashMap<String, Object>();
    	
    	map.put("forecastDay", "none");
    	map.put("minimumTemper", "none");
    	map.put("highestTemper", "none");
    	map.put("airport", "none");
    	map.put("azimuth", "none");
    	map.put("Knots", "none");
    	map.put("sight", "none");
    	map.put("littleRA", "none");
    	map.put("RA", "none");
    	map.put("lightRai", "none");
    	map.put("CAVOK", "none");
    	map.put("FEW", "none");
    	map.put("SCT", "none");
    	map.put("NSC", "none");
    	
    	int loopCnt = strArray.length;
    	if (loopCnt != 0) {
	    	for (int i=0; i<loopCnt; i++) {
	
		    	// Array�� str �� �ܾ parse
				String taf = strArray[i].toString();
				//log.info("taf: " + taf );
				
				// �������
				if(taf.indexOf("TN") != -1) { 
					map.replace("minimumTemper", taf.substring(2, 4) + "��C"); 
					log.info("minimumTemper : " + taf.substring(2, 4) + "��C");
				}
				
				// �ְ���
				if(taf.indexOf("TX") != -1) { 
					map.replace("highestTemper", taf.substring(2, 4) + "��C"); 
					log.info("highestTemper : " + taf.substring(2, 4) + "��C");  
				} 
				
				// airport
				if(taf.indexOf("RKSI") != -1) { 
					map.replace("airport", "��õ����");
					log.info("airport : " + "��õ����");
				}
				
				// ForeCastDay
				if((taf.indexOf("/") != -1) && (taf.indexOf("Z") == -1)) { 
					String forecastDay = taf.substring(0, 2);
					String forecastTime = taf.substring(2, 4);
					String forecastUntilDay = taf.substring(5, 7);
					String forecastUntilTime = taf.substring(7, 9);
					String foreCastDay = forecastDay + "�� " + forecastTime + "�� ~ " + forecastUntilDay + "�� " + forecastUntilTime + "��";
					map.replace("forecastDay", foreCastDay);
					log.info("forecastDay : " + foreCastDay); 
				}
				
				// ������, ǳ��
				if(taf.indexOf("KT") != -1) {
					// ������
					map.replace("azimuth", taf.substring(0, 3));
					log.info("azimuth : " + taf.substring(0, 3));
					
					// Knots
					map.replace("Knots", taf.substring(3, 5));
					log.info("Knots : " + taf.substring(3, 5));
				}
				
				// ���ðŸ� - ���ڸ� ���� ��� ���ðŸ��� �Ǵ�
				if (isNumeric(taf)) { 
					map.replace("sight", taf);
					log.info("sight : " + taf);
				}
	
				// �� �ణ
				if(taf.indexOf("-RA") != -1) { 
					map.replace("littleRA", "�� �ణ");
					log.info("littleRA : " + "�� �ణ"); 
				}
				
				// �� ����
				if(taf.indexOf("RA") != -1) { 
					map.replace("RA", "�� ����");
					log.info("RA : " + "�� ����");
				}
	
				// �ڹ� : �Ȱ����� ������ ���� ����
				if(taf.indexOf("BR") != -1) { 
					map.replace("lightRai", "�Ȱ����� ������ ���� ����"); 
					log.info("lightRai : " + "�ڹ�");
				}
	
				// ������ȣ : ������ ���쵵 ���� ��Ÿ Ư���� �ϱ��Ȳ�� ���� ��
				if(taf.indexOf("CAVOK") != -1) { 
					map.replace("CAVOK", "*������ ���쵵 ���� ��Ÿ Ư���� �ϱ� ��Ȳ�� ����");
					log.info("CAVOK : " + "������ȣ");
				}
	
				// ��� �߿��� ���� ����, ���������� ���� ����
				if(taf.indexOf("NSC") != -1) { 
					map.replace("NSC", "��� �߿��� ���� ����, ���������� ���� ����"); 
					log.info("NSC : " + "���������� ���� ����");
				}
				
				// ��, ����
				if(taf.indexOf("FEW") != -1) { 
					map.replace("FEW", taf.substring(4, 6) + "00ft" + " ���� ����"); 
					log.info("FEW : " + taf.substring(4, 6) + "00ft" + " ���� ����");
				}
				
				if(taf.indexOf("SCT") != -1) { 
					map.replace("SCT", taf.substring(4, 6) + "00ft" + " ���� ����"); 
					log.info("SCT : " + taf.substring(4, 6) + "00ft" + " ���� ����");
				}			
	    	}
    	}

		return map;
    }
    
	
    /**
	 * insertDepartureData
	 * ����ڰ� �Է��� �ⱹ������ insert
	 * @param String email, String departureDate
	 * @return 
     * @throws ParseException 
	 */
	@RequestMapping("/departureData")
	public String insertDepartureData(String email, String departureDate, 
								HttpServletRequest request, HttpServletResponse response) throws ParseException {
		
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
		param.put("seq",  Integer.valueOf(maxSeq.get("SEQ").toString()) + 1);
		
		if (!param.get("email").equals("") && !param.get("departureDate").equals("")) {			
	        // before check
	        if (dDay.before(tDate)) {
	        	log.info("- �ⱹ������ ���� ���� �̷����� �մϴ�.");
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
					resultStr = "minimumTemper : " + taf.substring(2, 4) + "��C" + "/n";
					log.info("minimumTemper : " + taf.substring(2, 4) + "��C");
				}
				
				// �ְ���
				if(taf.indexOf("TX") != -1) { 
					resultStr = "highestTemper : " + taf.substring(2, 4) + "��C" + "/n";
					log.info("highestTemper : " + taf.substring(2, 4) + "��C");  
				} 
				
				// airport
				if(taf.indexOf("RKSI") != -1) { 
					resultStr = "airport : " + "��õ����" + "/n";
					log.info("airport : " + "��õ����");
				}
				
				// ForeCastDay
				if((taf.indexOf("/") != -1) && (taf.indexOf("Z") == -1)) { 
					String forecastDay = taf.substring(0, 2);
					String forecastTime = taf.substring(2, 4);
					String forecastUntilDay = taf.substring(5, 7);
					String forecastUntilTime = taf.substring(7, 9);
					String foreCastDay = forecastDay + "�� " + forecastTime + "�� ~ " + forecastUntilDay + "�� " + forecastUntilTime + "��";
					resultStr = foreCastDay + "/n";
					log.info("forecastDay : " + foreCastDay); 
				}
				
				// ������, ǳ��
				if(taf.indexOf("KT") != -1) {
					// ������
					resultStr = "azimuth : " + taf.substring(0, 3) + "/n";
					log.info("azimuth : " + taf.substring(0, 3));
					
					// Knots
					resultStr = "Knots : " + taf.substring(3, 5) + "/m";
					log.info("Knots : " + taf.substring(3, 5));
				}
				
				// ���ðŸ� - ���ڸ� ���� ��� ���ðŸ��� �Ǵ�
				if (isNumeric(taf)) { 
					resultStr = "sight : " + taf + "/n";
					log.info("sight : " + taf);
				}
	
				// �� �ణ
				if(taf.indexOf("-RA") != -1) { 
					resultStr = "littleRA : " + "�� �ణ" + "/n";
					log.info("littleRA : " + "�� �ణ"); 
				}
				
				// �� ����
				if(taf.indexOf("RA") != -1) { 
					resultStr = "RA : " + "�� ����" + "/n";
					log.info("RA : " + "�� ����");
				}
	
				// �ڹ� : �Ȱ����� ������ ���� ����
				if(taf.indexOf("BR") != -1) { 
					resultStr = "lightRai : " + "�ڹ�" + "/n";
					log.info("lightRai : " + "�ڹ�");
				}
	
				// ������ȣ : ������ ���쵵 ���� ��Ÿ Ư���� �ϱ��Ȳ�� ���� ��
				if(taf.indexOf("CAVOK") != -1) { 
					resultStr = "CAVOK : " + "������ȣ" + "/n";
					log.info("CAVOK : " + "������ȣ");
				}
	
				// ��� �߿��� ���� ����, ���������� ���� ����
				if(taf.indexOf("NSC") != -1) {
					resultStr = "NSC : " + "��� �߿��� ���� ����, ���������� ���� ����" + "/n";
					log.info("NSC : " + "��� �߿��� ���� ����, ���������� ���� ����");
				}
				
				// ��, ����
				if(taf.indexOf("FEW") != -1) { 
					resultStr = "FEW : " + taf.substring(4, 6) + "00ft" + " ���� ����" + "/n";
					log.info("FEW : " + taf.substring(4, 6) + "00ft" + " ���� ����");
				}
				
				if(taf.indexOf("SCT") != -1) { 
					resultStr = "SCT : " + taf.substring(4, 6) + "00ft" + " ���� ����" + "/n";
					log.info("SCT : " + taf.substring(4, 6) + "00ft" + " ���� ����");
				}			
	    	}
    	}
    	
    	return resultStr;
    }



}
