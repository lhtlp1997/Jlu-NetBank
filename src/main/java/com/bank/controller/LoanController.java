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

import com.bank.model.Employee;
import com.bank.model.Loan;
import com.bank.model.User;
import com.bank.service.LoanService;
import com.bank.util.Constant;

@Controller
public class LoanController {
	
	private final LoanService loanService;
	
	@Autowired
	public LoanController(LoanService loanService) {
		this.loanService = loanService;
	}
	
	@GetMapping("/studentloan.html")
	public String studentget(Model model,HttpServletRequest request){
		model.addAttribute("rate", Constant.LOANRATE);
		model.addAttribute("loan", new Loan());
		Employee manager = (Employee)request.getSession().getAttribute("currentManager");
		model.addAttribute("currentManager", manager);
		return "studentloan";
	}
	
	@GetMapping("/houseloan.html")
	public String houseget(Model model,HttpServletRequest request){
		model.addAttribute("rate", Constant.LOANRATE);
		model.addAttribute("loan",new Loan());
		Employee manager = (Employee)request.getSession().getAttribute("currentManager");
		model.addAttribute("currentManager", manager);
		return "houseloan";
	}
	
	@GetMapping("/personloan.html")
	public String houseloan(Model model,HttpServletRequest request){
		model.addAttribute("rate", Constant.LOANRATE);
		model.addAttribute("loan", new Loan());
		Employee manager = (Employee)request.getSession().getAttribute("currentManager");
		model.addAttribute("currentManager", manager);
		return "personloan";
	}
	
	@GetMapping("/expand.html")
	public String expandget(Model model,HttpServletRequest request){
		model.addAttribute("rate", Constant.LOANRATE);
		model.addAttribute("loan", new Loan());
		Employee manager = (Employee)request.getSession().getAttribute("currentManager");
		model.addAttribute("currentManager", manager);
		return "expand";
	}
	
	@GetMapping("/back.html")
	public String backget(Model model,HttpServletRequest request){
		Employee manager = (Employee)request.getSession().getAttribute("currentManager");
		model.addAttribute("currentManager", manager);
		return "back";
	}
	
	
	
	
	//学生贷款
	@PostMapping("/student")
	public String studentpost(Model model,@ModelAttribute("loan") Loan loan,@RequestParam String creditnum
						,final RedirectAttributes attributes,@RequestParam String id){
		if(loan.getGuarantee1() == null || loan.getGuarantee2() == null){
			attributes.addFlashAttribute("message", "你的担保人不存在！");
			return "redirect:/" + "studentloan.html";
		}
		String idNum1 = loan.getGuarantee1().getIdNum();
		String idNum2 = loan.getGuarantee2().getIdNum();
		Double money = loan.getMoney();
		Integer loanDate = loan.getLoanDate();
		String str = loanService.studentLoan(id,idNum1, idNum2, money, loanDate, creditnum);
		if("您的身份证号输入错误".equals(str)){
			attributes.addFlashAttribute("message", str);
			return "redirect:/" + "studentloan.html";
		}
		if("不合理的借贷！".equals(str)){
			attributes.addFlashAttribute("message", str);
			return "redirect:/" + "studentloan.html";
		}
		if("您的账户处于注销状态".equals(str)){
			attributes.addFlashAttribute("message", str);
			return "redirect:/" + "studentloan.html";
		}
		if("您的这个账户不是活期账户，不能进行贷款！".equals(str)){
			attributes.addFlashAttribute("message", str);
			return "redirect:/" + "studentloan.html";
		}
		if("目前没有设置贷款利息，暂时不能进行借贷！".equals(str)){
			attributes.addFlashAttribute("message", str);
			return "redirect:/" + "studentloan.html";
		}
		if("您输入的账户号码有误".equals(str)){
			attributes.addFlashAttribute("message", str);
			return "redirect:/" + "studentloan.html";
		}
		if("贷款成功！".equals(str)){
			attributes.addFlashAttribute("message", str);
			return "redirect:/" + "index.html";
		}
		return "index";
	}
	
	
	//房屋贷款
	@PostMapping("/house")
	public String housepost(Model model,@ModelAttribute("loan") Loan loan,final RedirectAttributes attributes
							,@RequestParam("creditnum") String creditnum
							,@RequestParam String sex,@RequestParam String id){
		Double money = loan.getMoney();
		Integer backStyle=3;
		Integer loanDate = loan.getLoanDate();
		if("等额本金".equals(sex)){
			backStyle = 4;
		}else{
			backStyle = 3;
		}

		String str = loanService.houseLoan(id,money, loanDate, backStyle,  creditnum);
		if("您的身份证号输入错误".equals(str)){
			attributes.addFlashAttribute("message", str);
			return "redirect:/" + "houseloan.html";
		}
		if("您输入的账户号码有误".equals(str)){
			attributes.addFlashAttribute("message", str);
			return "redirect:/" + "houseloan.html";
		}
		if("您的账户处于注销状态".equals(str)){
			attributes.addFlashAttribute("message", str);
			return "redirect:/" + "studentloan.html";
		}
		if("您的这个账户不是活期账户，不能进行贷款！".equals(str)){
			attributes.addFlashAttribute("message", str);
			return "redirect:/" + "studentloan.html";
		}
		if("不合理的借贷！".equals(str)){
			attributes.addFlashAttribute("message", str);
			return "redirect:/" + "houseloan.html";
		}
		if("您没有房子抵押不了！".equals(str)){
			attributes.addFlashAttribute("message", str);
			return "redirect:/" + "houseloan.html";
		}
		if("一个人不可以同时有多个贷款！".equals(str)){
			attributes.addFlashAttribute("message", str);
			return "redirect:/" + "houseloan.html";
		}
		if("目前没有设置贷款利息，暂时不能进行借贷！".equals(str)){
			attributes.addFlashAttribute("message", str);
			return "redirect:/" + "houseloan.html";
		}
		if("借贷成功！".equals(str)){
			attributes.addFlashAttribute("message", str);
			return "redirect:/" + "index.html";
		}
		return "index";
	}
	
	
	
	
	//个人贷款
	@PostMapping("/person")
	public String personpost(Model model,final RedirectAttributes attributes
							,@ModelAttribute("loan") Loan loan,@RequestParam String creditnum
							,@RequestParam String creditnumloan,@RequestParam String id){
		Double money = loan.getMoney();
		Integer loanDate = loan.getLoanDate();
		String str = loanService.personLoan(id,money, loanDate,creditnumloan,creditnum);
		if("您的身份证号输入错误".equals(str)){
			attributes.addFlashAttribute("message", str);
			return "redirect:/" + "personloan.html";
		}
		if("您输入的账户号码有误！".equals(str)){
			attributes.addFlashAttribute("message", str);
			return "redirect:/" + "personloan.html";
		}
		if("您的账户处于注销状态".equals(str)){
			attributes.addFlashAttribute("message", str);
			return "redirect:/" + "personloan.html";
		}
		if("您的这个账户不是活期账户，不能进行贷款！".equals(str)){
			attributes.addFlashAttribute("message", str);
			return "redirect:/" + "personloan.html";
		}
		if("该账户储蓄类型不是整存整取，不能进行借贷！".equals(str)){
			attributes.addFlashAttribute("message", str);
			return "redirect:/" + "personloan.html";
		}
		if("不合理的借贷！".equals(str)){
			attributes.addFlashAttribute("message", str);
			return "redirect:/" + "personloan.html";
		}
		if("一个人不可以同时有多个贷款！".equals(str)){
			attributes.addFlashAttribute("message", str);
			return "redirect:/" + "personloan.html";
		}
		if("目前没有设置贷款利息，暂时不能进行借贷！".equals(str)){
			attributes.addFlashAttribute("message", str);
			return "redirect:/" + "personloan.html";
		}
		if("借贷成功！".equals(str)){
			attributes.addFlashAttribute("message", str);
			return "redirect:/" + "index.html";
		}
		attributes.addFlashAttribute("message", "你能进行到这里是真滴牛皮");
		return "index";
	}
	
