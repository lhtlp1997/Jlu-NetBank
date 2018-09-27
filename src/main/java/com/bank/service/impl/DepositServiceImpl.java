package com.bank.service.impl;

import java.util.Date;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.bank.model.ECard;
import com.bank.model.Record;
import com.bank.model.SubCredit;
import com.bank.model.User;
import com.bank.repository.ECardRepository;
import com.bank.repository.RecordRepository;
import com.bank.repository.SubCreditRepository;
import com.bank.repository.UserRepository;
import com.bank.service.DepositService;
import com.bank.util.CaculateTime;
import com.bank.util.Constant;
import com.bank.util.CurrentDay;

@Service
public class DepositServiceImpl implements DepositService {
	private final SubCreditRepository sc;
	private final UserRepository user;
	private final ECardRepository card;
	private final RecordRepository rec;
	

	@Autowired
	public DepositServiceImpl(SubCreditRepository subCreditRepository, 
						UserRepository user, ECardRepository card,RecordRepository rec) {
		this.sc = subCreditRepository;
		this.user = user;
		this.card = card;
		this.rec = rec;
	}

	public boolean openECard(String accountName,String name,String idNum,String address,String telephone,String password) {
		User u = this.user.findOne(idNum);
		if(card.findOne(accountName) != null){
			return false;
		}
		if(u == null){
			return false;
		}else {
			ECard eCard = new ECard(accountName, password);
			eCard.setUser(u);
			eCard.setState(1);
			card.save(eCard);
			return true;
		}
	}
	
	/** 开通子账户的时候不一定存钱 */
	public boolean openSubcredit(String accountName,String creditNum,Double money){
		ECard eCard = card.findOne(accountName);
		if(eCard == null){
			return false;
		}else if (creditNum.length() != 5 ) {
			return false;
		}
		if(sc.findOne(accountName) != null){
			return false;
		}
		if(money == null){
			money = 0.0;
		}
		else {
			SubCredit subCredit = new SubCredit(creditNum);
			subCredit.setInTime(CurrentDay.currentDay);
			subCredit.setOutTime(CurrentDay.currentDay);
			subCredit.setSubcreditState(1);
			subCredit.setCurrency(creditNum);
			subCredit.setDepositType(creditNum);
			if(subCredit.getCurrency() <1 || subCredit.getCurrency()>5){
				return false;
			}
			if(subCredit.getDepositType()<1 || subCredit.getDepositType()>3){
				return false;
			}
			//设置利率类型
			if(subCredit.getDepositType() == 1){
				subCredit.setRate(Constant.currentDepositRate);
			}
			if(subCredit.getDepositType() == 2){
				subCredit.setRate(Constant.fixedDepositRate);
			}
			if(subCredit.getDepositType() == 3){
				subCredit.setRate(Constant.currentDepositRate);
			}
			
			subCredit.seteCard(eCard);
			subCredit.setBalance(money);
			if(money > 0.0){
				Record record = new Record(CurrentDay.currentDay, 1, eCard,money);
				rec.save(record);
			}
			
			sc.save(subCredit);
		}
		return true;
	}
	
	// 活期存储函数
	@Override
	public String currentDeposit(String creditNum, double money) {
		/** 进来先判断这个子账户为什么状态 */
		SubCredit subCredit = sc.findOne(creditNum);
		if(subCredit == null){
			return "子账户不存在";
		}
		if (subCredit.getSubcreditState() != 1 || subCredit.getDepositType() != 1)
			// 如果该子账户不为活期存储或该子账户不存在则出错返回
			return "子账户类型不为活期账户";
		else {

			if (money < subCredit.getMinInputMoney())// 小于起存点
				return "小于起存点，存入失败";// 失败
			
			//余额可能为空
			if(subCredit.getBalance() == null){
				subCredit.setBalance(money);
			}else {
				subCredit.setBalance(subCredit.getBalance() + money);
			}
			
			subCredit.setInTime(CurrentDay.currentDay);
			subCredit.setOutTime(CurrentDay.currentDay);
		}
		sc.saveAndFlush(subCredit);
		Record record = new Record(CurrentDay.currentDay, 1, subCredit.geteCard(),money);
		rec.save(record);
		return "存入成功";
	}

