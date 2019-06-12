package com.huatu.tiku.course.spring.conf.base;

import com.huatu.common.spring.serializer.StringRedisKeySerializer;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisClusterConfiguration;
import org.springframework.data.redis.connection.RedisNode;
import org.springframework.data.redis.connection.jedis.JedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.GenericJackson2JsonRedisSerializer;
import redis.clients.jedis.JedisPoolConfig;

import java.util.HashSet;
import java.util.Set;


/**
 * @author hanchao
 * @date 2017/9/4 14:42
 */

@Configuration
@Slf4j
@EnableConfigurationProperties(SentinelSentinelProperties.class)
public class RedisSentinelConfig {
    @Value("${spring.application.name:unknown}")
    private String applicationName;


    @Autowired
    private SentinelSentinelProperties sentinelSentinelProperties;


    @Autowired
    private GenericJackson2JsonRedisSerializer genericJackson2JsonRedisSerializer;

    @Autowired
    private StringRedisKeySerializer stringRedisKeySerializer;

    @Bean(value = "sentinelPoolConfig")
    public JedisPoolConfig jedisPoolConfig() {
        log.info("sentinel pool config initialize start ...");
        JedisPoolConfig config = new JedisPoolConfig();


        //最大总量
        config.setMaxTotal(sentinelSentinelProperties.getPoolMaxTotal());
        //设置最大空闲数量
        config.setMaxIdle(sentinelSentinelProperties.getPoolMaxIdle());
        //设置最长等待时间
        config.setMaxWaitMillis(sentinelSentinelProperties.getMaxWaitMillis());
        //常规配置
        config.setTestOnBorrow(true);
        config.setTestOnReturn(true);
        log.info("sentinel pool config initialize end ...");
        return config;
    }


    /**
     * sentinelConfiguration
     *
     * @return
     */
    @Bean(value = "sentinelConfiguration")
    public RedisClusterConfiguration redisClusterConfiguration() {

        RedisClusterConfiguration redisClusterConfiguration = new RedisClusterConfiguration();
        Set<RedisNode> redisNodes = new HashSet<>();
        //获取到节点信息
        String nodeString = sentinelSentinelProperties.getNodes();
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
            String host = node.split(":")[0];
            int port = Integer.valueOf(node.split(":")[1]);
            RedisNode redisNode = new RedisNode(host, port);
            log.info("Read node : {}。", node);
            redisNodes.add(redisNode);
        }
        redisClusterConfiguration.setClusterNodes(redisNodes);
        return redisClusterConfiguration;
    }

    @Bean(value = "sentinelConnectionFactory")
    public JedisConnectionFactory jedisConnectionFactory(){
        JedisConnectionFactory jedisConnectionFactory = new JedisConnectionFactory(redisClusterConfiguration());
        jedisConnectionFactory.setPoolConfig(jedisPoolConfig());
        return jedisConnectionFactory;
    }

    @Bean(value = "sentinelRedisTemplate")
    public RedisTemplate redisTemplate(){
        RedisTemplate redisTemplate = new RedisTemplate();
        redisTemplate.setConnectionFactory(jedisConnectionFactory());
        redisTemplate.setKeySerializer(stringRedisKeySerializer);
        redisTemplate.setDefaultSerializer(genericJackson2JsonRedisSerializer);
        return redisTemplate;
    }
}
