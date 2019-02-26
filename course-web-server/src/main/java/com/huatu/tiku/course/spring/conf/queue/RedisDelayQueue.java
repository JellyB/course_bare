package com.huatu.tiku.course.spring.conf.queue;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import org.springframework.data.redis.core.HashOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ZSetOperations;

import java.io.IOException;
import java.util.Set;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.LockSupport;
import java.util.concurrent.locks.ReentrantLock;

/**
 * 描述：基于redis 实现的 delay queue
 *
 * @author biguodong
 * Create time 2019-02-26 下午2:46
 **/

public class RedisDelayQueue implements DelayQueue{

    private transient final ReentrantLock lock = new ReentrantLock();

    private final Condition available = lock.newCondition();

    private RedisTemplate redisTemplate;

    /**
     * 最大超时时间不能超过 10 天
     */
    private long MAX_TIMEOUT = TimeUnit.DAYS.toSeconds(10);

    private ObjectMapper objectMapper;

    private int unAckTime;

    private String messageStoreKey;

    private String realQueueName;

    private String queueName;

    private DelayQueueProcessListener delayQueueProcessListener;

    private AtomicBoolean nextAvailable = new AtomicBoolean(false);

    public RedisDelayQueue(String queueName, RedisTemplate redisTemplate, int unAckTime,
                           ObjectMapper objectMapper,
                           DelayQueueProcessListener delayQueueProcessListener) {
        this.objectMapper = objectMapper;
        this.queueName = queueName;
        this.messageStoreKey =  queueName + MESSAGE;
        this.unAckTime = unAckTime;
        this.redisTemplate = redisTemplate;
        realQueueName = queueName + QUEUE;
        this.delayQueueProcessListener = delayQueueProcessListener;
    }

    @Override
    public boolean push(Message message) {
        if (message.getTimeout() > MAX_TIMEOUT) {
            throw new RuntimeException("Maximum delay time should not be exceed 10 days");
        }
        try {
            String json = objectMapper.writeValueAsString(message);
            HashOperations hashOperations = redisTemplate.opsForHash();
            ZSetOperations zSetOperations = redisTemplate.opsForZSet();
            hashOperations.put(messageStoreKey, message.getId(), json);
            double priority = message.getPriority() / 100;
            double score = Long.valueOf(System.currentTimeMillis() + message.getTimeout()).doubleValue() + priority;
            zSetOperations.add(realQueueName, message.getId(), score);
            delayQueueProcessListener.pushCallback(message);
            nextAvailable.set(true);
            return true;
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }
        return false;

    }

    @Override
    public void listen() {
        while (true) {
            String id = peekId();
            if (id == null) {
                continue;
            }
            HashOperations<String, String, String> hashOperations = redisTemplate.opsForHash();
            String json = hashOperations.get(messageStoreKey, id);
            try {
                Message message = objectMapper.readValue(json, Message.class);
                if (message == null) {
                    continue;
                }
                long delay = message.getCreateTime() + message.getTimeout() - System.currentTimeMillis();
                System.out.println(delay);
                if (delay <= 0) {
                    delayQueueProcessListener.peekCallback(message);
                } else {
                    LockSupport.parkNanos(this, TimeUnit.NANOSECONDS.convert(delay, TimeUnit.MILLISECONDS));
                    delayQueueProcessListener.peekCallback(message);
                }
            } catch (IOException e) {
                e.printStackTrace();
            }

        }
    }

    @Override
    public boolean ack(String messageId) {
        String unAckQueueName = getUnAckQueueName(queueName);
        ZSetOperations zSetOperations = redisTemplate.opsForZSet();
        HashOperations hashOperations = redisTemplate.opsForHash();
        zSetOperations.remove(unAckQueueName, messageId);
        Long removed = zSetOperations.remove(realQueueName, messageId);
        Long msgRemoved = hashOperations.delete(messageStoreKey, messageId);
        if (removed > 0 && msgRemoved > 0) {
            return true;
        }
        return false;

    }

