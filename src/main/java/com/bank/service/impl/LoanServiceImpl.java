package com.bank.service.impl;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.bank.model.ECard;
import com.bank.model.Loan;
import com.bank.model.Record;
import com.bank.model.SubCredit;
import com.bank.model.User;
import com.bank.repository.LoanRepository;
import com.bank.repository.RecordRepository;
import com.bank.repository.SubCreditRepository;
import com.bank.repository.UserRepository;
import com.bank.service.LoanService;
import com.bank.util.Constant;
import com.bank.util.CurrentDay;

@Service
public class LoanServiceImpl implements LoanService{
	
	private final UserRepository userRepository;
	private final LoanRepository loanRepository;
	private final SubCreditRepository subCreditRepository;
	private final RecordRepository recordRepository;
	
	@Autowired
	public LoanServiceImpl(UserRepository userRepository,LoanRepository loanRepository,
							SubCreditRepository subCreditRepository,RecordRepository recordRepository) {
		this.userRepository = userRepository;
		this.loanRepository = loanRepository;
		this.subCreditRepository = subCreditRepository;
		this.recordRepository = recordRepository;
	}
	
	@Override
	public String studentLoan(String idNum0,String idNum1,String idNum2,Double money, Integer loanDate,String creditNum) {
		SubCredit subCredit = subCreditRepository.findOne(creditNum);
		User user = userRepository.findOne(idNum0);
		if(user == null){
			return "您的身份证号输入错误";
		}
		if(subCredit == null){
			return "您输入的账户号码有误";
		}
		if(subCredit.getSubcreditState() != 1){
			return "您的账户处于注销状态";
		}
		if(subCredit.getDepositType() != 1){
			return "您的这个账户不是活期账户，不能进行贷款！";
		}
		ECard eCard = subCredit.geteCard();
		
		if(money > 100000.0 || loanDate <12 || loanDate > 96 || money < 0.0){//业务中的判断
			return "不合理的借贷！";
		}
		if(eCard.getUser().getLoan() != null){
			return "一个人不可以同时有多个贷款！";
		}
		if(Constant.LOANRATE == 0.0){
			return "目前没有设置贷款利息，暂时不能进行借贷！";
		}
		
		Double givenMoney = money /(loanDate/12);
		Double currentBack = money * Constant.LOANRATE * loanDate + money;
		Record r = new Record(CurrentDay.currentDay, 4, eCard, money * Constant.LOANRATE * loanDate);
		recordRepository.save(r);
		Loan loan = new Loan(eCard.getUser(), money, loanDate, givenMoney);
		Date startDate = CurrentDay.currentDay;
		Date endDate = new Date(startDate.getTime() + (long)loanDate*30*24*60*60*1000);
		
		loan.setUser(user);
		loan.setSubCredit(subCredit);
		loan.setLoanTop(96);
		loan.setLoanLimit(12.0);
		loan.setStartDate(CurrentDay.currentDay);
		loan.setRateChangeDate(CurrentDay.currentDay);
		loan.setEndDate(endDate);
		loan.setBackDate(endDate);
		loan.setCurrentBack(currentBack);
		loan.setGivenMoney(givenMoney);
		loan.setMoneyTop(100000.0);
		loan.setMoneyFloor(0.0);
		loan.setBackStyle(1);
		loan.setGuarantee1(userRepository.findOne(idNum1));
		loan.setGuarantee2(userRepository.findOne(idNum2));
		loan.setRate(Constant.LOANRATE);
		subCredit.setBalance(subCredit.getBalance() + givenMoney);//发款
		loanRepository.save(loan);
		Record record = new Record(CurrentDay.currentDay, 5, eCard, givenMoney);
		recordRepository.save(record);
		return "贷款成功！";
	}
	
	
	@Override
	public String houseLoan(String idNum,Double money,Integer loanDate,Integer backStyle,String creditNum) {
		SubCredit subCredit = subCreditRepository.findOne(creditNum);
		User user = userRepository.findOne(idNum);
		if(user == null){
			return "您的身份证号输入错误";
		}
		if(subCredit == null){
			return "您输入的账户号码有误";
		}
		if(subCredit.getSubcreditState() != 1){
			return "您的账户处于注销状态";
		}
		if(subCredit.getDepositType() != 1){
			return "您的这个账户不是活期账户，不能进行贷款！";
		}
		ECard eCard = subCredit.geteCard();

		if(!"house".equals(eCard.getUser().getPledge())){
			return "您没有房子抵押不了！";
		}
		if(money>eCard.getUser().getPlegePrice()*0.8||loanDate<12||loanDate>240){
			return "不合理的借贷！";
		}
		if(loanRepository.findLoanByUser(eCard.getUser()) != null){
			return "一个人不可以同时有多个贷款！";
		}
		if(Constant.LOANRATE == 0.0){
			return "目前没有设置贷款利息，暂时不能进行借贷！";
		}
		Double givenMoney = money;
		Loan loan = new Loan(eCard.getUser(), money, loanDate, givenMoney,backStyle);
		Date startDate = CurrentDay.currentDay;
		Date endDate = new Date(startDate.getTime() + (long)loanDate*30*24*60*60*1000);
		
		Double currentBack = 0.0;
		
		loan.setUser(user);
		loan.setSubCredit(subCredit);
		loan.setLoanTop(240);
		loan.setLoanLimit(12.0);
		loan.setStartDate(CurrentDay.currentDay);
		loan.setRateChangeDate(CurrentDay.currentDay);
		loan.setEndDate(endDate);
		loan.setBackDate(new Date(CurrentDay.currentDay.getTime() + (long)30*24*60*60*1000));
		loan.setCurrentBack(currentBack);
		loan.setGivenMoney(givenMoney);
		loan.setMoneyTop(eCard.getUser().getPlegePrice()*0.8);
		loan.setMoneyFloor(0.0);
		loan.setBackStyle(backStyle);
		subCredit.setBalance(subCredit.getBalance() + givenMoney);
		Record r = new Record(CurrentDay.currentDay, 5, eCard, givenMoney);
		recordRepository.save(r);
		loan.setRate(Constant.LOANRATE);
		loanRepository.save(loan);
		return "借贷成功！";
	}
	
	
	@Override
	public String personLoan(String idNum,Double money,Integer loanDate,String creditNumLoan,String creditNumSave) {
		SubCredit subCreditLoan = subCreditRepository.findOne(creditNumLoan);
		User user = userRepository.findOne(idNum);
		if(user == null){
			return "您的身份证号输入错误";
		}
		if(subCreditLoan == null){
			return "您输入的账户号码有误！";
		}
		if(subCreditLoan.getSubcreditState() != 1){
			return "您的账户处于注销状态";
		}
		if(subCreditLoan.getDepositType() != 2){
			return "该账户储蓄类型不是整存整取，不能进行借贷！";
		}
		ECard eCard = subCreditLoan.geteCard();
		eCard.getUser().setPledge(creditNumLoan);
		eCard.getUser().setPlegePrice(subCreditLoan.getBalance());
		
		//这个等同于上面两个的活期子账户，用于储存金额
		SubCredit subCreditSave = subCreditRepository.findOne(creditNumSave);
		if(subCreditSave.getDepositType() != 1){
			return "该账户不是活期账户，不能进行借贷！";
		}
		
		//这里的借贷时间先变为日，因为实在不方便修改
		if(money<1000||money>eCard.getUser().getPlegePrice()*0.9||loanDate<1||loanDate>365){
			return "不合理的借贷！";
		}
		if(loanRepository.findLoanByUser(eCard.getUser()) != null){
			return "一个人不可以同时有多个贷款！";
		}
		if(Constant.LOANRATE == 0.0){
			return "目前没有设置贷款利息，暂时不能进行借贷！";
		}
		
		Double givenMoney = money;
		Double currentBack = money * Constant.LOANRATE;
		Loan loan = new Loan(eCard.getUser(), money, loanDate, givenMoney);
		Date startDate = CurrentDay.currentDay;
		Date endDate = new Date(startDate.getTime() + (long)loanDate*24*60*60*1000);
		
		loan.setUser(user);
		loan.setSubCredit(subCreditSave);
		loan.setLoanTop(365);
		loan.setLoanLimit((double) (1));
		loan.setStartDate(CurrentDay.currentDay);
		loan.setRateChangeDate(CurrentDay.currentDay);
		loan.setEndDate(endDate);
		loan.setBackDate(endDate);
		loan.setCurrentBack(currentBack);
		loan.setGivenMoney(givenMoney);
		loan.setMoneyTop(eCard.getUser().getPlegePrice()*0.8);
		loan.setMoneyFloor(0.0);
		loan.setBackStyle(1);
		subCreditSave.setBalance(subCreditSave.getBalance() + givenMoney);
		Record r = new Record(CurrentDay.currentDay, 5, eCard, givenMoney);
		recordRepository.save(r);
		loan.setRate(Constant.LOANRATE);
		loanRepository.save(loan);
		return "借贷成功！";
	}

	
	@Override
	public String back(String creditNum) {
		SubCredit subCredit = subCreditRepository.findOne(creditNum);
		if(subCredit == null){
			return "错误的子账号！";
		}
		ECard eCard = subCredit.geteCard();
		Loan loan = loanRepository.findLoanByUser(eCard.getUser());
		if(subCredit.getBalance() >= loan.getCurrentBack()){
			subCredit.setBalance(subCredit.getBalance() - loan.getCurrentBack());
			
			subCreditRepository.saveAndFlush(subCredit);
			Record record = new Record(CurrentDay.currentDay, 6, eCard, loan.getCurrentBack());
			loan.setCurrentBack(0.0);
			recordRepository.save(record);
			loanRepository.deleteLoanById(loan.getId());
			return "还款成功！";
		}else {
			return "没有足够的钱，还不了！";
		}
	}

