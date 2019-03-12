package com.huatu.tiku.course.service.v6.impl;


import java.text.SimpleDateFormat;
import java.util.*;
import java.util.Map.Entry;
import java.util.concurrent.TimeUnit;
import java.util.function.Function;
import java.util.stream.Collectors;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.huatu.common.exception.BizException;
import com.huatu.common.utils.collection.HashMapBuilder;
import com.huatu.tiku.common.bean.user.UserSession;
import com.huatu.tiku.course.dao.manual.CourseExercisesStatisticsMapper;
import com.huatu.tiku.course.netschool.api.v6.LessonServiceV6;
import com.huatu.tiku.course.service.manager.CourseExercisesStatisticsManager;
import com.huatu.tiku.course.util.ZTKResponseUtil;
import com.huatu.tiku.course.ztk.api.v1.paper.PracticeCardServiceV1;
import com.huatu.tiku.entity.CourseExercisesStatistics;
import com.huatu.ztk.knowledge.bean.QuestionPointTree;
import com.huatu.ztk.paper.bean.PracticeCard;
import com.huatu.ztk.paper.bean.PracticeForCoursePaper;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.MapUtils;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.connection.RedisConnection;
import org.springframework.data.redis.connection.RedisZSetCommands;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.stereotype.Service;

import com.alibaba.fastjson.JSONObject;
import com.google.common.base.Stopwatch;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.huatu.tiku.course.bean.NetSchoolResponse;
import com.huatu.tiku.course.bean.vo.PeriodTestListVO;
import com.huatu.tiku.course.common.EstimateCourseRedisKey;
import com.huatu.tiku.course.common.StudyTypeEnum;
import com.huatu.tiku.course.common.YesOrNoStatus;
import com.huatu.tiku.course.dao.manual.CourseExercisesProcessLogMapper;
import com.huatu.tiku.course.netschool.api.v6.CourseServiceV6;
import com.huatu.tiku.course.netschool.api.v6.UserCourseServiceV6;
import com.huatu.tiku.course.service.v6.CourseServiceV6Biz;
import com.huatu.tiku.course.util.CourseCacheKey;
import com.huatu.tiku.course.util.DateUtil;
import com.huatu.tiku.course.util.ResponseUtil;
import com.huatu.tiku.course.ztk.api.v4.paper.PeriodTestServiceV4;
import com.huatu.tiku.entity.CourseExercisesProcessLog;

import lombok.extern.slf4j.Slf4j;
import tk.mybatis.mapper.entity.Example;
import tk.mybatis.mapper.weekend.WeekendSqls;


/**
 * 描述：
 *
 * @author biguodong
 *         Create time 2019-01-08 下午11:13
 **/

@Service
@Slf4j
public class CourseServiceV6BizImpl implements CourseServiceV6Biz {

    /**
     * response from php
     */
    private static final String ORIGIN_TITLE = "title";
    private static final String ORIGIN_PRICE = "price";
    private static final String ORIGIN_LIVE_TIME = "liveDate";
    /**
     * response
     */
    private static final String RESPONSE_TITLE = "courseTitle";
    private static final String RESPONSE_LIVE_TIME = "liveDate";
    private static final String RESPONSE_PRICE = "price";
    private static final String RESPONSE_CLASS_ID = "classId";

    private static final String RESPONSE_CLASS_IDS = "classIds";

    @Autowired
    private RedisTemplate redisTemplate;

    @Autowired
    private CourseServiceV6 courseService;

    @Autowired
    private UserCourseServiceV6 userCourseServiceV6;

    @Autowired
    private CourseExercisesProcessLogMapper courseExercisesProcessLogMapper;

    @Autowired
    private CourseExercisesStatisticsManager courseExercisesStatisticsManager;

    @Autowired
    private PeriodTestServiceV4 periodTestServiceV4;

    @Autowired
    private PracticeCardServiceV1 practiceCardService;

    @Autowired
    private LessonServiceV6 lessonService;

