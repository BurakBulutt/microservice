package com.example.servicegateway.gateway.filters;

import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

@Order(Ordered.LOWEST_PRECEDENCE - 1)
public class UserFilter implements GlobalFilter {

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        return exchange.getPrincipal()
                .doOnNext(principal -> {
                    String user = principal.getName() != null ? principal.getName() : "anonymous";
                    exchange.getRequest().mutate().headers(httpHeaders ->
                            httpHeaders.add("X-User", user)
                    );
                })
                .then(chain.filter(exchange));
    }
}
