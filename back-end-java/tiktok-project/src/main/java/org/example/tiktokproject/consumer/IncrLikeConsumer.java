package org.example.tiktokproject.consumer;

import com.rabbitmq.client.Channel;
import jakarta.annotation.Resource;
import org.example.tiktokproject.AOP.MyLog;
import org.example.tiktokproject.mapper.VideoMapper;
import org.example.tiktokproject.pojo.NameAndUrl;
import org.example.tiktokproject.pojo.Video;
import org.example.tiktokproject.repository.LoginAndRegisterRedis;
import org.example.tiktokproject.repository.VideoRedis;
import org.example.tiktokproject.repository.VideoRepository;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.amqp.rabbit.annotation.RabbitHandler;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.amqp.support.AmqpHeaders;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.elasticsearch.core.ElasticsearchOperations;
import org.springframework.data.elasticsearch.core.mapping.IndexCoordinates;
import org.springframework.data.elasticsearch.core.query.*;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.TimeUnit;

@Component
@RabbitListener(queues = "direct.incrLike.queue")
public class IncrLikeConsumer {

    @Resource
    private VideoRepository videoRepository;

    @Resource
    private VideoMapper videoMapper;

    @Resource
    private RedissonClient redisson;

    @Resource
    private LoginAndRegisterRedis loginAndRegisterRedis;

    @MyLog
    @Transactional(rollbackFor = Exception.class)
    @RabbitHandler
    public void receiveIncrMessage(NameAndUrl nameAndUrl) throws IOException, InterruptedException {
        RLock lock = redisson.getLock("like" + nameAndUrl.getUrl());
        boolean b = lock.tryLock(5, TimeUnit.SECONDS);
        if(b) {
            try {
                String url = nameAndUrl.getUrl();
                videoMapper.updateVideoLikeCount(url);
                // 2. 使用 Repository 查询并更新
                List<Video> videoOptional = videoRepository.findByUrl(url);
                if (!videoOptional.isEmpty()) {
                    String name = nameAndUrl.getName();
                    Video first = videoOptional.getFirst();
                    loginAndRegisterRedis.storeUserTag(name,first.getDescription());
                    videoRepository.deleteById(first.getId());
                    first.setLikeCount(first.getLikeCount() + 1);
                    videoRepository.save(first);
                }

            } catch (Exception e) {
                throw new RuntimeException("消息拒绝失败", e);
            } finally {
                lock.unlock();
            }
        }
    }
}
