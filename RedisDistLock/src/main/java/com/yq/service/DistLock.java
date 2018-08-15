package com.yq.service;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.connection.RedisConnection;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.core.RedisConnectionUtils;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.Transaction;
import redis.clients.jedis.exceptions.JedisException;

import java.util.List;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

/**
 * Simple to Introduction
 * className: DistributedLock
 *
 */
@Service
public class DistLock {
    private static final Logger logger = LoggerFactory.getLogger(DistLock.class);
    private final static long LOCK_EXPIRED_TIME = 90 * 1000L;
    private final static long LOCK_TRY_INTERVAL = 30L;  //默认30ms尝试一次
    private final static long LOCK_TRY_TIMEOUT = 121 * 1000L; //默认尝试121s
    private final static String VALUE = "1";

    @Autowired
    private StringRedisTemplate template;

    public String tryLock(String key) {
        return getLock(key, LOCK_TRY_TIMEOUT, LOCK_TRY_INTERVAL, LOCK_EXPIRED_TIME);
    }

    public String tryLock(String key, long timeout) {
        return getLock(key, timeout, LOCK_TRY_INTERVAL, LOCK_EXPIRED_TIME);
    }

    public String tryLock(String key, long timeout, long tryInterval) {
        return getLock(key, timeout, tryInterval, LOCK_EXPIRED_TIME);
    }

    public String tryLock(String key, long timeout, long tryInterval, long lockExpireTime) {
        return getLock(key, timeout, tryInterval, lockExpireTime);
    }

    public String getLock(String key, long acquiredTimeout, long tryInterval, long lockExpiredTime) {
        try {
            if (StringUtils.isBlank(key) ) {
                return null;
            }
            String retIdentifier = null;
            long startTime = System.currentTimeMillis();
            // 随机生成一个value
            String identifier = UUID.randomUUID().toString();
            // 锁名，即key值
            String lockKey = "lock:" + key;
            // 超时时间，上锁后超过此时间则自动释放锁
            //lockExpiredTime

            // 获取锁的超时时间，超过这个时间则放弃获取锁
            long end = System.currentTimeMillis() + acquiredTimeout;
            ValueOperations<String, String> ops = template.opsForValue();

            while (System.currentTimeMillis() < end) {
                if (ops.setIfAbsent(lockKey, identifier)) {
                    template.expire(lockKey, lockExpiredTime, TimeUnit.MILLISECONDS);
                    //ops.expire(lockKey, lockExpire);
                    // 返回value值，用于释放锁时间确认
                    retIdentifier = identifier;
                    return retIdentifier;
                }
                // 返回-1代表key没有设置超时时间，为key设置一个超时时间
                long expired = template.getExpire(lockKey);
                if (expired == -1) {
                    template.expire(lockKey, lockExpiredTime, TimeUnit.MILLISECONDS);
                }

                try {
                    Thread.sleep(tryInterval);
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                }
            }
        } catch (Exception e) {
            logger.error(e.getMessage());
            return null;
        }
        return null;
    }

    /**
     * 释放锁
     * @param lockName 锁的key
     * @param identifier    释放锁的标识
     * @return
     */
    public boolean releaseLock(String lockName, String identifier) {
        String lockKey = "lock:" + lockName;
        boolean releaseFlag = false;
        RedisConnectionFactory connectionFactory = template.getConnectionFactory();
        RedisConnection redisConnection = connectionFactory.getConnection();
        try {
            while (true) {
                // 监视lock，准备开始事务
                redisConnection.watch(lockKey.getBytes());
                // 通过前面返回的value值判断是不是该锁，若是该锁，则删除，释放锁
                byte[] valueBytes = redisConnection.get(lockKey.getBytes());
                if(valueBytes == null){
                    redisConnection.unwatch();
                    releaseFlag = false;
                    break;
                }

                String identifierValue = new String(valueBytes);
                if (identifier.equals(identifierValue)) {
                    redisConnection.multi();
                    redisConnection.del(lockKey.getBytes());
                    List<Object> results =  redisConnection.exec();
                    if (results == null) {
                        continue;
                    }
                    releaseFlag = true;
                }
                redisConnection.unwatch();
                break;
            }
        } catch (Exception e) {
            logger.info("delete key by transaction");
            e.printStackTrace();
        }

        RedisConnectionUtils.releaseConnection(redisConnection, connectionFactory);
        return releaseFlag;
    }

    public boolean releaseLockByJedis(JedisPool jedisPool, String lockName, String identifier) {
        Jedis conn = null;
        String lockKey = "lock:" + lockName;
        boolean retFlag = false;
        try {
            conn = jedisPool.getResource();
            while (true) {
                // 监视lock，准备开始事务
                conn.watch(lockKey);
                template.watch(lockKey);
                // 通过前面返回的value值判断是不是该锁，若是该锁，则删除，释放锁
                String value = conn.get(lockKey);
                if (identifier.equals(value)) {
                    Transaction transaction = conn.multi();
                    transaction.del(lockKey);
                    List<Object> results = transaction.exec();
                    if (results == null) {
                        continue;
                    }
                    retFlag = true;
                }
                conn.unwatch();
                break;
            }
        } catch (JedisException e) {
            e.printStackTrace();
        } finally {
            if (conn != null) {
                conn.close();
            }
        }
        return retFlag;
    }
}



