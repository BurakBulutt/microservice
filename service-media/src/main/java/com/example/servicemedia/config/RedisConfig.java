package com.example.servicemedia.config;

import com.example.servicemedia.category.dto.CategoryDto;
import com.example.servicemedia.config.jackson.*;
import com.example.servicemedia.content.dto.ContentDto;
import com.example.servicemedia.media.dto.MediaDto;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.jsontype.impl.LaissezFaireSubTypeValidator;
import com.fasterxml.jackson.databind.module.SimpleModule;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.domain.Page;
import org.springframework.data.redis.cache.RedisCacheConfiguration;
import org.springframework.data.redis.cache.RedisCacheManager;
import org.springframework.data.redis.cache.RedisCacheWriter;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.serializer.GenericJackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.Jackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.RedisSerializationContext;
import org.springframework.data.redis.serializer.StringRedisSerializer;

import java.time.Duration;
import java.util.List;

@Configuration
@RequiredArgsConstructor
public class RedisConfig {
    @Value("${spring.application.name}")
    private String prefix;

    @Bean
    public RedisCacheManager cacheManager(RedisConnectionFactory connectionFactory) {
        RedisCacheWriter cacheWriter = RedisCacheWriter.nonLockingRedisCacheWriter(connectionFactory);
        return RedisCacheManager.builder(connectionFactory)
                .cacheWriter(cacheWriter)
                .cacheDefaults(defaultCacheConfig())
                .withCacheConfiguration("contentCache", contentCacheConfig())
                .withCacheConfiguration("contentPageCache", contentPageCacheConfig())
                .withCacheConfiguration("mediaCache", mediaCacheConfig())
                .withCacheConfiguration("mediaSourceListCache", mediaSourceListCacheConfig())
                .withCacheConfiguration("mediaPageCache", mediaPageCacheConfig())
                .withCacheConfiguration("categoryCache", categoryCacheConfig())
                .withCacheConfiguration("categoryPageCache", categoryPageCacheConfig())
                .disableCreateOnMissingCache()
                .enableStatistics()
                .build();
    }

    /*private ObjectMapper genericSerializerMapper() {
        ObjectMapper mapper = new ObjectMapper();

        mapper.activateDefaultTyping(
                LaissezFaireSubTypeValidator.instance,
                ObjectMapper.DefaultTyping.NON_FINAL,
                JsonTypeInfo.As.PROPERTY
        );

        mapper.registerModule(new JavaTimeModule());
        mapper.registerModule(new PageModule());

        return mapper;
    }

     */

    private ObjectMapper pageSerializerMapper(JsonDeserializer<? extends Page<?>> deserializer) {
        ObjectMapper mapper = new ObjectMapper();
        mapper.deactivateDefaultTyping();

        SimpleModule module = new SimpleModule();
        module.addDeserializer(Page.class, deserializer);

        mapper.registerModule(new JavaTimeModule());
        mapper.registerModule(module);

        return mapper;
    }

    private RedisCacheConfiguration defaultCacheConfig() {
        return RedisCacheConfiguration.defaultCacheConfig()
                .entryTtl(Duration.ofMinutes(1))
                .disableCachingNullValues()
                .serializeKeysWith(RedisSerializationContext.SerializationPair.fromSerializer(new StringRedisSerializer()))
                //.serializeValuesWith(RedisSerializationContext.SerializationPair.fromSerializer(new GenericJackson2JsonRedisSerializer(genericSerializerMapper())))
                .prefixCacheNameWith(prefix + "::");
    }


    private RedisCacheConfiguration mediaSourceListCacheConfig() {
        ObjectMapper mapper = new ObjectMapper();
        mapper.deactivateDefaultTyping();

        SimpleModule module = new SimpleModule();
        module.addDeserializer(List.class, new MediaSourceListDeserializer());

        mapper.registerModule(new JavaTimeModule());
        mapper.registerModule(module);

        return defaultCacheConfig()
                .entryTtl(Duration.ofSeconds(60))
                .serializeValuesWith(RedisSerializationContext.SerializationPair.fromSerializer(new Jackson2JsonRedisSerializer<>(mapper,List.class)))
                .enableTimeToIdle();
    }

    private RedisCacheConfiguration mediaPageCacheConfig() {
        ObjectMapper mapper = pageSerializerMapper(new MediaPageDeserializer());

        return defaultCacheConfig()
                .entryTtl(Duration.ofSeconds(60))
                .serializeValuesWith(RedisSerializationContext.SerializationPair.fromSerializer(new Jackson2JsonRedisSerializer<>(mapper,Page.class)))
                .enableTimeToIdle();
    }

    private RedisCacheConfiguration mediaCacheConfig() {
        return defaultCacheConfig()
                .entryTtl(Duration.ofSeconds(60))
                .serializeValuesWith(RedisSerializationContext.SerializationPair.fromSerializer(new Jackson2JsonRedisSerializer<>(MediaDto.class)))
                .enableTimeToIdle();
    }

    private RedisCacheConfiguration contentPageCacheConfig() {
        ObjectMapper mapper = pageSerializerMapper(new ContentPageDeserializer());

        return defaultCacheConfig()
                .entryTtl(Duration.ofSeconds(60))
                .serializeValuesWith(RedisSerializationContext.SerializationPair.fromSerializer(new Jackson2JsonRedisSerializer<>(mapper,Page.class)))
                .enableTimeToIdle();
    }

    private RedisCacheConfiguration contentCacheConfig() {
        return defaultCacheConfig()
                .entryTtl(Duration.ofSeconds(60))
                .serializeValuesWith(RedisSerializationContext.SerializationPair.fromSerializer(new Jackson2JsonRedisSerializer<>(ContentDto.class)))
                .enableTimeToIdle();
    }

    private RedisCacheConfiguration categoryPageCacheConfig() {
        ObjectMapper mapper = pageSerializerMapper(new CategoryPageDeserializer());

        return defaultCacheConfig()
                .entryTtl(Duration.ofSeconds(60))
                .serializeValuesWith(RedisSerializationContext.SerializationPair.fromSerializer(new Jackson2JsonRedisSerializer<>(mapper,Page.class)))
                .enableTimeToIdle();
    }

    private RedisCacheConfiguration categoryCacheConfig() {
        return defaultCacheConfig()
                .entryTtl(Duration.ofSeconds(60))
                .serializeValuesWith(RedisSerializationContext.SerializationPair.fromSerializer(new Jackson2JsonRedisSerializer<>(CategoryDto.class)))
                .enableTimeToIdle();
    }
}