    /**
     * 模考大赛解析课信息,多个id使用逗号分隔
     * 模考大赛专用
     *
     * @param classIds
     * @return
     */
    @Override
    public HashMap<String, LinkedHashMap> getClassAnalysis(String classIds) {
        HashMap<String, LinkedHashMap> responseMap = Maps.newHashMap();
        SimpleDateFormat courseDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS");
        try {
            String[] classArray = classIds.split(",");
            if (ArrayUtils.isEmpty(classArray)) {
                return responseMap;
            }
            for (String s : classArray) {
                LinkedHashMap<String, Object> linkedHashMap = Maps.newLinkedHashMap();
                int classId = Integer.valueOf(s);
                NetSchoolResponse netSchoolResponse = obtainNetNetSchoolResponseFromCache(classId);
                @SuppressWarnings("unchecked")
                LinkedHashMap<String, Object> result = (LinkedHashMap<String, Object>) netSchoolResponse.getData();
                if (!result.containsKey(ORIGIN_PRICE) || !result.containsKey(ORIGIN_LIVE_TIME)) {
                    return Maps.newLinkedHashMap();
                }
                String liveDate = String.valueOf(result.get(ORIGIN_LIVE_TIME));
                if (StringUtils.isEmpty(liveDate)) {
                    liveDate = courseDateFormat.format(new Date());
                }
                Date date = courseDateFormat.parse(liveDate);
                linkedHashMap.put(RESPONSE_TITLE, result.get(ORIGIN_TITLE));
                linkedHashMap.put(RESPONSE_LIVE_TIME, date.getTime());
                linkedHashMap.put(RESPONSE_PRICE, result.get(ORIGIN_PRICE));
                linkedHashMap.put(RESPONSE_CLASS_ID, classId);
                responseMap.put(s, linkedHashMap);
            }

            return responseMap;
        } catch (Exception e) {
            log.error("parse time info error, for classId:{}", classIds);
            return Maps.newLinkedHashMap();
        }
    }

    /**
     * 分页查询小模考历史解析课列表
     *
     * @param subject
     * @param page
     * @param size
     * @param startTime
     * @param endTime   @return
     */
    @Override
    public NetSchoolResponse analysisClassList(int subject, int page, int size, long startTime, long endTime) {
        RedisConnection connection = redisTemplate.getConnectionFactory().getConnection();
        String smallEstimateCourseIdKey = EstimateCourseRedisKey.getSmallEstimateCourseIdKey(subject);
        String courseIds = "";
        try {
            Set<RedisZSetCommands.Tuple> tuples = connection.zRevRangeWithScores(smallEstimateCourseIdKey.getBytes(), 0, -1);
            List<RedisZSetCommands.Tuple> all = tuples.stream().filter(i -> i.getScore().longValue() >= startTime)
                    .filter(i -> i.getScore().longValue() <= endTime)
                    .collect(Collectors.toList());
            //降序排序
            Collections.sort(all, (o1, o2) -> o2.getScore() - o1.getScore() > 0 ? 1 : -1);
            int startIndex = (page - 1) * size;
            int endIndex = page * size > all.size() ? all.size() : page * size;
            if (startIndex < 0 || startIndex >= endIndex) {
                return NetSchoolResponse.DEFAULT;
            }
            courseIds = all.subList(startIndex, endIndex)
                    .stream()
                    .map(RedisZSetCommands.Tuple::getValue)
                    .map(String::new)
                    .collect(Collectors.joining(","));
            Map<String, Object> params = Maps.newHashMap();
            params.put(RESPONSE_CLASS_IDS, courseIds);
            NetSchoolResponse netSchoolResponse = courseService.analysisClassList(params);
            Object data = netSchoolResponse.getData();
            if (data instanceof LinkedHashMap) {
                sortClassById((LinkedHashMap) data, courseIds);
                ((LinkedHashMap) data).put("current_page", page);
                ((LinkedHashMap) data).put("last_page", page * size >= all.size() ? page : page + 1);
                ((LinkedHashMap) data).put("per_page", size);
                ((LinkedHashMap) data).put("from", startIndex);
                ((LinkedHashMap) data).put("to", endIndex);
                ((LinkedHashMap) data).put("total", all.size());

            }
            return netSchoolResponse;
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            connection.close();
        }

        return NetSchoolResponse.DEFAULT;
    }

