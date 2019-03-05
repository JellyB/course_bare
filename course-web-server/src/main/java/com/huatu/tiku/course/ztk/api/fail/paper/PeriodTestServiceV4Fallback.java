package com.huatu.tiku.course.ztk.api.fail.paper;

import java.util.Set;

import org.springframework.stereotype.Component;

import com.huatu.tiku.course.bean.NetSchoolResponse;
import com.huatu.tiku.course.ztk.api.v4.paper.PeriodTestServiceV4;

/**
 * 
 * @author zhangchong
 *
 */
@Component
public class PeriodTestServiceV4Fallback implements PeriodTestServiceV4 {

	@Override
	public NetSchoolResponse getPaperStatus(int userId, long syllabusId, long paperId) {
		return NetSchoolResponse.DEFAULT_ERROR;
	}

	@Override
	public NetSchoolResponse getPaperStatusBath(int userId, Set<String> paperSyllabusSet) {
		return NetSchoolResponse.DEFAULT_ERROR;
	}

}
