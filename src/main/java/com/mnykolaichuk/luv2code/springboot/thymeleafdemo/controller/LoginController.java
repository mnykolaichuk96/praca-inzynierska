package com.mnykolaichuk.luv2code.springboot.thymeleafdemo.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
@RequestMapping("/login")
public class LoginController {

	@GetMapping()
	public String login(Model model,
						@RequestParam(name = "message", required = false) String message) {
		model.addAttribute("message", message);
		return "login";
	}
	
}









