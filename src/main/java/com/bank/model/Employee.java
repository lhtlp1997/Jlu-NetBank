package com.bank.model;

import java.io.Serializable;
import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

@Entity
public class Employee implements Serializable{
	private static final long serialVersionUID = 1L;
	
	
	/**
	 * 员工工号
	 * 5位。第一位数字代表所属的部门。1、储蓄部门，2、信用卡部门，3、贷款部门，4、系统管理部门
	 */
	@Id
	@Column(nullable = false,length=5)
	private String workNumber;
	
	
	/**
	 * 员工密码
	 * 8-16位，必须包括字母、数字和其他字符，不能有员工号
	 */
	@Column(nullable = false)
	private String password;

	
	/**
	 * 管理员的种类
	 */
	private Integer type;
	/**
	 * 规定的修改日期
	 */
	@Temporal(TemporalType.DATE)
	private Date modifyDate;
	
	
	/**
	 * 之前修改的密码
	 */
	public static int[][] previousPassword;
		
	
	/**
	 * 密码修改的时间（注册也认为是一次密码的修改）
	 */
	@Temporal(TemporalType.DATE)
	private Date startDate;

	public Employee() {}
	public Employee(String workNumber, String password){
		this.workNumber = workNumber;
		this.password = password;
	}
	public String getWorkNumber() {
		return workNumber;
	}
	public void setWorkNumber(String workNumber) {
		this.workNumber = workNumber;
	}
	public String getPassword() {
		return password;
	}
	public void setPassword(String password) {
		this.password = password;
	}
	public void setType(Integer type) {
		this.type = type;
	}
	public Integer getType() {
		return type;
	}
	public void setStartDate(Date startDate) {
		this.startDate = startDate;
	}
	public Date getStartDate() {
		return startDate;
	}
	public Date getModifyDate() {
		return modifyDate;
	}
	public void setModifyDate(Date modifyDate) {
		this.modifyDate = modifyDate;
	}
}
