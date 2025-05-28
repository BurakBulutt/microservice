package com.example.servicemedia.domain.fansub.elasticsearch.repo;

import com.example.servicemedia.domain.fansub.elasticsearch.model.ElasticFansub;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;

public interface ElasticFansubRepository extends ElasticsearchRepository<ElasticFansub, String> {
}
