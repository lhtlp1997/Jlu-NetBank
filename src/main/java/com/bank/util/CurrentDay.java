package com.bank.util;

import java.util.Calendar;
import java.util.Date;

public class CurrentDay {
	
	public static Date currentDay = new Date();//用于记录当前时间
	public static int day = 1;//天
	public static int year = 365;//年
	//调用这个方法，时间加1天
	public static Date getOneDayAfter(){
		Calendar calendar = Calendar.getInstance();
		calendar.setTime(new Date());
		calendar.add(Calendar.DATE, day);
		day++;//每次调用，加一天
		currentDay = calendar.getTime();
		return currentDay;
	}
	
	
	public static Date getOneYearAfter(){
		Calendar calendar = Calendar.getInstance();
		calendar.setTime(new Date());
		calendar.add(Calendar.DATE, day + year - 1);
		day+=366;//每次调用，加一年
		currentDay = calendar.getTime();
		return currentDay;
	}
}
