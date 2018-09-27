package com.bank;


import java.util.Date;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import com.bank.model.ECard;
import com.bank.model.Employee;
import com.bank.model.Loan;
import com.bank.model.SubCredit;
import com.bank.model.User;
import com.bank.repository.ECardRepository;
import com.bank.repository.EmployeeRepository;
import com.bank.repository.LoanRepository;
import com.bank.repository.SubCreditRepository;
import com.bank.repository.UserRepository;
import com.bank.service.DepositService;
import com.bank.service.impl.DepositServiceImpl;
import com.bank.util.Constant;
import com.bank.util.CurrentDay;

@SuppressWarnings("unused")
@Component
public class Init implements CommandLineRunner{
	private final UserRepository user;
	private final SubCreditRepository sub;
	private final LoanRepository loan;
	private final EmployeeRepository emp;
	private final ECardRepository ec;
	private final DepositService dd;
	
	@Autowired
	public Init(UserRepository user,SubCreditRepository sub,LoanRepository loan
				,EmployeeRepository emp,ECardRepository ec,DepositService dd) {
		this.user = user ;
		this.sub = sub;
		this.loan = loan;
		this.emp = emp;
		this.ec = ec;
		this.dd = dd;
	}
	
	@Override
	public void run(String... args) throws Exception {
		Constant.LOANRATE = 0.002;
		User testuser = new User("123456123456123456", "SB", "dongbei", "010010");
		user.save(testuser);
		dd.openECard("1234512345", "SB", "123456123456123456", "dongbei", "010010", "666666");
		dd.openSubcredit("1234512345", "21354", 9000.0);
		User user1 = new User("654301199701093510", "lightning", "beitun", "18843100180");
		User user2 = new User("654301199701093512","zhupi","langfang","54385438");
		User user3 = new User("654301199701093513","aixiding","akesu","88888888");
		user1.setPassword("123456");
		user2.setPassword("123456");
		user3.setPassword("123456");
		
		user3.setPledge("house");
		user3.setPlegePrice(100000.0);
		user.save(user1);
		user.save(user2);
		user.save(user3);
		
		dd.openECard("1223344556", "lightning", "654301199701093510", "beitun", "18843100180", "123456");
		dd.openECard("1234567890", "zhupixuan", "654301199701093512", "beitun", "18843100180", "123456");
		dd.openECard("1334455667", "aixiding", "654301199701093513", "beitun", "18843100180", "123456");

		
		
		dd.openSubcredit("1234567890", "12754", 10000.0);
		SubCredit subCredit1 = sub.findOne("12754");
		subCredit1.setIsOneYear(1);
		subCredit1.setIsContinue(-1);
		subCredit1.setOutTime(CurrentDay.currentDay);
		sub.saveAndFlush(subCredit1);
		
		dd.openSubcredit("1334455667", "12888", 10000.0);
		SubCredit subCredit2 = sub.findOne("12888");
		subCredit2.setIsOneYear(5);
		subCredit2.setIsContinue(1);
		subCredit2.setOutTime(CurrentDay.currentDay);
		sub.saveAndFlush(subCredit2);
		
		dd.openSubcredit("1223344556", "13457", 10000.0);
		SubCredit subCredit3 = sub.findOne("13457");
		subCredit3.setOutTime(CurrentDay.currentDay);
		sub.saveAndFlush(subCredit3);

		dd.openSubcredit("1223344556", "11555", 10000.0);
		SubCredit subCredit4 = sub.findOne("11555");
		subCredit4.setOutTime(CurrentDay.currentDay);
		sub.saveAndFlush(subCredit4);
		
		dd.openSubcredit("1334455667", "11247", 10000.0);
		SubCredit subCredit5 = sub.findOne("11247");
		subCredit5.setOutTime(CurrentDay.currentDay);
		sub.saveAndFlush(subCredit5);
		
		
		dd.openSubcredit("1234567890", "11666", 10000.0);
		SubCredit subCredit6 = sub.findOne("11666");
		subCredit6.setOutTime(CurrentDay.currentDay);
		sub.saveAndFlush(subCredit6);
		
		



		
		Employee employee = new Employee("44444", "123456789ll_");
		employee.setType(4);
		
		emp.save(employee);
		
	}
}
