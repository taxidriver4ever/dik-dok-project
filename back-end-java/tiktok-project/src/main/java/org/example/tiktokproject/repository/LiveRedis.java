package org.example.tiktokproject.repository;

import org.example.tiktokproject.pojo.Live;

import java.util.List;
import java.util.Set;
import java.util.concurrent.CopyOnWriteArrayList;

public interface LiveRedis {
    boolean existLive(String author);
    void createLive(String author,String title,String url,String coverUrl);
    void deleteLive(String author);
    String findLiveCoverUrl(String author);
    Live getLiveData(String author);
    void deleteLiving(String author);
    List<Live> getAllLives();
}
