package com.huatu.tiku.course.util;

import com.alibaba.fastjson.JSONObject;
import com.google.common.collect.Maps;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections.MapUtils;

import java.util.Map;

/**
 * @author hanchao
 * @date 2017/8/29 19:10
 */
@Slf4j
public class RequestUtil {

    /**
     * 对参数进行url展开后加密
     * @param params
     * @return
     */
    public static Map<String,Object> encryptParams(Map<String,Object> params){
        Map<String,Object> result = Maps.newHashMapWithExpectedSize(1);
        if (MapUtils.isEmpty(params)) {
            return result;
        }
        StringBuilder stringBuilder = new StringBuilder();
        params.keySet().stream()
                .filter(key->params.get(key) != null)
                .forEach(key -> stringBuilder.append(key + "=" + params.get(key) + "&"));
        //去掉最后的&
        String paramsUrl = stringBuilder.substring(0, stringBuilder.length() - 1);
        //加密
        String encryptparams = Crypt3Des.encryptMode(paramsUrl);
        log.info("params={},encrypt={}", params, encryptparams);
        result.put("p",encryptparams);
        return result;
    }

    /**
     * 对参数进行json序列化后加密
     * @param params
     * @return
     */
    public static Map<String,Object> encryptJsonParams(Map<String, Object> params){
        Map<String,Object> result = Maps.newHashMapWithExpectedSize(1);
        if (MapUtils.isEmpty(params)) {
            return result;
        }
        JSONObject jsonObject = new JSONObject();
        params.keySet().stream()
                .filter(key->params.get(key) != null)
                .forEach(key -> jsonObject.put(key, params.get(key).toString()));
        //去掉最后的&
        String paramsUrl = jsonObject.toString();
        //加密
        String encryptparams = Crypt3Des.encryptMode(paramsUrl);
        log.info("params={},encrypt={}", params, encryptparams);
        result.put("p",encryptparams);
        return result;
    }
}
