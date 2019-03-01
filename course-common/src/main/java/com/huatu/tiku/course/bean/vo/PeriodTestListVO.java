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
	private int currentPage;
	private int next;
	private List<CourseInfo> list;

	@AllArgsConstructor
	@Setter
	@Getter
	@Builder
	@NoArgsConstructor
	public static class CourseInfo {
		private int classId;
		private String className;
		private int undoCount;
		/**
		 * 阶段测试列表
		 */
		private List<PeriodTestInfo> child;
	}

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
		private long syllabusId;
		private int coursewareNum;//排序

	}
}
