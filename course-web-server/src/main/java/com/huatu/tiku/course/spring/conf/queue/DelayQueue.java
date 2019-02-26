package com.huatu.tiku.course.spring.conf.queue;

/**
 * 描述：
 *
 * @author biguodong
 * Create time 2019-02-26 下午2:40
 **/
public interface DelayQueue {

    String MESSAGE = "$message";
    String QUEUE = "$queue";
    String UNACK = "$unAck";
    String getName();

    int getUnAckTime();

    boolean ack(String messageId);

    boolean setUnAckTimeout(String messagId, long timeout);

    //boolean setTimeout(String messageId, long timeout);

    Message get(String messageId);

    long size();

    void clear();

    boolean push(Message message);

    void listen();


}
