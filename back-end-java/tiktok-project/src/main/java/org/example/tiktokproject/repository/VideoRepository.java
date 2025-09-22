package org.example.tiktokproject.repository;

import org.example.tiktokproject.pojo.Video;
import org.springframework.data.elasticsearch.annotations.Query;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;

import java.util.List;
import java.util.Optional;

public interface VideoRepository extends ElasticsearchRepository<Video, String> {
    List<Video> findByTitle(String title);
    List<Video> findByAuthor(String author);
    List<Video> findByTitleAndAuthor(String title, String author);
    List<Video> findByTitleContaining(String title);
    List<Video> findByAuthorContaining(String author);
    List<Video> findByTitleContainingAndAuthorContaining(String title, String author);
    List<Video> findByAuthorContainingAndTitleContaining(String author, String title);
    Video findByUrlContaining(String url);
    void deleteById(String id);

    @Query("""
        {
          "match": {
            "description": "?0"
          }
        }
        """)
    List<Video> findByDescriptionContaining(String description);

    List<Video> findByUrl(String url);
}
