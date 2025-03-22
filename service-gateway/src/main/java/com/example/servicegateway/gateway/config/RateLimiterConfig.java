package com.example.servicegateway.gateway.config;


import org.springframework.cloud.gateway.filter.ratelimit.KeyResolver;
import org.springframework.cloud.gateway.filter.ratelimit.RedisRateLimiter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.server.reactive.ServerHttpRequest;
import reactor.core.publisher.Mono;

import java.util.Objects;


@Configuration
public class RateLimiterConfig {

    @Bean
    public RedisRateLimiter rateLimiter() {
        return new RedisRateLimiter(5,10,1);
    }

    @Bean
    public KeyResolver keyResolver() {
        return exchange -> {
            ServerHttpRequest request = exchange.getRequest();

            String forwardedIp = request.getHeaders().getFirst("X-Forwarded-For");
            if (forwardedIp != null && !forwardedIp.isEmpty()) {
                return Mono.just(forwardedIp.split(",")[0].trim());
            }

            return Mono.just(Objects.requireNonNull(request.getRemoteAddress())).map(addr -> addr.getAddress().getHostAddress());
        };
    }
}
