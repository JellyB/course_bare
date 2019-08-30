package com.huatu.tiku.course.service.v7.impl;

import com.alibaba.fastjson.JSONObject;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.huatu.common.SuccessMessage;
import com.huatu.common.exception.BizException;
import com.huatu.tiku.course.bean.vo.CourseWorkCourseVo;
import com.huatu.tiku.course.bean.vo.CourseWorkWareVo;
import com.huatu.tiku.course.bean.vo.LiveRecordInfo;
import com.huatu.tiku.course.bean.vo.LiveRecordInfoWithUserInfo;
import com.huatu.tiku.course.common.StudyTypeEnum;
import com.huatu.tiku.course.common.SubjectEnum;
import com.huatu.tiku.course.common.YesOrNoStatus;
import com.huatu.tiku.course.consts.RabbitMqConstants;
import com.huatu.tiku.course.dao.manual.CourseExercisesProcessEssayLogMapper;
import com.huatu.tiku.course.service.manager.CourseExercisesProcessLogManager;
import com.huatu.tiku.course.service.v7.UserCourseBizV7Service;
import com.huatu.tiku.entity.CourseExercisesProcessEssayLog;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections.MapUtils;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import tk.mybatis.mapper.entity.Example;

import java.sql.Timestamp;
import java.util.List;
import java.util.Map;


/**
 * 描述：
 *
 * @author biguodong
 * Create time 2019-03-22 9:50 PM
 **/
@Service
@Slf4j
public class UserCourseBizV7ServiceImpl implements UserCourseBizV7Service {


    @Autowired
    private RabbitTemplate rabbitTemplate;

    @Autowired
    private CourseExercisesProcessLogManager courseExercisesProcessLogManager;

    @Autowired
    private CourseExercisesProcessEssayLogMapper courseExercisesProcessEssayLogMapper;





    /**
     * 直播数据上报处理
     *
     * @param userId
     * @param userName
     * @param subject
     * @param terminal
     * @param liveRecordInfo
     * @param cv
     * @throws BizException
     */
    @Override
    public void dealLiveReport(int userId, String userName, int subject, int terminal, String cv, LiveRecordInfo liveRecordInfo) throws BizException {



        LiveRecordInfoWithUserInfo liveRecordInfoWithUserId = LiveRecordInfoWithUserInfo
                .builder()
                .subject(subject)
                .terminal(terminal)
                .userName(userName)
                .cv(cv)
                .userId(userId)
                .liveRecordInfo(liveRecordInfo).build();
        log.debug("学员直播上报数据v7:{}", JSONObject.toJSONString(liveRecordInfoWithUserId));
        rabbitTemplate.convertAndSend("", RabbitMqConstants.COURSE_LIVE_REPORT_LOG, JSONObject.toJSONString(liveRecordInfoWithUserId));
    }

    /**
     * 课后作业全部已读 行测、申论
     *
     * @param userId
     * @param type
     * @param uName
     * @throws BizException
     */
    @Override
    public int allReadByType(long userId, String type, String uName) throws BizException {
        int updateCount = 0;
        try{
            updateCount = courseExercisesProcessLogManager.allReadByType(userId, type, uName);
            StudyTypeEnum studyTypeEnum = StudyTypeEnum.create(type);
            //更新申论课后作业小红点
            if(studyTypeEnum.equals(StudyTypeEnum.COURSE_WORK)){
                updateCount = updateCount + allReadEssayCourseWork(userId);
            }
        }catch (Exception e){
            e.printStackTrace();
            log.error("UserCourseBizV7Service.allReadByType error: userId:{}, type:{}", userId, type);
            updateCount = -1;
        }
        return updateCount;
    }

    /**
     * 单条已读
     *
     * @param userId
     * @param syllabusId
     * @return
     * @throws BizException
     */
    @Override
    public Object readyOneCourseWork(int userId, int type, long syllabusId) throws BizException {
        SubjectEnum subjectEnum = SubjectEnum.create(type);
        if(subjectEnum == SubjectEnum.XC){
            courseExercisesProcessLogManager.readyOneCourseWorkBySyllabusId(userId, syllabusId);
        }
        if(subjectEnum == SubjectEnum.SL){
            readyOneEssayCourseWorkBySyllabusId(userId, syllabusId);
        }
        return SuccessMessage.create("操作成功");
    }


