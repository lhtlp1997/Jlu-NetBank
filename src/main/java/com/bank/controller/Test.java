package com.bank.controller;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.bank.model.SubCredit;
import com.bank.model.User;
import com.bank.repository.ECardRepository;
import com.bank.repository.UserRepository;

@SuppressWarnings("unused")
@Controller
public class Test {
	
	@Autowired
	private UserRepository userRepository;
	@Autowired
	private ECardRepository ecard;
	
	@ResponseBody
	@GetMapping("/test")
	public String test(HttpServletRequest request){
		return request.getSession().getAttribute("currentUser").toString();
	}
}
