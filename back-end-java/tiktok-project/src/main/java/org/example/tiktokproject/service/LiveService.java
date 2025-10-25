package org.example.tiktokproject.service;


import org.apache.ibatis.javassist.compiler.ast.Keyword;
import org.example.tiktokproject.pojo.KeywordAndName;
import org.example.tiktokproject.pojo.Live;
import org.example.tiktokproject.pojo.LiveForES;
import org.example.tiktokproject.pojo.NormalUser;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;
import java.util.Set;

public interface LiveService {
    boolean startToLive(String author, String url, String title, MultipartFile file) throws IOException;
    boolean deleteToLive(String author);
    Live getInitialLiveData(String author);
    List<Live> getLiveData(NormalUser user);
    List<LiveForES> selectLiveDataByKeyword(KeywordAndName keyword);
}
