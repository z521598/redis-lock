/*
 * Copyright (C) 2018 Baidu, Inc. All Rights Reserved.
 */
package com.redis.uuid;

import java.util.UUID;

/**
 * Created by langshiquan on 2018/8/17.
 */
public class Main {
    public static void main(String[] args) {
        //        System.out.println(UUID.randomUUID().toString());
        //        System.out.println(UUID.randomUUID().toString().replace("-", ""));
        //        String uuid = UUID.randomUUID().toString();
        //        String u1 = UUID.fromString(uuid).toString();
        //        String u2 = UUID.nameUUIDFromBytes(uuid.getBytes()).toString();
        //        System.out.println(u1);
        //        System.out.println(u2);
        // 前18位按照mostSigBits进行16进制转换，后18位按照leastSigBits进行16进制转换，
        UUID uuid1 = new UUID(1234567L, 234567L);
        UUID uuid2 = new UUID(0X123456a, 0X23456b);
        System.out.println(uuid1);
        System.out.println(uuid2.toString());
    }
}
