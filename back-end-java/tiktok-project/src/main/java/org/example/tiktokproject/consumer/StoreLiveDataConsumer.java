package org.example.tiktokproject.consumer;

import jakarta.annotation.Resource;
import org.example.tiktokproject.AOP.MyLog;
import org.example.tiktokproject.mapper.LiveMapper;
import org.example.tiktokproject.pojo.LiveForES;
import org.example.tiktokproject.repository.LiveESRepository;
import org.springframework.amqp.rabbit.annotation.RabbitHandler;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
@RabbitListener(queues = "direct.live.queue")
public class StoreLiveDataConsumer {

    @Resource
    private LiveMapper liveMapper;

    @Resource
    private LiveESRepository liveESRepository;

    @MyLog
    @Transactional(rollbackFor = Exception.class)
    @RabbitHandler
    public void startLiveProcess(LiveForES liveForES) {
        if(liveForES != null) {
            try {
                liveMapper.insertLive(liveForES.getAuthor(), liveForES.getUrl(),liveForES.getTitle());
                liveESRepository.save(liveForES);
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }
    }
}
