package com.items.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

@Controller
public class PostMappingTest {
	
	@PostMapping(value = "/postJson", consumes = "text/plain")
	public String postMappingController(@RequestBody String requestBody) {
		System.out.println("requestBody: " + requestBody.toString());
		ObjectMapper objectMapper = new ObjectMapper();
		try {
			JsonNode jsonNode = objectMapper.readTree(requestBody);
			String input1 = jsonNode.get("input1").asText();
			String input2 = jsonNode.get("input2").asText();
			String input3 = jsonNode.get("input3").asText();
			System.out.println("input1: " + input1);
			System.out.println("input2: " + input2);
			System.out.println("input3: " + input3);
		} catch(Exception e) {
			return e.getMessage();
		}
		
		return "success";
		//*curl ø‰√ª
		// curl -X POST -H "Content-Type: text/plain" -d '{"input1": "value1", "input2": "value2", "input3": "value3"}' http://localhost:8081/postJson

	}
	
}
