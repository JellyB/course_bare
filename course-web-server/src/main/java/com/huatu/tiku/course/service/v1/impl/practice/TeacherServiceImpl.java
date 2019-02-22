package com.huatu.tiku.course.service.v1.impl.practice;

import com.google.common.collect.Lists;
import com.huatu.tiku.common.CourseQuestionTypeEnum;
import com.huatu.tiku.course.bean.practice.QuestionInfo;
import com.huatu.tiku.course.bean.practice.TeacherQuestionBo;
import com.huatu.tiku.course.service.v1.CourseBreakpointService;
import com.huatu.tiku.course.service.v1.practice.CoursePracticeQuestionInfoService;
import com.huatu.tiku.course.service.v1.practice.LiveCourseRoomInfoService;
import com.huatu.tiku.course.service.v1.practice.TeacherService;
import com.huatu.tiku.course.util.ZTKResponseUtil;
import com.huatu.tiku.course.ztk.api.v1.question.QuestionServiceV1;
import com.huatu.tiku.entity.CourseBreakpointQuestion;
import com.huatu.tiku.entity.CoursePracticeQuestionInfo;
import lombok.RequiredArgsConstructor;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.MapUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.AsyncResult;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.util.concurrent.ListenableFuture;
import org.springframework.util.concurrent.ListenableFutureCallback;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.function.Consumer;
import java.util.stream.Collectors;

/**
 * Created by lijun on 2019/2/21
 */
@Service
@RequiredArgsConstructor
public class TeacherServiceImpl implements TeacherService {

    final LiveCourseRoomInfoService liveCourseRoomInfoService;

    final CourseBreakpointService courseBreakpointService;
    final CoursePracticeQuestionInfoService coursePracticeQuestionInfoService;

    final QuestionServiceV1 questionService;

    @Override
    public List<TeacherQuestionBo> getQuestionInfoByRoomId(Long roomId) throws ExecutionException, InterruptedException {
        List<Integer> liveCourseIdListByRoomId = liveCourseRoomInfoService.getLiveCourseIdListByRoomId(roomId);
        if (CollectionUtils.isEmpty(liveCourseIdListByRoomId)) {
            return Lists.newArrayList();
        }
        //由于各个课件对应的试题信息肯定一致，此处只需要获取一个
        final Long courseId = Long.valueOf(liveCourseIdListByRoomId.get(0));
        List<CourseBreakpointQuestion> courseBreakpointQuestionList = courseBreakpointService.listQuestionByCourseTypeAndId(CourseQuestionTypeEnum.CourseType.LIVE.getCode(), courseId);
        if (CollectionUtils.isEmpty(courseBreakpointQuestionList)) {
            return Lists.newArrayList();
        }
        //获取所有试题的状态
        final List<Long> questionIdList = courseBreakpointQuestionList.stream()
                .map(CourseBreakpointQuestion::getQuestionId)
                .collect(Collectors.toList());
        //查询
        ListenableFuture<List<CoursePracticeQuestionInfo>> coursePracticeQuestionInfoByRoomId = getAsyncCoursePracticeQuestionInfoByRoomId(roomId, questionIdList);
        ListenableFuture<List<QuestionInfo>> baseQuestionInfoByRoomId = getAsyncBaseQuestionInfoByRoomId(questionIdList);
        final CountDownLatch questionSearchTask = new CountDownLatch(2);

        Consumer<ListenableFuture> addCallBack = future ->
                future.addCallback(new ListenableFutureCallback<List>() {
                    @Override
                    public void onFailure(Throwable ex) {
                        questionSearchTask.countDown();
                    }

                    @Override
                    public void onSuccess(List result) {
                        questionSearchTask.countDown();
                    }
                });
        addCallBack.accept(coursePracticeQuestionInfoByRoomId);
        addCallBack.accept(baseQuestionInfoByRoomId);
        questionSearchTask.await(3, TimeUnit.SECONDS);
        //获取两端数据
        List<QuestionInfo> baseQuestionInfoList = baseQuestionInfoByRoomId.get();
        List<CoursePracticeQuestionInfo> coursePracticeQuestionInfoList = coursePracticeQuestionInfoByRoomId.get();
        return combinationQuestionInfo(baseQuestionInfoList, courseBreakpointQuestionList, coursePracticeQuestionInfoList);
    }


