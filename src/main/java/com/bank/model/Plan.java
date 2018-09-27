package com.bank.model;

import java.io.Serializable;
import java.util.Date;

import javax.persistence.Entity;
import javax.persistence.Id;

/**
 * 新建表
 * @author 刘瀚霆
 *
 */
@Entity
public class Plan implements Serializable{
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	
	/**
	 * 计划执行时间
	 */
	@Id
	private Date executeDate;
	
	/**
	 * 执行状态
	 * 1表示正在进行、2表示还没执行、3表示已经结束
	 */
	private Integer executeState;
	
	private Double currentDepositRate;
	private Double fixedDepositRate;
	private Double fixAndDepositRate;
	private Double loanRate;
	
	public Plan() {}
	public Plan(Date executeDate,Double currentDepositRate, Double fixedDepositRate, Double loanRate){
		this.executeDate = executeDate;
		this.currentDepositRate = currentDepositRate;
		this.fixedDepositRate = fixedDepositRate;
		this.fixAndDepositRate = Double.parseDouble(String.format("%.3f", fixedDepositRate * 0.6) );
		this.loanRate = loanRate;
	}
	
	
	public void setExecuteDate(Date executeDate) {
		this.executeDate = executeDate;
	}
	public Date getExecuteDate() {
		return executeDate;
	}
	public Double getCurrentDepositRate() {
		return currentDepositRate;
	}
	public void setCurrentDepositRate(Double currentDepositRate) {
		this.currentDepositRate = currentDepositRate;
	}
	public Double getFixedDepositRate() {
		return fixedDepositRate;
	}
	public void setFixedDepositRate(Double fixedDepositRate) {
		this.fixedDepositRate = fixedDepositRate;
	}
	public Double getLoanRate() {
		return loanRate;
	}
	public void setLoanRate(Double loanRate) {
		this.loanRate = loanRate;
	}
	public Double getFixAndDepositRate() {
		return fixAndDepositRate;
	}
	public void setFixAndDepositRate(Double fixAndDepositRate) {
		this.fixAndDepositRate = fixAndDepositRate;
	}
	public void setExecuteState(Integer executeState) {
		this.executeState = executeState;
	}
	public Integer getExecuteState() {
		return executeState;
	}
}
