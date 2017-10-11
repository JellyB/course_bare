package com.huatu.tiku.course.netschool.api.fall;

import com.google.common.collect.Maps;
import com.huatu.tiku.course.bean.CourseListV3DTO;
import com.huatu.tiku.course.bean.NetSchoolResponse;
import com.huatu.tiku.course.netschool.api.v3.CourseServiceV3;
import com.huatu.tiku.course.util.RequestUtil;
import com.huatu.tiku.course.util.ResponseUtil;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;

import java.util.Map;

/**
 * @author hanchao
 * @date 2017/10/2 14:53
 */
@Component
public class CourseServiceV3Fallback implements CourseServiceV3 {


    /**
     * 添加课程缓存
     * @param params
     * @param response
     */
    public void setLiveList(Map<String,Object> params,NetSchoolResponse response){
        String key = "_mock_live_list$"+ RequestUtil.getParamSign(params);
        if(ResponseUtil.isSuccess(response)){
            FallbackCache.put(key,response);
        }
    }

    public void setCourseDetail(int rid,NetSchoolResponse response){
        String key = "_mock_course_detail$"+ rid;
        if(ResponseUtil.isSuccess(response)){
            FallbackCache.put(key,response);
        }
    }

    public void setCourseH5(int rid,String response){
        String key = "_mock_course_h5$"+ rid;
        if(StringUtils.isNotBlank(response)){
            FallbackCache.put(key,response);
        }
    }

    public void setRecordingList(Map<String,Object> params,NetSchoolResponse response){
        String key = "_mock_recoding_list$"+ RequestUtil.getParamSign(params);
        if(ResponseUtil.isSuccess(response)){
            FallbackCache.put(key,response);
        }
    }


    public void setCollectionDetail(String shortTitle, String username, int page,NetSchoolResponse response){
        String key = "_mock_collection_detail$"+ shortTitle+"$"+username+"$"+page;
        if(ResponseUtil.isSuccess(response)){
            FallbackCache.put(key,response);
        }
    }


    @Override
    public NetSchoolResponse getCollectionDetail(String shortTitle, String username, int page) {
        String key = "_mock_collection_detail$"+ shortTitle+"$"+username+"$"+page;
        NetSchoolResponse response = FallbackCache.get(key);
        if(response == null){
            return NetSchoolResponse.DEFAULT;
        }else{
            CourseListV3DTO courseListV3DTO = ResponseUtil.build(response, CourseListV3DTO.class, false);
            courseListV3DTO.setCache(true);
            return response;
        }
    }

    @Override
    public NetSchoolResponse getCourseLimit(Map<String, Object> params) {
        return NetSchoolResponse.newInstance(Maps.newHashMap());
    }

    @Override
    public NetSchoolResponse findRecordingList(Map<String, Object> params) {
        String key = "_mock_recoding_list$"+ RequestUtil.getParamSign(params);
        NetSchoolResponse response = FallbackCache.get(key);
        if(response == null){
            return NetSchoolResponse.DEFAULT;
        }else{
            return response;
        }
    }

    @Override
    public NetSchoolResponse findLiveList(Map<String, Object> params) {
        String key = "_mock_live_list$"+ RequestUtil.getParamSign(params);
        NetSchoolResponse response = FallbackCache.get(key);
        if(response == null){
            return NetSchoolResponse.DEFAULT;
        }else{
            CourseListV3DTO courseListV3DTO = ResponseUtil.build(response, CourseListV3DTO.class, false);
            courseListV3DTO.setCache(true);
            return response;
        }
    }

    @Override
    public NetSchoolResponse getCourseDetail(int rid) {
        String key = "_mock_course_detail$"+ rid;
        NetSchoolResponse response = FallbackCache.get(key);
        if(response == null){
            return NetSchoolResponse.DEFAULT;
        }else{
            return response;
        }
    }

    @Override
    public String getCourseHtml(int rid) {
        String key = "_mock_course_h5$"+ rid;
        String response = FallbackCache.get(key);
        if(StringUtils.isNotBlank(response)){
            return response;
        }else{
            return "人太多了，服务器要休息一会儿~";
        }
    }

    @Override
    public NetSchoolResponse getCourseSecrInfo(Map<String, Object> params) {
        return NetSchoolResponse.DEFAULT_ERROR;
    }

    @Override
    public NetSchoolResponse findTeachersByCourse(int rid) {
        return NetSchoolResponse.DEFAULT_ERROR;
    }

    @Override
    public NetSchoolResponse findTimetable(int rid) {
        return NetSchoolResponse.DEFAULT_ERROR;
    }

    @Override
    public NetSchoolResponse getHandouts(Map<String, Object> params) {
        return null;
    }
}