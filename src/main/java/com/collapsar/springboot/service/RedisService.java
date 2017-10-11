package com.collapsar.springboot.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

/**
 * Created by chenyong6 on 2017/8/28.
 */
@Slf4j
@Service
public class RedisService {
    @Autowired
    private StringRedisTemplate stringRedisTemplate;

    public String doSth(){
        log.debug("redis: start.");
        final String key = "mykey.test";
        this.stringRedisTemplate.opsForValue().set(key, "aaa");
        log.debug("redis: key has been set.");
        return this.stringRedisTemplate.opsForValue().get(key);
    }
}
