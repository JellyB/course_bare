package com.huatu.tiku.course.service.v6;

import com.huatu.common.exception.BizException;
import com.huatu.tiku.common.bean.user.UserSession;
import com.huatu.tiku.course.bean.NetSchoolResponse;
import com.huatu.tiku.springboot.users.support.Token;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * 描述：
 *
 * @author biguodong
 *         Create time 2019-01-08 下午11:12
 **/
public interface CourseServiceV6Biz {

    /**
     * 模考大赛解析课信息,多个id使用逗号分隔
     * 模考大赛专用
     *
     * @param classIds
     * @return
     */
    HashMap<String, LinkedHashMap> getClassAnalysis(String classIds);

    /**
     * 小模考历史解析课分页查询
     *
     * @param subject
     * @param page
     * @param size
     * @param startTime
     * @param endTime   @return
     */
    NetSchoolResponse analysisClassList(int subject, int page, int size, long startTime, long endTime);

    /*
     * 获取未完成的阶段测试列表
     * @param params
     * @return
     */
    Object periodTestList(Map<String, Object> params);

    /**
     * 查询我的课后作业报告
     * @param userSession
     * @param terminal
     * @param cardId
     * @return
     * @throws BizException
     */
    Object courseWorkReport(UserSession userSession, int terminal, long cardId) throws BizException;

    /**
     * 我的学习报告
     * @param userSession
     * @param bjyRoomId
     * @param classId
     * @param netClassId
     * @param courseWareId
     * @param videoType
     * @param terminal
     * @return
     * @throws BizException
     */
    Object learnReport(UserSession userSession, int bjyRoomId, int classId, int netClassId, int courseWareId, int videoType, long cardId, int terminal)throws BizException;

}
