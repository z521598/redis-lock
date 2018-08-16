/*
 * Copyright (C) 2018 Baidu, Inc. All Rights Reserved.
 */
package com.redis.lock.sdata.v2;

import java.util.concurrent.TimeUnit;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;

import com.redis.lock.util.LockKeyUtils;

/**
 * Created by langshiquan on 2018/8/16.
 */
@Component
public class LockV2Service {

    public final static long DEFAULT_LOCK_TIMEOUT = 10L * 1000;
    public static final long DEFAULT_ACQUIRE_RESOLUTION_MILLIS = 100L;
    @Autowired
    private StringRedisTemplate redisTemplate;

    public Lock tryLock(String lockKey) throws InterruptedException {
        return tryLock(lockKey, DEFAULT_ACQUIRE_RESOLUTION_MILLIS, DEFAULT_LOCK_TIMEOUT);
    }

    public Lock tryLock(String lockKey, long acquireTimeoutInMillis) throws InterruptedException {
        return tryLock(lockKey, acquireTimeoutInMillis, DEFAULT_LOCK_TIMEOUT);
    }

    public Lock tryLock(String lockKey, long acquireTimeoutInMillis, long lockExpiryInMillis)
            throws InterruptedException {
        String lockKeyPath = LockKeyUtils.getKey(lockKey);
        Lock lock = asLock(lockKey, lockExpiryInMillis);
        long timeout = 0L;
        while (timeout < acquireTimeoutInMillis) {
            // 未锁则直接获取
            if (redisTemplate.opsForValue().setIfAbsent(lockKeyPath, lock.getLockValue())) {
                return lock;
            }
            // 已锁则查看超时时间
            String currentLockExpriyStr = redisTemplate.opsForValue().get(lockKeyPath);
            long currentLockExpriy = Long.parseLong(currentLockExpriyStr);
            if (currentLockExpriy < System.currentTimeMillis()) {
                String oldValueStr = redisTemplate.opsForValue().getAndSet(lockKeyPath, lock.getLockValue());
                if (currentLockExpriyStr.equals(oldValueStr)) {
                    return lock;
                }
            }
            TimeUnit.MILLISECONDS.sleep(DEFAULT_ACQUIRE_RESOLUTION_MILLIS);
            timeout += DEFAULT_ACQUIRE_RESOLUTION_MILLIS;
        }
        return null;
    }

    private Lock asLock(String lockKey, long lockExpiryInMillis) {
        return new Lock(lockKey, System.currentTimeMillis() + lockExpiryInMillis);
    }

    public void release(Lock lock) {
        if (lock == null) {
            return;
        }
        String lockKeyPath = LockKeyUtils.getKey(lock.getLockKey());
        String lockUuid = redisTemplate.opsForValue().get(lockKeyPath);
        if (lock.getLockValue().equals(lockUuid)) {
            redisTemplate.delete(lockKeyPath);
        } else {
            throw new RuntimeException("can not release timeout lock");
        }
    }

}
