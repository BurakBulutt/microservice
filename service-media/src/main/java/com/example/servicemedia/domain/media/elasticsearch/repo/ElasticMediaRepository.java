package com.example.servicemedia.domain.media.elasticsearch.repo;

import com.example.servicemedia.domain.media.elasticsearch.model.ElasticMedia;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;

public interface ElasticMediaRepository extends ElasticsearchRepository<ElasticMedia, String> {
}
