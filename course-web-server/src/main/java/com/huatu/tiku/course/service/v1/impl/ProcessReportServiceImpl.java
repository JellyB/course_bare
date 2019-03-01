package com.huatu.tiku.course.service.v1.impl;

import com.huatu.common.ErrorResult;
import com.huatu.common.Result;
import com.huatu.common.SuccessMessage;
import com.huatu.common.exception.BizException;
import com.huatu.tiku.course.service.v1.ProcessReportService;
import com.huatu.tiku.course.spring.conf.queue.Message;
import com.huatu.tiku.course.spring.conf.queue.Payload;
import com.huatu.tiku.course.spring.conf.queue.RedisDelayQueue;
import com.huatu.tiku.course.util.ResponseUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;


import java.util.Map;
import java.util.Random;
import java.util.UUID;

/**
 * 描述：
 *
 * TODO 1. 直播数据上报延时时间处理
 * 2. 处理后插叙是否配置有课后作业
 * 3. 如果有配置课后作业，查询答题卡状态；
 * 4. 如果做完或者没有做，处理数据
 * @author biguodong
 * Create time 2019-02-22 上午11:17
 **/

@Service
@Slf4j
public class ProcessReportServiceImpl<T> implements ProcessReportService<T> {


    private static final String SYLLABUS_ID = "syllabusId";
    private static final String END_TIME = "endTime";
    private static final String USER_NAME = "userName";

    @Autowired
    private RedisDelayQueue redisDelayQueue;

    /**
     * 数据上报接口
     *
     * @param params
     * @return
     */
    @Override
    public Object liveReport(Map<String, Object> params) {
        if(!params.containsKey(SYLLABUS_ID) || !params.containsKey(END_TIME) || !params.containsKey(USER_NAME)){
            ErrorResult errorResult = ErrorResult.create(Result.SUCCESS_CODE, "数据为空", ResponseUtil.MOCK_PAGE_RESPONSE);
            throw new BizException(errorResult);
        }
        int syllabusId = Integer.valueOf(String.valueOf(params.get(SYLLABUS_ID)));
        long endTime = Long.valueOf(String.valueOf(params.get(END_TIME)));
        String userName = String.valueOf(params.get(USER_NAME));

        Random random = new Random();
        int id = random.nextInt(100);

        Payload payload = Payload.builder().syllabusId(id).userName( "test" + id).build();

        /*Payload payload = Payload.builder()
                .syllabusId(syllabusId)
                .userName(userName)
                .build();*/

        Message message = Message
                .builder()
                .id(UUID.randomUUID().toString())
                .timeout(id * 1000)
                .payload(payload)
                .createTime(System.currentTimeMillis())
                .build();

        redisDelayQueue.push(message);
        return SuccessMessage.create();
    }


    @Override
    public Object playBackReport(Map<String, Object> params) {
        return SuccessMessage.create();
    }


    private void dealPayload(Payload payload)throws BizException{
        log.info(">>>>>>>>>>>>>>>>>>>>>>>>:{}", payload.getSyllabusId());
    }
    /**
     * 确认并删除消息
     *
     * @param t
     * @return
     */
    @Override
    public void ack(T t) {
        if(t instanceof Payload){
            Payload payload = (Payload) t;
            dealPayload(payload);
        }
    }

}
