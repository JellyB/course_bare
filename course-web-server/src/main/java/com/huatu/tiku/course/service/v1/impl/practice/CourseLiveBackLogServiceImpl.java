package com.huatu.tiku.course.service.v1.impl.practice;

import java.util.List;

import org.apache.commons.collections.CollectionUtils;
import org.springframework.stereotype.Service;

import com.huatu.tiku.course.service.v1.practice.CourseLiveBackLogService;
import com.huatu.tiku.entity.CourseLiveBackLog;

import lombok.extern.slf4j.Slf4j;
import service.impl.BaseServiceHelperImpl;
import tk.mybatis.mapper.entity.Example;
import tk.mybatis.mapper.weekend.WeekendSqls;

/**
 * 
 * @author zhangchong
 *
 */
@Service
@Slf4j
public class CourseLiveBackLogServiceImpl extends BaseServiceHelperImpl<CourseLiveBackLog>
		implements CourseLiveBackLogService {

	public CourseLiveBackLogServiceImpl() {
		super(CourseLiveBackLog.class);
	}

	@Override
	public CourseLiveBackLog findByRoomIdAndLiveCoursewareId(Long roomId, Long coursewareId) {

		final WeekendSqls<CourseLiveBackLog> weekendSqls = WeekendSqls.<CourseLiveBackLog>custom()
				.andEqualTo(CourseLiveBackLog::getRoomId, roomId)
				.andEqualTo(CourseLiveBackLog::getLiveBackCoursewareId, coursewareId);
		final Example example = Example.builder(CourseLiveBackLog.class).where(weekendSqls).build();
		List<CourseLiveBackLog> courseLiveBackLogList = selectByExample(example);
		if (CollectionUtils.isNotEmpty(courseLiveBackLogList)) {
			return courseLiveBackLogList.get(0);
		}
		return null;
	}

}
