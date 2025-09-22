package org.example.tiktokproject.repository;

import org.example.tiktokproject.pojo.NameAndUrl;

public interface VideoRedis {
    boolean incrLike(NameAndUrl nameAndUrl);
    boolean decrLike(NameAndUrl nameAndUrl);
    void setInformation(String url) throws InterruptedException;
    boolean existUserLike(NameAndUrl nameAndUrl);
    Integer getUserLike(String url);
}
