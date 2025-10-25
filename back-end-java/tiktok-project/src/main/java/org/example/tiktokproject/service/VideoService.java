package org.example.tiktokproject.service;

import org.example.tiktokproject.pojo.*;

import java.util.List;

public interface VideoService {
    List<Video> getVideos(NormalUser normalUser);
    List<Video> searchTitle(KeywordAndName keyword);
    List<Video> searchVideo(KeywordAndName keyword);
    boolean clickLike(NameAndUrl nameAndUrl);
    boolean cancelLike(NameAndUrl nameAndUrl);
    StatusAndLikeTimes getLikeStatus(NameAndUrl nameAndUrl);
    void getVideoUrlTag(String userName,String url);
}
