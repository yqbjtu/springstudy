package com.yq.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;

import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * Simple to Introduction
 * className: MsgProcessor
 *
 * @author EricYang
 * @version 2018/7/14 12:06
 */
@Component
public class MsgProcessor {

    @Autowired
    StringRedisTemplate template;
    private static final ThreadPoolExecutor outputExecutor = new ThreadPoolExecutor(
            4,             /* minimum (core) thread count */
            6,        /* maximum thread count */
            Long.MAX_VALUE, /* timeout */
            TimeUnit.NANOSECONDS,
            new LinkedBlockingQueue<Runnable>(),
            new ThreadPoolExecutor.DiscardOldestPolicy());
    @Autowired
    private DistLock distLock;

    public void createRunnable(int count) {
        for (int i = 0; i < count ; i++) {
            CreateRecordRunnable runnable = new CreateRecordRunnable("001", distLock, template);
            outputExecutor.submit(runnable);
        }
    }

}
