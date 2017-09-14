package com.huatu.tiku.course.util;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.TypeReference;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Lists;
import com.huatu.common.ErrorResult;
import com.huatu.common.SuccessMessage;
import com.huatu.common.exception.BizException;
import com.huatu.tiku.course.common.NetSchoolConfig;
import com.huatu.tiku.course.netschool.bean.NetSchoolResponse;
import org.apache.commons.lang3.StringUtils;

import java.util.Map;

/**
 * transform
 * @author hanchao
 * @date 2017/8/29 12:28
 */
public class ResponseUtil {

    public static Map<String,Object> DEFAULT_RESPONSE = ImmutableMap.<String,Object>builder().put("result", Lists.newArrayList())
            .put("next",0).build();

    public static ErrorResult ERROR_NULL_RESPONSE = ErrorResult.create(0,"数据为空");


    public static Object build(NetSchoolResponse response) throws BizException {
        return build(response,false);
    }

    /**
     * 查看日志不存在非json的响应，碰到再进行调整
     * @param netSchoolResponse
     * @return
     * @throws BizException
     */
    public static Object build (NetSchoolResponse netSchoolResponse,boolean needDecrypt) throws BizException {
        Object data = null;
        if (netSchoolResponse.getCode() != NetSchoolConfig.SUCCESS_CODE) {
            final ErrorResult errorResult = ErrorResult.create(netSchoolResponse.getCode(), netSchoolResponse.getMsg());
            if (netSchoolResponse.getData() != null) {
                errorResult.setData(netSchoolResponse.getData());
            }
            throw new BizException(errorResult);
        } else {
            //只取data部分
            data = netSchoolResponse.getData();

            //code为1，但是没有data或者data为空字符串
            if (data == null || (data != null && StringUtils.isBlank(data.toString()))) {
                return SuccessMessage.create(netSchoolResponse.getMsg());
            }

            //需要解密的
            if (needDecrypt) {
                String json = Crypt3Des.decryptMode(String.valueOf(data));
                data = JSON.parseObject(json,Map.class);
            }
        }
        return data;
    }

    /**
     * 需要解密到对应class的，一般都是需要重新组装的（并行请求）
     * @param netSchoolResponse
     * @param clazz
     * @param needDecrypt
     * @param <T>
     * @return
     */
    public static <T> T build(NetSchoolResponse netSchoolResponse,Class<T> clazz,boolean needDecrypt){
        if(netSchoolResponse == null || netSchoolResponse.getCode()!= NetSchoolConfig.SUCCESS_CODE || netSchoolResponse.getData() == null){
            return null;
        }
        Object data = netSchoolResponse.getData();
        String serialize = "";
        if(needDecrypt){
            serialize = Crypt3Des.decryptMode(String.valueOf(data));
        }else{
            serialize = JSON.toJSONString(data);
        }
        return JSON.parseObject(serialize,clazz);
    }

    /**
     * 需要解密到对应class的，一般都是需要重新组装的（并行请求）
     * 泛型
     * @param netSchoolResponse
     * @param type
     * @param needDecrypt
     * @param <T>
     * @return
     */
    public static <T> T build(NetSchoolResponse netSchoolResponse, TypeReference<T> type, boolean needDecrypt){
        if(netSchoolResponse == null || netSchoolResponse.getCode()!= NetSchoolConfig.SUCCESS_CODE || netSchoolResponse.getData() == null){
            return null;
        }
        Object data = netSchoolResponse.getData();
        if(needDecrypt){
            return JSON.parseObject(Crypt3Des.decryptMode(String.valueOf(data)),type);
        }else{
            return JSON.parseObject(JSON.toJSONString(data),type);
        }
    }
}
