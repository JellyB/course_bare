package com.huatu.tiku.course.service.v6.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import com.huatu.common.utils.reflect.BeanUtil;
import com.huatu.tiku.course.netschool.api.v6.UserCourseServiceV6;
import com.huatu.tiku.course.service.v6.PeriodTestServiceV6;
import com.huatu.ztk.paper.vo.PeriodTestSubmitlPayload;

import lombok.extern.slf4j.Slf4j;

/**
 * 
 * @author zhangchong
 *
 */
@Service
@Slf4j
public class PeriodTestServiceV6Impl implements PeriodTestServiceV6 {
	
	@Autowired
	private UserCourseServiceV6 userCourseServiceV6;
	
	@Override
	@Async
	public void uploadPeriodStatus2PHP(PeriodTestSubmitlPayload payload) {
		
		userCourseServiceV6.stageTestStudyRecord(BeanUtil.toMap(payload));
	}

}