	@PostMapping("/idnum")
	public String indum(Model model,@RequestParam String idnum,final RedirectAttributes attributes){
		User user = loanService.idTest(idnum);
		if(user == null){
			attributes.addFlashAttribute("message", "输入了错误的身份证号！");
			return "redirect:/" + "back.html";
		}
		
		if(user.getLoan() == null){
			attributes.addFlashAttribute("message", "您的名下没有贷款！");
			return "redirect:/" + "back.html";
		}
		attributes.addFlashAttribute("idnum", idnum);
		attributes.addFlashAttribute("money", user.getLoan().getCurrentBack());
		return "redirect:/" + "back.html";
	}
	

	//还款
	@PostMapping("/back")
	public String backpost(Model model,@RequestParam String card,final RedirectAttributes attributes){

		String str = loanService.back(card);
		if("错误的子账号！".equals(str)){
			attributes.addFlashAttribute("message", str);
			return "redirect:/" + "back.html";
		}
		if("还款成功！".equals(str)){
			attributes.addFlashAttribute("message", str);
			return "redirect:/" + "index.html";
		}
		if("没有足够的钱，还不了！".equals(str)){
			attributes.addFlashAttribute("message", str);
			return "redirect:/" + "index.html";
		}
		return "index.html";
	}
	
	
	
	@PostMapping("/expand")
	public String expandpost(Model model,final RedirectAttributes attributes,@RequestParam String idnum
							,@RequestParam Long date){
		
		String str = loanService.expand(idnum, date);
		if("身份证号错误！".equals(str)){
			attributes.addFlashAttribute("message", str);
			return "redirect:/" + "expand.html";
		}
		if("该账户没有贷款业务！".equals(str)){
			attributes.addFlashAttribute("message", str);
			return "redirect:/" + "expand.html";
		}
		if("已经展期过一次了！".equals(str)){
			attributes.addFlashAttribute("message", str);
			return "redirect:/" + "expand.html";
		}
		if("超出时间上限！".equals(str)){
			attributes.addFlashAttribute("message", str);
			return "redirect:/" + "expand.html";
		}
		if("贷款逾期之后只能还款，不能进行展期！".equals(str)){
			attributes.addFlashAttribute("message", str);
			return "redirect:/" + "expand.html";
		}
		if("申请展期成功".equals(str)){
			attributes.addFlashAttribute("message", str);
			return "redirect:/" + "index.html";
		}
		attributes.addFlashAttribute("message", "未知的错误");
		return "index";
	}
	
	
	
	
	
	
	
	
	
	
	
}
