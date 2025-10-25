package org.example.tiktokproject.repository.imp;

import jakarta.annotation.Resource;
import org.example.tiktokproject.repository.LoginAndRegisterRedis;
import org.springframework.data.redis.core.RedisOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.SessionCallback;
import org.springframework.stereotype.Repository;

import java.util.Objects;
import java.util.concurrent.TimeUnit;

@Repository
public class LoginAndRegisterRedisImp implements LoginAndRegisterRedis {

    @Resource
    private RedisTemplate<String, Object> redisTemplate;

    @Override
    public void storeOTP(String otp, String userEmail) {
        redisTemplate.opsForValue().set("user:OTP:" + userEmail, otp, 5, TimeUnit.MINUTES);
    }

    @Override
    public boolean verifyOTP(String otp, String userEmail) {
        return Objects.equals(redisTemplate.opsForValue().get("user:OTP:" + userEmail), otp);
    }

    @Override
    public void storeUserNameWithUUID(String userName, String uuid) {
        redisTemplate.opsForValue().set("user:UUID:" + userName, uuid, 5, TimeUnit.DAYS);
    }

    @Override
    public boolean verifyUserNameWithUUID(String userName, String uuid) {
        String string = (String) redisTemplate.opsForValue().get("user:UUID:" + userName);
        if (!Objects.isNull(string)) return Objects.equals(string, uuid);
        return false;
    }

    @Override
    public void storeSubscribeUserName(String userName, String subscribeUserName) {
        redisTemplate.opsForZSet().addIfAbsent("user:subscribe:" + userName, subscribeUserName, System.currentTimeMillis());
    }

    @Override
    public boolean verifySubscribeUserName(String userName, String subscribeUserName) {
        return redisTemplate.opsForZSet().score("user:subscribe:" + userName, subscribeUserName) != null;
    }

    @Override
    public boolean deleteSubscribeUserName(String userName, String subscribeUserName) {
        if(verifySubscribeUserName(userName, subscribeUserName)) {
            redisTemplate.opsForZSet().remove("user:subscribe:" + userName, subscribeUserName);
            return true;
        }
        return false;
    }

    @Override
    public void storeUserTag(String userName, String tag) {
        redisTemplate.execute(new SessionCallback<Object>() {
            @Override
            public Object execute(RedisOperations operations) {
                operations.multi();
                String string = (String) redisTemplate.opsForValue().get("user:tag:" + userName);
                if (string != null) {
                    if(string.length() == 1000) {
                        int length = tag.length();
                        String res = tag + string.substring(length);
                        redisTemplate.opsForValue().set("user:tag:" + userName, res, 3, TimeUnit.DAYS);
                    }
                    else redisTemplate.opsForValue().set("user:tag:" + userName, string + tag, 3, TimeUnit.DAYS);
                }
                return operations.exec();
            }
        });
    }

    @Override
    public String getUserTag(String userName) {
        return (String) redisTemplate.opsForValue().get("user:tag:" + userName);
    }
}
