package com.bank.service;

import java.util.List;

import com.bank.model.User;

public interface LoanService {
	/**
	 * 住房贷款
	 * @param money
	 * @param loanDate
	 * @param backStyle
	 * @param creditNum
	 * @return
	 */
	String houseLoan(String idNum,Double money,Integer loanDate,Integer backStyle,String creditNum);
	
	/**
	 * 个人贷款
	 * @param user
	 * @param money
	 * @param loanDate
	 * @param backStyle
	 * @param creditNum
	 * @return
	 */
	String personLoan(String idNum,Double money,Integer loanDate,String creditNumLoan,String creditNumSave);
	
	/**
	 * 学生贷款
	 * @param user
	 * @param money
	 * @param loanDate
	 * @param creditNum
	 * @return
	 */
	String studentLoan(String idNum0,String idNum1,String idNum2,Double money, Integer loanDate,String creditNum);     
	/**
	 * 还款
	 * @param user
	 * @param creditNum
	 * @return
	 */
	String back(String creditNum);
	/**
	 * 申请展期
	 * @param user
	 * @param date
	 * @return
	 */
	String expand(String idNum,Long date);
	/**
	 * 逾期状态。
	 * @param user
	 * @return
	 */
	List<User> overdue();
	
	/**
	 * 这个方法是写崩用的
	 * @param idNum
	 * @return
	 */
	User idTest(String idNum);
}
