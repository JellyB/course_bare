package com.huatu.tiku.course.service.v5.impl;

import com.huatu.common.utils.collection.HashMapBuilder;
import com.huatu.tiku.course.bean.NetSchoolResponse;
import com.huatu.tiku.course.netschool.api.v5.CourseServiceV5;
import com.huatu.tiku.course.service.cache.CacheUtil;
import com.huatu.tiku.course.service.cache.CourseCacheKey;
import com.huatu.tiku.course.service.v5.CourseServiceV5Biz;
import com.huatu.tiku.course.util.ResponseUtil;
import org.apache.commons.collections.MapUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import java.util.function.Supplier;
import java.util.stream.Collectors;

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
    public String getClassExt(int classId, int terminal) {
        Supplier key = () -> CourseCacheKey.classExtKeyV5(classId, terminal);
        Supplier<String> value = () -> {
            String classExt = courseService.getClassExt(classId, terminal);
            return classExt;
        };
        return cacheUtil.getCacheStringValue(key, value, 30, TimeUnit.MINUTES);
    }

    @Override
    public Object findTimetable(int classId, int parentId, int page, int pageSize) {
        Supplier key = () -> CourseCacheKey.timeTableKeyV5(classId, parentId, page, pageSize);
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
            return cacheUtil.getCacheStringValue(key, value, 5, TimeUnit.MINUTES);
        }
        return value.get();
    }


    @Override
    public Object findPurchasesTimetable(HashMap<String, Object> map) {
        NetSchoolResponse purchasesTimetable = courseService.findPurchasesTimetable(map);
        LinkedHashMap response = (LinkedHashMap) (ResponseUtil.build(purchasesTimetable));
        response.computeIfPresent("list", (key, value) -> {
                    if (null != MapUtils.getString(response, "type") && MapUtils.getString(response, "type").equals("2")) {
                        HashMap<String, Object> answerCard = HashMapBuilder.<String, Object>newBuilder()
                                .put("status", 1)
                                .put("questionCount", 5)
                                .put("rightCount", 2)
                                .put("wrongCount", 3)
                                .build();
                        List<Map> buildResultList = ((List<Map>) value).parallelStream()
                                .map(dataMap -> {
                                    dataMap.put("answerCard", answerCard);
                                    return dataMap;
                                })
                                .collect(Collectors.toList());
                        return buildResultList;
                    } else {
                        return value;
                    }
                }
        );
        return response;
    }
}
