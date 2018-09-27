package com.bank.controller;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.bank.model.User;
import com.bank.service.DepositService;

@Controller
public class LostAndModifyController {
	
	private final DepositService dep;
	
	@Autowired
	public LostAndModifyController(DepositService dep) {
		this.dep = dep;
	}
	
	@GetMapping("/lost.html")
	public String lostGet(HttpServletRequest request,Model model){
		User user = (User) request.getSession().getAttribute("currentUser");
		model.addAttribute("currentUser", user);
		return "lost";
	}
	
	@GetMapping("/modify.html")
	public String modifyGet(HttpServletRequest request,Model model){
		User user = (User) request.getSession().getAttribute("currentUser");
		model.addAttribute("currentUser", user);
		return "modify";
	}
	
	@PostMapping("/lost")
	public String losePost(final RedirectAttributes attributes,@RequestParam String idnum){
		String str = dep.hangLoss(idnum);
		attributes.addFlashAttribute("message", str);
		if("一卡通成功挂失".equals(str)){
			return "redirect:/" + "index.html";
		}else {
			return "redirect:/" + "lost.html";
		}
	}
	
	@PostMapping("/unlost")
	public String unlosePost(final RedirectAttributes attributes,@RequestParam String idnum){
		String str = dep.unHangLoss(idnum);
		attributes.addFlashAttribute("message", str);
		if("取消挂失成功!".equals(str)){
			return "redirect:/" + "index.html";
		}else {
			return "redirect:/" + "lost.html";
		}
	}
	
	@PostMapping("/resubmit")
	public String resubmitPost(final RedirectAttributes attributes,@RequestParam String idnum){
		String str = dep.reSubmit(idnum);
		attributes.addFlashAttribute("message", str);
		if("成功补办一卡通".equals(str)){
			return "redirect:/" + "index.html";
		}else {
			return "redirect:/" + "lost.html";
		}
	}
	
	
	
	//销户
	@PostMapping("/delete")
	public String deletePost(final RedirectAttributes attributes,@RequestParam String idnum){
		String str = dep.deleteDeposit(idnum);
		attributes.addFlashAttribute("message", str);
		if("成功销户".equals(str)){
			return "redirect:/" + "index.html";
		}else {
			return "redirect:/" + "modify.html";
		}
	}
	
	//修改密码
	@PostMapping("/resetpassword")
	public String passwordPost(final RedirectAttributes attributes,@RequestParam String password,
									@RequestParam String confirm,@RequestParam String accountname){
		String str = dep.resetPassword(accountname, password, confirm);
		attributes.addFlashAttribute("message", str);
		if("密码重设成功".equals(str)){
			return "redirect:/" + "index.html";
		}else {
			return "redirect:/" + "modify.html";
		}
	}
}
