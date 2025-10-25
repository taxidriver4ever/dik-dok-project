package org.example.tiktokproject.service.imp;

import com.aliyun.oss.OSS;
import com.aliyun.oss.OSSClientBuilder;
import com.aliyun.oss.model.PutObjectResult;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.example.tiktokproject.mapper.LiveMapper;
import org.example.tiktokproject.pojo.*;
import org.example.tiktokproject.repository.LiveESRepository;
import org.example.tiktokproject.repository.LiveRedis;
import org.example.tiktokproject.service.LiveService;
import org.example.tiktokproject.util.OssUtils;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.Set;
import java.util.UUID;


@Slf4j
@Service
public class LiveServiceImp implements LiveService {

    @Resource
    private LiveMapper liveMapper;

    @Resource
    private LiveESRepository liveESRepository;

    @Resource
    private RabbitTemplate rabbitTemplate;

    @Resource
    private LiveRedis liveRedis;

    @Value("${aliyun.oss.bucket}")
    private String bucketName;

    @Resource
    private OSS ossClient;

    @Override
    public boolean startToLive(String author, String url, String title, MultipartFile file) throws IOException {
        if(liveRedis.existLive(author)) return false;

        LiveForES live = new LiveForES();
        live.setTitle(title);
        live.setUrl(url);
        live.setAuthor(author);
        live.setCreatedTimeMilli(System.currentTimeMillis());
        live.setUpdatedTimeMilli(System.currentTimeMillis());

        String coverUrl = "";
        if(!file.isEmpty()) {
            String fileName = author + UUID.randomUUID() + file.getOriginalFilename();
            // 使用 try-with-resources 自动关闭流
            try (InputStream inputStream = file.getInputStream()) {
                ossClient.putObject(bucketName, fileName, inputStream);
                coverUrl = "https://" + bucketName + ".oss-cn-guangzhou.aliyuncs.com/" + fileName;
            } // 这里会自动调用 inputStream.close()
        }
        live.setCoverUrl(coverUrl);
        liveRedis.createLive(author,title,url,coverUrl);
        rabbitTemplate.convertAndSend("direct.exchange.live", "store_live_data", live);
        return true;
    }

    @Override
    public boolean deleteToLive(String author) {
        if(!liveRedis.existLive(author)) return false;
        String liveCoverUrl = liveRedis.findLiveCoverUrl(author);
        String prefix = "https://" + bucketName + ".oss-cn-guangzhou.aliyuncs.com/";
        ossClient.deleteObject(bucketName, liveCoverUrl.substring(prefix.length()));
        liveRedis.deleteLive(author);
        liveRedis.deleteLiving(author);
        rabbitTemplate.convertAndSend("direct.exchange.liveDelete", "delete_live_data", author);
        return true;
    }

    @Override
    public Live getInitialLiveData(String author) {
        Live live = liveRedis.getLiveData(author);
        log.info("getInitialLiveData:{}",live);
        return live;
    }

    @Override
    public List<Live> getLiveData(NormalUser user) {
        return liveRedis.getAllLives();
    }

    @Override
    public List<LiveForES> selectLiveDataByKeyword(KeywordAndName keyword) {
        List<LiveForES> byTitle = liveESRepository.findByTitle(keyword.getKeyword());
        List<LiveForES> byType = liveESRepository.findByType(keyword.getKeyword());
        byTitle.addAll(byType);
        return byTitle;
    }
}
