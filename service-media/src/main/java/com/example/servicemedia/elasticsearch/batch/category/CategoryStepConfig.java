package com.example.servicemedia.elasticsearch.batch.category;

import com.example.servicemedia.domain.category.elasticsearch.model.ElasticCategory;
import com.example.servicemedia.domain.category.elasticsearch.repo.ElasticCategoryRepository;
import com.example.servicemedia.domain.category.model.Category;
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
public class CategoryStepConfig {
    private static final int CHUNK_SIZE = 10;

    private final JobRepository jobRepository;
    private final PlatformTransactionManager platformTransactionManager;

    @Bean("elasticCategoryDataStep")
    public Step elasticCategoryDataStep() {
        return new StepBuilder("elasticCategoryDataStep", jobRepository)
                .<Category, ElasticCategory>chunk(CHUNK_SIZE, platformTransactionManager)
                .reader(categoryItemReader(null))
                .processor(categoryItemProcessor())
                .writer(categoryItemWriter(null))
                .build();
    }

    @Bean("categoryItemReader")
    public ItemReader<Category> categoryItemReader(EntityManagerFactory entityManagerFactory) {
        return new JpaPagingItemReaderBuilder<Category>()
                .name("categoryItemReader")
                .entityManagerFactory(entityManagerFactory)
                .pageSize(CHUNK_SIZE)
                .queryString("select c from Category c")
                .build();
    }

    @Bean("categoryItemProcessor")
    public ItemProcessor<Category, ElasticCategory> categoryItemProcessor() {
        return ( category) -> {
            ElasticCategory elasticCategory = new ElasticCategory();
            elasticCategory.setId(category.getId());
            elasticCategory.setCreated(category.getCreated().atOffset(ZoneOffset.UTC));
            elasticCategory.setName(category.getName());
            elasticCategory.setSlug(category.getSlug());

            return elasticCategory;
        };
    }

    @Bean("categoryItemWriter")
    public ItemWriter<ElasticCategory> categoryItemWriter(ElasticCategoryRepository repository) {
        return (elasticCategories) -> repository.saveAll(elasticCategories, RefreshPolicy.IMMEDIATE);
    }
}
