package com.huatu.tiku.course.dao.provider;

import com.google.common.base.Joiner;
import com.google.common.collect.Lists;
import com.huatu.tiku.essay.constant.status.EssayAnswerConstant;
import com.huatu.tiku.essay.essayEnum.EssayStatusEnum;

import java.util.List;

/**
 * 描述：
 *
 * @author biguodong
 * Create time 2019-03-06 5:48 PM
 **/
public class CourseExercisesProcessEssayLogProvider {

    public String getEssayCoursePageInfo(long userId, int page, int size){
        String tempTable = essayTempTable(userId);
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append(" SELECT * FROM (")
                .append(tempTable)
                .append(" ) AS temp_table")
                .append(" LIMIT ")
                .append(page -1).append(",").append(size);
        return stringBuilder.toString();
    }

    /**
     * 申论查询信息
     * @param userId
     * @return
     */
    private String essayTempTable(long userId){
        List<Integer> list = Lists.newArrayList(EssayAnswerConstant.EssayAnswerBizStatusEnum.INIT.getBizStatus(),
                EssayAnswerConstant.EssayAnswerBizStatusEnum.UNFINISHED.getBizStatus());

        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append(" SELECT");
        stringBuilder.append(" course_id as courseId,");
        stringBuilder.append(" GROUP_CONCAT( distinct syllabus_id) AS syllabusIds");
        stringBuilder.append(" FROM");
        stringBuilder.append(" v_essay_exercises_answer_meta");
        stringBuilder.append(" WHERE");
        stringBuilder.append(" biz_status in (");
        stringBuilder.append(Joiner.on(",").join(list));
        stringBuilder.append(" )");
        stringBuilder.append(" AND user_id = ").append(userId);
        stringBuilder.append(" AND status = ").append(EssayStatusEnum.NORMAL.getCode());
        stringBuilder.append(" GROUP BY");
        stringBuilder.append(" course_id");
        //stringBuilder.append(" ORDER BY");
        //stringBuilder.append(" gmt_modify DESC");
        return stringBuilder.toString();
    }
}
