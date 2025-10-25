package org.example.tiktokproject.repository;

import org.example.tiktokproject.pojo.ESUser;
import org.example.tiktokproject.pojo.LiveForES;
import org.example.tiktokproject.pojo.Video;
import org.springframework.data.elasticsearch.annotations.Query;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface LiveESRepository extends ElasticsearchRepository<LiveForES, String> {
    void deleteByAuthor(String author);
    @Query("""
        {
          "match": {
            "type": "?0"
          }
        }
        """)
    List<LiveForES> findByType(String type);
    @Query("""
        {
          "match": {
            "title": "?0"
          }
        }
        """)
    List<LiveForES> findByTitle(String title);
}