    /**
     * 单条已读 - 大纲id
     * @param userId
     * @param syllabusId
     * @return
     * @throws BizException
     */
    private int readyOneEssayCourseWorkBySyllabusId(int userId, long syllabusId) throws BizException {
        CourseExercisesProcessEssayLog courseExercisesProcessEssayLog = new CourseExercisesProcessEssayLog();
        courseExercisesProcessEssayLog.setGmtModify(new Timestamp(System.currentTimeMillis()));
        courseExercisesProcessEssayLog.setIsAlert(YesOrNoStatus.NO.getCode());

        Example example = new Example(CourseExercisesProcessEssayLog.class);
        example.and().andEqualTo("userId", userId)
                .andEqualTo("syllabusId", syllabusId);
        return	courseExercisesProcessEssayLogMapper.updateByExampleSelective(courseExercisesProcessEssayLog, example);
    }



    /**
     * 更新申论课后作业全部已读
     */
    private int allReadEssayCourseWork(long userId){

        CourseExercisesProcessEssayLog courseExercisesProcessEssayLog = new CourseExercisesProcessEssayLog();
        courseExercisesProcessEssayLog.setGmtModify(new Timestamp(System.currentTimeMillis()));
        courseExercisesProcessEssayLog.setIsAlert(YesOrNoStatus.NO.getCode());

        Example example = new Example(CourseExercisesProcessEssayLog.class);
        example.and().andEqualTo("status", YesOrNoStatus.YES.getCode())
                .andEqualTo("userId", userId)
                .andEqualTo("isAlert", YesOrNoStatus.YES.getCode());
        int count = courseExercisesProcessEssayLogMapper.updateByExampleSelective(courseExercisesProcessEssayLog,
                example);
        return count;
    }


    /**
     * 获取行测、申论课后作业列表
     *
     * @param userId
     * @param type
     * @param page
     * @param size
     * @return
     * @throws BizException
     */
    @Override
    public Object courseWorkList(long userId, int type, int page, int size) throws BizException {
        SubjectEnum subjectEnum = SubjectEnum.create(type);
        Map<String,Object> result = Maps.newHashMap();
        List<CourseWorkCourseVo> list = Lists.newArrayList();
        if(subjectEnum == SubjectEnum.XC){
            list.addAll((List<CourseWorkCourseVo>) courseExercisesProcessLogManager.courseWorkList(userId, page, size));
        }
        if(subjectEnum == SubjectEnum.SL){
            // TODO 通过 rest 接口获取申论课后作业列表
            list.addAll((List<CourseWorkCourseVo>) courseExercisesProcessLogManager.courseWorkList(userId, page, size));
            list.forEach(item -> {
                List<CourseWorkWareVo> courseWorkWareVos = item.getWareInfoList();
                for (CourseWorkWareVo courseWorkWareVo : courseWorkWareVos) {
                    courseWorkWareVo.setQuestionType(0);
                    courseWorkWareVo.setSyllabusId(141324L);
                    courseWorkWareVo.getAnswerCardInfo().setType(SubjectEnum.SL.getCode());
                }
            });
        }
        result.put("list", list);
        result.put("civilUnRead", 1);
        result.put("essayUnRead", 3);
        return result;
    }

    /**
     * 课后作业未读数
     *
     * @param userId
     * @param userName
     * @return
     * @throws BizException
     */
    @Override
    public Map<String, Integer> getCountByType(long userId, String userName) throws BizException {
        Map<String, Integer> result = courseExercisesProcessLogManager.getCountByType(userId, userName);
        //todo 通过 redis 查询申论课后作业数目
        int essay = 0;
        int origin = MapUtils.getIntValue(result, StudyTypeEnum.COURSE_WORK.getKey(), 0);
        result.put(StudyTypeEnum.COURSE_WORK.getKey(), essay + origin);
        return result;
    }


    /**
     * 申论课后作业处理队列
     *
     * @param type
     * @param message
     * @throws BizException
     */
    @Override
    public void dealEssayCourseWork(String type, String message) throws BizException {

    }
}
