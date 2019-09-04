package com.huatu.tiku.course.dao.provider;

import com.google.common.base.Joiner;
import com.google.common.collect.Lists;
import com.huatu.tiku.essay.constant.status.EssayAnswerConstant;
import com.huatu.tiku.essay.essayEnum.EssayStatusEnum;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.SelectProvider;

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
                EssayAnswerConstant.EssayAnswerBizStatusEnum.UNFINISHED.getBizStatus(),
                EssayAnswerConstant.EssayAnswerBizStatusEnum.CORRECT_RETURN.getBizStatus());

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


    public String selectQuestionBaseById(@Param(value = "questionBaseId") Long questionBaseId){
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append(" SELECT");
        stringBuilder.append(" *");
        stringBuilder.append(" FROM");
        stringBuilder.append(" v_essay_question_base t");
        stringBuilder.append(" WHERE");
        stringBuilder.append(" t.id = ").append(questionBaseId);
        return stringBuilder.toString();
    }

    public String selectPaperBaseById(@Param(value = "paperBaseId") Long paperBaseId){
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append(" SELECT");
        stringBuilder.append(" *");
        stringBuilder.append(" FROM");
        stringBuilder.append(" v_essay_paper_base");
        stringBuilder.append(" WHERE");
        stringBuilder.append(" id = ").append(paperBaseId);
        return stringBuilder.toString();
    }

    /**
     * 根据 id 查询 question_detail
     * @param detailId
     * @return
     */
    public String selectQuestionDetailById(@Param(value = "detailId") Long detailId){
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append(" SELECT");
        stringBuilder.append(" *");
        stringBuilder.append(" FROM");
        stringBuilder.append(" v_essay_question_detail");
        stringBuilder.append(" WHERE");
        stringBuilder.append(" id = ").append(detailId);
        return stringBuilder.toString();
    }
}
