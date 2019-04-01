package com.huatu.tiku.course.service.v1.impl;

import com.google.common.base.Stopwatch;
import com.google.common.collect.Lists;
import com.huatu.tiku.course.service.v1.CourseBreakpointQuestionService;
import com.huatu.tiku.course.service.cache.CacheUtil;
import com.huatu.tiku.course.service.cache.CourseBreakpointCacheKey;
import com.huatu.tiku.course.util.ZTKResponseUtil;
import com.huatu.tiku.course.ztk.api.v1.question.QuestionServiceV1;
import com.huatu.tiku.entity.CourseBreakpointQuestion;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import service.impl.BaseServiceHelperImpl;
import tk.mybatis.mapper.entity.Example;
import tk.mybatis.mapper.weekend.WeekendSqls;

import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import java.util.function.Supplier;
import java.util.stream.Collectors;

/**
 * Created by lijun on 2018/6/20
 */
@Slf4j
@Service
public class CourseBreakpointQuestionServiceImpl extends BaseServiceHelperImpl<CourseBreakpointQuestion> implements CourseBreakpointQuestionService {

    public CourseBreakpointQuestionServiceImpl() {
        super(CourseBreakpointQuestion.class);
    }

    @Autowired
    private CacheUtil cacheUtil;

    @Autowired
    private QuestionServiceV1 questionService;

    @Override
    public List<Map<String, Object>> listQuestionIdByPointId(Long breakpointId) {
        //1.生成key
        Supplier keySupplier = () -> CourseBreakpointCacheKey.breakpointQuestionKey(breakpointId);
        //2.生成value
        Supplier<List<Map<String, Object>>> valueSupplier = () -> {
            WeekendSqls<CourseBreakpointQuestion> sql = WeekendSqls.custom();
            sql.andEqualTo(CourseBreakpointQuestion::getBreakpointId, breakpointId);

            Example example = Example.builder(CourseBreakpointQuestion.class)
                    .where(sql)
                    .build();
            List<CourseBreakpointQuestion> courseBreakpointQuestionList = selectByExample(example);
            if (courseBreakpointQuestionList.size() == 0) {
                return null;
            }
            //获取所有的questionId
            Stopwatch stopwatch = Stopwatch.createStarted();
            String questionIds = courseBreakpointQuestionList.stream()
                    .map(CourseBreakpointQuestion::getQuestionId)
                    .map(String::valueOf)
                    .collect(Collectors.joining(","));
            Object listQuestionByIds = questionService.listQuestionByIds(questionIds);
            log.info("课程-断点-获取考题信息 ===》{}", stopwatch.elapsed(TimeUnit.MILLISECONDS));
            List<Map<String, Object>> result = (List<Map<String, Object>>) ZTKResponseUtil.build(listQuestionByIds);
            return result;
        };

        List<Map<String, Object>> cacheStringValue = cacheUtil.getCacheStringValue(keySupplier, valueSupplier, 5, TimeUnit.MINUTES);
        return cacheStringValue;
    }


    @Override
    public List<CourseBreakpointQuestion> listQuestionIdByBreakpointIdList(List<Long> list) {
        if(CollectionUtils.isEmpty(list)){
            return Lists.newArrayList();
        }
        WeekendSqls<CourseBreakpointQuestion> sql = WeekendSqls.custom();
        sql.andIn(CourseBreakpointQuestion::getBreakpointId, list);
        Example example = Example.builder(CourseBreakpointQuestion.class)
				.where(sql).orderByAsc("pptIndex", "id")
                .build();
        List<CourseBreakpointQuestion> questions = selectByExample(example);
        return questions;
    }
}
