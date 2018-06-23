package com.huatu.tiku.course.service.v1.impl;

import com.huatu.common.spring.cache.Cached;
import com.huatu.tiku.course.service.v1.CourseBreakpointQuestionService;
import com.huatu.tiku.course.service.v1.CourseBreakpointService;
import com.huatu.tiku.course.service.v1.cache.CacheUtil;
import com.huatu.tiku.course.service.v1.cache.CourseBreakpointCacheKey;
import com.huatu.tiku.entity.CourseBreakpoint;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import service.impl.BaseServiceHelperImpl;
import tk.mybatis.mapper.entity.Example;
import tk.mybatis.mapper.weekend.WeekendSqls;

import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.concurrent.TimeUnit;
import java.util.function.Supplier;
import java.util.stream.Collectors;

/**
 * Created by lijun on 2018/6/19
 */
@Slf4j
@Service
public class CourseBreakpointServiceImpl extends BaseServiceHelperImpl<CourseBreakpoint> implements CourseBreakpointService {

    public CourseBreakpointServiceImpl() {
        super(CourseBreakpoint.class);
    }

    @Autowired
    private CacheUtil cacheUtil;

    @Autowired
    CourseBreakpointQuestionService courseBreakpointQuestionService;

    @Cached(name = "课程-断点信息",
            key = "T(com.huatu.tiku.course.service.v1.cache).breakpointKey(#map)",
            params = {@Cached.Param(name = "courseType=?&courseId=?", value = "map", type = Map.class)})
    @Override
    public Map<Integer, List<CourseBreakpoint>> listByCourseTypeAndId(Integer courseType, Long courseId) {
        //生成key
        Supplier keySupplier = () -> CourseBreakpointCacheKey.breakpointKey(courseId, courseType);
        //生成value
        Supplier<Map<Integer, List<CourseBreakpoint>>> supplier = () -> {
            WeekendSqls<CourseBreakpoint> sql = WeekendSqls.custom();
            sql.andEqualTo(CourseBreakpoint::getCourseType, courseType);
            sql.andEqualTo(CourseBreakpoint::getCourseId, courseId);

            Example example = Example.builder(CourseBreakpoint.class)
                    .where(sql)
                    .orderBy("position ")
                    .build();
            List<CourseBreakpoint> courseBreakpoints = selectByExample(example);
            if (courseBreakpoints.size() == 0) {
                return null;
            }
            Map<Integer, List<CourseBreakpoint>> map = courseBreakpoints.stream()
                    .collect(Collectors.groupingBy(CourseBreakpoint::getPosition));
            //按照 position(时间顺序) 排序
            TreeMap<Integer, List<CourseBreakpoint>> scoreMap = new TreeMap<>(Integer::compareTo);
            scoreMap.putAll(map);
            return scoreMap;
        };
        //生成缓存
        Map<Integer, List<CourseBreakpoint>> cacheStringValue = cacheUtil.getCacheStringValue(keySupplier, supplier, 30, TimeUnit.MINUTES);
        return cacheStringValue;
    }

    @Override
    public List<Long> listAllQuestionId(Integer courseType, Long courseId) {
        Supplier key = () -> CourseBreakpointCacheKey.breakpointCardQuestionKey(courseId, courseType);
        Supplier<List<Long>> value = () -> {
            Map<Integer, List<CourseBreakpoint>> map = listByCourseTypeAndId(courseType, courseId);
            if (map == null) {
                return null;
            }
            //获取所有的端点ID
            List<Long> breakPointIdList = map.entrySet().stream()
                    .flatMap(data -> data.getValue().stream().map(CourseBreakpoint::getId))
                    .collect(Collectors.toList());

            List<Long> questionIdList = courseBreakpointQuestionService.listQuestionIdByBreakpointIdList(breakPointIdList);
            return questionIdList;
        };
        //生成缓存
        return cacheUtil.getCacheStringValue(key, value, 30, TimeUnit.MINUTES);
    }
}
