package com.huatu.tiku.course.dao.essay;

import com.huatu.tiku.course.dao.provider.CourseExercisesProcessEssayLogProvider;
import com.huatu.tiku.essay.entity.courseExercises.EssayExercisesAnswerMeta;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.SelectProvider;
import org.springframework.stereotype.Repository;
import tk.mybatis.mapper.common.Mapper;

import java.util.HashMap;
import java.util.List;

/**
 * 描述：
 *
 * @author biguodong
 * Create time 2019-08-31 1:45 PM
 **/

@Repository
public interface EssayExercisesAnswerMetaMapper extends Mapper<EssayExercisesAnswerMeta> {

    /**
     * 获取课后作业分页详情
     * @param userId
     * @param page
     * @param size
     * @return
     */
    @SelectProvider(type = CourseExercisesProcessEssayLogProvider.class, method = "getEssayCoursePageInfo")
    List<HashMap<String, Object>> getEssayCoursePageInfo(long userId, int page, int size);

    /**
     *
     * @param answerCardId
     * @return
     */
    @SelectProvider(type = CourseExercisesProcessEssayLogProvider.class, method = "getBizStatusByCardId")
    HashMap<String, Object> getBizStatusByCardId(@Param(value = "answerCardId") long answerCardId);


}