    /**
     * 返回列表倒序排序
     *
     * @param data
     * @param courseIds
     */
    private void sortClassById(LinkedHashMap data, String courseIds) {
        try{
            List<LinkedHashMap> list = (List<LinkedHashMap>) data.get("data");
            String[] split = courseIds.split(",");
            if (CollectionUtils.isEmpty(list)) {
                return;
            }
            List<LinkedHashMap> result = Lists.newArrayList();
            for (String id : split) {
                Optional<LinkedHashMap> any = list.stream().filter(i ->
                        id.equals(MapUtils.getString(i, "classId")) ||
                                id.equals(MapUtils.getString(i, "id")))
                        .findAny();
                if(any.isPresent()){
                    result.add(any.get());
                }
            }
            data.put("data",result);
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    /**
     * 缓存课程信息
     *
     * @param classId
     * @return
     */
    private NetSchoolResponse obtainNetNetSchoolResponseFromCache(int classId) {
        Map<String, Object> params = Maps.newHashMap();
        params.put(RESPONSE_CLASS_ID, classId);
        String key = CourseCacheKey.courseAnalysisV6(String.valueOf(classId));
        ValueOperations<String, String> valueOperations = redisTemplate.opsForValue();

        NetSchoolResponse netSchoolResponse;
        if (null == valueOperations.get(key)) {
            netSchoolResponse = courseService.analysis(params);
            if (null != netSchoolResponse.getData()) {
                valueOperations.set(key, JSONObject.toJSONString(netSchoolResponse.getData()), 1, TimeUnit.DAYS);
            } else {
                log.info("obtain course info from php client error, classId:{}", classId);
                return NetSchoolResponse.newInstance(Maps.newLinkedHashMap());
            }
        } else {
            String valueStr = String.valueOf(valueOperations.get(key));
            LinkedHashMap valueData = JSONObject.parseObject(valueStr, LinkedHashMap.class);
            netSchoolResponse = NetSchoolResponse.newInstance(valueData);
        }
        return netSchoolResponse;
    }


    @Override
	public Object periodTestList(Map<String, Object> params) {
		Stopwatch stopwatch = Stopwatch.createStarted();
		int uid = (int) params.get("userId");
		NetSchoolResponse<PeriodTestListVO> response = userCourseServiceV6.unfinishStageExamList(params);
		log.info("接口unfinish_stage_exam_list调用php响应用时:{}", String.valueOf(stopwatch.stop()));
		log.info("接口unfinish_stage_exam_list调用php响应返回:{}", response);
		if (ResponseUtil.isSuccess(response)) {
			Stopwatch stopwatchExplain = Stopwatch.createStarted();
			PeriodTestListVO periodTestListVO = response.getData();
			// key为paperid_syllabusId value 为试卷信息
			Map<String, PeriodTestListVO.PeriodTestInfo> paperMap = Maps.newHashMap();
			periodTestListVO.getList().forEach(courseInfo -> {
				courseInfo.setUndoCount(courseInfo.getChild().size());
				courseInfo.getChild().forEach(periodTestInfo -> {
					// TODO 后续变为从redis获取
					int count = courseExercisesProcessLogMapper
							.selectCountByExample(new Example.Builder(CourseExercisesProcessLog.class)
									.where(WeekendSqls.<CourseExercisesProcessLog>custom()
											.andEqualTo(CourseExercisesProcessLog::getSyllabusId,
													periodTestInfo.getSyllabusId())
											.andEqualTo(CourseExercisesProcessLog::getUserId, uid)
											.andEqualTo(CourseExercisesProcessLog::getIsAlert,
													YesOrNoStatus.YES.getCode())
											.andEqualTo(CourseExercisesProcessLog::getStatus,
													YesOrNoStatus.YES.getCode())
											.andEqualTo(CourseExercisesProcessLog::getDataType,
													StudyTypeEnum.PERIOD_TEST.getKey()))

									.build());
					if (count > 0) {
						periodTestInfo.setIsAlert(YesOrNoStatus.YES.getCode());
					}
					// 填充app端展示所需时间
					periodTestInfo.setShowTime(
							DateUtil.getSimpleDate(periodTestInfo.getStartTime(), periodTestInfo.getEndTime()));
					paperMap.put(periodTestInfo.getExamId() + "_" + periodTestInfo.getSyllabusId(), periodTestInfo);
					// 填充考试状态
//					NetSchoolResponse netSchoolResponse = periodTestServiceV4.getPaperStatus(uid,
//							periodTestInfo.getSyllabusId(), periodTestInfo.getExamId());
//					if (ResponseUtil.isSuccess(netSchoolResponse)) {
//						periodTestInfo.setStatus((int) netSchoolResponse.getData());
//					}
				});
			});
			Set<String> paperSyllabusSet = paperMap.keySet();
			NetSchoolResponse<Map<String, Integer>> bathResponse = periodTestServiceV4.getPaperStatusBath(uid,
					paperSyllabusSet);
			if (ResponseUtil.isSuccess(bathResponse)) {
				// key为paperid_syllabusId value 为试卷状态
				Map<String, Integer> retMap = bathResponse.getData();
				log.info("getPaperStatusBath ret is:{}", retMap.toString());
				// 修改试卷状态
				for (Entry<String, Integer> paperRet : retMap.entrySet()) {
					paperMap.get(paperRet.getKey()).setStatus(paperRet.getValue());
				}
			}
			log.info("getpaper param is:{}", paperSyllabusSet.toString());
			log.info("接口unfinish_stage_exam_list解析用时:{}", String.valueOf(stopwatchExplain.stop()));
			return periodTestListVO;
		}
		return null;
	}

    /**
     * 查询我的课后作业报告
     *
     * @param userSession
     * @param terminal
     * @param cardId
     * @param courseType
     * @param lessonId
     * @return
     * @throws BizException
     */
    @Override
    public Object courseWorkReport(UserSession userSession, int terminal , long cardId, int courseType, long lessonId) throws BizException {

        Object response = ResponseUtil.build(practiceCardService.getAnswerCard(userSession.getToken(), terminal, cardId));
        JSONObject data = new JSONObject((LinkedHashMap<String, Object>) response);

        JSONObject paper = data.getJSONObject("paper");
        PracticeForCoursePaper practiceForCoursePaper = JSONObject.parseObject(paper.toJSONString(), PracticeForCoursePaper.class);

        PracticeCard practiceCard = JSONObject.parseObject(data.toJSONString(), PracticeCard.class);
        practiceCard.setPaper(practiceForCoursePaper);
        List<QuestionPointTree> points_ = Lists.newArrayList();

        List<QuestionPointTree> level1Points = practiceCard.getPoints();
        level1Points.forEach(level1Item -> {
            if(CollectionUtils.isNotEmpty(level1Item.getChildren())){
                List<QuestionPointTree> level2Points = level1Item.getChildren();
                level2Points.forEach(level2Item ->{
                    List<QuestionPointTree> level3Points = level2Item.getChildren();
                    if(CollectionUtils.isNotEmpty(level3Points)){
                        level3Points.forEach(level3Item -> points_.add(level3Item));
                    }
                });
            }
        });

        data.put("points", points_);
        data.remove("paper");
        data.putAll(courseExercisesStatisticsManager.obtainCourseRankInfo(practiceCard));
        data.put("tcount", practiceForCoursePaper.getQcount());
        data.put("rcount", practiceCard.getRcount());
        return data;
    }

    /**
     * 我的学习报告
     *
     * @param userSession
     * @param bjyRoomId
     * @param classId
     * @param netClassId
     * @param courseWareId
     * @param videoType
     * @param cardId
     * @param terminal
     * @return
     * @throws BizException
     */
    @Override
    public Object learnReport(UserSession userSession, int bjyRoomId, int classId, int netClassId, int courseWareId, int videoType, long cardId, int terminal) throws BizException {
        Map<String,Object> result = Maps.newHashMap();

        Map<String,Object> liveReport = Maps.newHashMap();//直播听课记录
        Map<String,Object> classPractice = Maps.newHashMap();//随堂练习
        Map<String,Object> courseWorkPractice = Maps.newHashMap();//课后作业报告

        /**
         * 处理听课记录
         */
        liveReport.put("learnTime", 0);
        liveReport.put("gold", 0);
        liveReport.put("learnPercent", 0);
        liveReport.put("abovePercent", 0);
        NetSchoolResponse netSchoolResponse = lessonService.studyReport(bjyRoomId, userSession.getUname(), classId, netClassId, courseWareId, videoType);
        if(ResponseUtil.isSuccess(netSchoolResponse)){
            LinkedHashMap<String,Object> data = (LinkedHashMap<String,Object>)netSchoolResponse.getData();
            liveReport.put("learnTime", data.get("listenLength"));
            liveReport.put("learnPercent", 0);
            liveReport.put("abovePercent", 0);
        }
        /**
         * 处理课后作业报告
         */
        courseWorkPractice.putAll((Map<String, Object>)courseWorkReport(userSession, terminal, cardId, videoType, courseWareId));
        /**
         * 处理随堂随堂练习报告
         */
        //TODO
        result.put("classPractice", classPractice);
        result.put("courseWorkPractice", courseWorkPractice);
        result.put("liveReport", liveReport);

        return null;
    }
}
