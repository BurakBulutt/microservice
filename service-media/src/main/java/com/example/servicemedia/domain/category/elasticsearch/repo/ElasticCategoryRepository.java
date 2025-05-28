package com.example.servicemedia.domain.category.elasticsearch.repo;

import com.example.servicemedia.domain.category.elasticsearch.model.ElasticCategory;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;

public interface ElasticCategoryRepository extends ElasticsearchRepository<ElasticCategory, String> {
}
