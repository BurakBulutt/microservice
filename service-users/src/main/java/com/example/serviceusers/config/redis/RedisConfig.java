package com.example.serviceusers.config.redis;

import com.example.serviceusers.config.jackson.deserializers.CustomPageDeserializer;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.module.SimpleModule;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.keycloak.representations.idm.UserRepresentation;
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

@Configuration
public class RedisConfig {
    @Value("${spring.application.name}")
    private String prefix;

    @Bean
    public RedisCacheManager cacheManager(RedisConnectionFactory connectionFactory) {
        RedisCacheWriter cacheWriter = RedisCacheWriter.nonLockingRedisCacheWriter(connectionFactory);
        return RedisCacheManager.builder(connectionFactory)
                .cacheWriter(cacheWriter)
                .cacheDefaults(defaultCacheConfig())
                .withCacheConfiguration("userCache", cacheConfig(UserRepresentation.class, UserRepresentation.class))
                .withCacheConfiguration("userPageCache", cacheConfig(UserRepresentation.class, Page.class))
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

    private RedisCacheConfiguration cacheConfig(Class<?> clazz, Class<?> serializeClazz) {
        ObjectMapper mapper = serializerMapper(clazz);

        return defaultCacheConfig()
                .entryTtl(Duration.ofSeconds(30))
                .enableTimeToIdle()
                .serializeValuesWith(RedisSerializationContext.SerializationPair.fromSerializer(new Jackson2JsonRedisSerializer<>(mapper, serializeClazz)));
    }
}
