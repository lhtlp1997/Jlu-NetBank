package com.bank.controller;

import java.io.UnsupportedEncodingException;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.bank.model.Employee;
import com.bank.model.User;
import com.bank.repository.UserRepository;
import com.bank.service.ManageService;

@Controller
public class LoginController {
	
	private final ManageService manageService;
	private final UserRepository userRepository;
	
	@Autowired
	public LoginController(ManageService manageService,UserRepository userRepository) {
		this.manageService = manageService;
		this.userRepository = userRepository;
	}
	
	@GetMapping("/login.html")
	public String get(Model model){
		model.addAttribute("employee",new Employee());
		return "login";
	}
	
	//管理员登录
	@PostMapping("/login")
	public String post(Model model,HttpServletRequest request,@ModelAttribute("employee") Employee employee
						,final RedirectAttributes attributes) throws UnsupportedEncodingException{
		if(manageService.employeeLogin(employee.getWorkNumber(), employee.getPassword()) != null){
			request.getSession().setAttribute("currentManager", employee);
			request.getSession().setAttribute("currentUser", null);
			attributes.addFlashAttribute("message","登录成功！请进行系统管理操作！");
			return "redirect:/" + "index.html";
		}else {
			attributes.addFlashAttribute("message", "账号密码有误！请重新输入");
			return "redirect:/" + "login.html";
		}
	}

	//用户登录
	@PostMapping("/banklogin")
	public String bankPost(HttpServletRequest request,final RedirectAttributes attributes,
						@RequestParam String name,@RequestParam String password){
		String str = manageService.userLogin(name, password);
		User user = userRepository.findUserByNameAndPassword(name, password);
		attributes.addFlashAttribute("message", str);
		if("登录成功".equals(str)){
			request.getSession().setAttribute("currentUser", user);
			request.getSession().setAttribute("currentManager", null);
			return "redirect:/" + "index.html";
		}else {
			return "redirect:/" + "login.html";
		}
	}
}
