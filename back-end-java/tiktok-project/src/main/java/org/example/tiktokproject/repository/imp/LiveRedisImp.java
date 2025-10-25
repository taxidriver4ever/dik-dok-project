package org.example.tiktokproject.repository.imp;

import jakarta.annotation.Resource;
import org.apache.juli.logging.Log;
import org.apache.juli.logging.LogFactory;
import org.example.tiktokproject.pojo.Live;
import org.example.tiktokproject.repository.LiveRedis;
import org.springframework.dao.DataAccessException;
import org.springframework.data.redis.core.RedisOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.SessionCallback;
import org.springframework.stereotype.Repository;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.CopyOnWriteArraySet;

@Repository
public class LiveRedisImp implements LiveRedis {

    @Resource
    private RedisTemplate<String,String> redisTemplate;

    private final static Log log = LogFactory.getLog(LiveRedisImp.class);

    @Override
    public boolean existLive(String author) {
        return redisTemplate.hasKey("live:author:" + author);
    }

    @Override
    public void createLive(String author,String title,String url,String coverUrl) {
        redisTemplate.execute(new SessionCallback<>() {
            @Override
            public Object execute(RedisOperations operations) throws DataAccessException {
                // 1. 开启事务
                operations.multi();

                long l = System.currentTimeMillis();
                // 2. 将多个命令放入队列
                operations.opsForHash().put("live:author:" + author, "url" ,url);
                operations.opsForHash().put("live:author:" + author, "title" ,title);
                operations.opsForHash().put("live:author:" + author, "coverUrl" ,coverUrl);
                operations.opsForHash().put("live:author:" + author, "createdTime" ,l);
                operations.opsForZSet().add("living", author ,l);

                // 3. 执行事务（EXEC命令）
                // 如果所有命令成功，则全部生效；如果任何一个失败，则全部回滚
                return operations.exec();
            }
        });
    }

    @Override
    public void deleteLive(String author) {
        redisTemplate.delete("live:author:" + author);
    }

    @Override
    public String findLiveCoverUrl(String author) {
        return (String) redisTemplate.opsForHash().get("live:author:" + author, "coverUrl");
    }

    @Override
    public Live getLiveData(String author) {
        Live live = new Live();
        String title = (String)redisTemplate.opsForHash().get("live:author:" + author, "title");
        String url = (String)redisTemplate.opsForHash().get("live:author:" + author, "url");
        if(title != null && url != null) {
            live.setTitle(title);
            live.setUrl(url);
            live.setAuthor(author);
            return live;
        }
        return null;
    }

    @Override
    public void deleteLiving(String author) {
        redisTemplate.opsForZSet().remove("living", author);
    }

    @Override
    public List<Live> getAllLives() {
        try {
            Set<String> living = redisTemplate.opsForZSet().reverseRange("living", 0, -1);
            if (living == null || living.isEmpty()) {
                return new CopyOnWriteArrayList<>(); // 返回空集合而不是null
            }

            CopyOnWriteArrayList<Live> liveList = new CopyOnWriteArrayList<>();

            for (String author : living) {
                String hashKey = "live:author:" + author;

                // 批量获取hash字段，减少网络请求
                List<Object> hashValues = redisTemplate.opsForHash().multiGet(hashKey,
                        Arrays.asList("url", "title", "coverUrl", "createdTime", "type"));

                if (hashValues == null || hashValues.size() != 5) {
                    continue; // 跳过数据不完整的记录
                }

                String url = (String) hashValues.get(0);
                String title = (String) hashValues.get(1);
                String coverUrl = (String) hashValues.get(2);
                Long createdTime = (Long) hashValues.get(3);
                String type = (String) hashValues.get(4);

                log.info(url + " " + title + " " + coverUrl + " " + createdTime + " " + type);
                // 空值检查
                if (url == null || title == null || createdTime == null || type == null) {
                    continue;
                }

                try {
                    Live live = new Live();
                    live.setAuthor(author);
                    live.setTitle(title);
                    live.setUrl(url);
                    live.setCoverUrl(coverUrl); // coverUrl允许为null
                    live.setType(type);

                    // 正确的时间戳转换
                    live.setCreatedTime(LocalDateTime.ofInstant(
                            Instant.ofEpochSecond(createdTime),
                            ZoneId.systemDefault()
                    ));
                    log.info(live);
                    liveList.add(live);
                } catch (NumberFormatException e) {
                    // 时间戳格式错误，跳过该记录
                    log.warn("Invalid timestamp format for author");
                    continue;
                }
            }

            return liveList;

        } catch (Exception e) {
            log.error("Error retrieving live data from Redis", e);
            return new CopyOnWriteArrayList<>(); // 异常时返回空集合
        }
    }


}
