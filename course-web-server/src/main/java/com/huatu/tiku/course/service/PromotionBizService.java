package com.huatu.tiku.course.service;

import com.ctrip.framework.apollo.model.ConfigChange;
import com.ctrip.framework.apollo.model.ConfigChangeEvent;
import com.ctrip.framework.apollo.spring.annotation.ApolloConfigChangeListener;
import com.huatu.common.consts.ApolloConfigConsts;
import com.huatu.tiku.course.netschool.api.v3.OrderServiceV3;
import com.huatu.tiku.springboot.basic.support.ConfigSubscriber;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Set;

/**
 * @author hanchao
 * @date 2017/10/17 17:44
 */
@Service
@Slf4j
public class PromotionBizService implements ConfigSubscriber{
    private static volatile String configSign = "";
    //促销的产品id
    private Set<Integer> promoteCourseIds;

    @Autowired
    private OrderServiceV3 orderServiceV3;

    //监听配置变化的问题
    @ApolloConfigChangeListener
    private void listen(ConfigChangeEvent changeEvent){

    }

    @Override
    public void update(ConfigChange configChange) {

    }

    @Override
    public String key() {
        return "promotion";
    }

    @Override
    public String namespace() {
        return ApolloConfigConsts.NAMESPACE_DEFAULT;
    }
}
