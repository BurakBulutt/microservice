package com.example.servicemedia.domain.xml.elasticsearch.repo;

import com.example.servicemedia.domain.xml.elasticsearch.model.ElasticXmlDefinition;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;

public interface ElasticXmlDefinitionRepository extends ElasticsearchRepository<ElasticXmlDefinition, String> {
}
