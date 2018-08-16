/*
 * Copyright (C) 2018 Baidu, Inc. All Rights Reserved.
 */
package com.redis.lock.sdata.v1;

import java.util.UUID;
import java.util.concurrent.TimeUnit;

import org.junit.Assert;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import com.redis.base.SpringBaseTest;
import com.redis.lock.sdata.v1.LockKeyUtils;
import com.redis.lock.sdata.v1.LockService;

/**
 * Created by langshiquan on 2018/8/16.
 */

public class LockServiceTest extends SpringBaseTest {

    @Autowired
    private LockService lockService;

    @Test
    public void testLockAndRelease() throws Exception {
        UUID lockUuid = lockService.tryLock(LockKeyUtils.getKey(1));
        Assert.assertNotNull(lockUuid);
        lockService.release(LockKeyUtils.getKey(1), lockUuid);
    }

    @Test
    public void testLockExpire() throws Exception {
        UUID lockUuid = lockService.tryLock(LockKeyUtils.getKey(2));
        Assert.assertNotNull(lockUuid);
        TimeUnit.SECONDS.sleep(11);
        UUID secondlockUuid = lockService.tryLock(LockKeyUtils.getKey(2));
        Assert.assertNotNull(secondlockUuid);
    }

    @Test
    public void testTryLockFailed() throws Exception {
        UUID uuid1 = lockService.tryLock(LockKeyUtils.getKey(3));
        Assert.assertNotNull(uuid1);
        UUID uuid2 = lockService.tryLock(LockKeyUtils.getKey(3), 8 * 1000);
        Assert.assertNull(uuid2);
        lockService.release(LockKeyUtils.getKey(3), uuid1);
    }

    @Test
    public void testTryLockSucc() throws Exception {
        UUID uuid1 = lockService.tryLock(LockKeyUtils.getKey(4));
        Assert.assertNotNull(uuid1);
        UUID uuid2 = lockService.tryLock(LockKeyUtils.getKey(4), 11 * 1000);
        Assert.assertNotNull(uuid2);
        lockService.release(LockKeyUtils.getKey(4), uuid2);
    }

    @Test(expected = RuntimeException.class)
    public void testReleaseTimeoutLock() throws Exception {
        UUID uuid1 = lockService.tryLock(LockKeyUtils.getKey(5), 1000, 1000);
        Assert.assertNotNull(uuid1);
        UUID uuid2 = lockService.tryLock(LockKeyUtils.getKey(5), 2000, 1000);
        Assert.assertNotNull(uuid2);
        lockService.release(LockKeyUtils.getKey(5), uuid1);
    }

}