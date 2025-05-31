package com.example.servicemedia.elasticsearch.batch.xml;

import com.example.servicemedia.domain.xml.elasticsearch.model.ElasticXmlDefinition;
import com.example.servicemedia.domain.xml.elasticsearch.repo.ElasticXmlDefinitionRepository;
import com.example.servicemedia.domain.xml.model.XmlDefinition;
import com.example.servicemedia.elasticsearch.ElasticEntityMapper;
import jakarta.persistence.EntityManagerFactory;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.batch.item.ItemReader;
import org.springframework.batch.item.ItemWriter;
import org.springframework.batch.item.database.builder.JpaPagingItemReaderBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.elasticsearch.core.RefreshPolicy;
import org.springframework.transaction.PlatformTransactionManager;

@Configuration
@RequiredArgsConstructor
@Slf4j
public class XmlDefinitionStepConfig {
    private static final int CHUNK_SIZE = 10;

    private final JobRepository jobRepository;
    private final PlatformTransactionManager platformTransactionManager;

    @Bean("elasticXmlDefinitionDataStep")
    public Step elasticXmlDefinitionDataStep() {
        return new StepBuilder("elasticXmlDefinitionDataStep", jobRepository)
                .<XmlDefinition, ElasticXmlDefinition>chunk(CHUNK_SIZE, platformTransactionManager)
                .reader(xmlDefinitionItemReader(null))
                .processor(xmlDefinitionItemProcessor())
                .writer(xmlDefinitionItemWriter(null))
                .build();
    }

    @Bean("xmlDefinitionItemReader")
    public ItemReader<XmlDefinition> xmlDefinitionItemReader(EntityManagerFactory entityManagerFactory) {
        return new JpaPagingItemReaderBuilder<XmlDefinition>()
                .name("xmlDefinitionItemReader")
                .entityManagerFactory(entityManagerFactory)
                .pageSize(CHUNK_SIZE)
                .queryString("select xml from XmlDefinition xml")
                .build();
    }

    @Bean("xmlDefinitionItemProcessor")
    public ItemProcessor<XmlDefinition, ElasticXmlDefinition> xmlDefinitionItemProcessor() {
        return ElasticEntityMapper::toElasticXmlDefinition;
    }

    @Bean("xmlDefinitionItemWriter")
    public ItemWriter<ElasticXmlDefinition> xmlDefinitionItemWriter(ElasticXmlDefinitionRepository repository) {
        return (elasticXmlDefinitions) -> repository.saveAll(elasticXmlDefinitions, RefreshPolicy.IMMEDIATE);
    }
}
