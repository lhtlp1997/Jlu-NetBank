package com.bank.util;

public class CaculateTime {
	/**
	 * 计算两个时间之间有多少天
	 * @param startTime 开始时间
	 * @param endTime 结束时间
	 * @return int型数据
	 */
    public static int totalDays(long startTime,long endTime) {
    	return (int) ((endTime-startTime)/(1000*60*60*24));
    }
}
