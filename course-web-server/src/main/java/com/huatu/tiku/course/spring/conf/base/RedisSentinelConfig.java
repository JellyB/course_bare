package com.huatu.tiku.course.spring.conf.base;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import redis.clients.jedis.JedisPoolConfig;
import redis.clients.jedis.JedisSentinelPool;

import java.util.HashSet;
import java.util.Set;


/**
 * @author hanchao
 * @date 2017/9/4 14:42
 */

@Configuration
@Slf4j
public class RedisSentinelConfig {
    @Value("${spring.application.name:unknown}")
    private String applicationName;


    @Autowired
    private SentinelProperties sentinelProperties;


    @Bean(value = "jedisPoolConfig")
    public JedisPoolConfig jedisPoolConfig() {
        log.info("JedisPool initialize start ...");
        JedisPoolConfig config = new JedisPoolConfig();


        //最大总量
        config.setMaxTotal(sentinelProperties.getPoolMaxTotal());
        //设置最大空闲数量
        config.setMaxIdle(sentinelProperties.getPoolMaxIdle());
        //设置最小空闲数量
        config.setMinIdle(sentinelProperties.getPoolMinIdle());
        //常规配置
        config.setTestOnBorrow(true);
        config.setTestOnReturn(true);
        log.info("JedisPool initialize end ...");
        return config;


    }


    /**
     * jedis sentinel pool
     *
     * @param jedisPoolConfig
     * @return
     */
    @Bean(value = "sentinelPool")
    public JedisSentinelPool jedisSentinelPool(@Qualifier(value = "jedisPoolConfig") JedisPoolConfig jedisPoolConfig) {

        Set<String> nodeSet = new HashSet<>();
        //获取到节点信息
        String nodeString = sentinelProperties.getNodes();
        //判断字符串是否为空
        if (nodeString == null || "".equals(nodeString)) {
            log.error("RedisSentinelConfiguration initialize error nodeString is null");
            throw new RuntimeException("RedisSentinelConfiguration initialize error nodeString is null");
        }
        String[] nodeArray = nodeString.split(",");
        //判断是否为空
        if (nodeArray == null || nodeArray.length == 0) {
            log.error("RedisSentinelConfiguration initialize error nodeArray is null");
            throw new RuntimeException("RedisSentinelConfiguration initialize error nodeArray is null");
        }
        //循环注入至Set中
        for (String node : nodeArray) {
            log.info("Read node : {}。", node);
            nodeSet.add(node);
        }
        //创建连接池对象
        JedisSentinelPool sentinelPool = new JedisSentinelPool(sentinelProperties.getMasterName(), nodeSet, jedisPoolConfig, sentinelProperties.getTimeout());
        return sentinelPool;
    }
}
