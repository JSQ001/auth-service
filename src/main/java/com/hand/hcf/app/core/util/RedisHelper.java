package com.hand.hcf.app.core.util;

import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;

import java.util.concurrent.TimeUnit;

@Component
public class RedisHelper {

    //处理K、V均为String类型的数据
    private StringRedisTemplate stringRedisTemplate;

    public RedisHelper(StringRedisTemplate stringRedisTemplate) {
        this.stringRedisTemplate = stringRedisTemplate;
    }

    /**
     * String类型的键值写入redis,并设置失效时间
     *
     * @param key
     * @param value
     * @param timeout
     */
    public void setStringWithExpireTime(String key, String value, long timeout) {
        stringRedisTemplate.opsForValue().set(key, value, timeout, TimeUnit.SECONDS);
    }

    /**
     * String类型的键值写入redis,不会失效
     *
     * @param key
     * @param value
     */
    public void setString(String key, String value) {
        stringRedisTemplate.opsForValue().set(key, value);
    }

    /**
     * 根据key获取Redis里的value
     *
     * @param key
     * @return
     */
    public String getStringValueByKey(String key) {
        return stringRedisTemplate.opsForValue().get(key);
    }

    /**
     * 指定key的value增加delta
     *
     * @param key
     * @param delta 可为负数
     * @return 增加之后的结果
     */
    public Long increment(String key, long delta) {
        return stringRedisTemplate.boundValueOps(key).increment(delta);
    }

    /**
     * 根据key删除缓存
     *
     * @param key
     */
    public void deleteByKey(String key) {
        stringRedisTemplate.delete(key);
    }

    /**
     * 查询key是否存在
     * @param key
     * @return
     */
    public Boolean existKey(String key){
        return stringRedisTemplate.hasKey(key);
    }

}
