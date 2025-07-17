package com.example.servicegateway.gateway;

import io.github.resilience4j.circuitbreaker.CircuitBreakerConfig;
import io.github.resilience4j.timelimiter.TimeLimiterConfig;

import org.springframework.cloud.circuitbreaker.resilience4j.ReactiveResilience4JCircuitBreakerFactory;
import org.springframework.cloud.circuitbreaker.resilience4j.Resilience4JConfigBuilder;
import org.springframework.cloud.client.circuitbreaker.Customizer;
import org.springframework.cloud.gateway.filter.ratelimit.KeyResolver;
import org.springframework.cloud.gateway.filter.ratelimit.RedisRateLimiter;
import org.springframework.cloud.gateway.route.RouteLocator;
import org.springframework.cloud.gateway.route.builder.RouteLocatorBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.http.HttpMethod;

import java.time.Duration;
import java.time.LocalDateTime;

@Configuration
@Profile("default")
public class GatewayConfig {
    private final RedisRateLimiter redisRateLimiter;
    private final KeyResolver keyResolver;

    public GatewayConfig(RedisRateLimiter redisRateLimiter, KeyResolver keyResolver) {
        this.redisRateLimiter = redisRateLimiter;
        this.keyResolver = keyResolver;
    }

    @Bean
    public RouteLocator routeConfig(RouteLocatorBuilder builder) {
        return builder.routes()
                .route(predicateSpec -> predicateSpec
                        .path("/api/fansubs/**","/api/media-entity-log/**","/api/xml/**","/api/medias/**", "/api/contents/**", "/api/categories/**")
                        .filters(filterSpec -> filterSpec.rewritePath("/api/(?<segment>.*)", "/${segment}")
                                .addResponseHeader("X-Response-Time", LocalDateTime.now().toString())
                                .circuitBreaker(config -> config
                                        .setName("mediaCircuitBreaker"))
                                .retry(config -> config
                                        .setMethods(HttpMethod.GET)
                                        .setRetries(3)
                                        .setBackoff(Duration.ofMillis(200), Duration.ofSeconds(2), 2, true))
                                .requestRateLimiter(config -> config
                                        .setKeyResolver(keyResolver)
                                        .setRateLimiter(redisRateLimiter))
                        )
                        .uri("lb://SERVICE-MEDIA"))
                .route(predicateSpec -> predicateSpec
                        .path("/api/reaction-entity-log/**","/api/comments/**", "/api/likes/**")
                        .filters(filterSpec -> filterSpec.rewritePath("/api/(?<segment>.*)", "/${segment}")
                                .addResponseHeader("X-Response-Time", LocalDateTime.now().toString())
                                .circuitBreaker(config -> config
                                        .setName("reactionCircuitBreaker"))
                                .retry(config -> config
                                        .setMethods(HttpMethod.GET)
                                        .setRetries(3)
                                        .setBackoff(Duration.ofMillis(200), Duration.ofSeconds(2), 2, true))
                                .requestRateLimiter(config -> config
                                        .setKeyResolver(keyResolver)
                                        .setRateLimiter(redisRateLimiter))

                        )
                        .uri("lb://SERVICE-REACTION"))
                .route(predicateSpec -> predicateSpec
                        .path("/api/users/**")
                        .filters(filterSpec -> filterSpec.rewritePath("/api/(?<segment>.*)", "/${segment}")
                                .addResponseHeader("X-Response-Time", LocalDateTime.now().toString())
                                .circuitBreaker(config -> config
                                        .setName("usersCircuitBreaker"))
                                .retry(config -> config
                                        .setMethods(HttpMethod.GET)
                                        .setRetries(3)
                                        .setBackoff(Duration.ofMillis(200), Duration.ofSeconds(2), 2, true))
                                .requestRateLimiter(config -> config
                                        .setKeyResolver(keyResolver)
                                        .setRateLimiter(redisRateLimiter))
                        )
                        .uri("lb://SERVICE-USERS"))
                .build();
    }

    @Bean
    public Customizer<ReactiveResilience4JCircuitBreakerFactory> defaulCircuitBreakerCustomizer() {
        return factory -> factory.configureDefault(id -> new Resilience4JConfigBuilder(id)
                .circuitBreakerConfig(CircuitBreakerConfig.ofDefaults())
                .timeLimiterConfig(TimeLimiterConfig.custom()
                        .timeoutDuration(Duration.ofSeconds(10L))
                        .cancelRunningFuture(Boolean.TRUE)
                        .build())
                .build());
    }
}
