package com.example.servicegateway.gateway.filters;

import org.springframework.web.server.ServerWebExchange;
import org.springframework.web.server.WebFilter;
import org.springframework.web.server.WebFilterChain;
import reactor.core.publisher.Mono;

public class UserFilter implements WebFilter {

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, WebFilterChain chain) {
        return exchange.getPrincipal()
                .doOnNext(principal -> {
                    String user = principal.getName() != null ? principal.getName() : "anonymous";
                    exchange.getRequest().getHeaders().add("X-User-Id", user);
                })
                .then(chain.filter(exchange));
    }
}
