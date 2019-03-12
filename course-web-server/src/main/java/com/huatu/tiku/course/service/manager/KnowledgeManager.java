package com.huatu.tiku.course.service.manager;

import com.google.common.collect.Lists;
import com.huatu.common.exception.BizException;
import com.huatu.tiku.course.dao.manual.KnowledgeMapper;
import com.huatu.tiku.entity.knowledge.Knowledge;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import tk.mybatis.mapper.entity.Example;
import tk.mybatis.mapper.weekend.WeekendSqls;

import java.util.List;

/**
 * 描述：
 *
 * @author biguodong
 * Create time 2019-03-12 8:54 PM
 **/

@Slf4j
@Component
public class KnowledgeManager {


    @Autowired
    private KnowledgeMapper knowledgeMapper;

    /**
     * 根据多个知识点id获取知识点信息
     *
     * @param list
     * @return
     * @throws BizException
     */
    public List<Knowledge> findBatch(List<Long> list) throws BizException {
        if (CollectionUtils.isEmpty(list)) {
            return Lists.newArrayList();
        }
        WeekendSqls<Knowledge> weekendSqls = WeekendSqls.custom();
        weekendSqls.andIn(Knowledge::getId, list);
        Example example = Example.builder(Knowledge.class).where(weekendSqls).build();
        List<Knowledge> result = knowledgeMapper.selectByExample(example);
        return result;
    }
}
