package com.huatu.tiku.course.service.v1.impl.practice;

import com.alibaba.fastjson.JSON;
import com.huatu.tiku.course.bean.practice.LiveCallbackBo;
import com.huatu.tiku.course.bean.practice.PracticeRoomRankUserBo;
import com.huatu.tiku.course.bean.practice.QuestionMetaBo;
import com.huatu.tiku.course.service.v1.practice.LiveCallBackService;
import com.huatu.tiku.course.service.v1.practice.PracticeQuestionInfoService;
import com.huatu.tiku.course.service.v1.practice.TeacherService;
import com.huatu.tiku.entity.CoursePracticeQuestionInfo;
import lombok.RequiredArgsConstructor;
import org.apache.commons.collections.CollectionUtils;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import tk.mybatis.mapper.entity.Example;
import tk.mybatis.mapper.weekend.WeekendSqls;

import java.util.List;
import java.util.concurrent.ExecutionException;

/**
 * Created by lijun on 2019/3/7
 */
@Service
@RequiredArgsConstructor
public class LiveCallBackServiceImpl implements LiveCallBackService {

    private final PracticeMetaComponent practiceMetaComponent;

    private final TeacherService teacherService;
    private final PracticeQuestionInfoService practiceQuestionInfoService;

    @Override
    @Async
    public void liveCallBackAllInfo(Long roomId, List<LiveCallbackBo> liveCallbackBoList) throws ExecutionException, InterruptedException {
        //获取所有试题信息

    }

    /**
     * 持久化 用户统计信息sa
     * 此处执行新增操作
     */
    private void enduranceUserMeta(Long roomId) {
        List<PracticeRoomRankUserBo> roomRankInfoList = practiceMetaComponent.getRoomRankInfo(roomId, 0, -1);
        //1.远程处理 - 创建答题卡信息 - 获取答题卡ID
        //1.1 获取用户排名 集合
        practiceMetaComponent.getRoomRankInfo(roomId,0 ,-1);
        //1.2 获取获取所有的答题信息
        // teacher.getQuestion() 作为试题基础数组
        //1.3 获取用的所有答题信息
        //practiceMetaComponent.getUserMeta()

        //2.

    }


    /**
     * 持久化试题统计信息
     * - 试题统计信息只缺最终的答题统计信息，此处执行修改操作
     */
    public void enduranceQuestionInfo(final Long roomId) {
        List<Long> roomPracticedQuestion = practiceMetaComponent.getRoomPracticedQuestion(roomId);
        if (CollectionUtils.isEmpty(roomPracticedQuestion)) {
            return;
        }
        roomPracticedQuestion.forEach(questionId -> {
            QuestionMetaBo questionMetaBo = practiceMetaComponent.getQuestionMetaBo(roomId, questionId);
            WeekendSqls<CoursePracticeQuestionInfo> sql = WeekendSqls.<CoursePracticeQuestionInfo>custom()
                    .andEqualTo(CoursePracticeQuestionInfo::getRoomId, roomId)
                    .andEqualTo(CoursePracticeQuestionInfo::getQuestionId, questionId);
            Example example = Example.builder(CoursePracticeQuestionInfo.class)
                    .where(sql)
                    .build();
            CoursePracticeQuestionInfo coursePracticeQuestionInfo = CoursePracticeQuestionInfo.builder()
                    .meta(JSON.toJSONString(questionMetaBo))
                    .build();
            practiceQuestionInfoService.updateByExampleSelective(coursePracticeQuestionInfo, example);
        });
    }

}
