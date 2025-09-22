package org.example.tiktokproject.service;


public interface LiveService {
    boolean startToLive(String author,String url,String title);
    boolean deleteToLive(String author);
}
