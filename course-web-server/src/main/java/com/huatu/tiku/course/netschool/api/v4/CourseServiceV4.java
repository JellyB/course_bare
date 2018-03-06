package com.huatu.tiku.course.netschool.api.v4;

import com.huatu.tiku.course.bean.NetSchoolResponse;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.Map;

/**
 * @author hanchao
 * @date 2018/3/6 14:46
 */
public interface CourseServiceV4 {
    /**
     * 录播课程列表
     * @param params
     * @return
     */
    @GetMapping("/v3/classSearch_new.php")
    NetSchoolResponse findRecordingList(@RequestParam Map<String,Object> params);
}
