package com.huatu.tiku.course.util;

import com.alibaba.fastjson.JSON;
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
}
