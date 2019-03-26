package com.huatu.tiku.course.netschool.api.fall;


import com.huatu.tiku.course.bean.NetSchoolResponse;
import com.huatu.tiku.course.netschool.api.v6.LessonServiceV6;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.Map;

/**
 * 描述：
 *
 * @author biguodong
 * Create time 2019-03-13 1:25 PM
 **/

@Slf4j
@Component
public class LessonServiceFallback implements LessonServiceV6 {

    /**
     * 图书扫码听课详情
     *
     * @param params
     * @return
     */
    @Override
    public NetSchoolResponse playLesson(Map<String, Object> params) {
        return NetSchoolResponse.DEFAULT_ERROR;
    }

    /**
     * 课件收藏列表
     *
     * @param params
     * @return
     */
    @Override
    public NetSchoolResponse collections(Map<String, Object> params) {
        return NetSchoolResponse.DEFAULT_ERROR;
    }

    /**
     * 课件添加收藏
     *
     * @param params
     * @return
     */
    @Override
    public NetSchoolResponse collectionAdd(Map<String, Object> params) {
        return NetSchoolResponse.DEFAULT_ERROR;
    }

    /**
     * 课件取消收藏
     *
     * @param params
     * @return
     */
    @Override
    public NetSchoolResponse collectionCancel(Map<String, Object> params) {
        return NetSchoolResponse.DEFAULT_ERROR;
    }

    /**
     * 我的学习时长
     *
     * @param params 请求参数a
     * @return
     */
    @Override
    public NetSchoolResponse studyReport(Map<String, Object> params) {
        return NetSchoolResponse.DEFAULT_ERROR;
    }
}
