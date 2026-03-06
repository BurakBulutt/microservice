package com.example.serviceusers.elasticsearch.batch.user;

import com.example.serviceusers.domain.user.elasticsearch.model.ElasticUser;
import com.example.serviceusers.domain.user.elasticsearch.repo.ElasticUserRepository;
import com.example.serviceusers.domain.user.model.User;
import com.example.serviceusers.elasticsearch.ElasticEntityMapper;
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
public class UserStepConfig {
    private static final int CHUNK_SIZE = 10;

    private final JobRepository jobRepository;
    private final PlatformTransactionManager platformTransactionManager;

    @Bean("elasticUserDataStep")
    public Step elasticUserDataStep() {
        return new StepBuilder("elasticUserDataStep", jobRepository)
                .<User, ElasticUser>chunk(CHUNK_SIZE, platformTransactionManager)
                .reader(UserItemReader(null))
                .processor(UserItemProcessor())
                .writer(UserItemWriter(null))
                .build();
    }

    @Bean("UserItemReader")
    public ItemReader<User> UserItemReader(EntityManagerFactory entityManagerFactory) {
        return new JpaPagingItemReaderBuilder<User>()
                .name("UserItemReader")
                .entityManagerFactory(entityManagerFactory)
                .pageSize(CHUNK_SIZE)
                .queryString("select c from User c")
                .build();
    }

    @Bean("UserItemProcessor")
    public ItemProcessor<User, ElasticUser> UserItemProcessor() {
        return ElasticEntityMapper::toElasticUser;
    }

    @Bean("UserItemWriter")
    public ItemWriter<ElasticUser> UserItemWriter(ElasticUserRepository repository) {
        return (elasticCategories) -> repository.saveAll(elasticCategories, RefreshPolicy.NONE);
    }
}
