package org.example.tiktokproject.repository;

import org.example.tiktokproject.pojo.ESUser;
import org.example.tiktokproject.pojo.LiveForES;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface LiveESRepository extends ElasticsearchRepository<LiveForES, String> {
    void deleteByAuthor(String author);
}
