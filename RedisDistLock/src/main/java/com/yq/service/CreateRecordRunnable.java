package com.yq.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.ValueOperations;

import java.util.UUID;

/**
 * Simple to Introduction
 * className: CreateRecordRunnable
 *
 * @author EricYang
 * @version 2018/7/14 12:06
 */

@Slf4j
public class CreateRecordRunnable implements Runnable {
    private String userId;
    private DistLock distLock;

    private StringRedisTemplate template;

    public CreateRecordRunnable(String userId, DistLock distLock, StringRedisTemplate template) {
        this.userId = userId;
        this.distLock = distLock;
        this.template = template;
    }

    @Override
    public void run() {
        long threadId = Thread.currentThread().getId();
        log.debug("Current thread={}", Thread.currentThread().getName());
        String lockKey = userId;
        Boolean isAcquired = false;
        String lockValue = null;
        try {
            lockValue = distLock.tryLock(lockKey);
            if (lockValue != null) {
                isAcquired = true;
                log.info("userId={}, acquired lock. lockValue={}, threadId={}", userId, lockValue, threadId);
                //redis初始值为0， 然后每个获取着，将其修改为线程id，
                ValueOperations<String, String> ops = template.opsForValue();
                String key = "key_" + userId;
                String existingValue = ops.get(key);
                if (existingValue == null || existingValue.equals("0")) {
                    ops.set(key, "1");
                    ops.set("record_"+  UUID.randomUUID().toString(), "1_" + threadId);
                    log.info("userId={}, key={}, create record. existingValue={}, threadId={}", userId, key, existingValue, threadId);
                }
                else {
                    log.info("userId={}, key={}, has value,skip it. existingValue={}, threadId={}", userId, key, existingValue, threadId);
                }
            } else {
                log.info("userId={}, acquired distLock failed. threadId={}", userId, threadId);
            }
        } catch (Exception ex) {
            log.info("userId={}, threadId={}, exception when generating record", userId, threadId);
            ex.printStackTrace();
        } finally {
            if (isAcquired) {
                long releaseTime = System.currentTimeMillis();
                boolean result = distLock.releaseLock(lockKey, lockValue);
                log.info("userId={}, lockValue={}, unlock result={}. releaseTime={}, threadId={}",
                        userId, lockValue, result, releaseTime, threadId);
            } else {
                log.info("userId={}, no unlock. threadId={}", userId, threadId);
            }
        }

        log.info("userId={}  run end threadId={}", userId, threadId);
    }
}
