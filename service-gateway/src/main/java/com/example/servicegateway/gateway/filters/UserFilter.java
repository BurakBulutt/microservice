package com.example.servicegateway.gateway.filters;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.server.ServerWebExchange;
import org.springframework.web.server.WebFilter;
import org.springframework.web.server.WebFilterChain;
import reactor.core.publisher.Mono;

public class UserFilter implements WebFilter {

    private static final Logger log = LoggerFactory.getLogger(UserFilter.class);

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, WebFilterChain chain) {
        return exchange.getPrincipal()
                .doOnNext(principal -> {
                    String user = principal.getName() != null ? principal.getName() : "anonymous";
                    log.warn("User: {}", user);
                    exchange.getRequest().getHeaders().add("X-User-Id", user);
                })
                .then(chain.filter(exchange));
    }
}
