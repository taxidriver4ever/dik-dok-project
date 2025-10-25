package org.example.tiktokproject.consumer;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.annotation.Resource;
import org.apache.juli.logging.Log;
import org.apache.juli.logging.LogFactory;
import org.example.tiktokproject.AOP.MyLog;
import org.example.tiktokproject.mapper.LiveMapper;
import org.example.tiktokproject.pojo.DetectionResponse;
import org.example.tiktokproject.pojo.LiveForES;
import org.example.tiktokproject.pojo.RecognitionRequest;
import org.example.tiktokproject.repository.LiveESRepository;
import org.springframework.amqp.rabbit.annotation.RabbitHandler;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.http.*;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestTemplate;

@Component
@RabbitListener(queues = "direct.live.queue")
public class StoreLiveDataConsumer {

    @Resource
    private LiveMapper liveMapper;

    @Resource
    private LiveESRepository liveESRepository;

    @Resource
    private RestTemplate restTemplate;

    private static final Log log = LogFactory.getLog(StoreLiveDataConsumer.class);

    @Resource
    private ObjectMapper objectMapper;

    @Resource
    private RedisTemplate<String,String> redisTemplate;

    @MyLog
    @Transactional(rollbackFor = Exception.class)
    @RabbitHandler
    public void startLiveProcess(LiveForES liveForES) {
        if(liveForES != null) {
            try {
                //配置发送的数据请求体
                RecognitionRequest request = new RecognitionRequest();
                request.setImage_url(liveForES.getCoverUrl());
                request.setTop_k("3");
                request.setTimeout("5");
                String requestBody = objectMapper.writeValueAsString(request);

                HttpHeaders headers = new HttpHeaders();
                headers.setContentType(MediaType.APPLICATION_JSON);
                HttpEntity<String> requestEntity = new HttpEntity<>(requestBody, headers);

                ResponseEntity<String> response = restTemplate.exchange(
                        "http://localhost:8000/api/VideoRecognition",
                        HttpMethod.POST,
                        requestEntity,
                        String.class
                );

                //如果python代码鉴定成功才存在数据库
                if (response.getStatusCode() == HttpStatus.OK) {
                    log.debug("Successfully received response from Python service");
                    log.info(response.getBody());

                    StringBuilder type = new StringBuilder();
                    JsonNode jsonNode = objectMapper.readTree(response.getBody());
                    for (int i = 0; i < 3; i++)
                        type.append(jsonNode.get("predictions").get(i).get("object_zh"));
                    liveForES.setType(type.toString());

                    redisTemplate.opsForHash().put("live:author:"+ liveForES.getAuthor(),"type",liveForES.getType());
                    liveMapper.insertLive(liveForES.getAuthor(), liveForES.getUrl(),liveForES.getTitle(),liveForES.getCoverUrl(),liveForES.getType());
                    liveESRepository.save(liveForES);
                } else {
                    throw new RuntimeException("Python service returned status: " + response.getStatusCode());
                }
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }
    }
}
