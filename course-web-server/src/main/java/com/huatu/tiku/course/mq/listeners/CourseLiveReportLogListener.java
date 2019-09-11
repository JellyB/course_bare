package com.huatu.tiku.course.mq.listeners;

import com.alibaba.fastjson.JSONObject;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import com.google.common.collect.Table;
import com.huatu.tiku.course.bean.vo.LiveRecordInfo;
import com.huatu.tiku.course.bean.vo.LiveRecordInfoWithUserInfo;
import com.huatu.tiku.course.bean.vo.SyllabusWareInfo;
import com.huatu.tiku.course.common.SubjectEnum;
import com.huatu.tiku.course.consts.RabbitMqConstants;
import com.huatu.tiku.course.netschool.api.v6.UserCourseServiceV6;
import com.huatu.tiku.course.service.manager.CourseExercisesProcessLogManager;
import com.huatu.tiku.course.service.manager.EssayExercisesAnswerMetaManager;
import com.huatu.tiku.essay.essayEnum.CourseWareTypeEnum;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Map;

/**
 * 描述：
 *
 * @author biguodong
 * Create time 2019-03-22 10:31 PM
 **/

@Slf4j
@Component
public class CourseLiveReportLogListener {

    @Autowired
    private CourseExercisesProcessLogManager courseExercisesProcessLogManager;

    @Autowired
    private EssayExercisesAnswerMetaManager essayExercisesAnswerMetaManager;

    @Autowired
    private UserCourseServiceV6 userCourseService;

    @RabbitListener(queues = RabbitMqConstants.COURSE_LIVE_REPORT_LOG)
    public void onMessage(String message){
        log.info("直播观看到满足进度，创建课后作业答题卡v7:{}", message);
        LiveRecordInfoWithUserInfo liveRecordInfoWithUserId = JSONObject.parseObject(message, LiveRecordInfoWithUserInfo.class);
        if(null == liveRecordInfoWithUserId){
            return;
        }
        LiveRecordInfo liveRecordInfo = liveRecordInfoWithUserId.getLiveRecordInfo();
        if(null == liveRecordInfo){
            return;
        }
        if(liveRecordInfo.getSyllabusId() == 0){
            return;
        }

        Map<String,Object> params = Maps.newHashMap();
        params.put("userName", liveRecordInfoWithUserId.getUserName());
        params.put("syllabusId", liveRecordInfo.getSyllabusId());
        params.put("terminal", liveRecordInfoWithUserId.getTerminal());
        params.put("cv", liveRecordInfoWithUserId.getCv());
        userCourseService.saveLiveRecord(params);
        //创建课后作业

        Table<String, Long, SyllabusWareInfo> syllabusWareInfoTable = courseExercisesProcessLogManager.dealSyllabusInfo(Sets.newHashSet(liveRecordInfo.getSyllabusId()));
        SyllabusWareInfo syllabusWareInfo = syllabusWareInfoTable.get(CourseExercisesProcessLogManager.LESSON_LABEL, liveRecordInfo.getSyllabusId());
        if(null == syllabusWareInfo){
            return;
        }
        /**
         * 移动端数据上报，只处理录播的学习进度，回放不处理
         */
        if(syllabusWareInfo.getVideoType() == CourseWareTypeEnum.VideoTypeEnum.LIVE_PLAY_BACK.getVideoType()){
            return;
        }
        // 申论创建课后作业
        if(null != syllabusWareInfo.getSubjectType() && syllabusWareInfo.getSubjectType() == SubjectEnum.SL.getCode()){
            int courseType = CourseWareTypeEnum.changeVideoType2TableCourseType(syllabusWareInfo.getVideoType());
            essayExercisesAnswerMetaManager.createEssayInitUserMeta(liveRecordInfoWithUserId.getUserId(), liveRecordInfo.getSyllabusId(), courseType, syllabusWareInfo.getCoursewareId(), syllabusWareInfo.getClassId());
        }else{
            courseExercisesProcessLogManager.saveLiveRecord(liveRecordInfoWithUserId.getUserId(),
                    liveRecordInfoWithUserId.getSubject(),
                    liveRecordInfoWithUserId.getTerminal(),
                    liveRecordInfo.getSyllabusId(),
                    liveRecordInfoWithUserId.getCv());
        }
    }
}
