package com.huatu.tiku.course.web.controller.util;

import com.alibaba.fastjson.JSONObject;
import com.google.common.base.Stopwatch;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.huatu.common.exception.BizException;
import com.huatu.common.spring.event.EventPublisher;
import com.huatu.common.utils.collection.HashMapBuilder;
import com.huatu.tiku.common.bean.user.UserSession;
import com.huatu.tiku.course.bean.NetSchoolResponse;
import com.huatu.tiku.course.common.LiveStatusEnum;
import com.huatu.tiku.course.common.TypeEnum;
import com.huatu.tiku.course.common.VideoTypeEnum;
import com.huatu.tiku.course.common.YesOrNoStatus;
import com.huatu.tiku.course.consts.SyllabusInfo;
import com.huatu.tiku.course.hbase.api.v1.VideoServiceV1;
import com.huatu.tiku.course.service.v1.CourseExercisesService;
import com.huatu.tiku.course.service.v1.practice.CourseLiveBackLogService;
import com.huatu.tiku.course.util.ResponseUtil;
import com.huatu.tiku.course.util.ZTKResponseUtil;
import com.huatu.tiku.course.ztk.api.v1.paper.PracticeCardServiceV1;
import com.huatu.tiku.course.ztk.api.v4.paper.PeriodTestServiceV4;
import com.huatu.tiku.entity.CourseLiveBackLog;
import com.huatu.tiku.springboot.basic.reward.RewardAction;
import com.huatu.tiku.springboot.basic.reward.event.RewardActionEvent;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.MapUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.StopWatch;
import tk.mybatis.mapper.entity.Example;

import java.math.BigDecimal;
import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * Created by lijun on 2018/6/25
 */
@Slf4j
@Component
public class CourseUtil {

    @Autowired
    private EventPublisher eventPublisher;

    @Autowired
    private VideoServiceV1 videoServiceV1;

    @Autowired
    private PracticeCardServiceV1 practiceCardServiceV1;

    @Autowired
    private PeriodTestServiceV4 PeriodTestService;

    @Autowired
    private CourseLiveBackLogService courseLiveBackLogService;

    @Autowired
    private CourseExercisesService courseExercisesService;

    /**
     * 添加课程播放的时间-用以每日任务处理
     */
    public void pushPlayEvent(UserSession userSession, NetSchoolResponse netSchoolResponse, Object response) {
        //发布事件
        if (ResponseUtil.isSuccess(netSchoolResponse) && response instanceof Map && ((Map) response).containsKey("course")) {
            Object courseDetail = ((Map) response).get("course");
            boolean isFree = (courseDetail instanceof Map && ((Map) courseDetail).containsKey("free") && "1".equals(String.valueOf(((Map) courseDetail).get("free"))));
            pushPlayEvent(userSession, isFree);
        }
    }