	//定期存储
	@Override
	public String fixedDeposit(String creditNum, double money, Integer isOneYear,Integer isContinue) {
		SubCredit subCredit = sc.findOne(creditNum);
		if(subCredit == null){
			return "子账户不存在";
		}
		/** 进来先判断这个子账户当前是 什么状态 */
		if (subCredit.getSubcreditState() != 1 || subCredit.getDepositType() != 2)
			// 如果该子账户不为整存整取存储或该子账户不存在则出错返回
			return "子账户类型不为整存整取账户";
		if (subCredit.getIsContinue() != null)// 如果还有账户余额，此时不能存入，也就是说只能整存第一笔钱，必须一次或多次取完之后
			// 才能再次存入
			return "整存整取只能整存一次";// 如果还有余额又想存入，必须新开通整存整取子账户
		else {
			subCredit.setIsOneYear(isOneYear);
			/** 进来首先判断是否是一年期 */
			if (money < subCredit.getMinInputMoney())// 小于起存点
				return "小于起存点，存入失败";// 失败
			
			//余额一定为空
			if(subCredit.getBalance() == null){
				subCredit.setBalance(money);
			}else {
				subCredit.setBalance(subCredit.getBalance() + money);
			}
			
			subCredit.setInTime(CurrentDay.currentDay);
			subCredit.setOutTime(CurrentDay.currentDay);
			subCredit.setIsContinue(isContinue);
		}
		sc.saveAndFlush(subCredit);
		Record record = new Record(CurrentDay.currentDay, 1, subCredit.geteCard(),money);
		rec.save(record);
		return "存入成功";
	}

	//定活两便存储
	@Override
	public String fixAndCurDeposit(String creditNum, double money) {
		SubCredit subCredit = sc.findOne(creditNum);
		if(subCredit == null){
			return "子账户不存在";
		}
		if (subCredit.getSubcreditState() != 1 || subCredit.getDepositType() != 3)
			// 如果该子账户不为定活两便账户或该子账户不存在则出错返回
			return "子账户类型不为定活两便账户";
		else {

			if (money < subCredit.getMinInputMoney())// 小于起存点
				return "小于起存点，存入失败";// 失败
			
			//余额可能为空
			if(subCredit.getBalance() == null){
				subCredit.setBalance(money);
			}else {
				subCredit.setBalance((double)subCredit.getBalance() + money);
			}	
			
			subCredit.setInTime(CurrentDay.currentDay);
			subCredit.setOutTime(CurrentDay.currentDay);
		}
		sc.saveAndFlush(subCredit);
		Record record = new Record(CurrentDay.currentDay, 1, subCredit.geteCard(),money);
		rec.save(record);
		return "存入成功";
	}
	
	
	public String draw(String idNum,String creditNum,double money){
		SubCredit subCredit = sc.findOne(creditNum);
		String str = "";
		if(subCredit == null){
			return "对应子账户不存在！";
		}
		User u = subCredit.geteCard().getUser();
		if(u != user.findOne(idNum)){
			return "身份证与子账户不匹配！";
		}
		if(subCredit.getDepositType() == 1){
			str = drawFromCurrentDeposit(idNum, creditNum, money);
		}
		if(subCredit.getDepositType() == 2){
			str = drawFromFixedDeposit(idNum, creditNum, money);
		}
		if(subCredit.getDepositType() == 3){
			str = drawFromFixedAndCurDeposit(idNum, creditNum, money);
		}
		return str;
	}
	
	
	/** 从活期账户取款 */
	//加上利息的计算
	public String drawFromCurrentDeposit(String idNum, String creditNum, double money) {
		SubCredit subCredit = sc.findOne(creditNum);
		if (subCredit.getSubcreditState() != 1)
			return "子账户状态不允许取款";
		if(subCredit.getDepositType()!=1)
		    return "账户类型不为活期账户";
		Date currentTime=CurrentDay.currentDay;
	
		
		//判断是否有利息改变，如果改变，变全部按照6月30日计算（如果没到6月30号之前没有取钱，定时任务里自动计息一次）
		//没有改变
		if(subCredit.getRate() == Constant.currentDepositRate){
			
			//如果不是全部支取，就按照存入的时候的利率计算
			if(money != subCredit.getBalance()){
				double rate = subCredit.getBalance() * subCredit.getRate()/30.0 *
						CaculateTime.totalDays(subCredit.getOutTime().getTime(), currentTime.getTime());
				subCredit.setBalance(subCredit.getBalance() + rate);
				subCredit.setOutTime(currentTime);
				Record r = new Record(currentTime, 4, subCredit.geteCard(), rate);
				rec.save(r);
				sc.saveAndFlush(subCredit);
			//全部支取，按照支取那一天的算
			}else {
				double rate = subCredit.getBalance() * Constant.currentDepositRate/30.0 *
						CaculateTime.totalDays(subCredit.getOutTime().getTime(), currentTime.getTime())/12;
				subCredit.setBalance(subCredit.getBalance() + rate);
				subCredit.setOutTime(currentTime);
				Record r = new Record(currentTime, 4, subCredit.geteCard(), rate);
				rec.save(r);
				sc.saveAndFlush(subCredit);
			}
			
		//利息有改变，用6月30日的计算
		}else {
			//全部支取操作也是这样的
			double rate = subCredit.getBalance() * Constant.currentDepositRate/30.0 *
					CaculateTime.totalDays(subCredit.getOutTime().getTime(), currentTime.getTime())/12;       
			Record r = new Record(currentTime, 4, subCredit.geteCard(), rate);
			rec.save(r);
			subCredit.setBalance(subCredit.getBalance() + rate);
			subCredit.setOutTime(currentTime);
			sc.saveAndFlush(subCredit);
		}	
		
		
		if (subCredit.getBalance() <= money)// 加上利息还不够取钱
			return "余额不足";
		else {
			subCredit.setBalance(subCredit.getBalance()- money);
			subCredit.setOutTime(currentTime);// 设置取出钱的时间
			sc.saveAndFlush(subCredit);
			Record record = new Record(CurrentDay.currentDay, 2, subCredit.geteCard(),money);
			rec.save(record);
			return "取款成功！";
		}
	}

	
	
