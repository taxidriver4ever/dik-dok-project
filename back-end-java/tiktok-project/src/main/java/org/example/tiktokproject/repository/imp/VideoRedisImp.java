package org.example.tiktokproject.repository.imp;

import io.micrometer.common.util.StringUtils;
import jakarta.annotation.Resource;
import org.example.tiktokproject.pojo.NameAndUrl;
import org.example.tiktokproject.repository.VideoRedis;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.dao.DataAccessException;
import org.springframework.data.redis.core.RedisOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.SessionCallback;
import org.springframework.data.redis.core.script.DefaultRedisScript;
import org.springframework.stereotype.Repository;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.TimeUnit;

@Repository
public class VideoRedisImp implements VideoRedis {

    @Resource
    private RedisTemplate<String, String> redisTemplate;

    private static final Logger log = LoggerFactory.getLogger(VideoRedisImp.class);

    @Override
    public boolean incrLike(NameAndUrl nameAndUrl) {
        String url = nameAndUrl.getUrl();
        String username = nameAndUrl.getName();

        if (StringUtils.isBlank(url) || StringUtils.isBlank(username)) {
            log.warn("点赞参数为空");
            return false;
        }

        String videoLikeKey = "video:like:" + url;

        // 先检查是否已点赞
        if (redisTemplate.opsForZSet().score(videoLikeKey, username) != null) {
            log.info("用户已点赞过");
            return false;
        }

        try {
            // 使用事务执行点赞操作
            redisTemplate.execute(new SessionCallback<Object>() {
                @Override
                public Object execute(RedisOperations operations) {
                    operations.multi();

                    String infoKey = "video:information:" + url;
                    String userLikeKey = "user:like:" + username;
                    long timestamp = System.currentTimeMillis();

                    operations.opsForHash().increment(infoKey, "like", 1);
                    operations.opsForZSet().add(videoLikeKey, username, timestamp);
                    operations.opsForZSet().add(userLikeKey, url, timestamp);

                    return operations.exec();
                }
            });

            log.info("点赞成功");
            return true;

        } catch (Exception e) {
            log.error("点赞异常", e);
            return false;
        }
    }

    @Override
    public boolean decrLike(NameAndUrl nameAndUrl) {
        String url = nameAndUrl.getUrl();
        String username = nameAndUrl.getName();

        if (StringUtils.isBlank(url) || StringUtils.isBlank(username)) {
            log.warn("点赞参数为空");
            return false;
        }

        String videoLikeKey = "video:like:" + url;

        // 先检查是否已点赞
        if (redisTemplate.opsForZSet().score(videoLikeKey, username) == null) {
            log.info("用户没点赞过");
            return false;
        }

        try {
            // 使用事务执行点赞操作
            redisTemplate.execute(new SessionCallback<Object>() {
                @Override
                public Object execute(RedisOperations operations) {
                    operations.multi();

                    String infoKey = "video:information:" + url;
                    String userLikeKey = "user:like:" + username;

                    operations.opsForHash().increment(infoKey, "like", -1);
                    operations.opsForZSet().remove(videoLikeKey, username);
                    operations.opsForZSet().remove(userLikeKey, url);

                    return operations.exec();
                }
            });

            log.info("取赞成功");
            return true;

        } catch (Exception e) {
            log.error("点赞异常", e);
            return false;
        }
    }

    @Override
    public void setInformation(String url) throws InterruptedException {
        String key = "video:information:" + url;
        // 使用putIfAbsent作为原子性的存在检查
        Boolean isNew = redisTemplate.opsForHash().putIfAbsent(key, "view", 0);

        if (Boolean.TRUE.equals(isNew)) {
            // 如果是新创建的，设置其他字段
            redisTemplate.opsForHash().put(key, "like", 0);
            redisTemplate.opsForHash().put(key, "comment", 0);
            redisTemplate.opsForHash().put(key, "share", 0);
        }
    }

    @Override
    public boolean existUserLike(NameAndUrl nameAndUrl) {
        return redisTemplate.opsForZSet().score("user:like:" + nameAndUrl.getName(), nameAndUrl.getUrl()) != null;
    }

    @Override
    public Integer getUserLike(String url) {
        return (Integer) redisTemplate.opsForHash().get("video:information:" + url, "like");
    }


}
