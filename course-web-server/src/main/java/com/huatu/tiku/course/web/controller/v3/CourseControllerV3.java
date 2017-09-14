package com.huatu.tiku.course.web.controller.v3;

import com.google.common.collect.Maps;
import com.huatu.common.exception.BizException;
import com.huatu.common.utils.collection.HashMapBuilder;
import com.huatu.tiku.common.bean.AreaConstants;
import com.huatu.tiku.course.netschool.api.v3.CourseServiceV3;
import com.huatu.tiku.course.service.CourseBizService;
import com.huatu.tiku.course.util.ResponseUtil;
import com.huatu.tiku.springboot.users.bean.UserSession;
import com.huatu.tiku.springboot.users.support.Token;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Map;
import java.util.concurrent.ExecutionException;

/**
 * @author hanchao
 * @date 2017/9/13 15:41
 */
@RestController
@RequestMapping(value = "v3/courses")
public class CourseControllerV3 {

    @Autowired
    private CourseServiceV3 courseServiceV3;
    @Autowired
    private CourseBizService courseBizService;
    /**
     * 录播课程列表
     * @param categoryid
     * @param orderid
     * @param page
     * @param userSession
     * @return
     */
    @GetMapping("/recordings")
    public Object recordingList(@RequestParam int categoryid,
                             @RequestParam int orderid,
                             @RequestParam int page,
                             @RequestParam int subjectid,
                             @RequestParam String keyword,
                             @Token UserSession userSession){
        int provinceId = AreaConstants.getNetSchoolProvinceId(userSession.getArea());
        Map<String,Object> params = HashMapBuilder.<String,Object>newBuilder()
                .put("categoryid",categoryid)
                .put("orderid",orderid)
                .put("page",page)
                .put("subjectid",subjectid)
                .put("keyword",keyword)
                .put("provinceid",provinceId).build();

        return courseServiceV3.findRecordingList(params);
    }

    /**
     * 获取直播课程列表
     * @param orderid
     * @param page
     * @param priceid
     * @param userSession
     * @return
     * @throws InterruptedException
     * @throws ExecutionException
     * @throws BizException
     */
    @GetMapping("/lives")
    public Object liveList(@RequestParam int orderid,
                           @RequestParam int page,
                           @RequestParam int priceid,
                           @Token UserSession userSession) throws InterruptedException, ExecutionException, BizException {
        Map<String,Object> params = HashMapBuilder.<String,Object>newBuilder()
                .put("orderid",orderid)
                .put("page",page)
                .put("priceid",priceid).build();
        return courseBizService.getCourseListV3(userSession.getUname(),params);
    }



    /**
     * 课程详情页
     * @param userSession
     * @param rid
     * @return
     * @throws BizException
     */
    @GetMapping("/{rid}")
    public Object getCourseDetail(@Token UserSession userSession,
                                      @PathVariable int rid) throws BizException {
        //return ResponseUtil.build(courseServiceV3.findTimetable(rid));
        return null;
    }



    /**
     * 课程播放接口
     * @param userSession
     * @param rid
     * @return
     * @throws BizException
     */
    @GetMapping("/{rid}/secrinfo")
    public Object getCourseSecrInfo(@Token UserSession userSession,
                                    @PathVariable int rid) throws BizException {
        Map<String,Object> params = Maps.newHashMap();
        params.put("rid",rid);
        params.put("username",userSession.getUname());

        return ResponseUtil.build(courseServiceV3.getCourseSecrInfo(params));
    }


    /**
     * 课程播放接口
     * @param userSession
     * @param rid
     * @return
     * @throws BizException
     */
    @GetMapping("/{rid}/teachers")
    public Object findCourseTeachers(@Token UserSession userSession,
                                    @PathVariable int rid) throws BizException {
        return ResponseUtil.build(courseServiceV3.findTeachersByCourse(rid));
    }


    /**
     * 课程大纲
     * @param userSession
     * @param rid
     * @return
     * @throws BizException
     */
    @GetMapping("/{rid}/timetable")
    public Object findCourseTimetable(@Token UserSession userSession,
                                     @PathVariable int rid) throws BizException {
        return ResponseUtil.build(courseServiceV3.findTimetable(rid));
    }






}
