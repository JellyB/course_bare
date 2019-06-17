package com.huatu.tiku.course.spring.conf.base;

import java.util.HashSet;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.data.redis.connection.RedisSentinelConfiguration;
import org.springframework.data.redis.connection.jedis.JedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.GenericJackson2JsonRedisSerializer;

import com.huatu.common.spring.serializer.StringRedisKeySerializer;

import lombok.extern.slf4j.Slf4j;
import redis.clients.jedis.JedisPoolConfig;


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

   
    
    @Bean(value = "sentinelPool")
    public JedisPoolConfig jedisPoolConfig() {
        log.info("JedisSentinelPool  config initialize start ...");
        
        JedisPoolConfig config = new JedisPoolConfig();
		config.setMaxTotal(Integer.valueOf(1000));
		config.setMaxIdle(Integer.valueOf(20));
		// 表示当borrow(引入)一个jedis实例时，最大的等待时间，如果超过等待时间，则直接抛出JedisConnectionException；
		config.setMinEvictableIdleTimeMillis(Integer.valueOf(-1));
		config.setTestOnBorrow(Boolean.valueOf(true));
        log.info("sentinel pool config initialize end ...");
        return config;
    }
    


    /**
     * sentinelConfiguration
     *
     * @return
     */
    @Bean(value = "sentinelConfiguration")
    public RedisSentinelConfiguration sentinelConfiguration() {
		String master = "resque";
		Set<String> sentinels = new HashSet<String>();
		sentinels.add("192.168.100.21:26479");
		sentinels.add("192.168.100.21:26489");
		RedisSentinelConfiguration sentinelConfiguration = new RedisSentinelConfiguration(master,sentinels);
        return sentinelConfiguration;
    }
    
    @Bean(value = "sentinelJedisConnectionFactory")
    public JedisConnectionFactory jedisConnectionFactory(JedisPoolConfig jedisPoolConfig, RedisSentinelConfiguration sentinelConfig) {
        JedisConnectionFactory jedisConnectionFactory = new JedisConnectionFactory(sentinelConfig, jedisPoolConfig);
        return jedisConnectionFactory;
    }
   

    @Primary
    @Bean(value = "redisTemplate")
    public RedisTemplate sentinelRedisTemplate(){
        RedisTemplate sentinelRedisTemplate = new RedisTemplate();
        sentinelRedisTemplate.setConnectionFactory(jedisConnectionFactory(jedisPoolConfig(),sentinelConfiguration()));
        sentinelRedisTemplate.setKeySerializer(stringRedisKeySerializer);
        sentinelRedisTemplate.setDefaultSerializer(genericJackson2JsonRedisSerializer);
        return sentinelRedisTemplate;
    }
}
