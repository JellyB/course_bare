package com.huatu.tiku.course.service.v5.impl;

import com.huatu.common.utils.collection.HashMapBuilder;
import com.huatu.tiku.course.bean.NetSchoolResponse;
import com.huatu.tiku.course.netschool.api.v5.CourseServiceV5;
import com.huatu.tiku.course.service.cache.CacheUtil;
import com.huatu.tiku.course.service.cache.CourseCacheKey;
import com.huatu.tiku.course.service.v5.CourseServiceV5Biz;
import com.huatu.tiku.course.util.ResponseUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.concurrent.TimeUnit;
import java.util.function.Supplier;

/**
 * Created by lijun on 2018/6/25
 */
@Service
public class CourseServiceV5BizImpl implements CourseServiceV5Biz {

    @Autowired
    private CacheUtil cacheUtil;

    @Autowired
    private CourseServiceV5 courseService;

    @Override
    public Object getClassDetail(HashMap<String, Object> params) {
        Supplier key = () -> CourseCacheKey.courseDetailKey(params);
        Supplier<Object> value = () -> {
            NetSchoolResponse netSchoolResponse = courseService.getClassDetail(params);
            Object result = ResponseUtil.build(netSchoolResponse);
            return result;
        };
        return cacheUtil.getCacheStringValue(key, value, 30, TimeUnit.MINUTES);
    }

    @Override
    public Object getCourseIntroduction(String userName, int classId) {
        Supplier key = () -> CourseCacheKey.courseIntroductionKey(userName, classId);
        Supplier<Object> value = () -> {
            NetSchoolResponse netSchoolResponse = courseService.getCourseIntroduction(userName, classId);
            return ResponseUtil.build(netSchoolResponse);
        };
        return cacheUtil.getCacheStringValue(key, value, 30, TimeUnit.MINUTES);
    }

    @Override
    public String getClassExt(int classId, int terminal) {
        Supplier key = () -> CourseCacheKey.classExtKey(classId, terminal);
        Supplier<String> value = () -> {
            String classExt = courseService.getClassExt(classId, terminal);
            return classExt;
        };
        return cacheUtil.getCacheStringValue(key, value, 30, TimeUnit.MINUTES);
    }

    @Override
    public Object findTimetable(int classId, int parentId, int page, int pageSize) {
        Supplier key = () -> CourseCacheKey.timeTableKey(classId, parentId, page, pageSize);
        Supplier<Object> value = () -> {
            HashMap<String, Object> map = HashMapBuilder.<String, Object>newBuilder()
                    .put("classId", classId)
                    .put("parentId", parentId)
                    .put("page", page)
                    .put("pageSize", pageSize)
                    .build();
            NetSchoolResponse timetable = courseService.findTimetable(map);
            return ResponseUtil.build(timetable);
        };
        if (page == 1) {//只缓存第一页数据
            return cacheUtil.getCacheStringValue(key, value, 30, TimeUnit.MINUTES);
        }
        return value.get();
    }
}
