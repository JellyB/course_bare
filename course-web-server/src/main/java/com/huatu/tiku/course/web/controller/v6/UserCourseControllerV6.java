package com.huatu.tiku.course.web.controller.v6;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.huatu.common.utils.collection.HashMapBuilder;
import com.huatu.tiku.course.service.manager.CourseExercisesProcessLogManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.huatu.springboot.web.version.mapping.annotation.ApiVersion;
import com.huatu.tiku.common.bean.user.UserSession;
import com.huatu.tiku.course.bean.NetSchoolResponse;
import com.huatu.tiku.course.netschool.api.v6.UserCourseServiceV6;
import com.huatu.tiku.course.service.v6.CourseBizV6Service;
import com.huatu.tiku.course.service.v6.CourseServiceV6Biz;
import com.huatu.tiku.course.spring.conf.aspect.mapParam.LocalMapParam;
import com.huatu.tiku.course.spring.conf.aspect.mapParam.LocalMapParamHandler;
import com.huatu.tiku.course.util.ResponseUtil;
import com.huatu.tiku.springboot.users.support.Token;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
/**
 * 描述：我的课程接口
 *
 * @author biguodong
 * Create time 2018-11-26 下午5:28
 **/

@Slf4j
@RestController
@RequestMapping("/my")
@ApiVersion("v6")
public class UserCourseControllerV6 {


    @Autowired
    private UserCourseServiceV6 userCourseService;

    @Autowired
    private CourseBizV6Service courseBizV6Service;
    
    @Autowired
    private CourseServiceV6Biz courseServiceV6Biz;

    @Autowired
    private CourseExercisesProcessLogManager courseExercisesProcessLogManager;


    /**
     * 获取我的学习日历接口
     * @param userSession
     * @param type
     * @param cv
     * @param terminal
     * @param date
     * @return
     */
    @LocalMapParam(checkToken = true)
    @GetMapping(value = "learnCalendar")
    public Object obtainLearnCalendar(@Token UserSession userSession,
                                      @RequestHeader(value = "cv") String cv,
                                      @RequestHeader(value = "terminal") int terminal,
                                      @RequestParam(value = "date") String date,
                                      @RequestParam(value = "type") String type){
        Map<String,Object> params = LocalMapParamHandler.get();
        return courseBizV6Service.obtainLearnCalender(params);
    }

    /**
     * 我的学习界面 - 课后作业&阶段考试未完成数量
     * @param userSession
     * @param cv
     * @param terminal
     * @return
     */
    @LocalMapParam(checkToken = true)
    @GetMapping(value = "unFinishNum")
    public Object obtainUnFinishedNum(@Token UserSession userSession,
                                      @RequestHeader(value = "cv") String cv,
                                      @RequestHeader(value = "terminal") int terminal){
        return courseExercisesProcessLogManager.getCountByType(userSession.getId());
    }

    /**
     * 全部已读
     * @param userSession
     * @param type
     * @return
     */
    @PutMapping(value = "allRead/{type}")
    public Object allReadCourseWork(@Token UserSession userSession,
                                    @PathVariable(value = "type")String type){

        return courseExercisesProcessLogManager.allReadByType(userSession.getId(), type);
    }

    /**
     * 课后作业&阶段测试 单条已读
     * @param userSession
     * @param type
     * @param id
     * @return
     */
    @PutMapping(value = "oneRead/{type}/{id}")
    public Object readOneCourseWork(@Token UserSession userSession,
                                    @PathVariable(value = "type") String type,
                                    @PathVariable(value = "id")int id){
        return courseExercisesProcessLogManager.readyOne(id, type);
    }

    /**
     * 课后作业&阶段考试列表
     * @param userSession
     * @return
     */
    @GetMapping(value = "courseWork/detailList")
    public Object studyList(@Token UserSession userSession,
                            @RequestParam(value = "page", defaultValue = "1")int page,
                            @RequestParam(value = "size", defaultValue = "20") int size){

        return courseExercisesProcessLogManager.courseWorkList(userSession.getId(), page, size);
    }

   
    /**
     * 阶段测试列表
     * @param userSession
     * @param page
     * @param size
     * @return
     */
    @LocalMapParam(checkToken = true)
    @GetMapping(value = "periodTest/detailList")
    public Object periodTestList(@Token UserSession userSession,
                            @RequestParam(value = "page", defaultValue = "1")int page,
                            @RequestParam(value = "pageSize", defaultValue = "20") int size){
    	 Map<String,Object> params = LocalMapParamHandler.get();
    	 params.put("userId", userSession.getId());
        return courseServiceV6Biz.periodTestList(params);
    }

