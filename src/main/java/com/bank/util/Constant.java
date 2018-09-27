package com.bank.util;

public class Constant {

	
	//初始设定利率
	public static double currentDepositRate = 0.03;//活期利率
	public static double fixedDepositRate = 0.15;//定期利率
	public static double fixAndDepositRate = Double.parseDouble(String.format("%.3f", fixedDepositRate * 0.6) );//大于一年的定活两期利率	
	
	public static double sixthirty = 0.03;//每年6月30日的利率
	
	public static double LOANRATE = 0.02;//贷款利率
	
}
