package com.bank.controller;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.bank.model.Loan;
import com.bank.model.Plan;
import com.bank.model.Record;
import com.bank.model.SubCredit;
import com.bank.repository.ECardRepository;
import com.bank.repository.EmployeeRepository;
import com.bank.repository.LoanRepository;
import com.bank.repository.PlanRepository;
import com.bank.repository.RecordRepository;
import com.bank.repository.SubCreditRepository;
import com.bank.repository.UserRepository;
import com.bank.service.DepositService;
import com.bank.service.LoanService;
import com.bank.service.ManageService;
import com.bank.util.CaculateTime;
import com.bank.util.Constant;
import com.bank.util.CurrentDay;

@Controller
public class TimeController {
	private final DepositService depositService;
	private final LoanService loanService;
	private final ManageService manageService;
	
	private final ECardRepository eCardRepository;
	private final EmployeeRepository employeeRepository;
	private final LoanRepository loanRepository;
	private final PlanRepository planRepository;
	private final RecordRepository recordRepository;
	private final SubCreditRepository subCreditRepository;
	private final UserRepository userRepository;
	
	
	@Autowired
	public TimeController(DepositService depositService,LoanService loanService,ManageService manageService
			,ECardRepository eCardRepository,EmployeeRepository employeeRepository,LoanRepository loanRepository
			,PlanRepository planRepository,RecordRepository recordRepository,SubCreditRepository subCreditRepository
			,UserRepository userRepository) {
		
		this.depositService = depositService;
		this.loanService = loanService;
		this.manageService = manageService;
		this.eCardRepository = eCardRepository;
		this.employeeRepository = employeeRepository;
		this.loanRepository = loanRepository;
		this.planRepository = planRepository;
		this.recordRepository = recordRepository;
		this.subCreditRepository = subCreditRepository;
		this.userRepository = userRepository;
		
	}
	
