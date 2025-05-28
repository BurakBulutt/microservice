package com.example.servicemedia.elasticsearch.batch.media;


import com.example.servicemedia.domain.media.elasticsearch.model.ElasticMedia;
import com.example.servicemedia.domain.media.elasticsearch.repo.ElasticMediaRepository;
import com.example.servicemedia.domain.media.model.Media;
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
public class MediaStepConfig {
    private static final int CHUNK_SIZE = 10;

    private final JobRepository jobRepository;
    private final PlatformTransactionManager platformTransactionManager;

    @Bean("elasticMediaDataStep")
    public Step elasticMediaDataStep() {
        return new StepBuilder("elasticMediaDataStep", jobRepository)
                .<Media, ElasticMedia>chunk(CHUNK_SIZE, platformTransactionManager)
                .reader(mediaItemReader(null))
                .processor(mediaItemProcessor())
                .writer(mediaItemWriter(null))
                .build();
    }

    @Bean("mediaItemReader")
    public ItemReader<Media> mediaItemReader(EntityManagerFactory entityManagerFactory) {
        return new JpaPagingItemReaderBuilder<Media>()
                .name("mediaItemReader")
                .entityManagerFactory(entityManagerFactory)
                .pageSize(CHUNK_SIZE)
                .queryString("select m from Media m")
                .build();
    }

    @Bean("mediaItemProcessor")
    public ItemProcessor<Media, ElasticMedia> mediaItemProcessor() {
        return (media) -> {
            ElasticMedia elasticMedia = new ElasticMedia();
            elasticMedia.setId(media.getId());
            elasticMedia.setCreated(media.getCreated().atOffset(ZoneOffset.UTC));
            elasticMedia.setName(media.getName());
            elasticMedia.setSlug(media.getSlug());
            elasticMedia.setContentId(media.getContent().getId());
            elasticMedia.setPublishDate(media.getPublishDate());

            return elasticMedia;
        };
    }

    @Bean("mediaItemWriter")
    public ItemWriter<ElasticMedia> mediaItemWriter(ElasticMediaRepository repository) {
        return (elasticContents) -> repository.saveAll(elasticContents, RefreshPolicy.IMMEDIATE);
    }
}
