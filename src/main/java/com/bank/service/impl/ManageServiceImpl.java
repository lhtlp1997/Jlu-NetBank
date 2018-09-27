package com.bank.service.impl;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.bank.model.Employee;
import com.bank.model.Loan;
import com.bank.model.Plan;
import com.bank.model.SubCredit;
import com.bank.model.User;
import com.bank.repository.EmployeeRepository;
import com.bank.repository.PlanRepository;
import com.bank.repository.UserRepository;
import com.bank.service.ManageService;
import com.bank.util.Constant;
import com.bank.util.CurrentDay;

@SuppressWarnings("unused")
@Service
public class ManageServiceImpl implements ManageService{
	private final EmployeeRepository empRepository;
	private final UserRepository userRepository;
	private final PlanRepository planRepository;
	@Autowired
	public ManageServiceImpl(EmployeeRepository employeeRepository,PlanRepository planRepository
							,UserRepository userRepository) {
		this.empRepository = employeeRepository;
		this.planRepository = planRepository;
		this.userRepository = userRepository;
	}
	
	@Override
	public void employeeRegister(String workNumber, String password) {
		Employee employee = new Employee(workNumber, password);
		employee.setType(workNumber.toCharArray()[0]-'0');
		employee.setStartDate(CurrentDay.currentDay);
		employee.setModifyDate(new Date(employee.getStartDate().getTime() + 3*30*24*60*60*1000));
		empRepository.save(employee);
	}
	
	@Override
	public Employee employeeLogin(String workNumber, String password) {
		return empRepository.findEmployeeByWorkNumberAndPassword(workNumber,password);
	}

	@Override
	public String passwordModify(String workNumber, String newPassword) {
		Employee employee = empRepository.findOne(workNumber);
		String regex = "\\d+[a-zA-Z]+[_]+";
		if(employee == null){
			return "密码格式不对";
		}
		if(newPassword.matches(regex) || newPassword.length()>=8 || newPassword.length()<=16){
			employee.setPassword(newPassword);
			employee.setModifyDate(new Date(CurrentDay.currentDay.getTime() + 3*30*24*60*60*1000));
			empRepository.saveAndFlush(employee);
			return "成功！";
		}else {
			return "失败！";
		}
	}

	
	//员工修改
	@Override
	public String modify(String workNumber,String password) {
		Employee employee = empRepository.findOne(workNumber);
		if(employee.getPassword().equals(password)){
			return "密码与之前三次一样";
		}
		if(password.contains(workNumber)){
			return "密码中不能包含有员工号";
		}
		employee.setWorkNumber(workNumber);
		employee.setPassword(password);
		empRepository.saveAndFlush(employee);
		return "修改完成！";
	}

	//员工信息删除
	@Override
	public String delete(String workNumber) {
		Employee employee = empRepository.findOne(workNumber);
		if(employee == null){
			return "没有这个用户";
		}
		empRepository.delete(employee);
		return "删除成功！";
	}


	
	//执行计划  和  恢复计划其实是一样的
	@Override
	public String planExecute(Plan plan) {
		
		//把当前正在执行的计划的状态变为已执行
		if(planRepository.findPlanByExecuteState(1) != null){
			Plan currentPlan = planRepository.findPlanByExecuteState(1);
			currentPlan.setExecuteState(3);
			planRepository.saveAndFlush(currentPlan);
		}
		
		Constant.currentDepositRate = plan.getCurrentDepositRate();
		Constant.fixedDepositRate = plan.getFixedDepositRate();
		Constant.fixAndDepositRate = plan.getFixAndDepositRate();
		Constant.LOANRATE = plan.getLoanRate();
		plan.setExecuteState(1);
		Date date;
		try {
			date = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").parse(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(CurrentDay.currentDay));
		} catch (ParseException e) {
			return "时间格式不正确！";
		}
		//plan.setExecuteDate(date);
		planRepository.saveAndFlush(plan);//执行的计划保存
		return "执行成功";
	}
	
	public void deletePlan(Plan plan){
		planRepository.delete(plan);
	}
	
	
	//创建计划
	@Override
	public String createPlan(Date executeDate, Double currentDepositRate, Double fixedDepositRate,
							Double loanRate) {
		List<Plan> plans = planRepository.findAll();
		if(CurrentDay.currentDay.getTime() > executeDate.getTime()){
			return "计划必须大于今天！";
		}
		if(planRepository.findOne(executeDate) != null){
			return "该天已存在计划！";
		}
		if(currentDepositRate > fixedDepositRate){
			return "你家活期利率比定期利率还大？？";
		}
		if(currentDepositRate>1.0 || fixedDepositRate>1.0 || loanRate>1.0){
			return "利率不能大于1啊！";
		}
		if(currentDepositRate<0.0 || fixedDepositRate<0.0 || loanRate<0.0){
			return "利率不能为负！";
		}
		for(Plan p:plans){
			if(p.getExecuteState() == 2){
				return "已存在一个未执行计划！";
			}
		}
		Plan plan = new Plan(executeDate, currentDepositRate, fixedDepositRate, loanRate);
		plan.setExecuteState(2);
		planRepository.save(plan);
		return "创建计划成功！";
	}
	
	
	//计划修改
	@Override
	public String dataModify(Plan plan,Date executeDate, Double currentDepositRate, Double fixedDepositRate,
			 Double loanRate) {
		if(executeDate.before(CurrentDay.currentDay)){
			return "计划必须在今天之后！";
		}
		if(planRepository.findOne(executeDate) != null){
			return "该天已存在计划！";
		}
		if(currentDepositRate > fixedDepositRate){
			return "你家活期利率比定期利率还大？？";
		}
		if(currentDepositRate>1.0 || fixedDepositRate>1.0 || loanRate>1.0){
			return "利率不能大于1啊！";
		}
		if(currentDepositRate<0.0 || fixedDepositRate<0.0 || loanRate<0.0){
			return "利率不能为负！";
		}
		plan.setCurrentDepositRate(currentDepositRate);
		plan.setExecuteDate(executeDate);
		plan.setFixedDepositRate(fixedDepositRate);
		plan.setLoanRate(loanRate);
		planRepository.saveAndFlush(plan);
		return "计划修改成功！";
	}

	//网上银行的登录
	@Override
	public String userLogin(String name, String password) {
		if(userRepository.findUserByNameAndPassword(name, password) == null){
			return "账号密码错误";
		}
		return "登录成功";
	}

	//网上银行的注册
	@Override
	public String userRegister(String idNum,String name, String password, String confirmPassword) {
		String regex = "[a-zA-Z0-9]{1,16}";
		if(idNum.length() != 18){
			return "身份证长度错误";
		}
		if(!name.matches(regex)){
			return "用户名格式错误，请输入小于16位的数字字母下划线";
		}
		if(password.length() > 16){
			return "密码格式错误，请小于16位";
		}
		if(!password.equals(confirmPassword)){
			return "两次输入的密码不一致";
		}
		User user = new User(idNum, name, password);
		userRepository.save(user);
		return "注册成功";
	}





}
