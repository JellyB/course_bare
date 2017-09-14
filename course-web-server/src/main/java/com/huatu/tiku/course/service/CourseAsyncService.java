package com.huatu.tiku.course.service;

import com.alibaba.fastjson.JSON;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import com.huatu.common.exception.BizException;
import com.huatu.common.utils.cache.NullHolder;
import com.huatu.common.utils.encrypt.SignUtil;
import com.huatu.tiku.course.bean.CourseDetailV2DTO;
import com.huatu.tiku.course.bean.CourseDetailV3DTO;
import com.huatu.tiku.course.bean.CourseListV2DTO;
import com.huatu.tiku.course.bean.CourseListV3DTO;
import com.huatu.tiku.course.common.NetSchoolConfig;
import com.huatu.tiku.course.netschool.api.CourseServiceV1;
import com.huatu.tiku.course.netschool.api.v3.CourseServiceV3;
import com.huatu.tiku.course.netschool.api.v3.UserCoursesServiceV3;
import com.huatu.tiku.course.netschool.bean.NetSchoolResponse;
import com.huatu.tiku.course.util.CourseCKConst;
import com.huatu.tiku.course.util.RequestUtil;
import com.huatu.tiku.course.util.ResponseUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.exception.ExceptionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.AsyncResult;
import org.springframework.stereotype.Service;
import org.springframework.util.concurrent.ListenableFuture;

import javax.annotation.Resource;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

/**
 * 在同一个类中a
 * @author hanchao
 * @date 2017/9/12 21:46
 */
@Service
@Slf4j
@Async
public class CourseAsyncService {
    @Autowired
    private CourseServiceV1 courseServiceV1;
    @Autowired
    private CourseServiceV3 courseServiceV3;
    @Autowired
    private UserCoursesServiceV3 userCoursesServiceV3;

    @Resource(name = "redisTemplate")
    private ValueOperations valueOperations;


    /**
     * 获取用户购买课程
     * @param username
     * @return
     */
    @Async
    public ListenableFuture<Set<Integer>> getUserBuy(String username){
        Map<String,Object> params = Maps.newHashMapWithExpectedSize(1);
        params.put("username",username);
        //默认返回，list，里面包含了字符串的产品id
        NetSchoolResponse response = userCoursesServiceV3.findProducts(RequestUtil.encryptParams(params));
        if(response == null || response.getData() == null){
            return new AsyncResult(Sets.newHashSet());
        }else{
            List<String> data = (List<String>) response.getData();
            Set<Integer> result = data.stream().map(Integer :: parseInt).collect(Collectors.toSet());
            return new AsyncResult(result);
        }
    }


    /**
     * 获取产品数量限额
     * @param courseId
     * @return
     */
    @Async
    public ListenableFuture<Map<Integer,Integer>> getCourseLimit(int courseId){
        Map<String,Object> params = Maps.newHashMapWithExpectedSize(2);
        params.put("rid", courseId);
        NetSchoolResponse response = courseServiceV3.getCourseLimit(params);
        if(response == null || response.getData() == null){
            return new AsyncResult(Maps.<Integer,Integer>newHashMap());
        }else{
            //默认返回，课程id(string)->购买量(string)
            Map<String,String> data = (Map<String, String>) response.getData();
            Map<Integer,Integer> result = data.keySet().stream().collect(Collectors.toMap(Integer::parseInt,(k)-> Integer.parseInt(data.get(k))));
            return new AsyncResult(result);
        }
    }

