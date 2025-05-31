package com.example.servicereaction.domain.comment.elasticsearch.repo;

import com.example.servicereaction.domain.comment.elasticsearch.model.ElasticComment;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;

public interface ElasticCommentRepository extends ElasticsearchRepository<ElasticComment, String> {
}
