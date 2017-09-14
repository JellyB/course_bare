package com.huatu.tiku.course.netschool.api.v3;

import com.huatu.tiku.course.netschool.bean.NetSchoolResponse;
import org.springframework.cloud.netflix.feign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.Map;

/**
 * @author hanchao
 * @date 2017/9/12 21:58
 */
//fallbackFactory = CourseServiceV3FallbackFactory.class
@FeignClient(value = "course-service")
public interface CourseServiceV3 {
    /**
     * 获取商品购买量
     * @param params
     * @return
     */
    @PostMapping(value="/v3/getBuyNum.php")
    NetSchoolResponse getCourseLimit(@RequestParam Map<String,Object> params);

    /**
     * 录播课程列表
     * @param params
     * @return
     */
    @GetMapping("/v3/classSearch.php")
    NetSchoolResponse findRecordingList(@RequestParam Map<String,Object> params);


    /**
     * 全部直播列表
     * @param params
     * @return
     */
    @GetMapping("/v3/collectionClassSearch.php")
    NetSchoolResponse findLiveList(@RequestParam Map<String,Object> params);


    /**
     * 课程详情接口
     * @param rid
     * @return
     */
    @GetMapping("/v3/Class_Details_Buy.php")
    NetSchoolResponse getCourseDetail(@RequestParam("rid") int rid);


    /**
     * 课程播放接口，获取课程cc平台信息等
     * @param params
     * @return
     */
    @PostMapping("/v3/lecture.php")
    NetSchoolResponse getCourseSecrInfo(@RequestParam Map<String,Object> params);


    /**
     * 获取课程下的老师列表
     * @param rid
     * @return
     */
    @PostMapping("/v3/teacherList.php?action=list")
    NetSchoolResponse findTeachersByCourse(@RequestParam("rid") int rid);

    /**
     * 获取课程大纲
     * @param rid
     * @return
     */
    @GetMapping("/v3/classContents.php")
    NetSchoolResponse findTimetable(@RequestParam("rid") int rid);
}
