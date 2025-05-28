package com.example.servicemedia.domain.content.elasticsearch.repo;

import com.example.servicemedia.domain.content.elasticsearch.model.ElasticContent;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;

public interface ElasticContentRepository extends ElasticsearchRepository<ElasticContent, String> {
}
