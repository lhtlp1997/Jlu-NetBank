package com.bank.controller;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.bank.model.ECard;
import com.bank.model.Employee;
import com.bank.model.SubCredit;
import com.bank.service.DepositService;
import com.bank.service.ManageService;

@Controller
public class RegisterController {
	
	private final DepositService depositService;
	private final ManageService manageService;
	
	@Autowired
	public RegisterController(ManageService manageService,DepositService depositService){     
		this.manageService = manageService;
		this.depositService = depositService;
	}
	
	@GetMapping("/open.html")
	public String open(Model model){
		model.addAttribute("subcredit",new SubCredit());
		model.addAttribute("ecard", new ECard());
		return "userregister";
	}
	
	@GetMapping("/userregister.html")
	public String getUser(Model model){
		model.addAttribute("subcredit",new SubCredit());
		model.addAttribute("ecard", new ECard());
		return "userregister";
	}
	
	@GetMapping("/employeeregister.html")
	public String getEmployee(Model model){
		model.addAttribute("employee", new Employee());
		return "employeeregister";
	}
	
	
	
	@PostMapping("/ecard")
	public String postECard(Model model,@ModelAttribute("ecard") ECard eCard,
						final RedirectAttributes attributes,
						@RequestParam String newpassword,@RequestParam String idnum,@RequestParam String name,
						@RequestParam String address,@RequestParam String tel){
		if(eCard.getAccountName().length() != 10){
			attributes.addFlashAttribute("message", "一卡通账号错误");
			return "redirect:/" + "userregister.html";
		}
		if(eCard.getPassword().length() != 6){
			attributes.addFlashAttribute("message", "密码长度不是6位");
			return "redirect:/" + "userregister.html";
		}
		if(!newpassword.equals(eCard.getPassword())){
			attributes.addFlashAttribute("message", "两次密码不一致");
			return "redirect:/" + "userregister.html";
		}
		if(depositService.openECard(eCard.getAccountName(), name, idnum, address, tel, eCard.getPassword())){    
			attributes.addFlashAttribute("message", "一卡通注册成功");
			return "redirect:/" + "index.html";
		}else {
			attributes.addFlashAttribute("message", "没有这个用户！");
			return "redirect:/" + "userregister.html";
		}
	}
	
	
	@PostMapping("/subcredit")
	public String postSubcredit(Model model,@ModelAttribute("subcredit") SubCredit subCredit
								,@RequestParam String accountname,
								final RedirectAttributes attributes){
		if(subCredit == null){
			return "redirect:/" + "userregister.html";
		}
		if(depositService.openSubcredit(accountname, subCredit.getCreditNum(), subCredit.getBalance())){
			attributes.addFlashAttribute("message", "子账户注册成功！");
			return "redirect:/" + "index.html";
		}else {
			attributes.addFlashAttribute("message", "注册失败");
			return "redirect:/" + "userregister.html";
		}
	}

	
	
	@PostMapping("/employeeregister")
	public String employeePost(Model model,@ModelAttribute("employee") Employee employee
							,final RedirectAttributes attributes
							,@RequestParam String newpassword){
		//目前只判断字母数字下划线
		String regex = "^[0-9a-zA-Z_]{8,16}$";
		if(employee.getWorkNumber().toCharArray().length != 5 || employee.getWorkNumber().toCharArray()[0]-'0'>4 || employee.getWorkNumber().toCharArray()[0]-'0'<1){    
			attributes.addFlashAttribute("message", "工号格式错误");
			return "redirect:/" + "employeeregister.html";
		}
		else if(!employee.getPassword().matches(regex) || employee.getPassword().length()<8 || employee.getPassword().length()>16){
			attributes.addFlashAttribute("message", "密码输入错误");
			return "redirect:/" + "employeeregister.html";
		}else if (!newpassword.equals(employee.getPassword())) {
			attributes.addFlashAttribute("message", "两次密码不一致");
			return "redirect:/" + "employeeregister.html";
		}else {
			attributes.addFlashAttribute("message", "注册成功！请开始登录");
			manageService.employeeRegister(employee.getWorkNumber(), employee.getPassword());
			return "redirect:/" + "login.html";
		}
	}
	
	
	//网上银行用户注册
	@PostMapping("/userregister")
	public String userPost(HttpServletRequest request,final RedirectAttributes attributes
						,@RequestParam String name,@RequestParam String password
						,@RequestParam String confirm,@RequestParam String idnum){
		String str = manageService.userRegister(idnum,name, password, confirm);
		attributes.addFlashAttribute("message", str);
		if("注册成功".equals(str)){
			return "redirect:/" + "login.html";
		}else {
			return "employeeregister.html";
		}
	}
}
