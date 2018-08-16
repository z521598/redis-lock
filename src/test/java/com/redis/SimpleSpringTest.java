/*
 * Copyright (C) 2018 Baidu, Inc. All Rights Reserved.
 */
package com.redis;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import com.redis.service.SimpleBean;

/**
 * Created by langshiquan on 2018/8/16.
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration({"classpath:applicationContext.xml"})
public class SimpleSpringTest {

    @Autowired
    ApplicationContext applicationContext;

    @Autowired
    RedisTemplate<String, String> redisTemplate;

    @Autowired
    StringRedisTemplate stringRedisTemplate;

    @Test
    public void testGetBean() throws Exception {
        SimpleBean simpleBean = (SimpleBean) applicationContext.getBean("simpleBean");
        Assert.assertEquals(1L, simpleBean.getId().longValue());
        Assert.assertEquals("lsq", simpleBean.getName());
    }

    @Test
    public void testJedisConnectionFactory() throws Exception {
        Assert.assertNotNull(applicationContext.getBean("jedisConnectionFactory"));
    }

    @Test
    public void testRedisTemplate() throws Exception {
        // 不设置范型无效
        redisTemplate.opsForValue().set("key1", "value1");
        Assert.assertEquals("value1", redisTemplate.opsForValue().get("key1"));
    }

    @Test
    public void testStringRedisTemplate() throws Exception {
        stringRedisTemplate.opsForValue().set("key2", "value2");
        Assert.assertEquals("value2", stringRedisTemplate.opsForValue().get("key2"));

    }
}
