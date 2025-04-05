package com.example.servicegateway.gateway.config;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cloud.gateway.filter.ratelimit.KeyResolver;
import org.springframework.cloud.gateway.filter.ratelimit.RedisRateLimiter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.server.reactive.ServerHttpRequest;
import reactor.core.publisher.Mono;

import java.util.Objects;


@Configuration
public class RateLimiterConfig {
    private static final Logger log = LoggerFactory.getLogger(RateLimiterConfig.class);

    @Bean
    public RedisRateLimiter rateLimiter() {
        return new RedisRateLimiter(5,10,1);
    }

    @Bean
    public KeyResolver keyResolver() {
        return exchange -> {
            ServerHttpRequest request = exchange.getRequest();

            final String forwardedIp = request.getHeaders().getFirst("X-Forwarded-For");

            if (forwardedIp != null && !forwardedIp.isEmpty()) {
                log.info("X-Forwarded-For header found: {}", forwardedIp);
                return Mono.just(forwardedIp.split(",")[0].trim());
            }

            log.info("X-Forwarded-For header not found. Key will resolving with host addr...");
            return Mono.just(Objects.requireNonNull(request.getRemoteAddress())).map(addr -> addr.getAddress().getHostAddress());
        };
    }
}