    /**
     * 老版本拆分课程详情
     * @param courseId
     * @return 会有null情况，需判断
     */
    @Async
    public ListenableFuture<CourseDetailV2DTO> getCourseDetailV2(int courseId){
        String cacheKey = String.format(CourseCKConst.COURSE_DETAIL_V2,courseId);
        CourseDetailV2DTO result = null;
        Object object = valueOperations.get(cacheKey);
        if(object instanceof NullHolder){
            return new AsyncResult<>(null);
        }
        if(object == null){
            Map<String,Object> params = Maps.newHashMap();
            params.put("rid", courseId);
            NetSchoolResponse response = courseServiceV1.courseDetailSp(RequestUtil.encryptParams(params));
            if(response == null){//fallback等
                return new AsyncResult<>(null);
            }
            if(response.getCode() == -3){ //课程不存在
                valueOperations.set(cacheKey,NullHolder.DEFAULT,300, TimeUnit.SECONDS);
                return new AsyncResult<>(null);
            }
            if(response.getData() == null ){//其他未知错误
                return new AsyncResult<>(null);
            }
            try {
                String serialize = JSON.toJSONString(ResponseUtil.build(response,true));
                result = JSON.parseObject(serialize,CourseDetailV2DTO.class);
                valueOperations.set(cacheKey,result,300, TimeUnit.SECONDS);
            } catch (BizException e) {
                log.error("catch BizException,{}", ExceptionUtils.getFullStackTrace(e));
            }
        }else{
            result = (CourseDetailV2DTO) object;
        }
        return new AsyncResult<>(result);
    }


    /**
     * 老版本的异步获取课程列表
     * @param params
     * @return 会有null
     */
    @Async
    public ListenableFuture<CourseListV2DTO> getCourseListV2(Map<String,Object> params){
        params.remove("username");
        String cacheKey = String.format(CourseCKConst.COURSE_LIST_V2, buildMapKey(params));
        CourseListV2DTO result = (CourseListV2DTO) valueOperations.get(cacheKey);
        if(result == null){
            NetSchoolResponse response = courseServiceV1.collectionList(params);
            if(response == null || response.getCode() != NetSchoolConfig.SUCCESS_CODE || response.getData() == null){
                return new AsyncResult<>(null);
            }
            result = JSON.parseObject(JSON.toJSONString(response.getData()),CourseListV2DTO.class);
            valueOperations.set(cacheKey,result,10, TimeUnit.SECONDS);
        }
        return new AsyncResult<>(result);
    }


    /**
     * 异步获取课程列表 v3
     * @param params
     * @return 会有null
     */
    @Async
    public ListenableFuture<CourseListV3DTO> getCourseListV3(Map<String,Object> params){
        params.remove("username");

        String cacheKey = String.format(CourseCKConst.COURSE_LIST_V3, buildMapKey(params));
        CourseListV3DTO result = (CourseListV3DTO) valueOperations.get(cacheKey);
        if(result == null){
            NetSchoolResponse response = courseServiceV3.findLiveList(params);
            if(response == null || response.getCode() != NetSchoolConfig.SUCCESS_CODE || response.getData() == null){
                return new AsyncResult<>(null);
            }
            result = JSON.parseObject(JSON.toJSONString(response.getData()),CourseListV3DTO.class);
            valueOperations.set(cacheKey,result,10, TimeUnit.SECONDS);
        }
        return new AsyncResult<>(result);
    }


    /**
     * 异步获取课程详情 v3
     * @param courseId
     * @return 会有null情况，需判断
     */
    @Async
    public ListenableFuture<CourseDetailV3DTO> getCourseDetailV3(int courseId){
        String cacheKey = String.format(CourseCKConst.COURSE_DETAIL_V3,courseId);
        CourseDetailV3DTO result = null;
        Object object = valueOperations.get(cacheKey);
        if(object instanceof NullHolder){
            return new AsyncResult<>(null);
        }
        if(object == null){
            NetSchoolResponse response = courseServiceV3.getCourseDetail(courseId);
            if(response == null){//fallback等
                return new AsyncResult<>(null);
            }
            if(response.getCode() == -3){ //课程不存在
                valueOperations.set(cacheKey,NullHolder.DEFAULT,300, TimeUnit.SECONDS);
                return new AsyncResult<>(null);
            }
            if(response.getData() == null ){//其他未知错误
                return new AsyncResult<>(null);
            }
            result = ResponseUtil.build(response,CourseDetailV3DTO.class,false);
            valueOperations.set(cacheKey,result,300, TimeUnit.SECONDS);
        }else{
            result = (CourseDetailV3DTO) object;
        }
        return new AsyncResult<>(result);
    }


    /**
     * 参数排序md5后作为redis key
     * @param params
     * @return
     */
    private String buildMapKey(Map<String,Object> params){
        TreeMap treeMap = Maps.newTreeMap();
        treeMap.putAll(params);
        return SignUtil.getPaySign(treeMap,null);
    }


}
