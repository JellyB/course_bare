package com.huatu.tiku.course.service.v1.impl.practice;

import com.alibaba.fastjson.JSONObject;
import com.google.common.collect.Lists;
import com.huatu.tiku.course.bean.practice.QuestionMetaBo;
import com.huatu.tiku.course.bean.vo.CoursewarePracticeQuestionVo;
import com.huatu.tiku.course.service.v1.practice.CoursewarePracticeQuestionInfoService;
import com.huatu.tiku.course.service.v1.practice.LiveCourseRoomInfoService;
import com.huatu.tiku.entity.CoursePracticeQuestionInfo;
import com.huatu.tiku.entity.CoursewarePracticeQuestionInfo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import service.impl.BaseServiceHelperImpl;
import tk.mybatis.mapper.entity.Example;
import tk.mybatis.mapper.weekend.WeekendSqls;

import java.util.List;

/**
 * @author shanjigang
 * @date 2019/3/23 19:07
 */
@Service
@Slf4j
public class CoursewarePracticeQuestionInfoServiceImpl extends BaseServiceHelperImpl<CoursewarePracticeQuestionInfo>
        implements CoursewarePracticeQuestionInfoService {
    public CoursewarePracticeQuestionInfoServiceImpl() {
        super(CoursewarePracticeQuestionInfo.class);
    }

    private  LiveCourseRoomInfoService liveCourseRoomInfoService;

    private  PracticeMetaComponent practiceMetaComponent;
    /**
     * 根据 roomId coursewareId 列表查询
     */
    public List<CoursewarePracticeQuestionVo> listByCoursewareIdAndQuestionIds(Long roomId, Long coursewareId){
        WeekendSqls<CoursewarePracticeQuestionInfo> weekendSqls = WeekendSqls.<CoursewarePracticeQuestionInfo>custom()
                .andEqualTo(CoursewarePracticeQuestionInfo::getRoomId, roomId)
                .andEqualTo(CoursewarePracticeQuestionInfo::getCoursewareId, coursewareId);
        Example example = Example.builder(CoursewarePracticeQuestionInfo.class).where(weekendSqls).build();
        List<CoursewarePracticeQuestionInfo> list=selectByExample(example);
        List<CoursewarePracticeQuestionVo> coursewarePracticeQuestionVos=Lists.newArrayList();
       for(int i=0;i<list.size();i++){
            CoursewarePracticeQuestionInfo info=list.get(i);
            String meta=info.getMeta();
            List<QuestionMetaBo> questionMetaBos=(List<QuestionMetaBo>)JSONObject.parseObject(meta,QuestionMetaBo.class);
            CoursewarePracticeQuestionVo course=new CoursewarePracticeQuestionVo();
            course.setCoursewareId(info.getCoursewareId());
            course.setRoomId(info.getRoomId());
            course.setMeta(questionMetaBos);
            coursewarePracticeQuestionVos.add(course);
        }
        return coursewarePracticeQuestionVos;
    }

    /**
     * 生成课件下试题作答信息
     */
    public void generateCoursewareAnswerCardInfo(Long roomId){
        //获取房间下的课件Id
        List<Integer> coursewareIds = liveCourseRoomInfoService.getLiveCourseIdListByRoomId(roomId);
        //获取房间下的已作答试题
        List<Long> roomPracticedQuestionIds = practiceMetaComponent.getRoomPracticedQuestion(roomId);

        coursewareIds.forEach(coursewareId->{
            List<QuestionMetaBo> questionMetaBos=Lists.newArrayList();
            roomPracticedQuestionIds.forEach(questionId->{
                QuestionMetaBo questionMetaBo = practiceMetaComponent.getCourseQuestionMetaBo( roomId,Long.valueOf(coursewareId),questionId);
                questionMetaBos.add(questionMetaBo);
            });
            //插入数据库
            CoursewarePracticeQuestionInfo coursewarePracticeQuestionInfo=new CoursewarePracticeQuestionInfo();
            String meta = JSONObject.toJSONString(questionMetaBos);
            coursewarePracticeQuestionInfo.setMeta(meta);
            coursewarePracticeQuestionInfo.setCoursewareId(Long.valueOf(coursewareId));
            coursewarePracticeQuestionInfo.setRoomId(roomId);
            save(coursewarePracticeQuestionInfo);
        });
    }
}
