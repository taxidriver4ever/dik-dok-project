package org.example.tiktokproject.repository;

public interface LiveRedis {
    boolean existLive(String author);
    void createLive(String author);
    void deleteLive(String author);
}
