package org.example.tiktokproject.mapper;

import org.apache.ibatis.annotations.Mapper;
import org.example.tiktokproject.pojo.MySQLVideo;
import org.example.tiktokproject.pojo.NormalUser;
import org.example.tiktokproject.pojo.Video;

import java.time.LocalDateTime;
import java.util.List;

@Mapper
public interface VideoMapper {
    public void insertVideo(MySQLVideo video);
    public void updateVideoLikeCount(String url);
    public void updateVideoLikeCountDecr(String url);
    public List<MySQLVideo> selectVideo();
    public List<NormalUser> selectUser();
}
