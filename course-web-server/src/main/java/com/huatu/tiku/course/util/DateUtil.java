package com.huatu.tiku.course.util;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * 
 * @author zhangchong
 *
 */
public class DateUtil {
	
	public static String ERRORSTR = "0000-00-00 00:00:00";

	public static String getSimpleDate(String startTime, String endTime) {
		if (ERRORSTR.equals(startTime.trim()) || ERRORSTR.equals(endTime.trim())) {
			return "";
		}
		DateTimeFormatter dtfOld = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
		DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy年MM月dd日 HH:mm");
		DateTimeFormatter dtfShort = DateTimeFormatter.ofPattern("MM月dd日 HH:mm");
		DateTimeFormatter dtfDay = DateTimeFormatter.ofPattern("dd日 HH:mm");
		DateTimeFormatter dtfMin = DateTimeFormatter.ofPattern("HH:mm");
		LocalDateTime startDate = LocalDateTime.parse(startTime, dtfOld);
		LocalDateTime endDate = LocalDateTime.parse(endTime, dtfOld);
		if (startDate.getYear() == endDate.getYear()) {
			// 年相等判断月是否相等
			if (startDate.getMonthValue() == endDate.getMonthValue()) {
				if (startDate.getDayOfMonth() == endDate.getDayOfMonth()) {
					// 日相等
					return new StringBuilder(startDate.format(dtfShort)).append("-").append(endDate.format(dtfMin))
							.toString();
				} else {
					// 月相等
					return new StringBuilder(startDate.format(dtfShort)).append("-").append(endDate.format(dtfDay))
							.toString();
				}
			} else {
				// 返回 月-日 - 月-日
				return new StringBuilder(startDate.format(dtfShort)).append("-").append(endDate.format(dtfShort))
						.toString();
			}
		} else {
			return new StringBuilder(startDate.format(dtf)).append("-").append(endDate.format(dtf)).toString();
		}

	}

//	public static void main(String[] args) {
//		System.out.println(getSimpleDate("2018-02-10 17:20:20", "2019-02-10 17:20:20"));
//		System.out.println(getSimpleDate("2019-02-10 17:20:20", "2019-03-10 17:20:20"));
//		System.out.println(getSimpleDate("2019-02-10 17:20:20", "2019-02-11 17:20:20"));
//		System.out.println(getSimpleDate("2019-02-11 17:20:20", "2019-02-11 17:21:20"));
//	}

//	public static void main(String[] args) {
//		Map<String, Integer> result = new HashMap<>();
//		result.put("20", 2);
//		result.put("30", 1);
//		result.put("40", 5);
//		Entry<String, Integer>  ret= result.entrySet().stream().sorted(Map.Entry.<String, Integer>comparingByValue().reversed()).findFirst().get();
//		System.out.println(ret);
//	}
}
