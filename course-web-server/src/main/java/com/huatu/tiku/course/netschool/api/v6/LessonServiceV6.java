package com.huatu.tiku.course.netschool.api.v6;

import com.huatu.tiku.course.bean.NetSchoolResponse;
import org.springframework.cloud.netflix.feign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.Map;

/**
 * 描述：
 *
 * @author biguodong
 * Create time 2019-01-17 下午6:23
 **/

@FeignClient(value = "o-course-service", path = "/lumenapi")
public interface LessonServiceV6 {


    /**
     * 图书扫码听课详情
     * @param params
     * @return
     */
    @GetMapping(value = "/v5/c/lesson/play_lessions")
    NetSchoolResponse playLesson(@RequestParam Map<String, Object> params);


    /**
     * 课件收藏列表
     * @param params
     * @return
     */
    @GetMapping(value = "/v5/c/lesson/collection_list")
    NetSchoolResponse collections(@RequestParam Map<String, Object> params);


    /**
     * 课件添加收藏
     * @param params
     * @return
     */
    @PutMapping(value = "/v5/c/lesson/collection_add")
    NetSchoolResponse collectionAdd(@RequestParam Map<String, Object> params);

    /**
     * 课件取消收藏
     * @param params
     * @return
     */
    @PutMapping(value = "/v5/c/lesson/collection_cancel")
    NetSchoolResponse collectionCancel(@RequestParam Map<String, Object> params);

    /**
     * 我的学习时长
     * @param bjyRoomId 百家云 roomId
     * @param userName 学员昵称
     * @param classId 课件上层课程id
     * @param netClassId 课程上层售课id
     * @param courseWareId 课件id
     * @param videoType 视频类型 2 直播 3 回放
     * @return
     */
    @GetMapping(value = "/v5/c/lesson/study_report")
    NetSchoolResponse studyReport(@RequestParam(value = "bjyRoomId") int bjyRoomId,
                                  @RequestParam(value = "userName") String userName,
                                  @RequestParam(value = "classId") int classId,
                                  @RequestParam(value = "netClassId") int netClassId,
                                  @RequestParam(value = "lessonId") int courseWareId,
                                  @RequestParam(value = "videoType") int videoType);




}