	@Override
	public String expand(String idNum,Long date) {
		User user = userRepository.findOne(idNum);
		Date d = CurrentDay.currentDay;
		if(user == null){
			return "身份证号错误！";
		}
		Loan loan = loanRepository.findLoanByUser(user);
		if(loan == null){
			return "该账户没有贷款业务！";
		}
		if(loan.getExpandDate() == 1){
			return "已经展期过一次了！";
		}
		if(CurrentDay.currentDay.after(loan.getEndDate())){
			return "贷款逾期之后只能还款，不能进行展期！";
		}
		
		//这里的时间分类讨论
		//住房和助学的分为一种、个人的单独分为一种
		if(loan.getLoanLimit() == 12.0){//住房和助学
			d = new Date(loan.getEndDate().getTime() + (long)date*30*24*60*60*1000);
			if(((loan.getEndDate().getTime() - loan.getStartDate().getTime())/1000/60/60/24/30 + date) > loan.getLoanTop()){
				return "超出时间上限！";
			}
		}else if(loan.getLoanLimit() == 1.0){
			d = new Date(loan.getEndDate().getTime() + (long)date*24*60*60*1000);
			if(((loan.getEndDate().getTime() - loan.getStartDate().getTime())/1000/60/60/24 + date) > loan.getLoanTop()){
				return "超出时间上限！";
			}
		}

		loan.setEndDate(d);
		loan.setExpandDate(1);
		loanRepository.saveAndFlush(loan);
		return "申请展期成功";
	}

	@Override
	public List<User> overdue() {//每天判断所有用户是不是有逾期的
		List<User> list = userRepository.findAll();
		List<User> overdueUsers = new ArrayList<>();
		for(User user:list){
			Loan loan = loanRepository.findLoanByUser(user);
			if(CurrentDay.currentDay.getTime() > loan.getEndDate().getTime()){
				loan.setOverdueRate(Constant.LOANRATE * 1.5);
				overdueUsers.add(user);
				loanRepository.saveAndFlush(loan);
			}
		}
		return overdueUsers;
	}
	
	
	@Override
	public User idTest(String idNum){
		User user = userRepository.findOne(idNum);
		if(user == null){
			return null;
		}
		return user;
	}
}
