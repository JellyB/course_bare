package com.huatu.tiku.course.web.controller.v3;

import com.alibaba.fastjson.JSONObject;
import com.google.common.collect.Maps;
import com.google.common.primitives.Ints;
import com.huatu.common.exception.BizException;
import com.huatu.common.spring.event.EventPublisher;
import com.huatu.common.utils.collection.HashMapBuilder;
import com.huatu.tiku.common.bean.AreaConstants;
import com.huatu.tiku.common.bean.user.UserSession;
import com.huatu.tiku.course.bean.CourseListV3DTO;
import com.huatu.tiku.course.bean.NetSchoolResponse;
import com.huatu.tiku.course.hbase.api.v1.VideoServiceV1;
import com.huatu.tiku.course.netschool.api.fall.CourseServiceV3Fallback;
import com.huatu.tiku.course.netschool.api.v3.CourseServiceV3;
import com.huatu.tiku.course.netschool.api.v3.UserCoursesServiceV3;
import com.huatu.tiku.course.service.CourseBizService;
import com.huatu.tiku.course.service.CourseCollectionBizService;
import com.huatu.tiku.course.service.VersionService;
import com.huatu.tiku.course.util.RequestUtil;
import com.huatu.tiku.course.util.ResponseUtil;
import com.huatu.tiku.springboot.basic.reward.RewardAction;
import com.huatu.tiku.springboot.basic.reward.event.RewardActionEvent;
import com.huatu.tiku.springboot.basic.subject.SubjectEnum;
import com.huatu.tiku.springboot.basic.subject.SubjectService;
import com.huatu.tiku.springboot.users.support.Token;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ExecutionException;
import java.util.stream.Collectors;

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
    @Autowired
    private UserCoursesServiceV3 userCoursesServiceV3;
    @Autowired
    private CourseServiceV3Fallback courseServiceV3Fallback;
    @Autowired
    private SubjectService subjectService;
    @Autowired
    private CourseCollectionBizService courseCollectionBizService;
    @Autowired
    private EventPublisher eventPublisher;
    @Autowired
    private VersionService versionService;

    @Autowired
    private RedisTemplate redisTemplate;

    @Autowired
    private VideoServiceV1 videoServiceV1;

    /**
     * 课程合集详情
     *
     * @param shorttitle
     * @return
     */
    @GetMapping("/collection")
    public Object getCollectionDetail(@RequestParam String shorttitle,
                                      @RequestParam int page,
                                      @Token UserSession userSession) {
        return courseBizService.getCollectionList(shorttitle, page);
    }

    /**
     * 图书列表
     *
     * @param categoryid
     * @param orderid
     * @param page
     * @param userSession
     * @return
     */
    @GetMapping("/books")
    public Object bookList(
            @RequestHeader(value = "terminal") Integer terminal,
            @RequestHeader(value = "cv") String cv,
            @RequestParam(required = false, defaultValue = "1001") int categoryid,
            @RequestParam(required = false, defaultValue = "1") int orderid,
            @RequestParam int page,
            @RequestParam(required = false, defaultValue = "") String keywords,
            @RequestParam(required = false, defaultValue = "1000") int subjectid,
            @Token UserSession userSession) {
        //TODO 此处用以判断是否为IOS内测版本，正式上线后可以删除
        //<editor-fold desc="此处用以判断是否为IOS内测版本，正式上线后可以删除">
//        Boolean member = false;
//        if (terminal == TerminalType.IPHONE || terminal == TerminalType.IPHONE_IPAD) {
//            member = redisTemplate.opsForSet().isMember(CourseCacheKey.IOS_AUDIT_VERSION, cv);
//        }
//        if (!member) {
//            return MOCK_PAGE_RESPONSE;
//        }
        //</editor-fold>
        int provinceId = AreaConstants.getNetSchoolProvinceId(userSession.getArea());
        Map<String, Object> params = HashMapBuilder.<String, Object>newBuilder()
                .put("categoryid", categoryid)
                .put("username", userSession.getUname())
                .put("orderid", orderid)
                .put("page", page)
                .put("subjectid", subjectid)
                .put("keywords", keywords)
                .put("provinceid", provinceId).build();
        NetSchoolResponse bookList = courseServiceV3.findBookList(params);
        return ResponseUtil.build(bookList);
    }

    /**
     * 录播课程列表
     *
     * @param categoryid
     * @param orderid
     * @param page
     * @param userSession
     * @return
     */
    @GetMapping("/recordings")
    public Object recordingList(@RequestParam(required = false, defaultValue = "1001") int categoryid,
                                @RequestParam(required = false, defaultValue = "1") int orderid,
                                @RequestParam int page,
                                @RequestParam(required = false, defaultValue = "") String keywords,
                                @RequestParam(required = false, defaultValue = "1000") int subjectid,
                                @Token UserSession userSession) {
        int provinceId = AreaConstants.getNetSchoolProvinceId(userSession.getArea());
        Map<String, Object> params = HashMapBuilder.<String, Object>newBuilder()
                .put("categoryid", categoryid)
                .put("username", userSession.getUname())
                .put("orderid", orderid)
                .put("page", page)
                .put("subjectid", subjectid)
                .put("keywords", keywords)
                .put("provinceid", provinceId).build();
        NetSchoolResponse recordingList = courseServiceV3.findRecordingList(params);
        courseServiceV3Fallback.setRecordingList(params, recordingList);
        return ResponseUtil.build(recordingList);
    }

    /**
     * 获取直播课程列表
     *
     * @param orderid
     * @param page
     * @param priceid
     * @param userSession
     * @return
     * @throws InterruptedException
     * @throws ExecutionException
     */
    @GetMapping("/lives")
    public Object liveList(
            @RequestHeader("cv") String cv,
            @RequestHeader("terminal") int terminal,
            @RequestParam(required = false, defaultValue = "0") int orderid,
            @RequestParam int page,
            @RequestParam(required = false, defaultValue = "1000") int priceid,
            @RequestParam(required = false, defaultValue = "") String keywords,
            @RequestParam(required = false, defaultValue = "") String category,//老版本的从session中映射，新版本的需要客户端自己传过来，直接做适配即可
            @Token UserSession userSession) throws InterruptedException, ExecutionException, BizException {
        int top = subjectService.top(userSession.getSubject());
        int categoryid = 1000;
        if (StringUtils.isBlank(category) || !StringUtils.isNumeric(category)) {
            //老版本未传递category
            SubjectEnum[] enums = SubjectEnum.values();
            for (SubjectEnum subjectEnum : enums) {
                if (subjectEnum.code() == top) {
                    categoryid = subjectEnum.categoryid();
                    break;
                }
            }
        } else {
            //新版本直接解析客户端传递过来的值
            categoryid = Optional.ofNullable(Ints.tryParse(category)).orElse(1000);
        }
        Map<String, Object> params = HashMapBuilder.<String, Object>newBuilder()
                .put("orderid", orderid)
                .put("page", page)
                .put("keywords", keywords)
                .put("categoryid", categoryid)
                .put("priceid", priceid)
                .put("cv", cv)
                .put("terminal", terminal)
                .build();
        CourseListV3DTO courseListV3 = courseBizService.getCourseListV3(params);
        return courseListV3;
    }


    /**
     * 课程详情页
     *
     * @param userSession
     * @param rid
     * @return
     */
    @GetMapping(value = "/{rid}", produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public Object getCourseDetail(@Token UserSession userSession,
                                  @PathVariable int rid) throws BizException, ExecutionException, InterruptedException {
        return courseBizService.getCourseDetailV3(rid, userSession.getUname());
    }

    /**
     * 课程详情页
     *
     * @param rid
     * @return
     */
    @GetMapping(value = "/{rid}", produces = MediaType.TEXT_HTML_VALUE + ";charset=utf-8")
    public Object getCourseHtml(@PathVariable int rid) throws BizException, ExecutionException, InterruptedException {
        return courseBizService.getCourseHtml(rid);
    }


    /**
     * 课程播放接口
     *
     * @param userSession
     * @param rid
     * @return
     */
    @GetMapping("/{rid}/secrinfo")
    public Object getCourseSecrInfo(@Token UserSession userSession,
                                    @PathVariable int rid,
                                    @RequestParam(required = false, defaultValue = "0") int isTrial,
                                    @RequestParam(required = false, defaultValue = "0") int fatherId,
                                    @RequestHeader("terminal") String terminal,
                                    @RequestHeader("cv") String cv
    ) {
        Map<String, Object> params = Maps.newHashMap();
        params.put("rid", rid);
        params.put("username", userSession.getUname());
        params.put("fatherId", fatherId);
        params.put("isTrial", isTrial);

        NetSchoolResponse netSchoolResponse = courseServiceV3.getCourseSecrInfo(params);
        Object response = ResponseUtil.build(netSchoolResponse, true);

        //发布事件
        if (ResponseUtil.isSuccess(netSchoolResponse) && response instanceof Map && ((Map) response).containsKey("course")) {
            Object courseDetail = ((Map) response).get("course");
            if (courseDetail instanceof Map && ((Map) courseDetail).containsKey("free") && "1".equals(String.valueOf(((Map) courseDetail).get("free")))) {
                //免费课
                eventPublisher.publishEvent(RewardActionEvent.class,
                        this,
                        (event) -> event.setAction(RewardAction.ActionType.WATCH_FREE)
                                .setUname(userSession.getUname())
                                .setUid(userSession.getId())
                );
            } else {
                //收费课
                eventPublisher.publishEvent(RewardActionEvent.class,
                        this,
                        (event) -> event.setAction(RewardAction.ActionType.WATCH_PAY)
                                .setUname(userSession.getUname())
                                .setUid(userSession.getId())
                );
            }
        }
        addStudyProcessIntoSecrInfo(response, userSession.getToken(), cv, terminal);
        return response;
    }


    /**
     * 课程播放接口
     *
     * @param userSession
     * @param rid
     * @return
     */
    @GetMapping("/{rid}/teachers")
    public Object findCourseTeachers(@Token UserSession userSession,
                                     @PathVariable int rid) {
        return ResponseUtil.build(courseServiceV3.findTeachersByCourse(rid));
    }


    /**
     * 课程大纲
     *
     * @param userSession
     * @param rid
     * @return
     */
    @GetMapping("/{rid}/timetable")
    public Object findCourseTimetable(@Token UserSession userSession,
                                      @PathVariable int rid) {
        return ResponseUtil.build(courseServiceV3.findTimetable(rid));
    }


    /**
     * 课程讲义
     *
     * @param rid
     * @return
     * @throws Exception
     */
    @RequestMapping(value = "{rid}/handouts", produces = MediaType.APPLICATION_JSON_UTF8_VALUE, method = RequestMethod.GET)
    public Object handout(@PathVariable int rid,
                          @Token UserSession userSession) throws Exception {
        final HashMap<String, Object> parameterMap = Maps.newHashMap();
        parameterMap.put("rid", rid);
        parameterMap.put("username", userSession.getUname());
        return ResponseUtil.build(courseServiceV3.getHandouts(parameterMap), true);
    }


    /**
     * 用户隐藏课程操作
     *
     * @param userSession
     * @param courseIds
     * @param orderIds
     * @return
     */
    @PostMapping("/hide")
    public Object hideUserCourse(@Token UserSession userSession,
                                 @RequestParam String courseIds,
                                 @RequestParam String orderIds) {
        String username = userSession.getUname();
        final HashMap<String, Object> params = HashMapBuilder.newBuilder()
                .put("netclassid", courseIds)
                .put("orderId", orderIds)
                .put("username", username)
                .buildUnsafe();
        return ResponseUtil.build(userCoursesServiceV3.hideCourse(RequestUtil.encryptParams(params)));
    }


    /**
     * 取消隐藏课程
     *
     * @param userSession
     * @param courseIds
     * @param orderIds
     * @return
     * @throws Exception
     */
    @DeleteMapping("/hide")
    public Object showCourse(@Token UserSession userSession,
                             @RequestParam String courseIds,
                             @RequestParam String orderIds) throws Exception {
        String username = userSession.getUname();
        final HashMap<String, Object> params = Maps.newHashMap();
        params.put("username", username);
        params.put("netclassid", courseIds);
        params.put("orderId", orderIds);
        return ResponseUtil.build(userCoursesServiceV3.showCourse(RequestUtil.encryptParams(params)));
    }


    /**
     * 我的套餐课-包含的课程
     *
     * @param courseId
     * @return
     * @throws Exception
     */
    @GetMapping(value = "{courseId}/suit")
    public Object suitDetail(@PathVariable int courseId,
                             @Token UserSession userSession) throws Exception {
        String username = userSession.getUname();
        final HashMap<String, Object> params = Maps.newHashMap();
        params.put("username", username);
        params.put("rid", courseId);
        return ResponseUtil.build(userCoursesServiceV3.getMyPackCourseDetail(RequestUtil.encryptJsonParams(params)));
    }

    /**
     * 在播放列表添加播放进去
     *
     * @param response 播放列表信息
     */
    private void addStudyProcessIntoSecrInfo(Object response, final String token, final String cv, final String terminal) {
        if (null != response) {
            JSONObject result = (JSONObject) response;
            Object resultList = result.get("lession");
            if (null != resultList) {
                //批量接口查询
                List<HashMap> paramList = ((List<Map>) resultList).parallelStream()
                        .map(data -> {
                            HashMap params = HashMapBuilder.<String, Object>newBuilder()
                                    .put("rid", String.valueOf(data.get("rid")))
                                    .put("joinCode", data.get("JoinCode") == null ? "" : String.valueOf(data.get("JoinCode")))
                                    .put("roomId", data.get("bjyRoomId") == null ? "" : String.valueOf(data.get("bjyRoomId")))
                                    .put("bjySessionId", data.get("bjySessionId") == null ? "" : String.valueOf(data.get("bjySessionId")))
                                    .build();
                            String hasTeacher = String.valueOf(data.get("hasTeacher"));
                            params.put(hasTeacher.equals("0") ? "videoIdWithTeacher" : "videoIdWithoutTeacher",
                                    data.get("bjyVideoId") == null ? "" : String.valueOf(data.get("bjyVideoId")));
                            return params;
                        })
                        .collect(Collectors.toList());
                Object data = videoServiceV1.videoProcessDetailV1(token, terminal, cv, paramList);
                List<HashMap<String, Object>> hbaseDataList = (List<HashMap<String, Object>>) ((Map<String, Object>) data).get("data");
                if (null != hbaseDataList) {
                    //组装进度数据
                    List<Map> list = ((List<Map>) resultList).parallelStream()
                            .map(lessionData -> {
                                //查询匹配的数据
                                Optional<HashMap<String, Object>> first = hbaseDataList.parallelStream()
                                        .filter(hBaseData -> String.valueOf(lessionData.get("rid")).equals(String.valueOf(hBaseData.get("rid"))))
                                        .findFirst();
                                //如果有匹配数据
                                if (first.isPresent()) {
                                    HashMap<String, Object> buildResult = first.get();
                                    if (null == buildResult || null == buildResult.get("wholeTime") || (int) buildResult.get("wholeTime") == 0) {
                                        lessionData.put("process", 0);
                                    } else {
                                        float process = Float.valueOf((int) buildResult.get("playTime"))
                                                / Float.valueOf((int) buildResult.get("wholeTime"));
                                        lessionData.put("process", (int) (process * 100));
                                    }
                                } else {
                                    lessionData.put("process", 0);
                                }
                                return lessionData;
                            })
                            .collect(Collectors.toList());
                    result.replace("result", list);
                }
            }
        }
    }
}
