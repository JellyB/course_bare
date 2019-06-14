package com.huatu.tiku.course.spring.conf.base;

import com.google.common.collect.Lists;
import com.huatu.common.spring.serializer.StringRedisKeySerializer;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.data.redis.connection.RedisClusterConfiguration;
import org.springframework.data.redis.connection.RedisNode;
import org.springframework.data.redis.connection.jedis.JedisConnectionFactory;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.serializer.GenericJackson2JsonRedisSerializer;
import redis.clients.jedis.JedisPoolConfig;

import java.util.List;


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

    @Bean(name = "SentinelPoolConfig")
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
    @Bean(name = "SentinelConfiguration")
    public RedisClusterConfiguration redisClusterConfiguration() {

        RedisClusterConfiguration redisClusterConfiguration = new RedisClusterConfiguration();
        List<RedisNode> redisNodes = Lists.newArrayList();
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
        redisClusterConfiguration.setMaxRedirects(10);
        return redisClusterConfiguration;
    }

    @Bean(name = "sentinelConnectionFactory")
    public JedisConnectionFactory jedisConnectionFactory(){
        JedisConnectionFactory jedisConnectionFactory = new JedisConnectionFactory(redisClusterConfiguration());
        jedisConnectionFactory.setPoolConfig(jedisPoolConfig());
        jedisConnectionFactory.setPassword("3k80azVAgeza");
        return jedisConnectionFactory;
    }

    @Primary
    @Bean(name = "redisTemplate")
    public StringRedisTemplate redisTemplate(){
        try{
            StringRedisTemplate redisTemplate = new StringRedisTemplate();
            redisTemplate.setConnectionFactory(jedisConnectionFactory());
            //redisTemplate.setKeySerializer(stringRedisKeySerializer);
            //redisTemplate.setDefaultSerializer(genericJackson2JsonRedisSerializer);
            return redisTemplate;
        }catch (Exception e){
            log.error("{}", e);
            return null;
        }
    }
}
