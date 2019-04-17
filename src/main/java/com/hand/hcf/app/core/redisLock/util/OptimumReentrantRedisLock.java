package com.hand.hcf.app.core.redisLock.util;

import com.hand.hcf.app.core.exception.BizException;
import com.hand.hcf.app.core.redisLock.domain.RedisValueObject;
import com.hand.hcf.app.core.util.RespCode;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataAccessException;
import org.springframework.data.redis.connection.RedisConnection;
import org.springframework.data.redis.core.RedisCallback;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.StringRedisSerializer;

@Slf4j
public class OptimumReentrantRedisLock {

    private RedisTemplate redisTemplate;

    // Lock key path.
    private String lockKey;

    private static final int ONE_SECOND = 1000;

//    private static final int DEFAULT_ACQUIRE_RESOLUTION_MILLIS = 100;

    // 锁超时时间,防止线程在入锁以后,无限的执行等待
    private int expireMsecs = 60 * 1000;

    // 锁等待时间,防止线程饥饿
    private int timeoutMsecs = 10 * 1000;

    private volatile boolean locked = false;

    private static final String SPACE = "_";

    private volatile boolean waiting = false;

    private int intervalTime = 100;

    public OptimumReentrantRedisLock(RedisTemplate redisTemplate, String lockKeyPrefix, String lockClassMethod, String lockFieldValue) {
        this.redisTemplate = redisTemplate;
        //prefix + className + methodName + lockFieldValue + LOCK
        //faster speed
        StringBuilder stringBuilder = new StringBuilder();
        this.lockKey = stringBuilder.append(lockKeyPrefix).append(SPACE).append(lockClassMethod).append(SPACE).append(lockFieldValue).append(SPACE).append("LOCK").toString();
    }

    public OptimumReentrantRedisLock(RedisTemplate redisTemplate, String lockKeyPrefix, String lockClassMethod, String lockFieldValue, int timeoutMsecs) {
        this(redisTemplate, lockKeyPrefix, lockClassMethod, lockFieldValue);
        this.timeoutMsecs = timeoutMsecs;
    }

    public OptimumReentrantRedisLock(RedisTemplate redisTemplate, String lockKeyPrefix, String lockClassMethod, String lockFieldValue, int timeoutMsecs, int expireMsecs,Boolean waiting,Integer intervalTime) {
        this(redisTemplate, lockKeyPrefix, lockClassMethod, lockFieldValue, timeoutMsecs);
        this.expireMsecs = expireMsecs;
        this.waiting = waiting;
        this.intervalTime = intervalTime;

    }

    /**
     * @return lock key
     */
    public String getLockKey() {
        return lockKey;
    }

    private String get(final String key) {
        Object obj = null;
        try {
            obj = redisTemplate.execute(new RedisCallback<Object>() {
                @Override
                public Object doInRedis(RedisConnection connection) throws DataAccessException {
                    StringRedisSerializer serializer = new StringRedisSerializer();
                    byte[] data = connection.get(serializer.serialize(key));
                    connection.close();
                    if (data == null) {
                        return null;
                    }
                    return serializer.deserialize(data);
                }
            });
        } catch (Exception e) {
            log.error("get redis error, key : {}", key);
        }
        return obj != null ? obj.toString() : null;
    }

    private boolean setNX(final String key, final String value) {
        Object obj = null;
        try {
            obj = redisTemplate.execute(new RedisCallback<Object>() {
                @Override
                public Object doInRedis(RedisConnection connection) throws DataAccessException {
                    StringRedisSerializer serializer = new StringRedisSerializer();
                    Boolean success = connection.setNX(serializer.serialize(key), serializer.serialize(value));
                    connection.close();
                    return success;
                }
            });
        } catch (Exception e) {
            log.error("setNX redis error, key : {}", key);
        }
        return obj != null ? (Boolean) obj : false;
    }

    private String getSet(final String key, final String value) {
        Object obj = null;
        try {
            obj = redisTemplate.execute(new RedisCallback<Object>() {
                @Override
                public Object doInRedis(RedisConnection connection) throws DataAccessException {
                    StringRedisSerializer serializer = new StringRedisSerializer();
                    byte[] ret = connection.getSet(serializer.serialize(key), serializer.serialize(value));
                    connection.close();
                    return serializer.deserialize(ret);
                }
            });
        } catch (Exception e) {
            log.error("setNX redis error, key : {}", key);
        }
        return obj != null ? (String) obj : null;
    }

