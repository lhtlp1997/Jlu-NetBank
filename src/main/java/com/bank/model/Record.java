package com.bank.model;

import java.util.Date;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

@Entity
public class Record {
	//存款
	//取款	
	//转账
	//利息计算
	//贷款发放
	//贷款偿还
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;
	
	/**
	 * 每一条记录对应一笔钱
	 */
	private Double money;
	
	
	@ManyToOne
	private ECard eCard;
	
	/**
	 * 记录种类。
	 * 1、存款		开通一卡通、储蓄（完成）
	 * 2、取款		活期、定期、定活两期（完成）
	 * 3、转账		转账（完成）
	 * 4、利息计算		*******定时任务
	 * 5、贷款发放		定时任务（助学贷款一年发放一次）、借贷时发放
	 * 6、贷款偿还		定时任务、提前还款
	 */
	private Integer type;
	
	/**
	 * 记录产生的时间
	 */
	@Temporal(TemporalType.DATE)
	private Date occurDate;

	
	public Record() {}

	public Record(Date occurDate,Integer type,ECard eCard,Double money){
		this.occurDate = occurDate;
		this.type = type;
		this.eCard = eCard;
		this.money = money;
	}
	
	public Double getMoney() {
		return money;
	}
	public void setMoney(Double money) {
		this.money = money;
	}

	public ECard geteCard() {
		return eCard;
	}

	public void seteCard(ECard eCard) {
		this.eCard = eCard;
	}

	public Integer getType() {
		return type;
	}

	public void setType(Integer type) {
		this.type = type;
	}

	public Date getOccurDate() {
		return occurDate;
	}

	public void setOccurDate(Date occurDate) {
		this.occurDate = occurDate;
	}
	
	public Long getId() {
		return id;
	}
}