	@PostMapping("/oneday")
	public String oneday(final RedirectAttributes attributes){
		SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
		Date currentDate = CurrentDay.getOneDayAfter();
		
		
		//***************************************贷款业务***************************************
		
		List<Loan> loans = loanRepository.findAll();
		
		//****************判断利息的改变
		for(Loan loan:loans){
			
				//是否为1年了
				if(currentDate.after(loan.getRateChangeDate())){
					
					//********************助学贷款的发放
					if(currentDate.before(loan.getEndDate())){
						if(loan.getBackStyle() == 1 && loan.getLoanTop() != 365){
							loan.getSubCredit().setBalance(loan.getSubCredit().getBalance() + loan.getGivenMoney());
							loan.setGivenMoney(loan.getMoney() / (loan.getLoanDate() / 12));
							loanRepository.saveAndFlush(loan);
							Record r = new Record(currentDate, 5, loan.getSubCredit().geteCard(), loan.getGivenMoney());
							recordRepository.save(r);
						}
					}
					

					loan.setRateChangeDate(new Date(loan.getRateChangeDate().getTime() + (long)365*24*60*60*1000));        
					loan.setRate(Constant.LOANRATE);
					loanRepository.saveAndFlush(loan);
				}

		}
		
		
		
		
		//**********************住房贷款、利息计算
		//**********************逾期状态的判断
		for(Loan loan:loans){
			
			//逾期状态的判断
			if(currentDate.after(loan.getEndDate())){
				if(loan.getSubCredit().getBalance() < loan.getCurrentBack()){
					loan.setOverdueStyle(1);
					loan.setOverdueRate(Constant.LOANRATE * 1.5);
					loan.setRate(Constant.LOANRATE * 1.5);
					loanRepository.saveAndFlush(loan);
				}
			}
			
			//住房贷款每日利息计算
			if(loan.getBackStyle() == 3 || loan.getBackStyle() == 4){
				//计算每天的利息
				if(loan.getOverdueStyle() != 1){
					double rate = (loan.getMoney() + loan.getMoney() * loan.getRate() * loan.getLoanDate())/loan.getLoanDate() / 30.0;    
					loan.setCurrentBack(loan.getCurrentBack() + rate);       
					Record r = new Record(currentDate, 4, loan.getSubCredit().geteCard(),rate);         
					recordRepository.save(r);
					loanRepository.saveAndFlush(loan);
				}			
			}
		}
		
		

		
		//********************每日还贷
		//********************部分利息计算
		for(Loan loan:loans){
			
			
			//助学贷款、第一天计息、一次性还清
			if(loan.getBackStyle() == 1 && loan.getLoanTop() != 365){
				
				
				if(currentDate.after(loan.getEndDate())){
					//********************助学贷款，利息早就已经算好了
					if(loan.getSubCredit().getBalance() >= loan.getMoney()){
						
						loan.getSubCredit().setBalance(loan.getSubCredit().getBalance() - loan.getCurrentBack());
						Record r = new Record(currentDate, 6, loan.getSubCredit().geteCard(), loan.getCurrentBack());    
						recordRepository.save(r);
						loan.setMoney(0.0);//设置为不用再还了，因为不能删除贷款记录
						loan.setCurrentBack(0.0);
						loan.setOverdueStyle(-1);
						loanRepository.saveAndFlush(loan);
						loanRepository.deleteLoanById(loan.getId());
					}
				}
			}
			
			
			
			//住房贷款、第一天计息、两种还款方式
			//等额本息  和  等额本金
			//一个月还一次
			if(loan.getBackStyle() == 3 || loan.getBackStyle() == 4){
				
				//正常还款状态
				if(currentDate.after(loan.getBackDate())){
					if(loan.getSubCredit().getBalance() >= loan.getCurrentBack()){
						loan.getSubCredit().setBalance(loan.getSubCredit().getBalance() - loan.getCurrentBack());
						Record r = new Record(currentDate, 6, loan.getSubCredit().geteCard(), loan.getCurrentBack());
						recordRepository.save(r);
						loan.setCurrentBack(0.0);
						loan.setBackDate(new Date(currentDate.getTime() + (long)30*24*60*60*1000));
						loan.setOverdueStyle(-1);
						loanRepository.saveAndFlush(loan);
					}else {//利滚利，每日利息计算
						//也包括了逾期利息的计算
						loan.setCurrentBack(loan.getCurrentBack() + loan.getCurrentBack() * loan.getRate()/30.0);
						Record r = new Record(currentDate, 4, loan.getSubCredit().geteCard(), loan.getCurrentBack() * loan.getRate()/30.0);        
						recordRepository.save(r);
						loanRepository.saveAndFlush(loan);
					}				
				}
			}
			
			
			
			
			//个人贷款
			if(loan.getLoanTop() == 365){
								
				//****************个人贷款，每天利息计算
				//如果最后一天没还起这个钱，这个就开始利滚利了
				double rate =  loan.getMoney() * Constant.LOANRATE/30.0;
				loan.setCurrentBack(loan.getCurrentBack() + rate);
				loanRepository.saveAndFlush(loan);
				Record r = new Record(currentDate, 4, loan.getSubCredit().geteCard(), rate);
				recordRepository.save(r);
				
				//最后一天还款
				if(currentDate.after(loan.getEndDate())){
					
					//本 + 利息这时候才算在一起
					loan.setCurrentBack(loan.getCurrentBack() + loan.getMoney());
					if(loan.getSubCredit().getBalance() >= loan.getCurrentBack()){
						loan.getSubCredit().setBalance(loan.getSubCredit().getBalance() - loan.getCurrentBack());
						Record rr = new Record(currentDate, 6, loan.getSubCredit().geteCard(), loan.getCurrentBack());
						recordRepository.save(rr);
						loan.setOverdueStyle(-1);
						loanRepository.saveAndFlush(loan);
						loanRepository.deleteLoanById(loan.getId());
					}
				}
			}
			
		}
		
		
		
		//*************逾期状态，利息计算  和  非逾期状态利息计算
		//这里就不用再算住房贷款的利息了
		for(Loan loan:loans){
			if(loan.getOverdueStyle() == 1 && loan.getBackStyle()!=3 && loan.getBackStyle()!=4){
				//如果大于一个月了
				if(currentDate.after(new Date(loan.getBackDate().getTime() + (long)30*24*60*60*1000))){
					//当前应还款、利滚利
					double rate = loan.getCurrentBack() * loan.getOverdueRate();
					loan.setCurrentBack(loan.getCurrentBack() + rate);
					Record r = new Record(currentDate, 4, loan.getSubCredit().geteCard(), rate);
					recordRepository.save(r);
					//还款日期又加一个月
					loan.setBackDate(new Date(loan.getBackDate().getTime() + (long)30*24*60*60*1000));
					loanRepository.saveAndFlush(loan);
				}
			}
		}
		
		
		

		//*********************************贷款业务**********************************************
		
		
		
		
		
		//*********************************储蓄业务**********************************************
		
		//*********************利息计算
		//整存整取的利息不用计算了，全部放在取的时候进行计算
		List<SubCredit> subCredits = subCreditRepository.findAll();
		
		for(SubCredit subCredit:subCredits){
			//活期存储
			if(subCredit.getDepositType() == 1){
				
				//活期存储6月30日自动计算利息，利滚利
				String check = format.format(currentDate);
				if("06".equals(check.substring(5, 7)) && "30".equals(check.substring(8, 10))){
					double rate = subCredit.getBalance() * subCredit.getRate()
							* CaculateTime.totalDays(subCredit.getOutTime().getTime(),currentDate.getTime())/30.0;
					subCredit.setBalance(subCredit.getBalance() + rate);
					subCredit.setOutTime(currentDate);
					subCreditRepository.saveAndFlush(subCredit);
					Record r = new Record(currentDate, 4, subCredit.geteCard(), rate);
					recordRepository.save(r);
				}
			}
			
		}
		
		
		//*****************续存、不续存
		for(SubCredit subCredit:subCredits){
			
			//判断所有的续存。到时间了就设置为不续存就好。
			//所有的利息都在取款的时候进行计算
			if(subCredit.getDepositType() == 2 && subCredit.getIsContinue() == 1){
				
				//1年续存
				if(subCredit.getIsOneYear() == 1){
					if(currentDate.after(new Date(subCredit.getInTime().getTime() + (long)730*24*60*60*1000))){
						subCredit.setDepositType("11");//设置为活期账户
						subCredit.setIsContinue(-1);
						subCreditRepository.saveAndFlush(subCredit);
					}
				}
				
				
				//5年续存
				if(subCredit.getIsOneYear() == 5){
					if(currentDate.after(new Date(subCredit.getInTime().getTime() + (long)3650*24*60*60*1000))){
						subCredit.setDepositType("11");//设置为活期账户
						subCredit.setIsContinue(-1);
						subCreditRepository.saveAndFlush(subCredit);
					}
				}
			}
			
			
		}
		
		

		//*********************************储蓄业务**********************************************
		
		
		//*********************************计划改变**********************************************
		
		List<Plan> plans = planRepository.findAll();
		for(Plan plan:plans){
			
			//这个纯粹就是防止bug
			if(currentDate.before(plan.getExecuteDate()) && plan.getExecuteState() != 2){
				manageService.deletePlan(plan);
			}
			
			//如果到一个计划的执行时间
			//就把当前正在执行的计划状态设置为已经执行，把未执行计划设置为正在执行
			//并且执行
			if(currentDate.after(plan.getExecuteDate()) && plan.getExecuteState() == 2){
				manageService.planExecute(plan);
			}
		}
		
		
		
		//*********************************计划改变**********************************************
		
		
		
		
		attributes.addFlashAttribute("message", "时间过去一天，现在的时间为：" + format.format(currentDate));
		return "redirect:/" + "index.html";
	}
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	@PostMapping("/oneyear")
	public String oneyearPost(final RedirectAttributes attributes){
		SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
		Date currentDate = CurrentDay.getOneYearAfter();
		
		
		//***************************************贷款业务***************************************
		
		List<Loan> loans = loanRepository.findAll();
		
		//****************判断利息的改变
		for(Loan loan:loans){
			
				//是否为1年了
				if(currentDate.after(loan.getRateChangeDate())){
					
					//********************助学贷款的发放
					if(currentDate.before(loan.getEndDate())){
						if(loan.getBackStyle() == 1 && loan.getLoanTop() != 365){
							loan.getSubCredit().setBalance(loan.getSubCredit().getBalance() + loan.getGivenMoney());
							loan.setGivenMoney(loan.getMoney() / (loan.getLoanDate() / 12));
							loanRepository.saveAndFlush(loan);
							Record r = new Record(currentDate, 5, loan.getSubCredit().geteCard(), loan.getGivenMoney());
							recordRepository.save(r);
						}
					}
					

					loan.setRateChangeDate(new Date(loan.getRateChangeDate().getTime() + (long)365*24*60*60*1000));        
					loan.setRate(Constant.LOANRATE);
					loanRepository.saveAndFlush(loan);
				}

		}
		
		
		
		
		//**********************住房贷款、利息计算
		//**********************逾期状态的判断
		for(Loan loan:loans){
			
			//逾期状态的判断
			if(currentDate.after(loan.getEndDate())){
				if(loan.getSubCredit().getBalance() < loan.getCurrentBack()){
					loan.setOverdueStyle(1);
					loan.setRate(Constant.LOANRATE * 1.5);
					loan.setOverdueRate(Constant.LOANRATE * 1.5);
					loanRepository.saveAndFlush(loan);
				}
			}
			
			//住房贷款利息计算
			if(loan.getBackStyle() == 3 || loan.getBackStyle() == 4){
				//计算每年的利息
				if(loan.getOverdueStyle() != 1){
					double totalmoney = (loan.getMoney() + loan.getMoney() * loan.getRate() * loan.getLoanDate())/loan.getLoanDate() * 12;
					double rate = loan.getMoney() * loan.getRate() * loan.getLoanDate() / loan.getLoanDate() * 12;
					loan.setCurrentBack(loan.getCurrentBack() + totalmoney);       
					Record r = new Record(currentDate, 4, loan.getSubCredit().geteCard(),rate);         
					recordRepository.save(r);
					loanRepository.saveAndFlush(loan);
				}			
			}
		}
		
		

		
		//********************每年还贷
		//********************部分利息计算
		for(Loan loan:loans){
			
			
			//助学贷款、第一天计息、一次性还清
			if(loan.getBackStyle() == 1 && loan.getLoanTop() != 365){
				
				
				if(currentDate.after(loan.getEndDate())){
					//********************助学贷款，利息早就已经算好了
					if(loan.getSubCredit().getBalance() >= loan.getMoney()){
						
						loan.getSubCredit().setBalance(loan.getSubCredit().getBalance() - loan.getCurrentBack());
						Record record = new Record(currentDate, 6, loan.getSubCredit().geteCard(), loan.getCurrentBack());
						recordRepository.save(record);
						loan.setMoney(0.0);//设置为不用再还了，因为不能删除贷款记录
						loan.setCurrentBack(0.0);
						loan.setOverdueStyle(-1);
						loanRepository.deleteLoanById(loan.getId());
					}
				}
			}
			
			
			
			//住房贷款、第一天计息、两种还款方式
			//等额本息  和  等额本金
			if(loan.getBackStyle() == 3 || loan.getBackStyle() == 4){
				
				//正常还款状态
				if(currentDate.after(loan.getBackDate())){
					if(loan.getSubCredit().getBalance() >= loan.getCurrentBack()){
						loan.getSubCredit().setBalance(loan.getSubCredit().getBalance() - loan.getCurrentBack());
						Record record = new Record(currentDate, 6, loan.getSubCredit().geteCard(), loan.getCurrentBack());
						recordRepository.save(record);
						loan.setCurrentBack(0.0);
						//判断有没有结束，结束了就删掉
						if(currentDate.after(loan.getEndDate())){
							loanRepository.deleteLoanById(loan.getId());
							loan.setMoney(0.0);
						}
						loan.setBackDate(new Date(currentDate.getTime() + (long)365*24*60*60*1000));

						
						loan.setOverdueStyle(-1);
						loanRepository.saveAndFlush(loan);
					}else {//利滚利，计算1年过去的利息（但是这里直接过去1年，不会计算利滚利）
						loan.setCurrentBack(loan.getCurrentBack() + loan.getCurrentBack() * loan.getRate() * 12);
						Record r = new Record(currentDate, 4, loan.getSubCredit().geteCard(), loan.getCurrentBack() * loan.getRate() * 12);        
						recordRepository.save(r);
						loanRepository.saveAndFlush(loan);
					}				
				}
			}
			
			
			
			
			//个人贷款
			if(loan.getLoanTop() == 365){
								
				//****************个人贷款，计算总利息
				double rate = loan.getMoney() * Constant.LOANRATE *
						CaculateTime.totalDays(loan.getStartDate().getTime(), loan.getEndDate().getTime()) / 30;
				loan.setCurrentBack(loan.getCurrentBack() + rate);
				loanRepository.saveAndFlush(loan);
				Record r = new Record(currentDate, 4, loan.getSubCredit().geteCard(), rate);
				recordRepository.save(r);
				
				//最后一天还款
				if(currentDate.after(loan.getEndDate())){
					
					//本 + 利息这时候才算在一起
					loan.setCurrentBack(loan.getCurrentBack() + loan.getMoney());
					if(loan.getSubCredit().getBalance() >= loan.getCurrentBack()){
						loan.getSubCredit().setBalance(loan.getSubCredit().getBalance() - loan.getCurrentBack());
						Record record = new Record(currentDate, 6, loan.getSubCredit().geteCard(), loan.getCurrentBack());
						recordRepository.save(record);
						loan.setOverdueStyle(-1);
						loanRepository.deleteLoanById(loan.getId());
					}
				}
			}
			
		}
		
		
		
		//*************逾期状态，利息计算  和  非逾期状态利息计算
		//这里就按1年算得了，直接按照一年的逾期计算(其实这样算是不太对的)
		for(Loan loan:loans){
			if(loan.getOverdueStyle() == 1 && loan.getBackStyle() !=3 && loan.getBackStyle() !=4){
				//如果大于一个月了
				if(currentDate.after(new Date(loan.getBackDate().getTime() + (long)30*24*60*60*1000))){
					//当前应还款、利滚利
					double rate = loan.getCurrentBack() * loan.getOverdueRate() * 12;
					loan.setCurrentBack(loan.getCurrentBack() + rate);
					Record record = new Record(currentDate, 4, loan.getSubCredit().geteCard(), rate);
					recordRepository.save(record);
					//还款日期又加一个年
					loan.setBackDate(new Date(loan.getBackDate().getTime() + (long)365*24*60*60*1000));
					loanRepository.saveAndFlush(loan);
				}
			}
		}
		
		
		

		//*********************************贷款业务**********************************************
		
		
		
		
		
		//*********************************储蓄业务**********************************************
		
		//*********************利息计算
		//整存整取的利息不用计算了，全部放在取的时候进行计算
		List<SubCredit> subCredits = subCreditRepository.findAll();
		
		for(SubCredit subCredit:subCredits){
			//活期存储
			if(subCredit.getDepositType() == 1){
				
				//活期存储6月30日自动计算利息，利滚利
				if(currentDate.after(new Date(currentDate.getYear(), 06, 30))){
					double rate = subCredit.getBalance() * subCredit.getRate()
							* CaculateTime.totalDays(subCredit.getOutTime().getTime(),currentDate.getTime())/30.0;
					subCredit.setBalance(subCredit.getBalance() + rate);
					subCredit.setOutTime(currentDate);
					subCreditRepository.saveAndFlush(subCredit);
					Record r = new Record(currentDate, 4, subCredit.geteCard(), rate);
					recordRepository.save(r);
				}
			}
			
		}
		
		
		//*****************续存、不续存
		for(SubCredit subCredit:subCredits){
			
			//判断所有的续存。到时间了就设置为不续存就好。
			//所有的利息都在取款的时候进行计算
			if(subCredit.getDepositType() == 2 && subCredit.getIsContinue() == 1){
				
				//1年续存
				if(subCredit.getIsOneYear() == 1){
					if(currentDate.after(new Date(subCredit.getInTime().getTime() + (long)730*24*60*60*1000))){
						subCredit.setDepositType("11");//设置为活期账户
						subCredit.setIsContinue(-1);
						subCreditRepository.saveAndFlush(subCredit);
					}
				}
				
				
				//5年续存
				if(subCredit.getIsOneYear() == 5){
					if(currentDate.after(new Date(subCredit.getInTime().getTime() + (long)3650*24*60*60*1000))){
						subCredit.setDepositType("11");//设置为活期账户
						subCredit.setIsContinue(-1);
						subCreditRepository.saveAndFlush(subCredit);
					}
				}
			}
			

		}
		
		

		//*********************************储蓄业务**********************************************
		
		
		//*********************************计划改变**********************************************
		
		List<Plan> plans = planRepository.findAll();
		for(Plan plan:plans){
			
			//这个纯粹就是防止bug
			if(currentDate.before(plan.getExecuteDate()) && plan.getExecuteState() != 2){
				manageService.deletePlan(plan);
			}
			
			//如果到一个计划的执行时间
			//就把当前正在执行的计划状态设置为已经执行，把未执行计划设置为正在执行
			//并且执行
			if(currentDate.after(plan.getExecuteDate()) && plan.getExecuteState() == 2){
				manageService.planExecute(plan);
			}
		}
		
		
		
		//*********************************计划改变**********************************************
		
		
		
		
		
		attributes.addFlashAttribute("message", "时间过去1年，现在时间为：" + format.format(currentDate));
		return "redirect:/" + "index.html";
	}
	
}
