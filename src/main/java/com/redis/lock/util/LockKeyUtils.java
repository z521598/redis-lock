/*
 * Copyright (C) 2018 Baidu, Inc. All Rights Reserved.
 */
package com.redis.lock.util;

/**
 * Created by langshiquan on 2018/8/16.
 */
public class LockKeyUtils {
    public static String getKey(Object obj) {
        return "lock:" + obj.toString();
    }
}
