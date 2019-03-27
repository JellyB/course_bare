package com.huatu.tiku.course.netschool.api.v5;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.huatu.common.Result;
import com.huatu.springboot.degrade.core.Degrade;
import com.huatu.tiku.course.bean.NetSchoolResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

/**
 * 描述：订单服务接口降级处理
 *
 * @author biguodong
 * Create time 2019-03-18 2:35 PM
 **/

@Service
@Slf4j
public class OrderDegradeService {

    @Autowired
    private OrderServiceV5 orderService;


    /**
     * 查询我的订单列表 - ZTK
     * @param params
     * @return
     */
    @Degrade(key="userOrderListZTKV5", name = "我的")
    public NetSchoolResponse userOrderListZTK(HashMap<String, Object> params){
        return orderService.userOrderListZTK(params);
    }

    /**
     * 查询我的订单列表 - ZTK，降级
     * @param params
     * @return
     */
    public NetSchoolResponse userOrderListZTKDegrade(HashMap<String, Object> params){
        Map<String,Object> result = Maps.newHashMap();
        result.put("current_page", 1);
        result.put("data", Lists.newArrayList());
        result.put("from", 1);
        result.put("last_page", 1);
        result.put("next_page_url", null);
        result.put("path", "http://testapi.huatu.com/lumenapi/v4/common/order/list");
        result.put("per_page", 10);
        result.put("prev_page_url", null);
        result.put("to", 4);
        result.put("total", 4);
        log.info("userOrderListZTKDegrade 降价处理:{}", params);
        return NetSchoolResponse.builder().code(Result.SUCCESS_CODE).msg("").data(result).build();
    }
}
