package com.example.servicemedia.elasticsearch.batch;

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
    public Job elasticDataJob(@Qualifier("categoryAndFansubAsyncFlow") Flow flow1, 
                              @Qualifier("contentAndMediaFlow") Flow flow2, 
                              @Qualifier("elasticXmlDefinitionDataStep") Step step1) {
        
        return new JobBuilder("elasticDataImportJob",jobRepository)
                .start(flow1)
                .next(flow2)
                .next(step1)
                .end()
                .build();
    }

    @Bean("categoryAndFansubAsyncFlow")
    public Flow categoryAndFansubAsyncFlow(@Qualifier("elasticCategoryDataStep") Step step1,
                                           @Qualifier("elasticFansubDataStep") Step step2) {
        return new FlowBuilder<Flow>("categoryAndFansubAsyncFlow")
                .split(new SimpleAsyncTaskExecutor())
                .add(
                        new FlowBuilder<Flow>("categoryStepFlow")
                                .start(step1)
                                .build(),
                        new FlowBuilder<Flow>("fansubStepFlow")
                                .start(step2)
                                .build()
                )
                .build();
    }

    @Bean("contentAndMediaFlow")
    public Flow contentAndMediaFlow(@Qualifier("elasticContentDataStep") Step step1,
                                    @Qualifier("elasticMediaDataStep") Step step2) {
        
        return new FlowBuilder<Flow>("contentAndMediaFlow")
                .start(step1)
                .next(step2)
                .build();
    }
}
