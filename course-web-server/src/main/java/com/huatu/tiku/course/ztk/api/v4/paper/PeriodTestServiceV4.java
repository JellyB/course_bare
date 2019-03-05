package com.huatu.tiku.course.ztk.api.v4.paper;

import java.util.Set;

import org.springframework.cloud.netflix.feign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import com.huatu.tiku.course.bean.NetSchoolResponse;
import com.huatu.tiku.course.ztk.api.fail.paper.PeriodTestServiceV4Fallback;

/**
 * 
 * @author zhangchong
 *
 */
@FeignClient(value = "ztk-service", fallback = PeriodTestServiceV4Fallback.class, path = "/p")
public interface PeriodTestServiceV4 {

	/**
	 * 获取用户试卷信息
	 * 
	 * @param userId
	 * @param syllabusId
	 * @param paperId
	 * @return
	 */
	@GetMapping(value = "/v4/periodTest/{userId}/{syllabusId}/{paperId}")
	NetSchoolResponse getPaperStatus(@PathVariable("userId") int userId, @PathVariable("syllabusId") long syllabusId,
			@PathVariable("paperId") long paperId);

	/**
	 * 批量获取用户试卷信息
	 * @param userId
	 * @param paperSyllabusSet
	 * @return
	 */
	@PostMapping(value = "/v4/periodTest/getPaperStatusBath/{userId}")
	NetSchoolResponse getPaperStatusBath(@PathVariable("userId") int userId, @RequestBody Set<String> paperSyllabusSet);
}
