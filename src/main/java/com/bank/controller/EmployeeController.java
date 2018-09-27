package com.bank.controller;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.bank.model.Employee;
import com.bank.model.Plan;
import com.bank.repository.EmployeeRepository;
import com.bank.repository.PlanRepository;
import com.bank.service.ManageService;
import com.bank.util.CurrentDay;

@Controller
public class EmployeeController {
	
	private final EmployeeRepository emp;
	private final ManageService manage;
	private final PlanRepository pl;
	
	@Autowired
	public EmployeeController(ManageService manage,PlanRepository pl,EmployeeRepository emp) {
		this.manage = manage;
		this.pl = pl;
		this.emp = emp;
	}
	
	@GetMapping("/employee.html")
	public String employeeget(Model model,HttpServletRequest request){
		List<Employee> employees = emp.findAll();
		model.addAttribute("employees", employees);
		
		Employee manager = (Employee)request.getSession().getAttribute("currentManager");
		model.addAttribute("currentManager", manager);
		
		return "employee";
	}
	
	
	@GetMapping("/plan.html")
	public String planGet(Model model,HttpServletRequest request){
		List<Plan> plans = pl.findAll();
		model.addAttribute("plans", plans);
		
		Employee manager = (Employee)request.getSession().getAttribute("currentManager");
		model.addAttribute("currentManager", manager);
		
		return "plan";
	}

	
	
	
	//管理员信息
	@PostMapping("/employee")
	public String employeepost(final RedirectAttributes attributes,@RequestParam String button,
							@RequestParam String number,@RequestParam String password,
							@RequestParam String confirm){
		Employee employee = emp.findOne(number);
		if(employee == null){
			attributes.addFlashAttribute("message", "找不到该员工！");
			return "redirect:/" + "employee.html";
		}
		if("删除".equals(button)){
			String str = manage.delete(employee.getWorkNumber());
			attributes.addFlashAttribute("message", str);
			if("删除成功！".equals(str)){
				return "redirect:/" + "index.html";
			}else {
				return "redirect:/" + "employee.html";
			}
		}
		
		if("修改".equals(button)){
			
			//少写了一个，这里必须让两次密码一致
			if(!password.equals(confirm)){
				attributes.addFlashAttribute("message", "两次密码不一致");
				return "redirect:/" + "employee.html";
			}
				
			String str = manage.modify(employee.getWorkNumber(),password);
			attributes.addFlashAttribute("message", str);
			if("修改完成！".equals(str)){
				return "redirect:/" + "index.html";
			}else {
				return "redirect:/" + "employee.html";
			}
		}
		
		return "employee";
	}
	
	
	
	
	
	
	//创建
	@PostMapping("/create")
	public String createPost(final RedirectAttributes attributes,@RequestParam String executeDate
						,@RequestParam Double currentDepositRate,@RequestParam Double fixedDepositRate
						,@RequestParam Double loanRate,@RequestParam String button){
		
		//确认的操作
		Date date;
		try {
			date = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").parse(executeDate);
		} catch (ParseException e) {
			attributes.addFlashAttribute("message", "时间格式错误");
			return "redirect:/" + "plan.html";
		}
		if("确认".equals(button)){
			String str = manage.createPlan(date, currentDepositRate, fixedDepositRate, loanRate);
			attributes.addFlashAttribute("message", str);
			if("创建计划成功！".equals(str)){
				return "redirect:/" + "index.html";
			}else {
				return "redirect:/" + "plan.html";
			}
		}
		
		return "rediret:/" + "plan.html";
	}

	
	//未执行计划
	@PostMapping("/wei")
	public String notExecutePost(final RedirectAttributes attributes,@RequestParam String executeDate
								,@RequestParam String button){
		//删除一个没有执行的计划
		Date date = CurrentDay.currentDay;
		try {
			date = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").parse(executeDate);
		} catch (ParseException e) {
			attributes.addFlashAttribute("message", "输入不合法的时间");
			return "redirect:/" + "plan.html";
		}
		Plan plan = pl.findOne(date);
		if(plan.getExecuteState() != 2){
			attributes.addFlashAttribute("message", "该计划不属于未执行计划！");
			return "redirect:/" + "plan.html";
		}
		if("删除".equals(button)){
			manage.deletePlan(plan);
			attributes.addFlashAttribute("message", "删除成功");
			return "redirect:/" + "index.html";
		}
		return "rediret:/" + "plan.html";
	}

	
	//已执行计划
	@PostMapping("/yi")
	public String beforePost(final RedirectAttributes attributes,@RequestParam String executeDate
							,@RequestParam String button){
		Date date = new Date();
		try {
			date = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").parse(executeDate);
		} catch (ParseException e) {
			attributes.addFlashAttribute("message", "输入不合法的时间");
			return "redirect:/" + "plan.html";
		}
		Plan plan = pl.findOne(date);
		if(plan.getExecuteState() != 3){
			attributes.addFlashAttribute("message", "该计划不属于已执行计划！");
			return "redirect:/" + "plan.html";
		}
		if("删除".equals(button)){
			manage.deletePlan(plan);
			attributes.addFlashAttribute("message", "删除成功");
			return "redirect:/" + "index.html";
		}
		if("恢复".equals(button)){
			manage.planExecute(plan);
			attributes.addFlashAttribute("message", "计划恢复成功");
			return "redirect:/" + "plan.html";
		}
		return "plan";
	}
}