	/**从整存整取账户取款*/
	public String drawFromFixedDeposit(String idNum,String creditNum,double money) {
		SubCredit subCredit = sc.findOne(creditNum);
		if (subCredit.getSubcreditState() != 1)
			return "子账户状态不允许取款";
		if(subCredit.getDepositType()!=2)
			return "账户类型不为整存整取账户";
		
		Date currentTime = CurrentDay.currentDay;
		
		
		// 一年期类型、不续存
		if ( subCredit.getIsOneYear()==1 && subCredit.getIsContinue() == -1) {
			if(money > subCredit.getBalance()){
				return "初始取钱数额不能大于：" + subCredit.getBalance();
			}
			// 存期为一年，但是取钱时间未满一年
			if (CaculateTime.totalDays(subCredit.getInTime().getTime(), currentTime.getTime()) < 365) {
			
				
				//这个是提取的部分，计算活期利率
				double rate = CaculateTime.totalDays(subCredit.getOutTime().getTime(), currentTime.getTime())
							/ 30.0 * Constant.currentDepositRate * money;
				subCredit.setBalance(subCredit.getBalance() + rate);
				subCredit.setOutTime(currentTime);
				sc.saveAndFlush(subCredit);
				Record r = new Record(currentTime, 4, subCredit.geteCard(), rate);
				rec.save(r);
				
				if (subCredit.getBalance() + rate < money ) // 加上利息还不够取钱
					/** 操作失败 */
					return "余额不足";
				else {
					subCredit.setBalance(subCredit.getBalance()- money);
					subCredit.setOutTime(currentTime);// 设置取出钱的时间
					sc.saveAndFlush(subCredit);
					Record record = new Record(CurrentDay.currentDay, 2, subCredit.geteCard(),money);
					rec.save(record);
					return "取款成功！";
				}
			}

			// 存期为一年，但是取钱时间超过一年
			else {
				//先计算利息+本金
				double rateBefore = 12*subCredit.getRate()*subCredit.getBalance();
				subCredit.setBalance(subCredit.getBalance() + rateBefore);
				sc.saveAndFlush(subCredit);
				//再计算一年后的活期利息
				Date oneYear = new Date(subCredit.getInTime().getTime() + (long)365*24*60*60*1000);
				Double rate = subCredit.getBalance() * CaculateTime.totalDays(oneYear.getTime(), currentTime.getTime()) /30.0 * Constant.currentDepositRate;

				subCredit.setBalance(subCredit.getBalance() + rate); 
				sc.saveAndFlush(subCredit);
				Record r = new Record(currentTime, 4, subCredit.geteCard(), rate + rateBefore);
				rec.save(r);

				if (subCredit.getBalance()< money) // 加上利息还不够取钱
					/** 操作失败 */
					return "余额不足";
				else {// 操作成功
					subCredit.setBalance(subCredit.getBalance()- money);
					subCredit.setOutTime(currentTime);// 设置取出钱的时间
					subCredit.setDepositType("11");
					sc.saveAndFlush(subCredit);
					Record record = new Record(CurrentDay.currentDay, 2, subCredit.geteCard(),money);
					rec.save(record);
					return "取款成功！";
				}
			}
		}

		
		
		//存1年，续存（其实上就是两年）
		if ( subCredit.getIsOneYear()==1 && subCredit.getIsContinue() == 1) {
			if(money > subCredit.getBalance()){
				return "初始取钱数额不能大于：" + subCredit.getBalance();
			}
			// 如果是再存钱是选择的是两年期，但是没有存到两年，
			if (CaculateTime.totalDays(subCredit.getInTime().getTime(), currentTime.getTime()) < 730) {
				// 存期为两年，但是取钱时间未满两年
				
				//这个是提取的部分，计算活期利率
				double rate = CaculateTime.totalDays(subCredit.getOutTime().getTime(), currentTime.getTime())
							/ 30.0 * Constant.currentDepositRate * money;
				
				subCredit.setBalance(subCredit.getBalance() + rate);
				subCredit.setOutTime(currentTime);
				sc.saveAndFlush(subCredit);
				Record r = new Record(currentTime, 4, subCredit.geteCard(), rate);
				rec.save(r);
				
				if (subCredit.getBalance() + rate < money ) // 加上利息还不够取钱
					/** 操作失败 */
					return "余额不足";
				else {
					subCredit.setBalance(subCredit.getBalance()- money);
					subCredit.setOutTime(currentTime);// 设置取出钱的时间
					sc.saveAndFlush(subCredit);
					Record record = new Record(CurrentDay.currentDay, 2, subCredit.geteCard(),money);
					rec.save(record);
					return "取款成功！";
				}
			}

			// 存取为两年，但是取钱时间超过两年
			else {
				//先计算利息+本金
				double rateBefore = 24*subCredit.getRate()*subCredit.getBalance();
				subCredit.setBalance(subCredit.getBalance() + rateBefore);
				sc.saveAndFlush(subCredit);
				//再计算两年后的活期利息
				Date twoYear = new Date(subCredit.getInTime().getTime() + (long)730*24*60*60*1000);
				
				double rate = subCredit.getBalance() * Constant.currentDepositRate/30.0 *
						CaculateTime.totalDays(twoYear.getTime(), currentTime.getTime());
				subCredit.setBalance(subCredit.getBalance() + rate);    
				subCredit.setOutTime(currentTime);
				sc.saveAndFlush(subCredit);
				//利息记录

				Record r = new Record(currentTime, 4, subCredit.geteCard(), rate + rateBefore);
				rec.save(r);

				if (subCredit.getBalance()< money) // 加上利息还不够取钱
					/** 操作失败 */
					return "余额不足";
				else {// 操作成功
					subCredit.setBalance(subCredit.getBalance()- money);
					subCredit.setOutTime(currentTime);// 设置取出钱的时间
					subCredit.setDepositType("11");
					sc.saveAndFlush(subCredit);
					Record record = new Record(CurrentDay.currentDay, 2, subCredit.geteCard(),money);
					rec.save(record);
					return "取款成功！";
				}
			}
		}
		
		
		
		
		//存期为五年期、不续存
		if ( subCredit.getIsOneYear()== 5 && subCredit.getIsContinue() == -1) {
			
			if(money > subCredit.getBalance()){
				return "初始取钱数额不能大于：" + subCredit.getBalance();
			}
			
			//五年期但是未到五年就进行了取款
			if (CaculateTime.totalDays(subCredit.getInTime().getTime(), currentTime.getTime()) < 1825) {
				

				//提取的部分按当日活期利率算
				double rate = CaculateTime.totalDays(subCredit.getOutTime().getTime(), currentTime.getTime())
						/ 30.0 * Constant.currentDepositRate * money;
				subCredit.setBalance(subCredit.getBalance() + rate);
				subCredit.setOutTime(currentTime);
				sc.saveAndFlush(subCredit);
				Record r = new Record(currentTime, 4, subCredit.geteCard(), rate);
				rec.save(r);
				if (subCredit.getBalance() + rate < money) // 加上利息还不够取钱
					return "余额不足";
				else {
					subCredit.setBalance(subCredit.getBalance()- money);
					subCredit.setOutTime(currentTime);// 设置取出钱的时间
					sc.saveAndFlush(subCredit);
					Record record = new Record(CurrentDay.currentDay, 2, subCredit.geteCard(),money);
					rec.save(record);
					return "取款成功！";
				}
			}
			
			// 存取五年，取钱时间超过五年
			else {
				//之计一次利息
				//先计算利息+本金
				double rateBefore = 60*subCredit.getRate()*subCredit.getBalance();
				subCredit.setBalance(subCredit.getBalance() + rateBefore);
				sc.saveAndFlush(subCredit);
				//再计算五年后的活期利息
				Date fiveYear = new Date(subCredit.getInTime().getTime() + (long)1825*24*60*60*1000);
				double rate = subCredit.getBalance() * CaculateTime.totalDays(fiveYear.getTime(), CurrentDay.currentDay.getTime()) /30.0 * Constant.currentDepositRate;
				subCredit.setBalance(subCredit.getBalance() + rate);     
				sc.saveAndFlush(subCredit);
				Record r = new Record(currentTime, 4, subCredit.geteCard(), rate + rateBefore);
				rec.save(r);

				if (subCredit.getBalance()< money) // 加上利息还不够取钱
					/** 操作失败 */
					return "余额不足";
				else {// 操作成功
					subCredit.setBalance(subCredit.getBalance()- money);
					subCredit.setOutTime(currentTime);// 设置取出钱的时间
					subCredit.setDepositType("11");
					sc.saveAndFlush(subCredit);
					Record record = new Record(CurrentDay.currentDay, 2, subCredit.geteCard(),money);
					rec.save(record);
					return "取款成功！";
				}
			}
		}
		
		
		//存期为五年期、续存（其实上就是10年）
				if ( subCredit.getIsOneYear()== 5 && subCredit.getIsContinue() == 1) {
					if(money > subCredit.getBalance()){
						return "初始取钱数额不能大于：" + subCredit.getBalance();
					}
					//五年期但是未到五年就进行了取款
					if (CaculateTime.totalDays(subCredit.getInTime().getTime(), currentTime.getTime()) < 3650) {
							
						
						//提取的部分按当日活期利率算
						double rate = CaculateTime.totalDays(subCredit.getOutTime().getTime(), currentTime.getTime())
								/ 30.0 * Constant.currentDepositRate * money;
						subCredit.setBalance(subCredit.getBalance() + rate);
						subCredit.setOutTime(currentTime);
						sc.saveAndFlush(subCredit);
						Record r = new Record(currentTime, 4, subCredit.geteCard(), rate);
						rec.save(r);
						
						if (subCredit.getBalance() + rate < money) // 加上利息还不够取钱
							/** 操作失败 */
							return "余额不足";
						else {
							subCredit.setBalance(subCredit.getBalance()- money);
							subCredit.setOutTime(currentTime);// 设置取出钱的时间
							sc.saveAndFlush(subCredit);
							Record record = new Record(CurrentDay.currentDay, 2, subCredit.geteCard(),money);
							rec.save(record);
							return "取款成功！";
						}
					}
					
					// 存取十年，取钱时间超过十年
					else {
						//之计一次利息
						//先计算利息+本金
						double rateBefore = 120*subCredit.getRate()*subCredit.getBalance();
						subCredit.setBalance(subCredit.getBalance() + rateBefore);
						sc.saveAndFlush(subCredit);
						//再计算十年后的活期利息
						Date oneYear = new Date(subCredit.getOutTime().getTime() + (long)3650*24*60*60*1000);
						double rate = CaculateTime.totalDays(oneYear.getTime(), CurrentDay.currentDay.getTime()) /30.0
								*Constant.currentDepositRate * subCredit.getBalance();
						subCredit.setBalance(subCredit.getBalance() + rate );     
						sc.saveAndFlush(subCredit);
						
						//利息计算
						Record r = new Record(currentTime, 4, subCredit.geteCard(), rate + rateBefore);
						rec.save(r);

						if (subCredit.getBalance()< money) // 加上利息还不够取钱
							/** 操作失败 */
							return "余额不足";
						else {// 操作成功
							subCredit.setBalance(subCredit.getBalance()- money);
							subCredit.setOutTime(currentTime);// 设置取出钱的时间
							subCredit.setDepositType("11");
							sc.saveAndFlush(subCredit);
							Record record = new Record(CurrentDay.currentDay, 2, subCredit.geteCard(),money);
							rec.save(record);
							return "取款成功！";
						}
					}
				}
		
		
		return "未知的错误";
	}

	
	
