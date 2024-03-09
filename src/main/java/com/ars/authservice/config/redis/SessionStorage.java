package com.ars.authservice.config.redis;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.util.concurrent.TimeUnit;

@Service
@Slf4j
public class SessionStorage {

    @Autowired
    private RedisTemplate<String, Object> redisTemplate;

    public void putCache(String cacheName, Object key, Object value, long timeToLive){
        redisTemplate.opsForValue().set(cacheName + key, value, timeToLive, TimeUnit.SECONDS);
    }

    public Object getCache(String cacheName, Object key){
        return redisTemplate.opsForValue().get(cacheName + key);
    }

    public Object getCache(String cacheName){
        return redisTemplate.opsForValue().get(cacheName);
    }

    public void removeCache (String cacheName, Object key){
        redisTemplate.opsForValue().getOperations().delete(cacheName + key);
    }

}
