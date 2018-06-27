/*
 * Copyright (c) 2018. Shanghai Zhenhui Information Technology Co,. ltd.
 * All rights are reserved.
 */
package com.helioscloud.atlantis.config;

import com.alibaba.fastjson.support.spring.FastJsonRedisSerializer;
import com.helioscloud.atlantis.dto.AuthenticationCode;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.data.redis.RedisProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.CachingConfigurerSupport;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.cache.interceptor.KeyGenerator;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.cache.RedisCacheManager;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.connection.RedisNode;
import org.springframework.data.redis.connection.RedisSentinelConfiguration;
import org.springframework.data.redis.connection.jedis.JedisConnectionFactory;
import org.springframework.data.redis.core.RedisOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.JdkSerializationRedisSerializer;
import org.springframework.data.redis.serializer.StringRedisSerializer;
import org.springframework.util.Assert;
import org.springframework.util.StringUtils;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

@Configuration
@EnableCaching
@ConditionalOnClass(RedisOperations.class)
@EnableConfigurationProperties(RedisProperties.class)
public class RedisConfiguration extends CachingConfigurerSupport {

    private final RedisProperties properties;
    private final RedisSentinelConfiguration sentinelConfiguration;

    public RedisConfiguration(RedisProperties properties, ObjectProvider<RedisSentinelConfiguration> sentinelConfigurationProvider) {
        this.properties = properties;
        this.sentinelConfiguration =
            sentinelConfigurationProvider.getIfAvailable();
    }

    /*private JedisPoolConfig jedisPoolConfig() {
        JedisPoolConfig config = new JedisPoolConfig();
        RedisProperties.Pool props = this.properties.getPool();
        config.setMaxTotal(props.getMaxActive());
        config.setMaxIdle(props.getMaxIdle());
        config.setMinIdle(props.getMinIdle());
        config.setMaxWaitMillis((long) props.getMaxWait());
        return config;
    }*/

    protected final RedisSentinelConfiguration getSentinelConfig() {
        if (this.sentinelConfiguration != null) {
            return this.sentinelConfiguration;
        } else {
            RedisProperties.Sentinel sentinelProperties = this.properties.getSentinel();
            if (sentinelProperties != null) {
                RedisSentinelConfiguration config = new RedisSentinelConfiguration();
                config.master(sentinelProperties.getMaster());
                config.setSentinels(this.createSentinels(sentinelProperties));
                return config;
            } else {
                return null;
            }
        }
    }

    private List<RedisNode> createSentinels(RedisProperties.Sentinel sentinel) {
        ArrayList nodes = new ArrayList();
        String[] var3 = (String[]) sentinel.getNodes().toArray();
        int var4 = var3.length;

        for (int var5 = 0; var5 < var4; ++var5) {
            String node = var3[var5];

            try {
                String[] ex = StringUtils.split(node, ":");
                Assert.state(ex.length == 2, "Must be defined as \'host:port\'");
                nodes.add(new RedisNode(ex[0], Integer.parseInt(ex[1])));
            } catch (RuntimeException var8) {
                throw new IllegalStateException("Invalid redis sentinel property \'" + node + "\'", var8);
            }
        }

        return nodes;
    }

   /* @Bean
    public JedisConnectionFactory jedisConnectionFactory() {
        JedisPoolConfig poolConfig = this.properties.getPool() != null ? this.jedisPoolConfig() : new JedisPoolConfig();
        JedisConnectionFactory jedisConnectionFactory = this.getSentinelConfig() != null
            ? new JedisConnectionFactory(this.getSentinelConfig(), poolConfig)
            : new JedisConnectionFactory(poolConfig);
        jedisConnectionFactory.setHostName(properties.getHost());
        jedisConnectionFactory.setPort(properties.getPort());
        if (this.properties.getPassword() != null) {
            jedisConnectionFactory.setPassword(this.properties.getPassword());
        }
        jedisConnectionFactory.setDatabase(this.properties.getDatabase());
        if (this.properties.getTimeout() > 0) {
            jedisConnectionFactory.setTimeout(this.properties.getTimeout());
        }
        return jedisConnectionFactory;
    }
*/
    /*@Bean
    public RedisTemplate redisTemplate() {
        RedisTemplate redisTemplate = new RedisTemplate();
        redisTemplate.setConnectionFactory(this.jedisConnectionFactory());
        redisTemplate.setKeySerializer(new StringRedisSerializer());
        redisTemplate.setValueSerializer(new JdkSerializationRedisSerializer());
        redisTemplate.setHashValueSerializer(new JdkSerializationRedisSerializer());
        redisTemplate.setHashKeySerializer(new StringRedisSerializer());
        return redisTemplate;
    }*/
   @Bean(name = "redisTemplate")
   @SuppressWarnings("unchecked")
   @ConditionalOnMissingBean(name = "redisTemplate")
   public RedisTemplate<Object, Object> redisTemplate(RedisConnectionFactory redisConnectionFactory) {
       RedisTemplate<Object, Object> template = new RedisTemplate<>();

       //使用fastjson序列化
       FastJsonRedisSerializer fastJsonRedisSerializer = new FastJsonRedisSerializer(Object.class);
       // value值的序列化采用fastJsonRedisSerializer
       template.setValueSerializer(fastJsonRedisSerializer);
       template.setHashValueSerializer(fastJsonRedisSerializer);
       // key的序列化采用StringRedisSerializer
       template.setKeySerializer(new StringRedisSerializer());
       template.setHashKeySerializer(new StringRedisSerializer());

       template.setConnectionFactory(redisConnectionFactory);
       return template;
   }


    @Bean
    public RedisTemplate<String, AuthenticationCode> authenticationServiceredisTemplate(RedisConnectionFactory redisConnectionFactory) {
        RedisTemplate<String, AuthenticationCode> redisTemplate =new  RedisTemplate<String, AuthenticationCode>();
     redisTemplate.setConnectionFactory(redisConnectionFactory);
        redisTemplate.setKeySerializer(new StringRedisSerializer());
        redisTemplate.setValueSerializer(new JdkSerializationRedisSerializer());
        redisTemplate.setHashValueSerializer(new JdkSerializationRedisSerializer());
        redisTemplate.setHashKeySerializer(new StringRedisSerializer());
        return redisTemplate;
    }

    @Bean
    public CacheManager cacheManager(RedisConnectionFactory redisConnectionFactory) {
        RedisCacheManager cacheManager = RedisCacheManager
                .RedisCacheManagerBuilder
                .fromConnectionFactory(redisConnectionFactory).build() ;
        /*RedisCacheManager cacheManager = new RedisCacheManager(redisTemplate());
        // Number of seconds before expiration. Defaults to unlimited (0)
        cacheManager.setDefaultExpiration(7200); // Sets the default expire time (in seconds)
        cacheManager.setUsePrefix(true);*/
        cacheManager.afterPropertiesSet();
        return cacheManager;
    }

    @Bean
    public KeyGenerator wiselyKeyGenerator() {
        return new KeyGenerator() {
            @Override
            public Object generate(Object target, Method method, Object... params) {
                StringBuilder sb = new StringBuilder();
                sb.append(target.getClass().getName());
                sb.append(method.getName());
                for (Object obj : params) {
                    sb.append(obj.toString());
                }
                return sb.toString();
            }
        };
    }
}
