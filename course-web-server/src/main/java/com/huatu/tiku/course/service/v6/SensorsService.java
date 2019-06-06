package com.huatu.tiku.course.service.v6;

import java.util.Map;

import com.huatu.tiku.course.consts.UserInfo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.google.common.collect.Maps;
import com.huatu.common.consts.SensorsEventEnum;
import com.huatu.tiku.course.bean.vo.CoursePracticeReportSensorsVo;
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
			//Integer uid = practiceData.getUserId();
			//NetSchoolResponse response = userServiceV4.getUserLevelBatch(Arrays.asList(uid + ""));
			//if (ResponseUtil.isSuccess(response)) {
				//List<Map<String, String>> userInfoList = (List<Map<String, String>>) response.getData();
				//String ucId = userInfoList.get(0).get("mobile");
				//log.info("reportCoursePracticeData ucId is:{},uid is:{}", ucId, uid);
				Map<String, Object> properties = Maps.newHashMap();
				// properties.put("couse_id", value);
				// properties.put("course_title", value);
				properties.put("class_id", practiceData.getCoursewareId());
				// properties.put("class_title", value);
				//properties.put("correct_number", practiceData.getRcount());
				//properties.put("exercise_done", practiceData.getDocount());
				//properties.put("exercise_duration", practiceData.getTimes());
				//properties.put("is_finish", false);
//				if (practiceData.getDocount() == practiceData.getQcount()) {
//					properties.put("is_finish", true);
//				}
				//获取课件名称
				
				properties.put("exercise_number", practiceData.getQcount());
				log.info("reportCoursePracticeData properties:{}", properties);
				sensorsAnalytics.track("9999", false, SensorsEventEnum.COURSE_PRACTICE_QUESTION_INFO.getCode(),
						properties);
				sensorsAnalytics.flush();
			//}
		} catch (Exception e) {
			log.error("reportCoursePracticeData error:{}", e);
		}

	}

	/**
	 * 课程送金币活动上报
	 * @param userInfo
	 */
	public void reportActivitySign(UserInfo userInfo) {
		try {
			log.info("course coins activity report start");
			Map<String, Object> properties = Maps.newHashMap();

			properties.put("coins", userInfo.getCoins());
			properties.put("time", userInfo.getTime());
			log.info("reportCoursePracticeData properties:{}", properties);
			sensorsAnalytics.track(userInfo.getUcId(), false, SensorsEventEnum.COURSE_ACTIVITY_COINS.getCode(),
					properties);
			sensorsAnalytics.flush();
			//}
		} catch (Exception e) {
			log.error("reportCoursePracticeData error:{}", e);
		}

	}

}
