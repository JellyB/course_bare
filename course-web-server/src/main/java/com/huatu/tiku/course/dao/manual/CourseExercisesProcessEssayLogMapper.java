package com.huatu.tiku.course.dao.manual;

import com.huatu.tiku.course.dao.provider.CourseExercisesProcessLogProvider;
import com.huatu.tiku.entity.CourseExercisesProcessEssayLog;
import org.apache.ibatis.annotations.SelectProvider;
import org.springframework.stereotype.Repository;
import tk.mybatis.mapper.common.Mapper;

import java.util.HashMap;
import java.util.List;

/**
 * 描述：
 *
 * @author biguodong
 * Create time 2019-02-25 下午6:13
 **/
@Repository
public interface CourseExercisesProcessEssayLogMapper extends Mapper<CourseExercisesProcessEssayLog> {

    /**
     * 获取课后作业分页详情
     * @param userId
     * @param page
     * @param size
     * @return
     */
    @SelectProvider(type = CourseExercisesProcessLogProvider.class, method = "getEssayCoursePageInfo")
    List<HashMap<String, Object>> getEssayCoursePageInfo(long userId, int page, int size);
}
