package com.example.servicemedia.elasticsearch.batch.content;

import com.example.servicemedia.domain.category.model.Category;
import com.example.servicemedia.domain.content.elasticsearch.model.ElasticContent;
import com.example.servicemedia.domain.content.elasticsearch.repo.ElasticContentRepository;
import com.example.servicemedia.domain.content.model.Content;
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

import java.time.ZoneOffset;

@Configuration
@RequiredArgsConstructor
@Slf4j
public class ContentStepConfig {
    private static final int CHUNK_SIZE = 10;

    private final JobRepository jobRepository;
    private final PlatformTransactionManager platformTransactionManager;

    @Bean("elasticContentDataStep")
    public Step elasticContentDataStep() {
        return new StepBuilder("elasticContentDataStep", jobRepository)
                .<Content, ElasticContent>chunk(CHUNK_SIZE, platformTransactionManager)
                .reader(contentItemReader(null))
                .processor(contentItemProcessor())
                .writer(contentItemWriter(null))
                .build();
    }

    @Bean("contentItemReader")
    public ItemReader<Content> contentItemReader(EntityManagerFactory entityManagerFactory) {
        return new JpaPagingItemReaderBuilder<Content>()
                .name("contentItemReader")
                .entityManagerFactory(entityManagerFactory)
                .pageSize(CHUNK_SIZE)
                .queryString("select c from Content c")
                .build();
    }

    @Bean("contentItemProcessor")
    public ItemProcessor<Content, ElasticContent> contentItemProcessor() {
        return (content) -> {
            ElasticContent elasticContent = new ElasticContent();
            elasticContent.setId(content.getId());
            elasticContent.setCreated(content.getCreated().atOffset(ZoneOffset.UTC));
            elasticContent.setName(content.getName());
            elasticContent.setSlug(content.getSlug());
            elasticContent.setPhotoUrl(content.getPhotoUrl());
            elasticContent.setType(content.getType().name());
            elasticContent.setStartDate(content.getStartDate());
            elasticContent.setCategories(content.getCategories().stream().map(Category::getId).toList());

            return elasticContent;
        };
    }

    @Bean("contentItemWriter")
    public ItemWriter<ElasticContent> contentItemWriter(ElasticContentRepository repository) {
        return (elasticContents) -> repository.saveAll(elasticContents, RefreshPolicy.IMMEDIATE);
    }
}
