package com.huatu.tiku.course.spring.conf.base;

import com.ctrip.framework.apollo.spring.annotation.EnableApolloConfig;
import com.huatu.common.spring.serializer.KryoRedisSerializer;
import com.huatu.common.spring.serializer.StringRedisKeySerializer;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.jedis.JedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.StringRedisTemplate;

/**
 * @author hanchao
 * @date 2017/9/4 14:42
 */
@EnableApolloConfig("tiku.redis-cluster")
@Configuration
public class RedisClusterConfig {
    @Value("${spring.application.name:unknown}")
    private String applicationName;

    @Bean
    public StringRedisKeySerializer stringRedisKeySerializer(){
        return new StringRedisKeySerializer(applicationName);
    }

    @Bean
    public StringRedisTemplate stringRedisTemplate(JedisConnectionFactory jedisConnectionFactory){
        return new StringRedisTemplate(jedisConnectionFactory);
    }

    @Bean
    public KryoRedisSerializer kryoRedisSerializer(){
        return new KryoRedisSerializer();
    }

    @Bean
    public RedisTemplate redisTemplate(StringRedisKeySerializer stringRedisKeySerializer,KryoRedisSerializer kryoSerializer,JedisConnectionFactory jedisConnectionFactory){
        RedisTemplate redisTemplate = new RedisTemplate();
        redisTemplate.setConnectionFactory(jedisConnectionFactory);
        redisTemplate.setKeySerializer(stringRedisKeySerializer);
        redisTemplate.setHashKeySerializer(stringRedisKeySerializer);
        redisTemplate.setDefaultSerializer(kryoSerializer);
        return redisTemplate;
    }
}
