package org.example.tiktokproject.repository.imp;

import jakarta.annotation.Resource;
import org.example.tiktokproject.repository.LiveRedis;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Repository;

@Repository
public class LiveRedisImp implements LiveRedis {

    @Resource
    private RedisTemplate<String,String> redisTemplate;


    @Override
    public boolean existLive(String author) {
        return redisTemplate.opsForValue().get("live:author:" + author) != null;
    }

    @Override
    public void createLive(String author) {
        redisTemplate.opsForValue().set("live:author:" + author, "living");
    }

    @Override
    public void deleteLive(String author) {
        redisTemplate.delete("live:author:" + author);
    }
}
