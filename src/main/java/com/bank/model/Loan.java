package com.bank.model;

import java.io.Serializable;
import java.util.Date;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToOne;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

//guarantee属性
@Entity
public class Loan implements Serializable{
	private static final long serialVersionUID = 1L;
	
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;
	
	/**
	 * 贷款总金额
	 */
	private Double money;
	
	
	/**
	 * 利率（默认为月）
	 * 这里认为利率应该是主键
	 */
	private Double rate;
	
	
	/**
	 * 子账户对象（需要的部分为，整存整取，存本取息）
	 * 一对一关系
	 */
	@OneToOne
	private SubCredit subCredit; 
	
	
	/**
	 * 用户对象（需要的是房子和房子的价格）
	 */
	@OneToOne
	private User user;
	
	
	/**
	 * 借款的总时间（默认为月）
	 */
	private Integer loanDate;
	
	
	/**
	 * 借款时间上限
	 */
	private Integer loanTop;
	
	
	/**
	 * 借款时间下限
	 */
	private Double loanLimit;
	
	
	/**
	 * 借款开始日
	 */
	@Temporal(TemporalType.DATE)
	private Date startDate;
	
	
	/**
	 * 借款结束日
	 */
	@Temporal(TemporalType.DATE)
	private Date endDate;
	
	/**
	 * 还款日
	 */
	@Temporal(TemporalType.DATE)
	private Date backDate;
	
	
	/**
	 * 当前应发款
	 */
	private Double currentBack;
	
	
	/**
	 * 贷款发放的金额（发放的方式）
	 */
	private Double givenMoney;
	
	
	/**
	 * 借款上限
	 */
	private Double moneyTop; 
	
	
	/**
	 * 借款下限
	 */
	private Double moneyFloor;
	
	
	/**
	 * 还款方式，1是利随本清，2是分期付息一次还本，3是等额本息，4是等额本金
	 */
	private Integer backStyle;
	
	
	/**
	 * 逾期状态。1表示逾期了，-1表示没有逾期。如果逾期了，rate = rate * 1.5，当前要还的钱=之前要还的钱*(1+罚息)
	 */
	private Integer overdueStyle;
	
	
	/**
	 * 展期状态。1表示展期了。-1表示没有展期
	 */
	private Integer expandDate;
	
	
	/**
	 * 逾期后的利率
	 */
	private Double overdueRate;
	
	
	/**
	 * 利率改变的日子（一年一变）
	 */
	private Date rateChangeDate;
	/**
	 * 担保人1
	 */
	@OneToOne
	private User guarantee1;
	
	
	/**
	 * 担保人2
	 */
	@OneToOne
	private User guarantee2;
	
	
	public Loan() {}
	public Loan(User user,Double money,Integer loanDate,Double givenMoney){
		this.user = user;
		this.money = money;
		this.loanDate = loanDate;
		this.givenMoney = givenMoney;
		this.expandDate = -1;
		this.overdueStyle = -1;
	}
	public Loan(User user,Double money,Integer loanDate,Double givenMoney,Integer backStyle){
		this.user = user;
		this.money = money;
		this.loanDate = loanDate;
		this.givenMoney = givenMoney;
		this.backStyle = backStyle;
		this.expandDate = -1;
		this.overdueStyle = -1;
	}
	
	public SubCredit getSubCredit() {
		return this.subCredit;
	}
	public void setSubCredit(SubCredit subCredit) {
		this.subCredit = subCredit;
	}
	public User getUser() {
		return user;
	}
	public void setUser(User user) {
		this.user = user;
	}
	public Integer getLoanDate() {
		return loanDate;
	}
	public void setLoanDate(Integer loanDate) {
		this.loanDate = loanDate;
	}
	public Integer getLoanTop() {
		return loanTop;
	}
	public void setLoanTop(Integer loanTop) {
		this.loanTop = loanTop;
	}
	public Double getLoanLimit() {
		return loanLimit;
	}
	public void setLoanLimit(Double loanLimit) {
		this.loanLimit = loanLimit;
	}
	public Date getStartDate() {
		return startDate;
	}
	public void setStartDate(Date startDate) {
		this.startDate = startDate;
	}
	public Date getEndDate() {
		return endDate;
	}
	public void setEndDate(Date endDate) {
		this.endDate = endDate;
	}
	public Double getMoney() {
		return money;
	}
	public void setMoney(Double money) {
		this.money = money;
	}
	public Double getCurrentBack() {
		return currentBack;
	}
	public void setCurrentBack(Double currentBack) {
		this.currentBack = currentBack;
	}
	public Double getGivenMoney() {
		return givenMoney;
	}
	public void setGivenMoney(Double givenMoney) {
		this.givenMoney = givenMoney;
	}
	public Double getMoneyTop() {
		return moneyTop;
	}
	public void setMoneyTop(Double moneyTop) {
		this.moneyTop = moneyTop;
	}
	public Double getMoneyFloor() {
		return moneyFloor;
	}
	public void setMoneyFloor(Double moneyFloor) {
		this.moneyFloor = moneyFloor;
	}
	public Integer getBackStyle() {
		return backStyle;
	}
	public void setBackStyle(Integer backStyle) {
		this.backStyle = backStyle;
	}
	public Integer getOverdueStyle() {
		return overdueStyle;
	}
	public void setOverdueStyle(Integer overdueStyle) {
		this.overdueStyle = overdueStyle;
	}
	public Integer getExpandDate() {
		return expandDate;
	}
	public void setExpandDate(Integer expandDate) {
		this.expandDate = expandDate;
	}
	public void setRate(Double rate) {
		this.rate = rate;
	}
	public Double getRate() {
		return rate;
	}
	public void setOverdueRate(Double overdueRate) {
		this.overdueRate = overdueRate;
	}
	public Double getOverdueRate() {
		return overdueRate;
	}
	public void setGuarantee1(User guarantee1) {
		this.guarantee1 = guarantee1;
	}
	public User getGuarantee1() {
		return guarantee1;
	}
	public void setGuarantee2(User guarantee2) {
		this.guarantee2 = guarantee2;
	}
	public User getGuarantee2() {
		return guarantee2;
	}
	public Long getId() {
		return id;
	}
	public void setId(Long id) {
		this.id = id;
	}
	public void setBackDate(Date backDate) {
		this.backDate = backDate;
	}
	public Date getBackDate() {
		return backDate;
	}
	public void setRateChangeDate(Date rateChangeDate) {
		this.rateChangeDate = rateChangeDate;
	}
	public Date getRateChangeDate() {
		return rateChangeDate;
	}
}
