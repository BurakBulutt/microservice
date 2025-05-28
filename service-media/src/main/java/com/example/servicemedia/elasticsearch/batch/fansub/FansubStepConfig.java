package com.example.servicemedia.elasticsearch.batch.fansub;

import com.example.servicemedia.domain.fansub.elasticsearch.model.ElasticFansub;
import com.example.servicemedia.domain.fansub.elasticsearch.repo.ElasticFansubRepository;
import com.example.servicemedia.domain.fansub.model.Fansub;
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
public class FansubStepConfig {
    private static final int CHUNK_SIZE = 10;

    private final JobRepository jobRepository;
    private final PlatformTransactionManager platformTransactionManager;

    @Bean("elasticFansubDataStep")
    public Step elasticCategoryDataStep() {
        return new StepBuilder("elasticFansubDataStep", jobRepository)
                .<Fansub, ElasticFansub>chunk(CHUNK_SIZE, platformTransactionManager)
                .reader(fansubItemReader(null))
                .processor(fansubItemProcessor())
                .writer(fansubItemWriter(null))
                .build();
    }

    @Bean("fansubItemReader")
    public ItemReader<Fansub> fansubItemReader(EntityManagerFactory entityManagerFactory) {
        return new JpaPagingItemReaderBuilder<Fansub>()
                .name("fansubItemReader")
                .entityManagerFactory(entityManagerFactory)
                .pageSize(CHUNK_SIZE)
                .queryString("select f from Fansub f")
                .build();
    }

    @Bean("fansubItemProcessor")
    public ItemProcessor<Fansub, ElasticFansub> fansubItemProcessor() {
        return ( fansub) -> {
            ElasticFansub elasticFansub = new ElasticFansub();
            elasticFansub.setId(fansub.getId());
            elasticFansub.setCreated(fansub.getCreated().atOffset(ZoneOffset.UTC));
            elasticFansub.setName(fansub.getName());

            return elasticFansub;
        };
    }

    @Bean("fansubItemWriter")
    public ItemWriter<ElasticFansub> fansubItemWriter(ElasticFansubRepository repository) {
        return (elasticFansubs) -> repository.saveAll(elasticFansubs, RefreshPolicy.IMMEDIATE);
    }
}
