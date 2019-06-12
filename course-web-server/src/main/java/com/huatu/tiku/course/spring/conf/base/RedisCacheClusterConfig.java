package com.huatu.tiku.course.spring.conf.base;

import com.ctrip.framework.apollo.spring.annotation.EnableApolloConfig;
import com.huatu.common.spring.serializer.StringRedisKeySerializer;
import org.bouncycastle.asn1.x509.V2AttributeCertificateInfoGenerator;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.jedis.JedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.serializer.GenericJackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.StringRedisSerializer;

import static com.huatu.common.consts.ApolloConfigConsts.NAMESPACE_TIKU_CACHE_REDIS;
import static com.huatu.common.consts.ApolloConfigConsts.NAMESPACE_TIKU_REDIS;

/**
 * @author hanchao
 * @date 2017/9/4 14:42
 */
@EnableApolloConfig(NAMESPACE_TIKU_CACHE_REDIS)
@Configuration
public class RedisCacheClusterConfig {
    @Value("${spring.application.name:unknown}")
    private String applicationName;

    @Bean
    public StringRedisKeySerializer stringRedisKeySerializer(){
        return new StringRedisKeySerializer(applicationName);
    }




    /**
     * 使用官方的，防止踩坑
     * @return
     */
    @Bean
    public GenericJackson2JsonRedisSerializer genericJackson2JsonRedisSerializer(){
        return new GenericJackson2JsonRedisSerializer();
    }

    /**
     * 仅做缓存的 redis template
     * @param stringRedisKeySerializer
     * @param genericJackson2JsonRedisSerializer
     * @param jedisConnectionFactory
     * @return
     */
    @Bean(value = "redisCacheTemplate")
    public RedisTemplate redisTemplate(StringRedisKeySerializer stringRedisKeySerializer,GenericJackson2JsonRedisSerializer genericJackson2JsonRedisSerializer){
        RedisTemplate redisCacheTemplate = new RedisTemplate();
        //redisCacheTemplate.setConnectionFactory(jedisConnectionFactory);
        redisCacheTemplate.setKeySerializer(stringRedisKeySerializer);
        redisCacheTemplate.setHashKeySerializer(new StringRedisSerializer());
        redisCacheTemplate.setDefaultSerializer(genericJackson2JsonRedisSerializer);
        return redisCacheTemplate;
    }

}
