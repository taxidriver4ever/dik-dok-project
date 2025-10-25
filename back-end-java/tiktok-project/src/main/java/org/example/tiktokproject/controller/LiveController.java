package org.example.tiktokproject.controller;

import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.example.tiktokproject.AOP.MyLog;
import org.example.tiktokproject.pojo.*;
import org.example.tiktokproject.service.LiveService;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

@Slf4j
@CrossOrigin
@RestController
@RequestMapping("/live")
public class LiveController {

    @Resource
    private LiveService liveService;

    @PostMapping("/live-data")
    public NormalResult startToLive(@RequestParam String author,
                                    @RequestParam String url,
                                    @RequestParam String title,
                                    @RequestParam(value = "coverImage", required = false) MultipartFile coverImage) throws IOException {
        if(liveService.startToLive(author,url,title,coverImage)) return NormalResult.success(new UrlAndTitleForLive(url,title));
        return NormalResult.error("Start to live failed");
    }

    @DeleteMapping("/live-data")
    public NormalResult deleteToLive(@RequestParam String author) {
        if(liveService.deleteToLive(author)) return NormalResult.success();
        return NormalResult.error("Start to live failed");
    }

    @PostMapping("/initial-data")
    public NormalResult getLiveData(@RequestParam String author) {
        Live initialLiveData = liveService.getInitialLiveData(author);
        if(initialLiveData != null) return NormalResult.success(initialLiveData);
        return NormalResult.error("Start to live failed");
    }

    @PostMapping("/living-all-data")
    public NormalResult getLivingData(@RequestBody NormalUser normalUser) {
        List<Live> liveData = liveService.getLiveData(normalUser);
        if(!liveData.isEmpty()) return NormalResult.success(liveData);
        return NormalResult.error("Select living failed");
    }

    @PostMapping("/data")
    public NormalResult selectLiveData(@RequestBody KeywordAndName keywordAndName) {
        List<LiveForES> liveForES = liveService.selectLiveDataByKeyword(keywordAndName);
        if(!liveForES.isEmpty()) return NormalResult.success(liveForES);
        return NormalResult.error("Select living By keyword failed");
    }
}
