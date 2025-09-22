package org.example.tiktokproject;

import co.elastic.clients.elasticsearch.transform.Settings;
import jakarta.annotation.Resource;
import org.elasticsearch.client.RequestOptions;
import org.example.tiktokproject.mapper.LoginAndRegisterMapper;
import org.example.tiktokproject.mapper.VideoMapper;
import org.example.tiktokproject.pojo.ESUser;
import org.example.tiktokproject.pojo.MySQLVideo;
import org.example.tiktokproject.pojo.NormalUser;
import org.example.tiktokproject.pojo.Video;
import org.example.tiktokproject.repository.LiveESRepository;
import org.example.tiktokproject.repository.UserESRepository;
import org.example.tiktokproject.repository.VideoRedis;
import org.example.tiktokproject.repository.VideoRepository;
import org.junit.jupiter.api.Test;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.redis.core.RedisTemplate;

import java.time.*;
import java.time.format.DateTimeFormatter;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

@SpringBootTest
class TiktokProjectApplicationTests {

    @Resource
    private LoginAndRegisterMapper loginAndRegisterMapper;

    @Resource
    private UserESRepository userESRepository;

    @Resource
    private VideoRepository videoRepository;

    @Resource
    private VideoMapper videoMapper;

    @Resource
    private VideoRedis videoRedis;

    @Resource
    private LiveESRepository liveESRepository;

    @Test
    void contextLoads() throws InterruptedException {
    }

}
