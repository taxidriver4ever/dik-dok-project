package org.example.tiktokproject.service.imp;

import jakarta.annotation.Resource;
import org.example.tiktokproject.mapper.LiveMapper;
import org.example.tiktokproject.pojo.Live;
import org.example.tiktokproject.pojo.LiveForES;
import org.example.tiktokproject.repository.LiveRedis;
import org.example.tiktokproject.service.LiveService;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


@Service
public class LiveServiceImp implements LiveService {

    @Resource
    private LiveMapper liveMapper;

    @Resource
    private RabbitTemplate rabbitTemplate;

    @Resource
    private LiveRedis liveRedis;

    @Override
    public boolean startToLive(String author, String url, String title) {
        if(liveRedis.existLive(author)) return false;
        liveRedis.createLive(author);
        LiveForES live = new LiveForES();
        live.setTitle(title);
        live.setUrl(url);
        live.setAuthor(author);
        live.setCreatedTimeMilli(System.currentTimeMillis());
        live.setUpdatedTimeMilli(System.currentTimeMillis());
        rabbitTemplate.convertAndSend("direct.exchange.live", "store_live_data", live);
        return true;
    }

    @Override
    public boolean deleteToLive(String author) {
        if(!liveRedis.existLive(author)) return false;
        liveRedis.deleteLive(author);
        rabbitTemplate.convertAndSend("direct.exchange.liveDelete", "delete_live_data", author);
        return true;
    }
}
