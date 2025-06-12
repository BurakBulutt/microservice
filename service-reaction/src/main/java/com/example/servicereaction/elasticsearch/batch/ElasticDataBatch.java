package com.example.servicereaction.elasticsearch.batch;

import com.example.servicereaction.domain.comment.elasticsearch.model.ElasticComment;
import com.example.servicereaction.domain.comment.elasticsearch.repo.ElasticCommentRepository;
import com.example.servicereaction.domain.comment.model.Comment;
import com.example.servicereaction.elasticsearch.ElasticEntityMapper;
import jakarta.persistence.EntityManagerFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.job.builder.JobBuilder;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.batch.item.ItemReader;
import org.springframework.batch.item.ItemWriter;
import org.springframework.batch.item.database.builder.JpaPagingItemReaderBuilder;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.elasticsearch.core.RefreshPolicy;
import org.springframework.transaction.PlatformTransactionManager;

@Configuration
@RequiredArgsConstructor
public class ElasticDataBatch {
    private static final int CHUNK_SIZE = 10;

    private final JobRepository jobRepository;
    private final PlatformTransactionManager platformTransactionManager;

    @Bean("elasticDataJob")
    public Job elasticDataJob(@Qualifier("elasticCommentDataStep") Step step1) {
        return new JobBuilder("elasticDataImportJob",jobRepository)
                .start(step1)
                .build();
    }

    @Bean("elasticCommentDataStep")
    public Step elasticCategoryDataStep() {
        return new StepBuilder("elasticCommentDataStep", jobRepository)
                .<Comment, ElasticComment>chunk(CHUNK_SIZE, platformTransactionManager)
                .reader(commentItemReader(null))
                .processor(commentItemProcessor())
                .writer(commentItemWriter(null))
                .build();
    }

    @Bean("commentItemReader")
    public ItemReader<Comment> commentItemReader(EntityManagerFactory entityManagerFactory) {
        return new JpaPagingItemReaderBuilder<Comment>()
                .name("categoryItemReader")
                .entityManagerFactory(entityManagerFactory)
                .pageSize(CHUNK_SIZE)
                .queryString("select c from Comment c")
                .build();
    }

    @Bean("commentItemProcessor")
    public ItemProcessor<Comment, ElasticComment> commentItemProcessor() {
        return ElasticEntityMapper::toElasticComment;
    }

    @Bean("commentItemWriter")
    public ItemWriter<ElasticComment> commentItemWriter(ElasticCommentRepository repository) {
        return (elasticCategories) -> repository.saveAll(elasticCategories, RefreshPolicy.IMMEDIATE);
    }
}
