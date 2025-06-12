package com.example.servicegateway.gateway.filters;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.security.Principal;

@Deprecated
public class UserFilter implements GlobalFilter {
    private static final Logger log = LoggerFactory.getLogger(UserFilter.class);

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        log.info("User principal adding request header...");
        return exchange.getPrincipal()
                .map(Principal::getName)
                .defaultIfEmpty("anonymousUser")
                .flatMap(username -> {
                    ServerHttpRequest mutatedRequest = exchange.getRequest().mutate()
                            .header("X-User-Principal", username)
                            .build();
                    return chain.filter(exchange.mutate().request(mutatedRequest).build());
                });
    }
}
