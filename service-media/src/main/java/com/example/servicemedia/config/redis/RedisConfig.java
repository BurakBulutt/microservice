package com.example.servicemedia.config.redis;

import com.example.servicemedia.config.jackson.modules.PageModule;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.jsontype.impl.LaissezFaireSubTypeValidator;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.cache.RedisCacheConfiguration;
import org.springframework.data.redis.cache.RedisCacheManager;
import org.springframework.data.redis.cache.RedisCacheWriter;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.serializer.GenericJackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.RedisSerializationContext;
import org.springframework.data.redis.serializer.StringRedisSerializer;

import java.time.Duration;
import java.util.Set;

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
                .initialCacheNames(Set.of("contentCache", "contentPageCache", "mediaCache", "mediaPageCache", "categoryCache", "categoryPageCache","fansubCache","fansubPageCache"))
                //.withCacheConfiguration("contentPageCache", cacheConfig(ContentDto.class,Page.class))
                .disableCreateOnMissingCache()
                .enableStatistics()
                .build();
    }

    private ObjectMapper genericSerializerMapper() {
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

    /*private ObjectMapper serializerMapper(Class<?> clazz) {
        ObjectMapper mapper = new ObjectMapper();
        mapper.deactivateDefaultTyping();

        SimpleModule module = new SimpleModule();
        module.addDeserializer(Page.class, new CustomPageDeserializer<>(clazz));

        mapper.registerModule(new JavaTimeModule());
        mapper.registerModule(module);

        return mapper;
    }

     */

    private RedisCacheConfiguration defaultCacheConfig() {
        return RedisCacheConfiguration.defaultCacheConfig()
                .entryTtl(Duration.ofMinutes(1))
                .enableTimeToIdle()
                .disableCachingNullValues()
                .serializeKeysWith(RedisSerializationContext.SerializationPair.fromSerializer(new StringRedisSerializer()))
                .serializeValuesWith(RedisSerializationContext.SerializationPair.fromSerializer(new GenericJackson2JsonRedisSerializer(genericSerializerMapper())))
                .prefixCacheNameWith(prefix + "::");
    }
}
