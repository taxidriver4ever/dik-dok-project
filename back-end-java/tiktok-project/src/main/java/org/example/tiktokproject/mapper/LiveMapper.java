package org.example.tiktokproject.mapper;

import org.apache.ibatis.annotations.Mapper;
import org.example.tiktokproject.pojo.Live;

import java.util.List;

@Mapper
public interface LiveMapper {
    void insertLive(String author,String url,String title);
    void deleteLive(String author);
    List<Integer> selectLiveByAuthor(String author);
}
