/*
 * Copyright (C) 2018 Baidu, Inc. All Rights Reserved.
 */
package com.redis.lock.sdata.v2;

/**
 * Created by langshiquan on 2018/8/17.
 */
public class Lock {
    private String lockKey;
    private long expriyTime;

    public Lock(String lockKey, long expriyTime) {
        this.lockKey = lockKey;
        this.expriyTime = expriyTime;
    }

    public String getLockKey() {
        return lockKey;
    }

    public String getLockValue() {
        return String.valueOf(expriyTime);
    }
}
