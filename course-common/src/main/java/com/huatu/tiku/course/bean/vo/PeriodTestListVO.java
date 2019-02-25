package com.huatu.tiku.course.bean.vo;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * 阶段测试列表vo
 * 
 * @author zhangchong
 *
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PeriodTestListVO {
	private int courseId;
	private String courseTitle;
	private int undoCount;
	/**
	 * 阶段测试列表
	 */
	private List<PeriodTestInfo> periodTestList;

	@AllArgsConstructor
	@Setter
	@Getter
	@Builder
	@NoArgsConstructor
	public static class PeriodTestInfo {
		private String examName;
		private long examId;
		private String endTime;
		private String startTime;
		private int isAlert;
		private String questionIds;

	}
}