    /**
     * 我的课后作业报告
     * @param userSession
     * @param cv
     * @param terminal
     * @param cardId
     * @return
     */
    @GetMapping(value = "/courseWork/{id}")
    @LocalMapParam(checkToken = true)
    public Object testReport(@Token UserSession userSession,
                             @RequestHeader(value = "cv", defaultValue = "1.0") String cv,
                             @RequestHeader(value = "terminal") int terminal,
                             @PathVariable(value = "id") long cardId){
        return courseServiceV6Biz.courseWorkReport(userSession, terminal, cardId);
    }


    /**
     * 听课记录 & 课后作业 学习报告
     * @param userSession
     * @param terminal
     * @param cv
     * @param bjyRoomId
     * @param classId
     * @param netClassId
     * @param courseWareId
     * @param videoType
     * @return
     */
    @GetMapping(value = "/learnReport/{syllabusId}")
    @LocalMapParam(checkToken = true)
    public Object learnReport(@Token UserSession userSession,
                              @RequestHeader(value = "terminal") int terminal,
                              @RequestHeader(value = "cv", defaultValue = "1.0") String cv,
                              @RequestParam(value = "bjyRoomId") String bjyRoomId,
                              @RequestParam(value = "classId") long classId,
                              @RequestParam(value = "netClassId") long netClassId,
                              @RequestParam(value = "lessonId") long courseWareId,
                              @RequestParam(value = "videoType") int videoType,
                              @RequestParam(value = "exerciseCardId") long exerciseCardId,
                              @RequestParam(value = "classCardId") long classCardId){

        courseServiceV6Biz.learnReport(userSession, bjyRoomId, classId, netClassId, courseWareId, videoType, exerciseCardId, classCardId, terminal);
        Map<String,Object> result = Maps.newHashMap();
        Map<String,Object> report = Maps.newHashMap();
        Map<String,Object> classPractice = Maps.newHashMap();
        Map<String,Object> workPractice = Maps.newHashMap();
        List<HashMap> points = Lists.newArrayList();


        classPractice.put("corrects", new int[]{1,2,2,2,2,1,1,1,1,1});
        classPractice.put("answers", new String[]{"1", "2", "3", "4", "4", "3", "2", "1", "2", "2"});
        classPractice.put("doubts", new int[] {1,1,1,1,1,0,0,0,0,0});
        classPractice.put("id", "8205958822857731640");
        classPractice.put("correctCount", 5);//答对
        classPractice.put("classAvgTimeOut", 150);//班级平均用时；
        classPractice.put("classAvgCorrect", 6);//班级平均答对;
        workPractice.put("classAvgTimeOut", 150);
        classPractice.put("avgTimeOut", 150);
        classPractice.put("timeInfo", "09/30 13:30");

        workPractice.put("corrects", new int[]{1,2,2,2,2,1,1,1,1,1});
        workPractice.put("answers", new String[]{"1", "2", "3", "4", "4", "3", "2", "1", "2", "2"});
        workPractice.put("doubts", new int[] {1,1,1,1,1,0,0,0,0,0});
        workPractice.put("id","8205958822857731640");
        workPractice.put("correctCount", 5);//答对
        workPractice.put("avgTimeOut", 150);//平均用时；
        workPractice.put("classAvgCorrect", 6);//平均答对;
        workPractice.put("classAvgTimeOut", 150);
        workPractice.put("classAvgTimeOut", 150);
        workPractice.put("timeInfo", "09/30 13:30");

        points.add(HashMapBuilder.newBuilder().put("name", "公文").put("id", 12345).build());
        points.add(HashMapBuilder.newBuilder().put("name", "管理").put("id", 2345).build());
        points.add(HashMapBuilder.newBuilder().put("name", "科技").put("id", 3456).build());
        points.add(HashMapBuilder.newBuilder().put("name", "人文").put("id", 4567).build());
        workPractice.put("points", points);
        workPractice.put("finishInfo", "完成了课程89%的内容，课后作业正确率低于45%，勤加练习才能将学到的内容转化为自己的技能。");

        report.put("learnTime", 123);//学习时长
        report.put("gold", 15);//获取 15 图币
        report.put("learnPercent", 86);//学习课程内容
        report.put("abovePercent", 34);

        result.put("classPractice", classPractice);
        result.put("courseWorkPractice", workPractice);
        result.put("liveReport", report);

        return result;


    }



    /**
     * 获取已过期课程
     * @param userSession
     * @param page
     * @param pageSize
     * @return
     */
    @LocalMapParam(checkToken = true)
    @GetMapping(value = "expiredCourses")
    public Object obtainExpiredCourses(@Token UserSession userSession,
                                       @RequestHeader(value = "terminal") int terminal,
                                       @RequestParam(value = "page", defaultValue = "1") int page,
                                       @RequestParam(value = "pageSize", defaultValue = "20") int pageSize){

        Map<String,Object> params = LocalMapParamHandler.get();
        NetSchoolResponse netSchoolResponse = userCourseService.obtainExpiredCourses(params);
        return ResponseUtil.build(netSchoolResponse);
    }


