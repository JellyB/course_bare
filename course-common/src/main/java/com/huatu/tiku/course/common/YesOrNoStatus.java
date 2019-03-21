package com.huatu.tiku.course.common;

/**
 * 0 1常量
 */
public enum YesOrNoStatus {
	/**
	 * YES TRUE 展示
	 */
	YES(1, "是"),

	/**
	 * NO FALSE 不展示
	 */
	NO(0, "否");

	private final Integer code;
	private final String des;

	private YesOrNoStatus(int code, String des) {
		this.code = code;
		this.des = des;
	}

	public Integer getCode() {
		return code;
	}

	public String getDes() {
		return des;
	}

}
