package org.example.tiktokproject.controller;

import jakarta.annotation.Resource;
import org.example.tiktokproject.pojo.*;
import org.example.tiktokproject.service.SendPhotoService;
import org.example.tiktokproject.service.VideoService;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

@CrossOrigin
@RestController
@RequestMapping("/video")
public class VideoGetController {

    @Resource
    private SendPhotoService sendPhotoService;

    @Resource
    private VideoService videoService;

    @PostMapping("/SendUrl")
    public NormalResult SendUrl(@RequestBody PhotoAndVideo urls) {
        sendPhotoService.SendToPython(urls);
        return NormalResult.success();
    }

    @PostMapping("/GetVideo")
    public NormalResult GetVideo(@RequestBody NormalUser normalUser) {
        List<Video> videos = videoService.getVideos();
        return NormalResult.success(videos);
    }

    @PostMapping("/nothing")
    public NormalResult nothing() {
        return NormalResult.success();
    }

    @PostMapping("/searchTitle")
    public NormalResult searchTitle(@RequestBody KeywordAndName keyword) {
        List<Video> videos = videoService.searchTitle(keyword);
        System.out.println(videos);
        return NormalResult.success(videos);
    }

    @PostMapping("/searchVideo")
    public NormalResult searchVideo(@RequestBody KeywordAndName keyword) {
        List<Video> videos = videoService.searchVideo(keyword);
        System.out.println(videos);
        return NormalResult.success(videos);
    }

    @PostMapping("/clickLike")
    public NormalResult clickLike(@RequestBody NameAndUrl nameAndUrl) {
        boolean b = videoService.clickLike(nameAndUrl);
        if (b) return NormalResult.success();
        return NormalResult.error("Can't like this video");
    }

    @PostMapping("/cancelLike")
    public NormalResult cancelLike(@RequestBody NameAndUrl nameAndUrl) {
        boolean b = videoService.cancelLike(nameAndUrl);
        if (b) return NormalResult.success();
        return NormalResult.error("Can't cancel like this video");
    }

    @PostMapping("/getLikeStatus")
    public NormalResult getLikeStatus(@RequestBody NameAndUrl nameAndUrl) {
        return NormalResult.success(
                videoService.getLikeStatus(nameAndUrl)
        );
    }
}
