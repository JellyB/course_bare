package com.huatu.tiku.course.web.controller.v6.practice;

import com.huatu.common.SuccessMessage;
import com.huatu.springboot.web.version.mapping.annotation.ApiVersion;
import com.huatu.tiku.course.bean.practice.LiveCallbackBo;
import com.huatu.tiku.course.service.v1.practice.LiveCallBackService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections.CollectionUtils;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.concurrent.ExecutionException;

/**
 * Created by lijun on 2019/3/5 直播生成回放时回调 使用 - 直播 roomId 会对应多个
 * courseId,用户在直播答题过程中能够获取到对应的 courseId, - 生成直播回放时 每个courseId 会对应一个 新测 courseId.
 * 1.持久化直播课的排名统计信息（json） 2.持久化直播课中每道题的统计信息(json) 3.直播生成回放，给对应的用户生成一个回放答题卡信息
 * 4.直播绑定的试题信息拷贝至录播回放（未实现）
 */
@RestController
@RequestMapping("practice/liveCallBack")
@RequiredArgsConstructor
@ApiVersion("v6")
@Slf4j
public class LiveCallBackController {

	private final LiveCallBackService liveCallBackService;

	@PostMapping(value = "/{roomId}/liveCallBack")
	public Object liveCallBack(@PathVariable Long roomId, @RequestBody List<LiveCallbackBo> liveCallbackBoList)
			throws ExecutionException, InterruptedException {
		if (CollectionUtils.isNotEmpty(liveCallbackBoList)) {
			liveCallBackService.liveCallBackAllInfo(roomId, liveCallbackBoList);
		}
		return SuccessMessage.create();
	}

	/**
	 * 直播结束回调接口
	 * 
	 * @param roomId
	 * @param courseIdList
	 * @return
	 */
	@PostMapping(value = "/{roomId}")
	public Object liveOver(@PathVariable Long roomId, @RequestBody List<Integer> courseIdList) {
		//TODO 生成答题卡
		return SuccessMessage.create();
	}
}
