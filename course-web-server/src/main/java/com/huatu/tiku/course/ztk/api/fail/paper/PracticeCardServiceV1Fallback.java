package com.huatu.tiku.course.ztk.api.fail.paper;

import com.huatu.tiku.course.bean.NetSchoolResponse;
import com.huatu.tiku.course.util.ResponseUtil;
import com.huatu.tiku.course.util.ZTKResponseUtil;
import com.huatu.tiku.course.ztk.api.v1.paper.PracticeCardServiceV1;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.List;

/**
 * Created by lijun on 2018/6/22
 */
@Component
public class PracticeCardServiceV1Fallback implements PracticeCardServiceV1 {

    @Override
    public Object createCourseExercisesPracticeCard(Integer terminal, Integer subject, Integer uid, String name, Integer courseType, Long courseId, String questionId) {
        return ZTKResponseUtil.defaultResult();
    }

    @Override
    public Object createCourseBreakPointPracticeCard(Integer terminal, Integer subject, Integer uid, String name, Integer courseType, Long courseId, String questionId, List<Object> questionInfoList) {
        return ZTKResponseUtil.defaultResult();
    }

    @Override
    public Object getCourseExercisesCardInfo(long userId, List<HashMap<String, Object>> paramsList) {
        return ZTKResponseUtil.defaultResult();
    }

    @Override
    public Object getCourseBreakPointCardInfo(long userId, List<HashMap<String, Object>> paramsList) {
        return ZTKResponseUtil.defaultResult();
    }

    /**
     * 根据id获取答题卡信息
     *
     * @param token
     * @param terminal
     * @param id
     * @return
     */
    @Override
    public NetSchoolResponse getAnswerCard(String token, int terminal, long id) {
        return ResponseUtil.DEFAULT_PAGE_EMPTY;
    }

	@Override
	public Object createAndSaveAnswerCoursePracticeCard(Integer terminal, Integer subject, Integer uid, String name,
			Integer courseType, Long courseId, String questionIds, String[] answers, int[] corrects, int[] times,
			List<Object> questionInfoList) {
		 return ZTKResponseUtil.defaultResult();
	}

    /**
     * 批量获取随堂练习报告状态
     *
     * @param userId
     * @param paramsList
     * @return
     */
    @Override
    public Object getClassExerciseStatus(int userId, List<HashMap<String, Object>> paramsList) {
        return ResponseUtil.DEFAULT_PAGE_EMPTY;
    }
}
