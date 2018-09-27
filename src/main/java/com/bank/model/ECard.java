package com.bank.model;

import java.io.Serializable;
import java.util.Set;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;

//更改了Ecard的构造方法
//密码长度为6
@Entity
public class ECard implements Serializable{
	private static final long serialVersionUID = 1L;
	
	/**
	 * 补办一卡通时，距离申请挂失时间间隔
	 * 大于7天可以补办，取消挂失没有时间限制
	 */
	private Long deadTime;
	
	
	/**
	 * 状态。0表示不存在或者注销状态，1表示正常存在，2表示挂失状态。
	 */
	private Integer state;
	/**
	 * 一卡通账号用户名
	 * 以1开头，10位
	 */
	@Id
	@Column(nullable = false,length = 10)
	private String accountName;
	
	
	/**
	 * 密码
	 * 长度为6位
	 */
	@Column(nullable = false,length = 6)
	private String password;
	
	
	/**
	 * 用户
	 * 1对1
	 */
	@OneToOne
	private User user;
	/**
	 * 子用户列表
	 * 一对多
	 */
	@OneToMany(mappedBy = "eCard",fetch=FetchType.EAGER)//这样设置是防止懒加载异常。懒加载是，数据只有在真正使用的时候才会访问数据库。
	private Set<SubCredit> subCredits;
	
	
	/**
	 * 记录
	 * 一个一卡通多条记录
	 */
	@OneToMany(mappedBy = "eCard",fetch=FetchType.EAGER)
	private Set<Record> records;
	
	public ECard() {}
	public ECard(String accountName,String password){
		this.accountName = accountName;
		this.password = password;
	}
	
	
	public String getAccountName() {
		return accountName;
	}
	public void setAccountName(String accountName) {
		this.accountName = accountName;
	}
	public String getPassword() {
		return password;
	}
	public void setPassword(String password) {
		this.password = password;
	}
	public Set<SubCredit> getSubCredits() {
		return subCredits;
	}
	public void setSubCredits(Set<SubCredit> subCredits) {
		this.subCredits = subCredits;
	}
	public void setDeadTime(Long deadTime) {
		this.deadTime = deadTime;
	}
	public Long getDeadTime() {
		return deadTime;
	}
	public void setState(Integer state) {
		this.state = state;
	}
	public Integer getState() {
		return state;
	}
	public User getUser() {
		return user;
	}
	public void setUser(User user) {
		this.user = user;
	}
	public void setRecords(Set<Record> records) {
		this.records = records;
	}
	public Set<Record> getRecords() {
		return records;
	}
}
