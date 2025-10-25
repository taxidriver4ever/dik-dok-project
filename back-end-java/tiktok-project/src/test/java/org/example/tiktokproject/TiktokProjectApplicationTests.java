package org.example.tiktokproject;

import co.elastic.clients.elasticsearch.transform.Settings;
import com.aliyun.oss.OSS;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.annotation.Resource;
import org.elasticsearch.client.RequestOptions;
import org.example.tiktokproject.mapper.LoginAndRegisterMapper;
import org.example.tiktokproject.mapper.VideoMapper;
import org.example.tiktokproject.pojo.*;
import org.example.tiktokproject.repository.LiveESRepository;
import org.example.tiktokproject.repository.UserESRepository;
import org.example.tiktokproject.repository.VideoRedis;
import org.example.tiktokproject.repository.VideoRepository;
import org.example.tiktokproject.util.OssUtils;
import org.junit.jupiter.api.Test;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.http.*;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.multipart.MultipartFile;

import java.time.*;
import java.time.format.DateTimeFormatter;
import java.util.Collection;
import java.util.List;
import java.util.Set;
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

    @Resource
    private ObjectMapper objectMapper;

    @Resource
    private RestTemplate restTemplate;

    @Resource
    private RedisTemplate<String, String> redisTemplate;

    @Test
    void contextLoads() throws InterruptedException, JsonProcessingException {
    }

}