    /**
     * 加金币
     */
    public void pushPlayEvent(UserSession userSession, boolean isFree) {
        if (isFree) {
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

    /**
     * 在播放列表添加播放进度
     *
     * @param response 播放列表信息
     */
    public void addStudyProcessIntoSecrInfo(Object response, final String token, final String cv, final int terminal) {
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
                                    .put("sessionId", data.get("bjySessionId") == null ? "" : String.valueOf(data.get("bjySessionId")))
                                    .build();
                            params.put((data.get("hasTeacher") == null || String.valueOf(data.get("hasTeacher")).equals("0")) ? "videoIdWithoutTeacher" : "videoIdWithTeacher",
                                    data.get("bjyVideoId") == null ? "" : String.valueOf(data.get("bjyVideoId")));
                            return params;
                        })
                        .collect(Collectors.toList());

                long currentTimeMillis = System.currentTimeMillis();
                Object data = videoServiceV1.videoProcessDetailV1(token, terminal, cv, paramList);
                //log.info(" videoServiceV1 videoProcessDetailV1 ===> token = {},paramList = {}",token, JSON.toJSON(paramList));
                List<HashMap<String, Object>> hbaseDataList = (List<HashMap<String, Object>>) ((Map<String, Object>) data).get("data");
                //log.info(" videoServiceV1 videoProcessDetailV1 ===> result = {},time = {}",JSON.toJSON(hbaseDataList),System.currentTimeMillis() - currentTimeMillis);

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
                                        BigDecimal bg = new BigDecimal(process);
                                        double f1 = bg.setScale(3, BigDecimal.ROUND_HALF_UP).doubleValue();
                                        lessionData.put("process", f1 * 100);
                                    }
                                } else {
                                    lessionData.put("process", 0);
                                }
                                return lessionData;
                            })
                            .collect(Collectors.toList());
                    result.replace("result", list);
                } else {
                    //此处只会在 快速失败的情况下被调用
                    List<Map> list = ((List<Map>) resultList).parallelStream()
                            .map(lessionData -> {
                                lessionData.put("process", 0);
                                return lessionData;
                            })
                            .collect(Collectors.toList());
                    result.replace("result", list);
                }
            }
        }
    }

    /**
     * 课程大纲-售后-添加课后答题结果信息
     *
     * @param response 响应结果集
     * @param userId   用户ID
     */
    public void addExercisesCardInfo(LinkedHashMap response, long userId, boolean need2Str) {
        Stopwatch stopwatch = Stopwatch.createStarted();
        response.computeIfPresent("list", (key, value) -> {
                    List<HashMap<String, Object>> paramsList = ((List<Map>) value).stream()
                            .filter(map -> (null != MapUtils.getString(map, SyllabusInfo.Type)
                                    && MapUtils.getString(map, SyllabusInfo.Type).equals(String.valueOf(TypeEnum.COURSE_WARE.getType())))
                            )
                            .filter(map -> null != map.get(SyllabusInfo.VideoType) && null != map.get(SyllabusInfo.CourseWareId))
                            .map(map -> {
                                HashMap<String, Object> build = HashMapBuilder.<String, Object>newBuilder()
                                        .put(SyllabusInfo.VideoType, MapUtils.getIntValue(map, SyllabusInfo.VideoType, 0))
                                        .put(SyllabusInfo.CourseId, MapUtils.getIntValue(map, SyllabusInfo.CourseWareId, 0))
                                        .build();
                                return build;
                            })
                            .collect(Collectors.toList());
                    //查询用户答题信息
                    log.info("获取课后练习的答题卡信息,参数信息，userId = {},paramsList = {}", userId, paramsList);
                    Object courseExercisesCardInfo = practiceCardServiceV1.getCourseExercisesCardInfo(userId, paramsList);
                    Object build = ZTKResponseUtil.build(courseExercisesCardInfo);

                    Map<Object, Object> defaultMap = HashMapBuilder.newBuilder()
                            .put("status", 0)
                            .put("rcount", 0)
                            .put("wcount", 0)
                            .put("ucount", 0)
                            .put("id", need2Str ? "0" : 0)
                            .build();
                    List<Map> courseExercisesCards = (List<Map>) build;         //课后作业相关答题卡
                    if (CollectionUtils.isNotEmpty(courseExercisesCards)) {
                        //获取答题卡信息状态
                        Function<HashMap<String, Object>, Map> getCourse = (valueData) -> {
                            Optional<Map> first = courseExercisesCards.stream()
                                    .filter(result -> null != result.get(SyllabusInfo.CourseId) && null != result.get("courseType"))
                                    .filter(result ->
                                            MapUtils.getString(result, SyllabusInfo.CourseId).equals(MapUtils.getString(valueData, SyllabusInfo.CourseWareId))
                                                    && MapUtils.getString(result, "courseType").equals(MapUtils.getString(valueData, SyllabusInfo.VideoType))
                                    )
                                    .findFirst();
                            if (first.isPresent()) {
                                Map map = first.get();
                                map.remove("courseId");
                                map.remove("courseType");
                                if(need2Str){
                                    map.computeIfPresent("id", (mapK, mapV) -> String.valueOf(mapV));
                                }
                                return map;
                            } else {
                                return defaultMap;
                            }
                        };
                        List<Map> mapList = ((List<Map>) value).stream()
                                .map(valueData -> {
                                    Map answerCard = getCourse.apply((HashMap<String, Object>) valueData);
                                    valueData.put("answerCard", answerCard);
                                    return valueData;
                                })
                                .collect(Collectors.toList());
                        return mapList;
                    } else {
                        List<Map> mapList = ((List<Map>) value).stream()
                                .map(valueData -> {
                                    valueData.put("answerCard", defaultMap);
                                    return valueData;
                                })
                                .collect(Collectors.toList());
                        return mapList;
                    }
                }
        );
        log.info("学习报告 - 课后作业答题卡信息:userId:{},耗时:{}", userId,  stopwatch.elapsed(TimeUnit.MILLISECONDS));
    }

    /**
     * 课程大纲-售后-添加课后答题结果信息
     * @param response
     * @param userId
     * @param need2Str
     */
    public void addLiveCardExercisesCardInfo(LinkedHashMap response, long userId, boolean need2Str){
        List<Map<String,Object>> list = (List<Map<String,Object>>) response.get("list");
        Map<Object, Object> defaultMap = HashMapBuilder.newBuilder()
                .put("status", 0)
                .put("rcount", 0)
                .put("wcount", 0)
                .put("ucount", 0)
                .put("id", need2Str ? "0" : 0)
                .build();

        /**
         * 查询直播回放的直播课件信息
         */
        for(Map<String,Object> detail : list){
            int type = MapUtils.getIntValue(detail, SyllabusInfo.Type);
            int videoType = MapUtils.getIntValue(detail, SyllabusInfo.VideoType);
            int courseWareId = MapUtils.getIntValue(detail, SyllabusInfo.CourseWareId);
            TypeEnum typeEnum = TypeEnum.create(type);
            VideoTypeEnum videoTypeEnum = VideoTypeEnum.create(videoType);
            if(typeEnum != TypeEnum.COURSE_WARE){
                continue;
            }
            if(videoTypeEnum != VideoTypeEnum.LIVE_PLAY_BACK){
                continue;
            }
            long bjyRoomId = MapUtils.getLongValue(detail, SyllabusInfo.BjyRoomId);
            CourseLiveBackLog courseLiveBackLog = checkLiveBackWithCourseWork(bjyRoomId, courseWareId);
            if(null == courseLiveBackLog){
                continue;
            }
            Stopwatch stopwatch = Stopwatch.createStarted();
            List<Map<String, Object>> listQuestionByCourseId = courseExercisesService.listQuestionByCourseId(VideoTypeEnum.LIVE.getVideoType(), courseLiveBackLog.getLiveCoursewareId());
            if (CollectionUtils.isEmpty(listQuestionByCourseId)) {
               continue;
            }
            detail.put(SyllabusInfo.AfterCourseNum, listQuestionByCourseId.size());
            HashMap<String,Object> params = Maps.newHashMap();
            params.put(SyllabusInfo.PaperCourseType, VideoTypeEnum.LIVE.getVideoType());
            params.put(SyllabusInfo.PaperCourseId, courseLiveBackLog.getLiveCoursewareId());
            Object courseExercisesCardInfo = practiceCardServiceV1.getCourseExercisesCardInfo(userId, Lists.newArrayList(params));
            Object build = ZTKResponseUtil.build(courseExercisesCardInfo);
            List<Map> courseExercisesCards = (List<Map>) build;
            if(CollectionUtils.isEmpty(courseExercisesCards)){
                detail.put("answerCard", defaultMap);
                continue;
            }else{
                Map<String,Object> answerCard = courseExercisesCards.get(0);
                answerCard.remove("courseId");
                answerCard.remove("courseType");
                if(need2Str){
                    answerCard.computeIfPresent("id", (mapK, mapV) -> String.valueOf(mapV));
                }
                detail.put("answerCard", answerCard);
            }
            log.info("学习报告 - 直播回放获取答题卡信息:userId:{},courseWareId:{}", userId, courseWareId);
        }
    }


    /**
     * 核查直播回放是否由直播生成
     * @param bjyRoomId
     * @param liveBackCoursewareId
     * @return
     */
    private CourseLiveBackLog checkLiveBackWithCourseWork(long bjyRoomId, long liveBackCoursewareId){
        Example example = new Example(CourseLiveBackLog.class);
        example.and()
                .andEqualTo("roomId", bjyRoomId)
                .andEqualTo("liveBackCoursewareId", liveBackCoursewareId);
        CourseLiveBackLog courseLiveBackLog = courseLiveBackLogService.findByRoomIdAndLiveCoursewareId(bjyRoomId, liveBackCoursewareId);
        return courseLiveBackLog;
    }


    /**
     * 处理阶段考试状态信息
     * @param response
     * @param userId
     */
    public void addPeriodTestInfo(LinkedHashMap response, int userId){
                response.computeIfPresent("list", (key, value) -> {
                        StopWatch stopWatch = new StopWatch("learnReport - addPeriodTestInfo");
                        stopWatch.start("addPeriodTestInfo - paperIds");
                        Set<String> paperIds = ((List<Map>) value).stream()
                                //videoType	1点播2直播3直播回放4阶段测试题
                                .filter(map -> VideoTypeEnum.create(MapUtils.getIntValue(map, "videoType")) == VideoTypeEnum.PERIOD_TEST)
                                    //coursewareId	课件id
                                .filter(map -> null != map.get(SyllabusInfo.CourseWareId) && null != map.get("id"))
                                .map(map -> {
                                    StringBuilder stringBuilder = new StringBuilder();
                                    stringBuilder
                                            .append(MapUtils.getString(map, "coursewareId"))
                                            .append("_")
                                            .append(MapUtils.getString(map,"id"));
                                    return stringBuilder.toString();
                                })
                                .collect(Collectors.toSet());
                        //查询用户答题信息
                        log.info("获取阶段测试完成情况：userId = {}, paperIds = {}", userId, paperIds);
                        NetSchoolResponse netSchoolResponse = PeriodTestService.getPaperStatusBath(userId, paperIds);
                        Map<String, Object> data = (Map<String, Object>) netSchoolResponse.getData();
                        stopWatch.stop();
                        log.info("addPeriodTestInfo - paperIds,耗时:{}", stopWatch.prettyPrint());
			if (null != data && data.size() > 0) {
				List<Map> mapList = ((List<Map>) value).stream().map(valueData -> {
					StringBuilder stringBuilder = new StringBuilder();
					stringBuilder.append(MapUtils.getString(valueData, "coursewareId")).append("_")
							.append(MapUtils.getString(valueData, "id"));
					valueData.put("testStatus", MapUtils.getInteger(data, stringBuilder.toString(), -1));
					// 设置是否过期
					if (1 == MapUtils.getInteger(valueData, "isEffective")
                            //当前时间大于结束时间（liveStartTime）
							&& System.currentTimeMillis() > (MapUtils.getLong(valueData, "liveStartTime"))*1000) {
						valueData.put("isExpired", 1);
					} else {
						valueData.put("isExpired", 0);
					}
					return valueData;
				}).collect(Collectors.toList());
				return mapList;
			} else {
                            List<Map> mapList = ((List<Map>) value).stream()
                                    .map(valueData -> {
                                        valueData.put("testStatus", -1);
                                        return valueData;
                                    })
                                    .collect(Collectors.toList());
                            return mapList;
                        }
                    }
            );
    }



    /**
     * 处理学习报告逻辑
     * @param response
     * @param userId
     */
    public void addLearnReportInfoV2(LinkedHashMap response, int userId){
        StopWatch stopWatch = new StopWatch("learnReport - addLearnReportInfoV2");
        List<Map<String, Object>> list = (List<Map<String, Object>>)response.get("list");
        if(CollectionUtils.isEmpty(list)){
            return;
        }
        for (Map<String, Object> stringObjectMap : list) {
            try{
                int type =  MapUtils.getInteger(stringObjectMap, SyllabusInfo.Type);
                TypeEnum typeEnum = TypeEnum.create(type);
                if(typeEnum != TypeEnum.COURSE_WARE){
                    stringObjectMap.put(SyllabusInfo.ReportStatus, YesOrNoStatus.UN_DEFINED.getCode());
                    continue;
                }
                VideoTypeEnum videoTypeEnum = VideoTypeEnum.create(MapUtils.getIntValue(stringObjectMap, SyllabusInfo.VideoType));
                long courseWareId = MapUtils.getLong(stringObjectMap, SyllabusInfo.CourseWareId);
                String bjyRoomId = MapUtils.getString(stringObjectMap, SyllabusInfo.BjyRoomId);
                switch (videoTypeEnum){
                    case LIVE:
                        Map live = doLiveReport(stringObjectMap);
                        stringObjectMap.putAll(live);
                        break;
                    case LIVE_PLAY_BACK:
                        Map playBack = doLivePlayBack(courseWareId, bjyRoomId);
                        stringObjectMap.putAll(playBack);
                        break;
                    case DOT_LIVE:
                        int studyReport = MapUtils.getIntValue(stringObjectMap, SyllabusInfo.StudyReport);
                        YesOrNoStatus studyReportEnum = YesOrNoStatus.create(studyReport);
                        if(studyReportEnum == YesOrNoStatus.NO){
                            stringObjectMap.put(SyllabusInfo.ReportStatus, YesOrNoStatus.UN_DEFINED.getCode());
                            continue;
                        }
                        Map dotLive = doDotLive(courseWareId, userId);
                        stringObjectMap.putAll(dotLive);
                        break;
                    default:
                        Map defaultMap = Maps.newHashMap();
                        defaultMap.put(SyllabusInfo.ReportStatus, YesOrNoStatus.NO.getCode());
                        stringObjectMap.putAll(defaultMap);
                }
            }catch (Exception e){
                log.error("处理大纲我的学习好高状态异常:{}, userId:{}",stringObjectMap, userId);
            }
        }
        stopWatch.stop();
        log.info("大纲列表 - 学习报告 - userId:{}, 耗时:{}",userId, stopWatch.prettyPrint());
    }

    /**
     * 直播报告处理
     * @param stringObjectMap
     * @return
     * @throws BizException
     */
    private Map doLiveReport(Map<String, Object> stringObjectMap) throws BizException{
        StopWatch stopWatch = new StopWatch("addLearnReportInfoV2 - live time");
        stopWatch.start();
        Map<String, Object> result = Maps.newHashMap();
        int liveStatus = MapUtils.getIntValue(stringObjectMap, SyllabusInfo.LiveStatus);
        LiveStatusEnum liveStatusEnum = LiveStatusEnum.create(liveStatus);
        if(liveStatusEnum == LiveStatusEnum.FINISHED){
            result.put(SyllabusInfo.ReportStatus, YesOrNoStatus.YES.getCode());
        }
        stopWatch.stop();
        log.info("addLearnReportInfoV2 - doLiveReport, 耗时:{}", stopWatch.prettyPrint());
        return result;
    }

    /**
     * 直播回放报告处理
     * @param liveBackCoursewareId
     * @param bjyRoomId
     * @return
     * @throws BizException
     */
    private Map doLivePlayBack(long liveBackCoursewareId, String bjyRoomId) throws BizException{
        StopWatch stopWatch = new StopWatch("addLearnReportInfoV2 - live - back time");
        stopWatch.start();
        Map<String,Object> result = Maps.newHashMap();
        Example example = new Example(CourseLiveBackLog.class);
        example.and()
                .andEqualTo("liveBackCoursewareId", liveBackCoursewareId)
                .andEqualTo("roomId", bjyRoomId);
        try{
            CourseLiveBackLog courseLiveBackLog = courseLiveBackLogService.findByRoomIdAndLiveCoursewareId(Long.valueOf(bjyRoomId), liveBackCoursewareId);
            if(null == courseLiveBackLog){
                result.put("reportStatus", YesOrNoStatus.NO.getCode());
            }else{
                result.put("reportStatus", YesOrNoStatus.YES.getCode());
            }
            stopWatch.stop();
            log.info("addLearnReportInfoV2 - doLivePlayBack, 耗时:{}", stopWatch.prettyPrint());
            return result;
        }catch (Exception e){
            result.put("reportStatus", YesOrNoStatus.NO.getCode());
            return result;
        }
    }

    /**
     * 录播逻辑处理
     * @param courseWareId
     * @return
     * @throws BizException
     */
    private Map doDotLive(long courseWareId, int userId) throws BizException{
        StopWatch stopWatch = new StopWatch("addLearnReportInfoV2 - dot -live time");
        stopWatch.start();
        Map<String,Object> result = Maps.newHashMap();
        result.put("reportStatus", YesOrNoStatus.NO.getCode());
        NetSchoolResponse netSchoolResponse = practiceCardServiceV1.getClassExerciseReport(courseWareId, VideoTypeEnum.DOT_LIVE.getVideoType(), userId);
        if(ResponseUtil.isSuccess(netSchoolResponse) && null != netSchoolResponse.getData() ){
            Map<String,Object> data = (Map<String,Object>)netSchoolResponse.getData();
            if(data.containsKey("id")){
                result.put("reportStatus", YesOrNoStatus.YES.getCode());
                return result;
            }
        }
        stopWatch.stop();
        log.info("addLearnReportInfoV2 - doDotLive, 耗时:{}", stopWatch.prettyPrint());
        return result;
    }
}
