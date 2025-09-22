package org.example.tiktokproject.consumer;


import jakarta.annotation.Resource;
import org.example.tiktokproject.AOP.MyLog;
import org.example.tiktokproject.mapper.LiveMapper;
import org.example.tiktokproject.repository.LiveESRepository;
import org.springframework.amqp.rabbit.annotation.RabbitHandler;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
@RabbitListener(queues = "direct.liveDelete.queue")
public class DeleteLiveDataConsumer {

    @Resource
    private LiveMapper liveMapper;

    @Resource
    private LiveESRepository liveESRepository;

    @MyLog
    @Transactional(rollbackFor = Exception.class)
    @RabbitHandler
    public void stopLiveProcess(String author) {
        if(author != null) {
            try {
                liveMapper.deleteLive(author);
                liveESRepository.deleteByAuthor(author);
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }
    }

}
