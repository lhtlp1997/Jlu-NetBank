package com.bank.model;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.OneToOne;

//去掉了password
@Entity(name = "bank_user")
public class User implements Serializable{
	private static final long serialVersionUID = 1L;

	/**
	 * 身份证号18位，主键 不能为空
	 */
	@Id
	@Column(length = 18)
	private String idNum;
	
	
	/**
	 * 用户名 不能为空
	 */
	@Column(nullable = false,length = 16)
	private String name;
	
	
	/**
	 * 网上银行密码,不超过16位
	 */
	@Column(length = 16)
	private String password;
	
	
	/**
	 * 一个用户有一个一卡通
	 */
	@OneToOne(mappedBy = "user")
	private ECard eCard;
	
	
	/**
	 * 一个用户只可以有一个贷款业务
	 */
	@OneToOne(mappedBy="user")
	private Loan loan;
	
	
	/**
	 * 住址
	 */
	private String address;
	
	
	/**
	 * 电话号码
	 */
	private String telephone;
	
	
	/**
	 * 抵押物（目前知道的是 房子 和 存款）
	 */
	private String pledge;
	
	
	/**
	 * 抵押物的价格
	 */
	private Double plegePrice;
	
	
	public User() {}
	
	public User(String idNum,String name,String password){
		this.idNum = idNum;
		this.name = name;
		this.password = password;
	}
	
	public User(String idNum, String name, String adress,String telephone) {
		this.idNum = idNum;
		this.name = name;
		this.address = adress;
		this.telephone = telephone;
	}

	public String getIdNum() {
		return idNum;
	}

	public void setIdNum(String idNum) {
		this.idNum = idNum;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getAddress() {
		return address;
	}

	public void setAddress(String address) {
		this.address = address;
	}

	public String getTelephone() {
		return telephone;
	}

	public void setTelephone(String telephone) {
		this.telephone = telephone;
	}

	public String getPledge() {
		return pledge;
	}

	public void setPledge(String pledge) {
		this.pledge = pledge;
	}

	public Double getPlegePrice() {
		return plegePrice;
	}

	public void setPlegePrice(Double plegePrice) {
		this.plegePrice = plegePrice;
	}
	
	public void setLoan(Loan loan) {
		this.loan = loan;
	}
	public Loan getLoan() {
		return loan;
	}
	public ECard geteCard() {
		return eCard;
	}
	public void seteCard(ECard eCard) {
		this.eCard = eCard;
	}
	public String getPassword() {
		return password;
	}
	public void setPassword(String password) {
		this.password = password;
	}
}
