package com.bank.controller;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.bank.model.Employee;
import com.bank.model.User;
import com.bank.service.DepositService;
import com.bank.util.Constant;

@Controller
public class DepositController {
	
	private final DepositService dep;
	
	@Autowired
	public DepositController(DepositService dep) {
		this.dep = dep;
	}
	
	@GetMapping("/deposit.html")
	public String depositGet(Model model,HttpServletRequest request){
		model.addAttribute("currentrate", Constant.currentDepositRate);
		model.addAttribute("fixedrate", Constant.fixedDepositRate);
		model.addAttribute("fixanddeposit", Constant.fixAndDepositRate);
		Employee manager = (Employee)request.getSession().getAttribute("currentManager");
		model.addAttribute("currentManager", manager);
		return "deposit";
	}
	
	@GetMapping("/draw.html")
	public String drawGet(Model model,HttpServletRequest request){
		Employee manager = (Employee)request.getSession().getAttribute("currentManager");
		model.addAttribute("currentManager", manager);
		return "draw";
	}
	
	@GetMapping("/transfer.html")
	public String transeferGet(Model model,HttpServletRequest request){
		User user = (User)request.getSession().getAttribute("currentUser");
		model.addAttribute("currentUser", user);
		return "transfer";
	}
	
	
	@PostMapping("/currentdeposit")
	public String depositPost(Model model,final RedirectAttributes attributes,@RequestParam String creditnum
							,@RequestParam Double money){
		String str = dep.currentDeposit(creditnum, money);
		attributes.addFlashAttribute("message", str);
		if("存入成功".equals(str)){
			return "redirect:/" + "index.html";
		}else {
			return "redirect:/" + "deposit.html";
		}
	}
	
	@PostMapping("/fixeddeposit")
	public String fixedPost(final RedirectAttributes attributes,@RequestParam String creditnum,
							@RequestParam Double money,@RequestParam String mode,@RequestParam String isContinue){
		Integer isOneYear;
		if("存一年".equals(mode)){
			isOneYear = 1;
		}else {
			isOneYear = 5;
		}
		
		Integer con;
		if("续存".equals(isContinue)){
			con = 1;//续存
		}else {
			con = -1;//不续存
		}
		
		String str = dep.fixedDeposit(creditnum, money, isOneYear,con);
		attributes.addFlashAttribute("message", str);
		if("存入成功".equals(str)){
			return "redirect:/" + "index.html";
		}else {
			return "redirect:/" + "deposit.html";
		}
	}
	
	@PostMapping("/fixandcur")
	public String fixandcurPost(final RedirectAttributes attributes,@RequestParam String creditnum,
								@RequestParam Double money){
		String str = dep.fixAndCurDeposit(creditnum, money);
		attributes.addFlashAttribute("message", str);
		if("存入成功".equals(str)){
			return "redirect:/" + "index.html";
		}else {
			return "redirect:/" + "deposit.html";
		}
	}
	
	
	
	
	//取钱
	@PostMapping("/draw")
	public String drawPost(final RedirectAttributes attributes,@RequestParam String idnum
							,@RequestParam String creditnum,@RequestParam Double money){
		String str = dep.draw(idnum, creditnum, money);
		attributes.addFlashAttribute("message", str);
		if("取款成功！".equals(str)){
			return "redirect:/" + "index.html";
		}else {
			return "redirect:/" + "draw.html";
		}
	}
	
	
	
	@PostMapping("/transfer")
	public String transferPost(final RedirectAttributes attributes,@RequestParam String source
							,@RequestParam Double money,@RequestParam String name,@RequestParam String destination){     
		
		String str = dep.transformMoney(source, money, name, destination);
		attributes.addFlashAttribute("message", str);
		if("转账成功".equals(str)){
			return "redirect:/" + "index.html";
		}else {
			return "redirect:/" + "transfer.html";
		}
	}
	
}
