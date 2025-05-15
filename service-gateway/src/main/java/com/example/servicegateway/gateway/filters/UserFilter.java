package com.example.servicegateway.gateway.filters;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.context.annotation.Bean;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.security.Principal;

@Component
@Order(Ordered.LOWEST_PRECEDENCE - 1)
public class UserFilter implements GlobalFilter {
    private static final Logger logger = LoggerFactory.getLogger(UserFilter.class);

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        return exchange.getPrincipal()
                .map(Principal::getName)
                .defaultIfEmpty("ANONYMOUS")
                .flatMap(username -> {
                    ServerHttpRequest mutatedRequest = exchange.getRequest().mutate()
                            .header("X-User-Principal", username)
                            .build();
                    return chain.filter(exchange.mutate().request(mutatedRequest).build());
                });
    }
}
