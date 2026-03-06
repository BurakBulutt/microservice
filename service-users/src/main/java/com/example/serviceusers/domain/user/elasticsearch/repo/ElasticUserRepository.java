package com.example.serviceusers.domain.user.elasticsearch.repo;

import com.example.serviceusers.domain.user.elasticsearch.model.ElasticUser;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;

public interface ElasticUserRepository extends ElasticsearchRepository<ElasticUser, String> {
}