    /**
     * 我的课程筛选列表
     * @param userSession
     * @return
     */
    @LocalMapParam(checkToken = true)
    @GetMapping(value = "filteredCourses")
    public Object obtainFilteredCourses(@Token UserSession userSession){
        Map<String,Object> params = LocalMapParamHandler.get();
        NetSchoolResponse netSchoolResponse = userCourseService.obtainCourseFilterList(params);
        return ResponseUtil.build(netSchoolResponse);
    }

    /**
     * 我的课程接口
     * @param userSession
     * @param examStatus
     * @param keyWord
     * @param priceStatus
     * @param recentlyStudy
     * @param studyStatus
     * @param teacherId
     * @param page
     * @param pageSize
     * @return
     */
    @LocalMapParam(checkToken = true)
    @GetMapping(value = "courses")
    public Object obtainMineCourses(
                                    @Token UserSession userSession,
                                    @RequestParam(value = "examStatus", required = false) String examStatus,
                                    @RequestParam(value = "isDelete", required = false, defaultValue = "0") int isDelete,
                                    @RequestParam(value = "keyWord", required = false) String keyWord,
                                    @RequestParam(value = "priceStatus", required = false) String priceStatus,
                                    @RequestParam(value = "recentlyStudy", defaultValue = "", required = false) String recentlyStudy,
                                    @RequestParam(value = "studyStatus", required = false) String studyStatus,
                                    @RequestParam(value = "teacherId", required = false) String teacherId,
                                    @RequestParam(value = "page", defaultValue = "1", required = false) int page,
                                    @RequestParam(value = "pageSize", defaultValue = "20", required = false) int pageSize){
        Map<String,Object> params = LocalMapParamHandler.get();
        return courseBizV6Service.obtainMineCourses(params);
    }


    /**
     * 一键清除我的已过期课程
     * @param cv
     * @param userSession
     * @return
     */
    @LocalMapParam
    @GetMapping(value = "clearExpired")
    public Object clearExpiredCourses(@RequestHeader(value = "cv") String cv,
                                      @Token UserSession userSession){
        Map<String,Object> params = LocalMapParamHandler.get();
        NetSchoolResponse netSchoolResponse = userCourseService.clearExpiredCourses(params);
        return ResponseUtil.build(netSchoolResponse);
    }


    /**
     * 课程所属考试接口
     * @param userSession
     * @return
     */
    @LocalMapParam
    @GetMapping(value = "cateList")
    public Object cateList(@Token UserSession userSession){
        Map<String,Object> params = LocalMapParamHandler.get();
        NetSchoolResponse netSchoolResponse = userCourseService.cateList(params);
        return ResponseUtil.build(netSchoolResponse);
    }


    /**
     * 列表设置考试类型
     * @param userSession
     * @return
     */
    @LocalMapParam
    @PutMapping(value = "category")
    public Object setCategory(@Token UserSession userSession,
                              @RequestParam(value = "setList") String setList){
        Map<String,Object> params = LocalMapParamHandler.get();
        NetSchoolResponse netSchoolResponse = userCourseService.setCategory(params);
        return ResponseUtil.build(netSchoolResponse);
    }

    /**
     * 直播学习记录上报
     * @param userSession
     * @param syllabusId
     * @return
     */
    @LocalMapParam
    @PostMapping(value = "liveRecord")
    public Object saveLiveRecord(@Token UserSession userSession,
                                 @RequestParam(defaultValue = "0") int syllabusId){

        Map<String,Object> params = LocalMapParamHandler.get();
        NetSchoolResponse netSchoolResponse = userCourseService.saveLiveRecord(params);
        return ResponseUtil.build(netSchoolResponse);

    }

    @AllArgsConstructor
    @Setter
    @Getter
    @Builder
    public static class CourseInfo{
        private String courseTitle;
        private int courseId;
        private int undoCount;
        private List<CourseWareInfo> wareInfoList;
    }

    @AllArgsConstructor
    @Setter
    @Getter
    @Builder
    public static class CourseWareInfo{
        private String courseWareTitle;
        private int courseWareId;
        private String videoLength;
        private int serialNumber;
        private long answerCardId;
        private String questionIds;
        private String answerCardInfo;
        private int isAlert;
    }



    @AllArgsConstructor
    @Setter
    @Getter
    @Builder
    public static class Points{
        private int id;
        private String name;
        private int qnum;
        private int rnum;
        private int wnum;
        private int unum;
        private int times;
        private int speed;
        private int level;
        private float accuracy;
    }
}
