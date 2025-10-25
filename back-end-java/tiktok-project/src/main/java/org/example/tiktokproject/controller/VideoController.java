package org.example.tiktokproject.controller;

import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.example.tiktokproject.AOP.MyLog;
import org.example.tiktokproject.pojo.*;
import org.example.tiktokproject.service.SendPhotoService;
import org.example.tiktokproject.service.VideoService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@CrossOrigin
@RestController
@RequestMapping("/video")
public class VideoController {

    @Resource
    private SendPhotoService sendPhotoService;

    @Resource
    private VideoService videoService;

    @PostMapping("/urls")
    public NormalResult sendUrl(@RequestBody PhotoAndVideo urls) {
        sendPhotoService.SendToPython(urls);
        return NormalResult.success();
    }

    @PostMapping("/random")
    public NormalResult getVideo(@RequestBody NormalUser normalUser) {
        List<Video> videos = videoService.getVideos(normalUser);
        return NormalResult.success(videos);
    }

    @PostMapping("/nothing")
    public NormalResult nothing() {
        return NormalResult.success();
    }

    @PostMapping("/title")
    public NormalResult searchTitle(@RequestBody KeywordAndName keyword) {
        List<Video> videos = videoService.searchTitle(keyword);
        return NormalResult.success(videos);
    }

    @PostMapping("/data")
    public NormalResult searchVideo(@RequestBody KeywordAndName keyword) {
        List<Video> videos = videoService.searchVideo(keyword);
        return NormalResult.success(videos);
    }

    @PostMapping("/click-like")
    public NormalResult clickLike(@RequestBody NameAndUrl nameAndUrl) {
        boolean b = videoService.clickLike(nameAndUrl);
        if (b) return NormalResult.success();
        return NormalResult.error("Can't like this video");
    }

    @PostMapping("/cancel-like")
    public NormalResult cancelLike(@RequestBody NameAndUrl nameAndUrl) {
        boolean b = videoService.cancelLike(nameAndUrl);
        if (b) return NormalResult.success();
        return NormalResult.error("Can't cancel like this video");
    }

    @PostMapping("/status")
    public NormalResult getLikeStatus(@RequestBody NameAndUrl nameAndUrl) {
        return NormalResult.success(
                videoService.getLikeStatus(nameAndUrl)
        );
    }

    @MyLog
    @PostMapping("/url-tag")
    public NormalResult getVideoUrlTag(@RequestParam String url,@RequestParam String userName) {
        videoService.getVideoUrlTag(userName,url);
        return NormalResult.success();
    }
}