    private void del(final String key) {
        try {
            redisTemplate.execute(new RedisCallback<Object>() {
                @Override
                public Object doInRedis(RedisConnection connection) throws DataAccessException {
                    StringRedisSerializer serializer = new StringRedisSerializer();
                    Long del = connection.del(serializer.serialize(key));
                    connection.close();
                    if (del == null) {
                        return null;
                    }
                    return del;
                }
            });
        } catch (Exception e) {
            log.error("del redis error, key : {}", key);
        }
    }

    /**
     * 获得 lock.
     * 实现思路: 主要是使用了redis 的setnx命令,缓存了锁.
     * redis缓存的key是锁的key,所有的共享, value是锁的到期时间(注意:这里把过期时间放在value了,没有时间上设置其超时时间)
     * 执行过程:
     * 1.通过setnx尝试设置某个key的值,成功(当前没有这个锁)则返回,成功获得锁
     * 2.锁已经存在则获取锁的到期时间,和当前时间比较,超时的话,则设置新的值
     *
     * @return true if lock is acquired, false acquire timeout
     * @throws InterruptedException in case of thread interruption
     */
    public boolean lock() throws InterruptedException {
        // 等待时间 (等待时间不能超过锁失效时间，防止出现锁失效但程序还在运行，这样锁的意义也就不存在了)
//        int timeout = Math.min(timeoutMsecs,expireMsecs);
        Long timeOutMill = System.currentTimeMillis() + Math.min(timeoutMsecs,expireMsecs);
//        while (timeout >= 0) {
        while (true) {
            //锁到期时间
            long expires = System.currentTimeMillis() + expireMsecs + 1;

            if (this.setNX(lockKey, LocationUtil.serializeCurrentRequest(expires))) {
                // lock acquired
                locked = true;
                return true;
            }

            String currentValueStr = this.get(lockKey);
            //redisValueObjectOld1 获得的是get指令得到的value
            RedisValueObject redisValueObjectOld1 = LocationUtil.deserializeCurrentRequest(currentValueStr);
            //优先判断是否是同一个mac地址同一个进程id同一个线程id,若都满足,则获取锁
            if (redisValueObjectOld1.getMacAddress().equals(LocationUtil.getLocalMac()) && redisValueObjectOld1.getJvmPid() == LocationUtil.getJvmProcessId() && redisValueObjectOld1.getThreadId() == LocationUtil.getThreadId()) {
                // lock acquired
                locked = true;
                return true;
            }
            long currentExpireTime = redisValueObjectOld1.getExpires();
            if (currentExpireTime != 0 && currentExpireTime < System.currentTimeMillis()) {
                //过期不能简单的delKey,若两个Client均发现key过期,C1先DEL并SETNX,C2再DEL并SETNX,这样导致两个C均获得锁
                //使用getSet原子性操作可规避此问题
                String oldValueStr = this.getSet(lockKey, LocationUtil.serializeCurrentRequest(expires));
                //redisValueObjectOld2 获得的是getSet指令得到的value
                RedisValueObject redisValueObjectOld2 = LocationUtil.deserializeCurrentRequest(oldValueStr);
                //保证get以及getSet之间没有其他线程修改此key对应的value
                //多个线程恰好都到了这里，但是只有一个线程的设置值和当前值相同
                if (redisValueObjectOld2.getExpires() != 0 && redisValueObjectOld2.getExpires() == currentExpireTime) {
                    // lock acquired
                    locked = true;
                    return true;
                }
            }
            if(! waiting){
                break;
            }
//            timeout -= intervalTime;

            /*
                延迟100 毫秒,  这里使用随机时间可能会好一点,可以防止饥饿进程的出现,即,当同时到达多个进程,
                只会有一个进程获得锁,其他的都用同样的频率进行尝试,后面有来了一些进行,也以同样的频率申请锁,这将可能导致前面来的锁得不到满足.
                使用随机的等待时间可以一定程度上保证公平性
             */
            if(timeOutMill - intervalTime < System.currentTimeMillis()){
                break;
            }
            Thread.sleep(intervalTime);

        }
        if(waiting){
            throw new BizException(RespCode.SYS_FAILED_TO_GET_RESOURCE_REQUEST_TIMEOUT);
        }
        return false;
    }

    /**
     * Acquired lock release.
     */
    public void unlock() {
        if (locked) {
//            redisTemplate.delete(lockKey);
            this.del(lockKey);
            locked = false;
        }
    }
}
