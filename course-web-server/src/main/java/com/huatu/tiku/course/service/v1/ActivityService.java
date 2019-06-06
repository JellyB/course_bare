package com.huatu.tiku.course.service.v1;

/**
 * 
 * @author zhangchong
 *
 */
public interface ActivityService {

	/**
	 * 618活动签到送图币
	 * @param userName
	 * @param ucId
	 */
	int signGiveCoin(String userName, String ucId);

	/**
	 * 签到记录
	 * @param uname
	 * @return
	 */
	Object signList(String uname);
}