	/**从定活两便账户取款*/
	public String drawFromFixedAndCurDeposit(String idNum,String creditNum,double money) {
		SubCredit subCredit = sc.findOne(creditNum);
		if (subCredit.getSubcreditState() != 1)
			return "子账户状态不允许取款";
		if(subCredit.getDepositType()!=3)
			return "账户类型不为定活两便账户";
		
		Date currentDate=CurrentDay.currentDay;
		
		
		//存期不到1年
		if(currentDate.before(new Date(subCredit.getOutTime().getTime() + (long)365*24*60*60*1000))){
			double rate = subCredit.getBalance() * subCredit.getRate()
						* CaculateTime.totalDays(subCredit.getOutTime().getTime(), currentDate.getTime())/30.0;
			subCredit.setBalance(rate +subCredit.getBalance());
			subCredit.setOutTime(currentDate);
			Record r = new Record(currentDate, 4, subCredit.geteCard(), rate);
			rec.save(r);
			sc.saveAndFlush(subCredit);
		}else {//存期超过1年
			subCredit.setRate(Constant.fixAndDepositRate);
			double rate = CaculateTime.totalDays(subCredit.getOutTime().getTime(), currentDate.getTime())
					/30.0 * subCredit.getBalance() * subCredit.getRate();
			subCredit.setBalance(rate + subCredit.getBalance());
			subCredit.setOutTime(currentDate);
			Record r = new Record(currentDate, 4, subCredit.geteCard(), rate);
			rec.save(r);
			sc.saveAndFlush(subCredit);
		}
		
		
		if (subCredit.getBalance()< money) // 加上利息还不够取钱
			/** 操作失败 */
			return "余额不足";
		else {
			subCredit.setBalance(subCredit.getBalance()-money);
			subCredit.setOutTime(currentDate);// 设置取出钱的时间
			sc.saveAndFlush(subCredit);
			Record record = new Record(CurrentDay.currentDay, 2, subCredit.geteCard(),money);
			rec.save(record);
			return "取款成功！";
		}
	}
	


