/*
 * Copyright (C) 2018 Baidu, Inc. All Rights Reserved.
 */
package com.redis.lock.sdata.v1;

import java.util.UUID;
import java.util.concurrent.TimeUnit;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;

/**
 * Created by langshiquan on 2018/8/16.
 */
@Component
public class LockService {

    public final static long DEFAULT_LOCK_TIMEOUT = 10L * 1000;
    public static final long DEFAULT_ACQUIRE_RESOLUTION_MILLIS = 100L;
    @Autowired
    private StringRedisTemplate redisTemplate;

    public UUID tryLock(String lockKey) throws InterruptedException {
        return tryLock(lockKey, DEFAULT_ACQUIRE_RESOLUTION_MILLIS, DEFAULT_LOCK_TIMEOUT);
    }

    public UUID tryLock(String lockKey, long acquireTimeoutInMillis) throws InterruptedException {
        return tryLock(lockKey, acquireTimeoutInMillis, DEFAULT_LOCK_TIMEOUT);
    }

    public UUID tryLock(String lockKey, long acquireTimeoutInMillis, long lockExpiryInMillis)
            throws InterruptedException {
        UUID uuid = UUID.randomUUID();
        long timeout = 0L;
        while (timeout < acquireTimeoutInMillis) {
            if (redisTemplate.opsForValue().setIfAbsent(lockKey, uuid.toString())) {
                // 过期时间 <= 0，则立刻过期
                redisTemplate.expire(lockKey, lockExpiryInMillis, TimeUnit.MILLISECONDS);
                return uuid;
            }
            TimeUnit.MILLISECONDS.sleep(DEFAULT_ACQUIRE_RESOLUTION_MILLIS);
            timeout += DEFAULT_ACQUIRE_RESOLUTION_MILLIS;
        }
        return null;
    }

    public void release(String lockKey, UUID uuid) {
        if (uuid == null) {
            return;
        }
        String lockUuid = redisTemplate.opsForValue().get(lockKey);
        if (uuid.toString().equals(lockUuid)) {
            redisTemplate.delete(lockKey);
        } else {
            throw new RuntimeException("can not release timeout lock");
        }
    }
}
