package com.example.serviceusers.elasticsearch.batch;

import lombok.RequiredArgsConstructor;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.job.builder.FlowBuilder;
import org.springframework.batch.core.job.builder.JobBuilder;
import org.springframework.batch.core.job.flow.Flow;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.task.SimpleAsyncTaskExecutor;

@Configuration
@RequiredArgsConstructor
public class ElasticDataBatch {
    private final JobRepository jobRepository;

    @Bean("elasticDataJob")
    public Job elasticDataJob(@Qualifier("elasticUserDataStep") Step step1) {
        
        return new JobBuilder("elasticDataImportJob",jobRepository)
                .start(step1)
                .build();
    }
}
