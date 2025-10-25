package org.example.tiktokproject.repository;

import org.example.tiktokproject.pojo.NameAndUrl;

import java.util.Set;

public interface VideoRedis {
    boolean incrLike(NameAndUrl nameAndUrl);
    boolean decrLike(NameAndUrl nameAndUrl);
    void setInformation(String url) throws InterruptedException;
    boolean existUserLike(NameAndUrl nameAndUrl);
    Integer getUserLike(String url);
    Set<String> getUserLikeVideo(String userName);
}
