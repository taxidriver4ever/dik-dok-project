package org.example.tiktokproject.service;

import org.example.tiktokproject.pojo.KeywordAndName;
import org.example.tiktokproject.pojo.NameAndUrl;
import org.example.tiktokproject.pojo.StatusAndLikeTimes;
import org.example.tiktokproject.pojo.Video;

import java.util.List;

public interface VideoService {
    List<Video> getVideos();
    List<Video> searchTitle(KeywordAndName keyword);
    List<Video> searchVideo(KeywordAndName keyword);
    boolean clickLike(NameAndUrl nameAndUrl);
    boolean cancelLike(NameAndUrl nameAndUrl);
    StatusAndLikeTimes getLikeStatus(NameAndUrl nameAndUrl);
}
