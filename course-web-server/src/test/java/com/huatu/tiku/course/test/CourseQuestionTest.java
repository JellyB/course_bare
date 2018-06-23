package com.huatu.tiku.course.test;

import com.huatu.common.test.BaseWebTest;
import com.huatu.tiku.course.service.v1.CourseBreakpointQuestionService;
import com.huatu.tiku.course.service.v1.CourseBreakpointService;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;
import java.util.Map;

/**
 * Created by lijun on 2018/6/19
 */
public class CourseQuestionTest extends BaseWebTest {

    @Autowired
    CourseBreakpointService service;

    @Autowired
    CourseBreakpointQuestionService questionService;

    @Test
    public void test(){
        List<Map<String, Object>> mapList = questionService.listQuestionIdByPointId(2L);
        System.out.println(mapList.size());
    }
}
