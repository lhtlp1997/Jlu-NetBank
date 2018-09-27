package com.bank.service;

//修改了openECard方法
public interface DepositService{
	/**活期存储存储，只用作存钱*/
	public String  currentDeposit(String creditNum,double money);
	/**定期存储存储，只用作存钱*/
	public String fixedDeposit(String creditNum,double money,Integer isOneYear,Integer isContinue);
	/**定活两便存储，只用作存钱*/	
	public String fixAndCurDeposit(String creditNum,double money);
	
	
	/**这个一个总的方法*/
	public String draw(String idNum,String creditNum,double money);
	
	/**从活期账户取款*/
	public String drawFromCurrentDeposit(String idNum,String creditNum,double money);
	
	/**从整存整取账户取款*/
	public String drawFromFixedDeposit(String idNum,String creditNum,double money);
	
	/**从定活两便账户取款*/
	public String drawFromFixedAndCurDeposit(String idNum,String creditNum,double money);
	


	/**用于开通一卡通的函数*/
	public boolean openECard(String accountName,String name,String idNum,String address,String telephone,String password);       
	/**开通一卡通下的子账户*/
	public boolean openSubcredit(String accountName,String creditNum,Double money);



	
	/**用于挂失一卡通的函数*/
	public String hangLoss(String idNum);
	/**用于取消一卡通挂失的函数*/
	public String unHangLoss(String idNum);
	/**补办一卡通的函数*/
	public String reSubmit(String idNum);
	/**销毁当前一卡通账户的函数*/
	public String deleteDeposit(String idNum);
	/**修改一卡通账户密码，不需要提供任何信息*/
	public String resetPassword(String accountName,String password,String confirmPassword);
	/**转账业务* */
	public String transformMoney(String sourceCreditName,double money,String name,String destinationCreditName);
}
