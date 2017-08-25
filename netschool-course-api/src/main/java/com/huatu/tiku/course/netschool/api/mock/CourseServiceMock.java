package com.huatu.tiku.course.netschool.api.mock;

import com.huatu.tiku.course.netschool.api.CourseService;
import com.huatu.tiku.course.netschool.bean.NetSchoolResponse;

/**
 * @author hanchao
 * @date 2017/8/25 17:47
 */
public class CourseServiceMock implements CourseService{
    @Override
    public NetSchoolResponse sydwList() {
        return NetSchoolResponse.DEFAULT;
    }
}
