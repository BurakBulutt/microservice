package com.example.servicemedia.config;

import com.example.servicemedia.category.dto.CategoryDto;
import com.example.servicemedia.config.jackson.*;
import com.example.servicemedia.content.dto.ContentDto;
import com.example.servicemedia.media.dto.MediaDto;
import com.example.servicemedia.media.dto.MediaSourceDto;
import com.fasterxml.jackson.databind.ObjectMapper;
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
                .withCacheConfiguration("contentCache", dtoCacheConfig(ContentDto.class))
                .withCacheConfiguration("contentPageCache", pageCacheConfig(ContentDto.class))
                .withCacheConfiguration("mediaCache", dtoCacheConfig(MediaDto.class))
                .withCacheConfiguration("mediaSourceListCache", listCacheConfig(MediaSourceDto.class))
                .withCacheConfiguration("mediaPageCache", pageCacheConfig(MediaDto.class))
                .withCacheConfiguration("categoryCache", dtoCacheConfig(CategoryDto.class))
                .withCacheConfiguration("categoryPageCache", pageCacheConfig(CategoryDto.class))
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

    private ObjectMapper serializerMapper(Class<?> clazz) {
        ObjectMapper mapper = new ObjectMapper();
        mapper.deactivateDefaultTyping();

        SimpleModule module = new SimpleModule();
        module.addDeserializer(Page.class, new CustomPageDeserializer<>(clazz));
        module.addDeserializer(List.class,new CustomListDeserializer<>(clazz));

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

    private RedisCacheConfiguration dtoCacheConfig(Class<?> clazz) {
        return defaultCacheConfig()
                .entryTtl(Duration.ofSeconds(30))
                .serializeValuesWith(RedisSerializationContext.SerializationPair.fromSerializer(new Jackson2JsonRedisSerializer<>(clazz)));
    }

    private RedisCacheConfiguration pageCacheConfig(Class<?> clazz) {
        ObjectMapper mapper = serializerMapper(clazz);

        return defaultCacheConfig()
                .entryTtl(Duration.ofSeconds(30))
                .serializeValuesWith(RedisSerializationContext.SerializationPair.fromSerializer(new Jackson2JsonRedisSerializer<>(mapper,Page.class)));
    }

    private RedisCacheConfiguration listCacheConfig(Class<?> clazz) {
        ObjectMapper mapper = serializerMapper(clazz);

        return defaultCacheConfig()
                .entryTtl(Duration.ofSeconds(30))
                .serializeValuesWith(RedisSerializationContext.SerializationPair.fromSerializer(new Jackson2JsonRedisSerializer<>(mapper,List.class)));
    }
}
