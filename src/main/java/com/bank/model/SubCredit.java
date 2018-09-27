package com.bank.model;

import java.io.Serializable;
import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

//改了个方法
@Entity
public class SubCredit implements Serializable{
	private static final long serialVersionUID = 1L;
	/**
	 * 存钱时间
	 */
	@Temporal(TemporalType.DATE)
	private Date inTime;
	
	
	/**
	 * 取钱时间
	 */
	@Temporal(TemporalType.DATE)
	private Date outTime;
	
	
	/**
	 * 存期
	 * 1表示1年，5表示5年
	 */
	private Integer isOneYear;
	
	/**
	 * 各个储蓄类型的利息
	 */
	private Double rate;
	
	/**
	 * 注销状态。0表示不存在或者注销状态，1表示正常存在，2表示挂失状态
	 */
	private Integer subcreditState;

	
	/**
	 * 子账号账号
	 * 五位（需正则判断）
	 */
	@Id
	@Column(nullable = false,length = 5)
	private String creditNum;
	
	
	/**
	 * 币种
	 * 有5种，需要根据账号首位来获得
	 * 1人民币、2美元、3港元、4日元、5欧元
	 */
	private Integer currency;
	
	
	/** 储蓄种类 
	 * 通过账号第二位来获得 储蓄类型 
	 * 1: 活期储蓄 2：整存整取定期储蓄 3：定活两便储蓄  0:带表错误类型*/
	private Integer depositType;
	
	
	/**
	 * 余额
	 */
	private Double balance;
	
	
	/**
	 * 多个子账户对应一个一卡通
	 */
	@ManyToOne
	private ECard eCard;
	
	
	/**
	 * 是否续存
	 * 1表示续存，-1表示不续存
	 */
	private Integer isContinue;
	
	
	public SubCredit() {}
	public Date getInTime() {
		return inTime;
	}

	public void setInTime(Date inTime) {
		this.inTime = inTime;
	}

	public Date getOutTime() {
		return this.outTime;
	}

	public void setOutTime(Date outTime) {
		this.outTime = outTime;
	}


	public SubCredit(String creditNum){
		this.creditNum = creditNum;
	}
	
	public String getCreditNum() {
		return creditNum;
	}

	public void setCreditNum(String creditNum) {
		this.creditNum = creditNum;
	}

	public Integer getCurrency() {
		return currency;
	}

	public void setCurrency(String creditNum) {
		this.currency = new Integer(creditNum.toCharArray()[0]-'0');
	}

	public Integer getDepositType() {
		return this.depositType;
	}

	public void setDepositType(String creditNum) {
		this.depositType = new Integer(creditNum.toCharArray()[1]-'0');
	}
	public Double getBalance() {
		return balance;
	}

	public void setBalance(Double balance) {
		this.balance = balance;
	}
	public Integer getSubcreditState() {
		return subcreditState;
	}
	public void setSubcreditState(Integer subcreditState) {
		this.subcreditState = subcreditState;
	}
	public void seteCard(ECard eCard) {
		this.eCard = eCard;
	}
	public ECard geteCard() {
		return eCard;
	}
	public void setIsContinue(Integer isContinue) {
		this.isContinue = isContinue;
	}
	public Integer getIsContinue() {
		return isContinue;
	}
	public void setRate(Double rate) {
		this.rate = rate;
	}
	public Double getRate() {
		return rate;
	}
	public Integer getIsOneYear() {
		return isOneYear;
	}
	public void setIsOneYear(Integer isOneYear) {
		this.isOneYear = isOneYear;
	}
	
	//新加方法，用于得到当前账号的起存金额，直接根据储蓄类型进行判断即可
	public int getMinInputMoney() {
		switch(depositType){
		case 1:return 1;
		case 2:return 100;
		case 3:return 50;
		default:return 0;
		}		
	}
}
