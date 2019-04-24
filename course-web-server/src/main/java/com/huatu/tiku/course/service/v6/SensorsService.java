package com.huatu.tiku.course.service.v6;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.google.common.collect.Maps;
import com.huatu.common.consts.SensorsEventEnum;
import com.huatu.tiku.course.bean.NetSchoolResponse;
import com.huatu.tiku.course.bean.vo.CoursePracticeReportSensorsVo;
import com.huatu.tiku.course.util.ResponseUtil;
import com.huatu.tiku.course.ztk.api.v4.user.UserServiceV4;
import com.sensorsdata.analytics.javasdk.SensorsAnalytics;

import lombok.extern.slf4j.Slf4j;

/**
 * 神策上报
 * 
 * @author zhangchong
 *
 */
@Service
@Slf4j
public class SensorsService {

	@Autowired
	private UserServiceV4 userServiceV4;

	@Autowired
	private SensorsAnalytics sensorsAnalytics;

	/**
	 * 上报随堂练数据
	 * 
	 * @param practiceData
	 */
	public void reportCoursePracticeData(CoursePracticeReportSensorsVo practiceData) {
		try {
			log.info("reportCoursePracticeData start");
			Integer uid = practiceData.getUserId();
			NetSchoolResponse response = userServiceV4.getUserLevelBatch(Arrays.asList(uid + ""));
			if (ResponseUtil.isSuccess(response)) {
				List<Map<String, String>> userInfoList = (List<Map<String, String>>) response.getData();
				String ucId = userInfoList.get(0).get("ucenterId");
				log.info("reportCoursePracticeData ucId is:{},uid is:{}", ucId, uid);
				Map<String, Object> properties = Maps.newHashMap();
				// properties.put("couse_id", value);
				// properties.put("course_title", value);
				properties.put("class_id", practiceData.getCoursewareId());
				// properties.put("class_title", value);
				properties.put("correct_number", practiceData.getRcount());
				properties.put("exercise_done", practiceData.getDocount());
				properties.put("exercise_duration", practiceData.getTimes());
				properties.put("is_finish", false);
				if (practiceData.getDocount() == practiceData.getQcount()) {
					properties.put("is_finish", true);
				}
				properties.put("exercise_number", practiceData.getQcount());
				log.info("reportCoursePracticeData properties:{}", properties);
				sensorsAnalytics.track(ucId, true, SensorsEventEnum.COURSE_PRACTICE_COMMIT_ANSWER_SUCCEED.getCode(),
						properties);
			}
		} catch (Exception e) {
			log.error("reportCoursePracticeData error:{}", e);
		}

	}

}
