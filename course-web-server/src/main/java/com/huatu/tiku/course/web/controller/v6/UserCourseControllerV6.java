package com.huatu.tiku.course.web.controller.v6;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.huatu.common.utils.collection.HashMapBuilder;
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
import com.huatu.common.SuccessMessage;
import com.huatu.springboot.web.version.mapping.annotation.ApiVersion;
import com.huatu.tiku.common.bean.user.UserSession;
import com.huatu.tiku.course.bean.NetSchoolResponse;
import com.huatu.tiku.course.common.StudyTypeEnum;
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
        result.put(StudyTypeEnum.COURSE_WORK.getKey(), 5);
        result.put(StudyTypeEnum.PERIOD_TEST.getKey(), 5);
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
    
    @GetMapping(value = "/courseWork/{id}")
    @LocalMapParam(checkToken = true)
    public Object testReport(@Token UserSession userSession,
                             @RequestHeader(value = "cv") String cv,
                             @RequestHeader(value = "terminal") int terminal,
                             @PathVariable(value = "id") int id){
        HashMap<String,Object> result = Maps.newHashMap();
        List<RankInfo> rankInfos = Lists.newArrayList();
        List<Points> points = Lists.newArrayList();
        points.add(Points.builder().id(642).name("判断推理").qnum(40).rnum(20).wnum(10).unum(10).times(45).speed(1).level(3).accuracy(22.5f).build());
        points.add(Points.builder().id(644).name("数量类").qnum(40).rnum(20).wnum(10).unum(10).times(45).speed(1).level(3).accuracy(22.5f).build());
        points.add(Points.builder().id(661).name("位置类").qnum(40).rnum(20).wnum(10).unum(10).times(45).speed(1).level(3).accuracy(22.5f).build());
        points.add(Points.builder().id(680).name("实体信息").qnum(40).rnum(20).wnum(10).unum(10).times(45).speed(1).level(3).accuracy(22.5f).build());
        points.add(Points.builder().id(1023).name("属性类").qnum(40).rnum(20).wnum(10).unum(10).times(45).speed(1).level(3).accuracy(22.5f).build());
        rankInfos.add(RankInfo.builder().rank(1).uid(233982730L).uname("app_ztk1156193081").avatar("http://tiku.huatu.com/cdn/images/vhuatu/avatars/default.png").rcount(20).expendTime(300).build());
        rankInfos.add(RankInfo.builder().rank(2).uid(233982729L).uname("app_ztk1665335536").avatar("http://tiku.huatu.com/cdn/images/vhuatu/avatars/default.png").rcount(20).expendTime(300).build());
        rankInfos.add(RankInfo.builder().rank(3).uid(233982728L).uname("app_ztk1095938062").avatar("http://tiku.huatu.com/cdn/images/vhuatu/avatars/default.png").rcount(20).expendTime(300).build());
        rankInfos.add(RankInfo.builder().rank(4).uid(233982727L).uname("0013590408802").avatar("http://tiku.huatu.com/cdn/images/vhuatu/avatars/default.png").rcount(20).expendTime(300).build());
        rankInfos.add(RankInfo.builder().rank(5).uid(233982726L).uname("app_ztk969032785").avatar("http://tiku.huatu.com/cdn/images/vhuatu/avatars/default.png").rcount(20).expendTime(300).build());
        rankInfos.add(RankInfo.builder().rank(6).uid(233982725L).uname("app_ztk1972809520").avatar("http://tiku.huatu.com/cdn/images/vhuatu/avatars/default.png").rcount(20).expendTime(300).build());
        rankInfos.add(RankInfo.builder().rank(7).uid(233982724L).uname("app_ztk1552890135").avatar("http://tiku.huatu.com/cdn/images/vhuatu/avatars/default.png").rcount(20).expendTime(300).build());
        rankInfos.add(RankInfo.builder().rank(8).uid(233982723L).uname("app_ztk988802909").avatar("http://tiku.huatu.com/cdn/images/vhuatu/avatars/default.png").rcount(20).expendTime(300).build());
        rankInfos.add(RankInfo.builder().rank(9).uid(233982722L).uname("app_ztk1572338085").avatar("http://tiku.huatu.com/cdn/images/vhuatu/avatars/default.png").rcount(20).expendTime(300).build());
        rankInfos.add(RankInfo.builder().rank(10).uid(233982721L).uname("app_ztk722506085").avatar("http://tiku.huatu.com/cdn/images/vhuatu/avatars/default.png").rcount(20).expendTime(300).build());
        result.put("id", "8205958822857731640");
        result.put("testTimeInfo", "2018-12-12");//测试时间
        result.put("submitTimeInfo", "2018-12-12:16:45");//交卷时间
        result.put("rcount", 10);//正确数
        result.put("tcount",100);//总题数
        result.put("avgTimeCost", 200);//平均耗时
        result.put("maxCorrect", 15);//最高答对题数
        result.put("avgCorrect", 14);//平均答对题数
        result.put("points", points);//知识点掌握情况
        result.put("ranks", rankInfos);
        result.put("corrects", new int[]{1,2,2,2,2,1,1,1,1,1});
        result.put("answers", new String[]{"1", "2", "3", "4", "4", "3", "2", "1", "2", "2"});
        result.put("times", new int[] {1, 2, 2, 2, 1, 1, 2, 2, 3, 1});
        result.put("doubts", new int[] {1,1,1,1,1,0,0,0,0,0});
        return result;
    }



    @GetMapping(value = "/learnReport/{syllabusId}")
    @LocalMapParam(checkToken = true)
    public Object learnReport(@Token UserSession userSession,
                              @RequestHeader(value = "terminal") int terminal,
                              @RequestHeader(value = "cv") String cv,
                              @PathVariable(value = "syllabusId") int syllabusId){

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
    public static class RankInfo{
        private int rank;
        private long uid;
        private String uname;
        private String avatar;
        private int rcount;
        private int expendTime;
        private int submitTimeInfo;
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