    @Override
    public boolean setUnAckTimeout(String messageId, long timeout) {
        double unAckScore = Long.valueOf(System.currentTimeMillis() + timeout).doubleValue();
        String unAckQueueName = getUnAckQueueName(queueName);

        ZSetOperations zSetOperations = redisTemplate.opsForZSet();
        Double score = zSetOperations.score(unAckQueueName, messageId);
        if (score != null) {

            zSetOperations.add(unAckQueueName, messageId, unAckScore);
            return true;
        }
        return false;

    }

    /*@Override
    public boolean setTimeout(String messageId, long timeout) {
        try {
            HashOperations<String, String, String> hashOperations = redisTemplate.opsForHash();
            String json = hashOperations.get(messageStoreKey, messageId);
            //String json = jedisCluster.hget(messageStoreKey, messageId);
            if (json == null) {
                return false;
            }
            Message message = om.readValue(json, Message.class);
            message.setTimeout(timeout);
            ZSetOperations zSetOperations = redisTemplate.opsForZSet();
            Double score = zSetOperations.score(realQueueName, messageId);
            //Double score = jedisCluster.zscore(realQueueName, messageId);
            if (score != null) {
                double priorityd = message.getPriority() / 100;
                double newScore = Long.valueOf(System.currentTimeMillis() + timeout).doubleValue() + priorityd;
                ZAddParams params = ZAddParams.zAddParams().xx();
                long added = jedisCluster.zadd(realQueueName, newScore, messageId, params);
                if (added == 1) {
                    json = om.writeValueAsString(message);
                    jedisCluster.hset(messageStoreKey, message.getId(), json);
                    return true;
                }
                return false;
            }
            return false;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }*/

    @Override
    public Message get(String messageId) {
        HashOperations<String, String, String> hashOperations = redisTemplate.opsForHash();
        String json = hashOperations.get(messageStoreKey, messageId);
        if (json == null) {
            return null;
        }
        Message msg;
        try {
            msg = objectMapper.readValue(json, Message.class);
            return msg;
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }

    }

    @Override
    public long size() {
        ZSetOperations zSetOperations = redisTemplate.opsForZSet();
        return zSetOperations.zCard(realQueueName);
    }

    @Override
    public void clear() {
        String unAckShard = getUnAckQueueName(queueName);
        redisTemplate.delete(realQueueName);
        redisTemplate.delete(unAckShard);
        redisTemplate.delete(messageStoreKey);

    }

    private String peekId() {
        try {
            if (nextAvailable.get()) {
                lock.lockInterruptibly();
                double max = Long.valueOf(System.currentTimeMillis() + MAX_TIMEOUT).doubleValue();
                ZSetOperations zSetOperations = redisTemplate.opsForZSet();
                Set<String> scanned = zSetOperations.rangeByScore(realQueueName, 0, max, 0 ,1);
                if (scanned.size() > 0) {
                    String messageId = scanned.toArray()[0].toString();
                    zSetOperations.remove(realQueueName, messageId);
                    setUnAckTimeout(messageId, unAckTime);
                    if (size() == 0) {
                        nextAvailable.set(false);
                    }
                    available.signal();
                    lock.unlock();
                    return messageId;
                }
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
            available.signal();
            lock.unlock();
        }
        return null;
    }

    /*public void processUnacks() {
        long queueDepth = size();
        int batchSize = 1_000;
        String unackQueueName = getUnackQueueName(queueName);
        double now = Long.valueOf(System.currentTimeMillis()).doubleValue();
        Set<Tuple> unacks = jedisCluster.zrangeByScoreWithScores(unackQueueName, 0, now, 0, batchSize);
        for (Tuple unack : unacks) {
            double score = unack.getScore();
            String member = unack.getElement();
            String payload = jedisCluster.hget(messageStoreKey, member);
            if (payload == null) {
                jedisCluster.zrem(unackQueueName, member);
                continue;
            }
            jedisCluster.zadd(realQueueName, score, member);
            jedisCluster.zrem(unackQueueName, member);
        }
    }*/

    private String getUnAckQueueName(String queueName) {
        return queueName + UNACK;
    }

    @Override
    public String getName() {
        return this.realQueueName;
    }

    @Override
    public int getUnAckTime() {
        return this.unAckTime;
    }
}
