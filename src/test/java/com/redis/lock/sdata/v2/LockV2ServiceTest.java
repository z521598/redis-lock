/*
 * Copyright (C) 2018 Baidu, Inc. All Rights Reserved.
 */
package com.redis.lock.sdata.v2;

import java.util.concurrent.TimeUnit;

import org.junit.Assert;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import com.redis.base.SpringBaseTest;

/**
 * Created by langshiquan on 2018/8/17.
 */
public class LockV2ServiceTest extends SpringBaseTest {

    @Autowired
    private LockV2Service lockService;

    @Test
    public void testLockAndRelease() throws Exception {
        Lock lock = lockService.tryLock("a");
        Assert.assertNotNull(lock);
        lockService.release(lock);
    }

    @Test
    public void testLockExpire() throws Exception {
        Lock lock = lockService.tryLock("b");
        Assert.assertNotNull(lock);
        TimeUnit.SECONDS.sleep(11);
        Lock secondlock = lockService.tryLock("b");
        Assert.assertNotNull(secondlock);
        lockService.release(secondlock);
    }

    @Test
    public void testTryLockFailed() throws Exception {
        Lock lock1 = lockService.tryLock("c");
        Assert.assertNotNull(lock1);
        Lock lock2 = lockService.tryLock("c", 8 * 1000);
        Assert.assertNull(lock2);
        lockService.release(lock1);
    }

    @Test
    public void testTryLockSucc() throws Exception {
        Lock lock1 = lockService.tryLock("d");
        Assert.assertNotNull(lock1);
        Lock lock2 = lockService.tryLock("d", 11 * 1000);
        Assert.assertNotNull(lock2);
        lockService.release(lock2);
    }

    @Test(expected = RuntimeException.class)
    public void testReleaseTimeoutLock() throws Exception {
        Lock lock1 = lockService.tryLock("e", 1000, 1000);
        Assert.assertNotNull(lock1);
        Lock lock2 = lockService.tryLock("e", 2000, 1000);
        Assert.assertNotNull(lock2);
        lockService.release(lock1);
    }

}