package com.bank.service;

import java.util.Date;

import com.bank.model.Employee;
import com.bank.model.Plan;

public interface ManageService {
	/**
	 * 所有部门的注册
	 * @param workNumber
	 * @param password
	 * @return
	 */
	void employeeRegister(String workNumber,String password);
	/**
	 * 所有部门的登录
	 * @param workNumber
	 * @param password
	 * @return
	 */
	Employee employeeLogin(String workNumber,String password);
	
	/**
	 * 用户网上银行的登录
	 * @param name
	 * @param password
	 * @return
	 */
	String userLogin(String name,String password);
	
	/**
	 * 用户网上银行的注册
	 * @param name
	 * @param password
	 * @param confirmPassword
	 * @return
	 */
	String userRegister(String idNum,String name,String password,String confirmPassword);
	
	/**
	 * 所有部门密码的修改
	 * @param workNumber
	 * @param newPassword
	 * @return
	 */
	String passwordModify(String workNumber,String newPassword);
	/**
	 * 对其他部门的修改
	 * @param workNumber
	 * @return
	 */
	String modify(String workNumber,String password);
	/**
	 * 对其他部门的删除
	 * @param workNumber
	 * @return
	 */
	String delete(String workNumber);
	/**
	 * 建立计划
	 * @return
	 */
	String createPlan(Date executeDate,Double currentDepositRate,Double fixedDepositRate,Double loanRate);
	/**
	 * 核心数据修改
	 * @return
	 */
	String dataModify(Plan plan,Date executeDate,Double currentDepositRate,Double fixedDepositRate,Double loanRate);
	/**
	 * 计划执行与恢复
	 * @return
	 */
	String planExecute(Plan plan);
	/**
	 * 计划的删除
	 * @param plan
	 * @return
	 */
	void deletePlan(Plan plan);
}
