package com.huatu.tiku.course.util;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * 
 * @author zhangchong
 *
 */
public class DateUtil {

	public static String getSimpleDate(String startTime, String endTime) {
		DateTimeFormatter dtfOld = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
		DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy年MM月dd日 HH:mm");
		DateTimeFormatter dtfShort = DateTimeFormatter.ofPattern("MM月-dd日 HH:mm");
		DateTimeFormatter dtfDay = DateTimeFormatter.ofPattern("dd日 HH:mm");
		DateTimeFormatter dtfMin = DateTimeFormatter.ofPattern("HH:mm");
		LocalDateTime startDate = LocalDateTime.parse(startTime, dtfOld);
		LocalDateTime endDate = LocalDateTime.parse(endTime, dtfOld);
		if (startDate.getYear() == endDate.getYear()) {
			// 年相等判断越是否相等
			if (startDate.getMonthValue() == endDate.getMonthValue()) {
				if (startDate.getDayOfMonth() == endDate.getDayOfMonth()) {
					// 日相等
					return startDate.format(dtfDay) + "-" + endDate.format(dtfMin);
				} else {
					// 月相等
					return startDate.format(dtfDay) + "-" + endDate.format(dtfDay);
				}
			} else {
				// 返回 月-日 - 月-日
				return startDate.format(dtfShort) + "-" + endDate.format(dtfShort);
			}
		} else {
			return startDate.format(dtf) + "-" + endDate.format(dtf);
		}

	}

	public static void main(String[] args) {
		System.out.println(getSimpleDate("2018-02-10 17:20:20", "2019-02-10 17:20:20"));
		System.out.println(getSimpleDate("2019-02-10 17:20:20", "2019-03-10 17:20:20"));
		System.out.println(getSimpleDate("2019-02-10 17:20:20", "2019-02-11 17:20:20"));
		System.out.println(getSimpleDate("2019-02-11 17:20:20", "2019-02-11 17:21:20"));
	}
}
