package com.items.controller;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

public class thymleafTest {
	@Controller
	public class HelloController {

	    @GetMapping("/hello")
	    public String hello(Model model) {
	    	System.out.println("HelloController");
	        model.addAttribute("message", "Hello, Thymeleaf!");
	        return "message";
	    }
	}
}
