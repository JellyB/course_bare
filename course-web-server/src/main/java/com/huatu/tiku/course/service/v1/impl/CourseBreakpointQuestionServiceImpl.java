package com.huatu.tiku.course.service.v1.impl;

import java.sql.Timestamp;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import java.util.function.Supplier;
import java.util.stream.Collectors;

import org.apache.commons.collections.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.google.common.base.Stopwatch;
import com.google.common.collect.Lists;
import com.huatu.tiku.common.CourseQuestionTypeEnum.CourseType;
import com.huatu.tiku.course.bean.CourseBreakPointPracticeDto;
import com.huatu.tiku.course.bean.CourseBreakpointQuestionDTO;
import com.huatu.tiku.course.common.YesOrNoStatus;
import com.huatu.tiku.course.dao.manual.CourseExercisesStatisticsMapper;
import com.huatu.tiku.course.service.cache.CacheUtil;
import com.huatu.tiku.course.service.cache.CourseBreakpointCacheKey;
import com.huatu.tiku.course.service.v1.CourseBreakpointQuestionService;
import com.huatu.tiku.course.service.v1.CourseBreakpointService;
import com.huatu.tiku.course.util.ZTKResponseUtil;
import com.huatu.tiku.course.ztk.api.v1.question.QuestionServiceV1;
import com.huatu.tiku.entity.CourseBreakpointQuestion;
import com.huatu.tiku.entity.CourseExercisesStatistics;

import lombok.extern.slf4j.Slf4j;
import service.impl.BaseServiceHelperImpl;
import tk.mybatis.mapper.entity.Example;
import tk.mybatis.mapper.weekend.WeekendSqls;

/**
 * Created by lijun on 2018/6/20
 */
@Slf4j
@Service
public class CourseBreakpointQuestionServiceImpl extends BaseServiceHelperImpl<CourseBreakpointQuestion>
		implements CourseBreakpointQuestionService {

	public CourseBreakpointQuestionServiceImpl() {
		super(CourseBreakpointQuestion.class);
	}

	@Autowired
	private CacheUtil cacheUtil;

	@Autowired
	private QuestionServiceV1 questionService;

	@Autowired
	private CourseExercisesStatisticsMapper courseExercisesStatisticsMapper;

	@Autowired
	private CourseBreakpointService courseBreakpointService;

	@Override
	public List<Map<String, Object>> listQuestionIdByPointId(Long breakpointId) {
		// 1.生成key
		Supplier keySupplier = () -> CourseBreakpointCacheKey.breakpointQuestionKey(breakpointId);
		// 2.生成value
		Supplier<List<Map<String, Object>>> valueSupplier = () -> {
			WeekendSqls<CourseBreakpointQuestion> sql = WeekendSqls.custom();
			sql.andEqualTo(CourseBreakpointQuestion::getBreakpointId, breakpointId);

			Example example = Example.builder(CourseBreakpointQuestion.class).where(sql).build();
			List<CourseBreakpointQuestion> courseBreakpointQuestionList = selectByExample(example);
			if (courseBreakpointQuestionList.size() == 0) {
				return null;
			}
			// 获取所有的questionId
			Stopwatch stopwatch = Stopwatch.createStarted();
			String questionIds = courseBreakpointQuestionList.stream().map(CourseBreakpointQuestion::getQuestionId)
					.map(String::valueOf).collect(Collectors.joining(","));
			Object listQuestionByIds = questionService.listQuestionByIds(questionIds);
			log.info("课程-断点-获取考题信息 ===》{}", stopwatch.elapsed(TimeUnit.MILLISECONDS));
			List<Map<String, Object>> result = (List<Map<String, Object>>) ZTKResponseUtil.build(listQuestionByIds);
			return result;
		};

		List<Map<String, Object>> cacheStringValue = cacheUtil.getCacheStringValue(keySupplier, valueSupplier, 5,
				TimeUnit.MINUTES);
		return cacheStringValue;
	}

	@Override
	public List<CourseBreakpointQuestion> listQuestionIdByBreakpointIdList(List<Long> list) {
		if (CollectionUtils.isEmpty(list)) {
			return Lists.newArrayList();
		}
		WeekendSqls<CourseBreakpointQuestion> sql = WeekendSqls.custom();
		sql.andIn(CourseBreakpointQuestion::getBreakpointId, list);
		Example example = Example.builder(CourseBreakpointQuestion.class).where(sql).orderByAsc("pptIndex", "sort")
				.build();
		List<CourseBreakpointQuestion> questions = selectByExample(example);
		return questions;
	}

	/**
	 * 持久化录播随堂练
	 */
	@Override
	public void saveCourseBreakpointPracticeInfo(List<CourseBreakPointPracticeDto> payload) {
		payload.forEach(practice -> {
			Object questioninfo = questionService.listQuestionByIds(practice.getQuestionId() + "");
			List<Map<String, Object>> result = (List<Map<String, Object>>) ZTKResponseUtil.build(questioninfo);
			if (CollectionUtils.isNotEmpty(result)) {
				Map<String, Object> question = result.get(0);

				// 更新试题统计表
				Example example = new Example(CourseExercisesStatistics.class);
				example.and().andEqualTo("courseId", practice.getCourseWareId())
						.andEqualTo("courseType", CourseType.RECORD.getCode())
						.andEqualTo("status", YesOrNoStatus.YES.getCode())
						// 1为随堂练
						.andEqualTo("type", YesOrNoStatus.YES.getCode());
				CourseExercisesStatistics courseExercisesStatistics = courseExercisesStatisticsMapper
						.selectOneByExample(example);

				if (null == courseExercisesStatistics) {
					// 获取课件绑定的试题数量
					List<CourseBreakpointQuestionDTO> quesitionList = courseBreakpointService
							.listAllQuestionId(CourseType.RECORD.getCode(), practice.getCourseWareId());
					courseExercisesStatistics = new CourseExercisesStatistics();
					courseExercisesStatistics.setStatus(YesOrNoStatus.YES.getCode());
					courseExercisesStatistics.setCorrects(practice.getCorrect() == 1 ? 1 : 0);
					courseExercisesStatistics.setCosts(practice.getTime());
					courseExercisesStatistics.setCounts(1);
					courseExercisesStatistics.setQuestionCount(quesitionList.size());
					courseExercisesStatistics.setCourseType(CourseType.RECORD.getCode());
					courseExercisesStatistics.setCourseId(practice.getCourseWareId().longValue());
					courseExercisesStatistics.setGmtModify(new Timestamp(System.currentTimeMillis()));
					courseExercisesStatistics.setGmtCreate(new Timestamp(System.currentTimeMillis()));
					courseExercisesStatisticsMapper.insertSelective(courseExercisesStatistics);
				} else {
					courseExercisesStatistics.setCounts(courseExercisesStatistics.getCounts() + 1);
					courseExercisesStatistics.setCosts(courseExercisesStatistics.getCosts() + practice.getTime());
					courseExercisesStatistics
							.setCorrects(courseExercisesStatistics.getCorrects() + practice.getCorrect() == 1 ? 1 : 0);
					courseExercisesStatistics.setGmtModify(new Timestamp(System.currentTimeMillis()));
					courseExercisesStatisticsMapper.updateByPrimaryKeySelective(courseExercisesStatistics);
				}

			}

		});
	}
}
