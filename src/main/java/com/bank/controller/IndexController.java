package com.bank.controller;

import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.bank.model.Employee;
import com.bank.model.SubCredit;
import com.bank.model.User;
import com.bank.repository.SubCreditRepository;
import com.bank.util.Constant;

@Controller
public class IndexController {
	
	private final SubCreditRepository sb;
	
	@Autowired
	public IndexController(SubCreditRepository sb) {
		this.sb = sb;
	}
	
	@GetMapping("/index.html")
	public String index(Model model,HttpServletRequest request,final RedirectAttributes attributes){
		
		Employee employee = (Employee)request.getSession().getAttribute("currentManager");
		User user = (User)request.getSession().getAttribute("currentUser");
		List<SubCredit> subCredits = sb.findAll();
		
		
		if(user == null && employee == null){
			attributes.addFlashAttribute("message", "必须先经过登录！");
			return "redirect:/" + "login.html";
		}
		
		model.addAttribute("indexDeposit", Constant.currentDepositRate);
		model.addAttribute("indexFixed", Constant.fixedDepositRate);
		model.addAttribute("indexLoan", Constant.LOANRATE);
		
		model.addAttribute("currentManager",employee);
		model.addAttribute("currentUser", user);
		
		model.addAttribute("subcredits",subCredits);
		
		return "index";
	}
	
	
}