	public String hangLoss(String idNum) {// 挂失
		User u = user.findOne(idNum);
		if(u == null){
			return "该身份证号找不到用户";
		}
		ECard c = card.findOne(u.geteCard().getAccountName());
		c.setDeadTime(CurrentDay.currentDay.getTime());
		if (u.geteCard() == null)
			return "没有一卡通，挂失失败!";// 没有一卡通,显示错误
		c.setState(2);// 设置为挂失状态
		card.saveAndFlush(c);
		for (SubCredit subCredit : c.getSubCredits()) {
			subCredit.setSubcreditState(2);// 所有子账户也设置为挂失状态
			sc.saveAndFlush(subCredit);
		}
		return "一卡通成功挂失";
	}

	public String unHangLoss(String idNum) {// 取消挂失
		User u = user.findOne(idNum);
		if(u == null){
			return "该身份证号找不到用户";
		}
		ECard c = card.findOne(u.geteCard().getAccountName());
		if (idNum.equals(u.getIdNum())) {
			if (c.getState() != 2)// 必须是挂失状态下才能取消挂失
				return "当前未挂失，无法取消挂失";
			else {
				c.setState(1);
				card.saveAndFlush(c);
				for (SubCredit subCredit : c.getSubCredits()) {
					subCredit.setSubcreditState(1);
					sc.saveAndFlush(subCredit);
				}
				return "取消挂失成功!";
			}
		} else
			return "客户信息错误";
	}