    /**
     * 通过 roomId 获取试题详情
     */
    @Async
    public ListenableFuture<List<QuestionInfo>> getAsyncBaseQuestionInfoByRoomId(List<Long> questionIdList) {
        if (CollectionUtils.isEmpty(questionIdList)) {
            return new AsyncResult<>(Lists.newArrayList());
        }
        //获取所有的试题信息
        final String questionIds = questionIdList.stream()
                .map(String::valueOf)
                .collect(Collectors.joining(","));
        Object listQuestionByIds = questionService.listQuestionByIds(questionIds);
        List<Map<String, Object>> result;
        try {
            result = (List<Map<String, Object>>) ZTKResponseUtil.build(listQuestionByIds);
        } catch (Exception e) {
            return new AsyncResult<>(Lists.newArrayList());
        }
        if (CollectionUtils.isEmpty(result)) {
            return new AsyncResult<>(Lists.newArrayList());
        }
        List<QuestionInfo> questionInfoList = result.stream()
                .map(map -> {
                    final QuestionInfo questionInfo = QuestionInfo.builder()
                            .id(MapUtils.getLong(map, "id"))
                            .stem(MapUtils.getString(map, "stem"))
                            .choiceList((List<String>) MapUtils.getObject(map, "choiceList"))
                            .answer(MapUtils.getString(map, "answer"))
                            .analysis(MapUtils.getString(map, "analysis"))
                            .extend(MapUtils.getString(map, "extend"))
                            .from(MapUtils.getString(map, "from"))
                            .build();
                    //材料
                    List<String> materialList = (List<String>) MapUtils.getObject(map, "materials");
                    if (CollectionUtils.isEmpty(materialList)) {
                        String material = MapUtils.getString(map, "material");
                        if (!StringUtils.isEmpty(material)) {
                            materialList = Lists.newArrayList(material);
                        }
                    }
                    questionInfo.setMaterialList(materialList);
                    Integer type = MapUtils.getInteger(map, "type");
                    questionInfo.setType(type);
                    questionInfo.setTypeName(questionService.getQuestionTypeName(type));
                    return questionInfo;
                })
                .collect(Collectors.toList());
        return new AsyncResult<>(questionInfoList);
    }

    /**
     * 通过 roomId 获取试题统计详情
     */
    @Async
    public ListenableFuture<List<CoursePracticeQuestionInfo>> getAsyncCoursePracticeQuestionInfoByRoomId(Long roomId, List<Long> questionIdList) {
        if (CollectionUtils.isEmpty(questionIdList)) {
            return new AsyncResult<>(Lists.newArrayList());
        }
        List<CoursePracticeQuestionInfo> questionInfoList = coursePracticeQuestionInfoService.listByRoomIdAndQuestionId(roomId, questionIdList);
        return new AsyncResult<>(questionInfoList);
    }

    /**
     * 数据合并
     *
     * @param baseQuestionInfoList           试题基础信息
     * @param courseBreakpointQuestionList   试题与断点绑定信息
     * @param coursePracticeQuestionInfoList 试题-直播 信息
     * @return 组合数据
     */
    private static List<TeacherQuestionBo> combinationQuestionInfo(
            final List<QuestionInfo> baseQuestionInfoList,
            List<CourseBreakpointQuestion> courseBreakpointQuestionList,
            final List<CoursePracticeQuestionInfo> coursePracticeQuestionInfoList) {
        if (CollectionUtils.isEmpty(courseBreakpointQuestionList)) {
            return Lists.newArrayList();
        }
        List<TeacherQuestionBo> result = courseBreakpointQuestionList.parallelStream()
                .map(courseBreakpointQuestion -> {
                    final TeacherQuestionBo teacherQuestionBo = new TeacherQuestionBo();
                    Optional<QuestionInfo> baseQuestionInfoOptional = baseQuestionInfoList.stream()
                            .filter(questionInfo -> questionInfo.getId().equals(courseBreakpointQuestion.getQuestionId()))
                            .findAny();
                    if (baseQuestionInfoOptional.isPresent()) {
                        BeanUtils.copyProperties(baseQuestionInfoOptional.get(), teacherQuestionBo);
                    }
                    Optional<CoursePracticeQuestionInfo> practiceQuestionInfoOptional = coursePracticeQuestionInfoList.stream()
                            .filter(coursePracticeQuestionInfo -> coursePracticeQuestionInfo.getId().equals(courseBreakpointQuestion.getId()))
                            .findAny();
                    if (practiceQuestionInfoOptional.isPresent()) {
                        final CoursePracticeQuestionInfo coursePracticeQuestionInfo = practiceQuestionInfoOptional.get();
                        teacherQuestionBo.setPracticeTime(coursePracticeQuestionInfo.getPracticeTime());
                        Long practiceTime = System.currentTimeMillis() - coursePracticeQuestionInfo.getStartPracticeTime();
                        //计算剩余时间
                        teacherQuestionBo.setStartPracticeTime(practiceTime < 0 ? -1 : practiceTime);
                    }
                    teacherQuestionBo.setPptIndex(courseBreakpointQuestion.getPptIndex());
                    return teacherQuestionBo;
                })
                .collect(Collectors.toList());
        return result;
    }
}
