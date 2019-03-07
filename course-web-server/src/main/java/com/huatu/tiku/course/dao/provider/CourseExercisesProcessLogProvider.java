package com.huatu.tiku.course.dao.provider;

import com.google.common.collect.Lists;
import com.huatu.tiku.course.common.BizStatusEnum;

import java.util.List;
import java.util.stream.Collectors;

/**
 * 描述：
 *
 * @author biguodong
 * Create time 2019-03-06 5:48 PM
 **/
public class CourseExercisesProcessLogProvider {

    public String getCoursePageInfo(long userId, int page, int size){
        String tempTable = tempTable(userId);
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append(" SELECT * FROM (")
                .append(tempTable)
                .append(" ) AS temp_table")
                .append(" LIMIT ")
                .append(page -1).append(",").append(size);
        return stringBuilder.toString();
    }

    private String tempTable(long userId){
        List<BizStatusEnum> list = Lists.newArrayList(BizStatusEnum.UN_DONE, BizStatusEnum.UN_FINISH);

        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append(" SELECT");
        stringBuilder.append(" course_id as courseId,");
        stringBuilder.append(" GROUP_CONCAT(syllabus_id) AS syllabusIds");
        stringBuilder.append(" FROM");
        stringBuilder.append(" course_exercises_process_log");
        stringBuilder.append(" WHERE");
        stringBuilder.append(" biz_status in (");
        stringBuilder.append(list.stream().map(BizStatusEnum::getKey).map(String::valueOf).collect(Collectors.joining(",")));
        stringBuilder.append(" )");
        stringBuilder.append(" AND user_id = ").append(userId);
        stringBuilder.append(" GROUP BY");
        stringBuilder.append(" course_id");
        stringBuilder.append(" ORDER BY");
        stringBuilder.append(" gmt_modify DESC");
        return stringBuilder.toString();
    }
}