	@Override
	public String reSubmit(String idNum) {// 补办一卡通
		User u = user.findOne(idNum);
		if(u == null){
			return "该身份证号找不到用户";
		}
		ECard c = u.geteCard();
		if(c.getDeadTime() == null){
			return "该账户没有挂失，不用进行补办";
		}
		long date = CurrentDay.currentDay.getTime() - c.getDeadTime();
		if ((date / (1000 * 60 * 60 * 24)) < 7)
			return "时间不足7天，无法补办";
		else {
			c.setState(1);
			card.saveAndFlush(c);
			for (SubCredit subCredit : c.getSubCredits()) {
				subCredit.setSubcreditState(1);
				sc.saveAndFlush(subCredit);
			}
			return "成功补办一卡通";
		}
	}


	//销户
	public String deleteDeposit(String idNum) {
		User u = user.findOne(idNum);
		if(u == null){
			return "没有这个用户";
		}
		if (u.geteCard() == null) {
			return "当前没有一卡通账户，销户失败";// 没有一卡通，此时没有销毁一卡通账户，错误返回
		} else {
			
			ECard eCard = u.geteCard();
			/** 数据库中也要同步删除对应的所有信息 */
			for(SubCredit subCredit:eCard.getSubCredits()){
				sc.delete(subCredit);
			}
			card.delete(eCard);
			u.seteCard(null);// 将用户的一卡通设置为null

			user.saveAndFlush(u);
			return "成功销户";// 操作成功
		}
	}

