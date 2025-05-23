package com.example.servicegateway.gateway.config;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cloud.gateway.filter.ratelimit.KeyResolver;
import org.springframework.cloud.gateway.filter.ratelimit.RedisRateLimiter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.http.server.reactive.ServerHttpRequest;
import reactor.core.publisher.Mono;

import java.util.Objects;


@Configuration
@Profile("default")
public class RateLimiterConfig {
    private static final Logger log = LoggerFactory.getLogger(RateLimiterConfig.class);

    @Bean
    public RedisRateLimiter rateLimiter() {
        return new RedisRateLimiter(5,20,1);
    }

    @Bean
    public KeyResolver keyResolver() {
        return exchange -> Mono.just(
                exchange.getRequest()
                        .getRemoteAddress()
                        .getAddress()
                        .getHostAddress()
        );
    }
}
