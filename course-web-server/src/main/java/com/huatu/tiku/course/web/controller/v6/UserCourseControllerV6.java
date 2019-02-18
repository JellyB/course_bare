package com.huatu.tiku.course.web.controller.v6;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.huatu.common.SuccessMessage;
import com.huatu.springboot.web.version.mapping.annotation.ApiVersion;
import com.huatu.tiku.common.bean.user.UserSession;
import com.huatu.tiku.course.bean.NetSchoolResponse;
import com.huatu.tiku.course.common.StudyTypeEnum;
import com.huatu.tiku.course.netschool.api.v6.UserCourseServiceV6;
import com.huatu.tiku.course.service.v6.CourseBizV6Service;
import com.huatu.tiku.course.spring.conf.aspect.mapParam.LocalMapParam;
import com.huatu.tiku.course.spring.conf.aspect.mapParam.LocalMapParamHandler;
import com.huatu.tiku.course.util.ResponseUtil;
import com.huatu.tiku.springboot.users.support.Token;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

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
        Map<String,Integer> result = Maps.newHashMap();
        result.put(StudyTypeEnum.COURSE_WORK.getType(), 5);
        result.put(StudyTypeEnum.PERIOD_TEST.getType(), 10);
        return result;
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

        return SuccessMessage.create("操作成功！");
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
        return SuccessMessage.create("操作成功");
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

        List<CourseInfo> list = Lists.newArrayList();
        List<CourseWareInfo> courseWareInfoList1 = Lists.newArrayList();
        List<CourseWareInfo> courseWareInfoList2 = Lists.newArrayList();
        courseWareInfoList1.add(CourseWareInfo.builder()
                .courseWareTitle("2014年资料分析真题-1")
                .courseWareId(942913)
                .videoLength("高清 - 28分钟37秒")
                .serialNumber(1)
                .answerCardId(12345678L)
                .questionIds("26603,26604,26605,26606,26607,26608")
                .answerCardInfo("剩余4/6题")
                .isAlert(1)
                .build());
        courseWareInfoList1.add(CourseWareInfo.builder()
                .courseWareTitle("2014年资料分析真题-2")
                .courseWareId(942914)
                .videoLength("高清 - 28分钟51秒")
                .serialNumber(2)
                .answerCardId(12345679L)
                .questionIds("26613,26614,26615,26616,26617,26618")
                .answerCardInfo("剩余4/6题")
                .isAlert(0)
                .build());

        courseWareInfoList2.add(CourseWareInfo.builder()
                .courseWareTitle("管理常识-1")
                .courseWareId(942951)
                .videoLength("高清 - 28分钟37秒")
                .serialNumber(1)
                .answerCardId(12345681L)
                .questionIds("26613,26614,26615,26616,26617,26618")
                .answerCardInfo("剩余4/6题")
                .isAlert(1)
                .build());
        courseWareInfoList2.add(CourseWareInfo.builder()
                .courseWareTitle("管理常识-3")
                .courseWareId(942952)
                .videoLength("高清 - 28分钟37秒")
                .serialNumber(3)
                .answerCardId(12345683L)
                .questionIds("26633,26634,26635,26636,26637,26638")
                .answerCardInfo("剩余4/6题")
                .isAlert(1)
                .build());
        courseWareInfoList2.add(CourseWareInfo.builder()
                .courseWareTitle("管理常识-5")
                .courseWareId(942955)
                .videoLength("高清 - 28分钟37秒")
                .serialNumber(5)
                .answerCardId(12345685L)
                .questionIds("26653,26654,26655,26656,26657,26658")
                .answerCardInfo("剩余4/6题")
                .isAlert(1)
                .build());
        courseWareInfoList2.add(CourseWareInfo.builder()
                .courseWareTitle("管理常识-7")
                .courseWareId(942957)
                .videoLength("高清 - 28分钟37秒")
                .serialNumber(7)
                .answerCardId(12345687L)
                .questionIds("26673,26674,26675,26676,26677,26678")
                .answerCardInfo("剩余4/6题")
                .isAlert(1)
                .build());
        courseWareInfoList2.add(CourseWareInfo.builder()
                .courseWareTitle("管理常识-9")
                .courseWareId(942959)
                .videoLength("高清 - 28分钟37秒")
                .serialNumber(9)
                .answerCardId(12345689L)
                .questionIds("26693,26694,26695,26696,26697,26698")
                .answerCardInfo("剩余4/6题")
                .isAlert(1)
                .build());


        list.add(CourseInfo.builder()
                .courseId(98017)
                .courseTitle("模考专用】14年国考资料分析真题")
                .undoCount(2)
                .wareInfoList(courseWareInfoList1)
                .build());
        list.add(CourseInfo.builder()
                .courseId(98018)
                .courseTitle("《管理常识》模块真题精讲")
                .undoCount(5)
                .wareInfoList(courseWareInfoList2)
                .build());
        return list;
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
}
