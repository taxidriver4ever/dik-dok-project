package org.example.tiktokproject.controller;

import jakarta.annotation.Resource;
import org.example.tiktokproject.AOP.MyLog;
import org.example.tiktokproject.pojo.NormalResult;
import org.example.tiktokproject.pojo.UrlAndTitleForLive;
import org.example.tiktokproject.service.LiveService;
import org.springframework.web.bind.annotation.*;

@CrossOrigin
@RestController
@RequestMapping("/live")
public class LiveController {

    @Resource
    private LiveService liveService;

    @PostMapping("/live-data")
    public NormalResult startToLive(@RequestParam String author,@RequestParam String url,@RequestParam String title) {
        if(liveService.startToLive(author,url,title)) return NormalResult.success(new UrlAndTitleForLive(url,title));
        return NormalResult.error("Start to live failed");
    }

    @DeleteMapping("/live-data")
    public NormalResult deleteToLive(@RequestParam String author) {
        if(liveService.deleteToLive(author)) return NormalResult.success();
        return NormalResult.error("Start to live failed");
    }
}