	//重设密码
	public String resetPassword(String accountName, String password,String confirmPassword) {
		ECard ecard = card.findOne(accountName);// 找到对应一卡通

		if (ecard == null)
			return "没有对应账号的一卡通账户，重设密码失败";
		if(password.length() != 6){
			return "密码不是6位！";
		}
		if(!password.equals(confirmPassword)){
			return "两次密码不一致";
		}
		ecard.setPassword(password);
		card.saveAndFlush(ecard);
		return "密码重设成功";
	}

	// 参数为转账子账户号，金额，被转账方姓名，被转账方的子账户号
	public String transformMoney(String sourceCreditName, double money, String name, String destinationCreditName) {
		SubCredit sub = sc.findOne(sourceCreditName);//转账方子账户

		if (sub == null)
			// 没有转账账户
			return "转账方子账户不存在";

		if (sub.getBalance() < money)
			return "该子账户下余额不足以转账";// 对应子账户没有足够余额来进行转账操作

		SubCredit sub1 = sc.findOne(destinationCreditName);// 被转账方子账户
		if (sub1 == null) // 转账对象的子账户找不到
			return "被转账子账户号不存在";
		else {
			if (sub.getCurrency() != sub1.getCurrency())// 币种不对应
				return "转账失败，币种不对应";
			if (sub.getDepositType() != sub1.getDepositType())// 储蓄类型不对应
				return "转账失败，储蓄类型不匹配";

			sub.setBalance(sub.getBalance() - money);
			sub1.setBalance(sub1.getBalance() + money);
			sc.saveAndFlush(sub);
			sc.saveAndFlush(sub1);
			Record record = new Record(CurrentDay.currentDay, 3, sub.geteCard(), money);
			rec.save(record);
			return "转账成功";
		}

	}

}
