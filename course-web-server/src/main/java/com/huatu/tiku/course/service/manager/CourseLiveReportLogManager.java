package com.huatu.tiku.course.service.manager;

import com.huatu.common.exception.BizException;
import com.huatu.tiku.course.bean.vo.LiveRecordInfo;
import com.huatu.tiku.course.bean.vo.LiveRecordInfoWithUserInfo;
import com.huatu.tiku.course.common.YesOrNoStatus;
import com.huatu.tiku.course.dao.manual.CourseLiveReportLogMapper;
import com.huatu.tiku.entity.CourseLiveReportLog;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import tk.mybatis.mapper.entity.Example;

/**
 * 描述：学员直播听课记录汇总
 *
 * @author biguodong
 * Create time 2019-03-23 2:43 PM
 **/

@Slf4j
@Component
public class CourseLiveReportLogManager {
    @Autowired
    private CourseLiveReportLogMapper courseLiveReportLogMapper;

    public synchronized void saveOrUpdate(LiveRecordInfoWithUserInfo liveRecordInfoWithUserInfo) throws BizException{
        LiveRecordInfo liveRecordInfo = liveRecordInfoWithUserInfo.getLiveRecordInfo();
        Example example = new Example(CourseLiveReportLog.class);
        example.and()
                .andEqualTo("userId", liveRecordInfoWithUserInfo.getUserId())
                .andEqualTo("bjyRoomId",liveRecordInfo.getBjyRoomId())
                .andEqualTo("classId", liveRecordInfo.getClassId())
                .andEqualTo("courseWareId", liveRecordInfo.getCourseWareId())
                .andEqualTo("status", YesOrNoStatus.YES.getCode());

        CourseLiveReportLog courseLiveReportLog = courseLiveReportLogMapper.selectOneByExample(example);
        if(null == courseLiveReportLog){
            CourseLiveReportLog newLog = new CourseLiveReportLog();
            newLog.setBjyRoomId(liveRecordInfo.getBjyRoomId());
            newLog.setClassId(liveRecordInfo.getClassId());
            newLog.setCardId(liveRecordInfo.getCourseWareId());
            newLog.setCourseWareId(liveRecordInfo.getCourseWareId());
            newLog.setUserId(new Long(liveRecordInfoWithUserInfo.getUserId()));
            newLog.setStatus(YesOrNoStatus.YES.getCode());
            courseLiveReportLogMapper.insertSelective(newLog);
        }else{
            return;
        }
    }
}
