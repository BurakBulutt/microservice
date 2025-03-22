package com.example.servicegateway.gateway.filters;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.util.UUID;

import static com.example.servicegateway.gateway.constants.GatewayConstants.CORRELATION_ID_HEADER;

@Order(1)
@Component
public class RequestTraceFilter implements GlobalFilter {
    private static final Logger log = LoggerFactory.getLogger(RequestTraceFilter.class);

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        HttpHeaders headers = exchange.getRequest().getHeaders();
        String correlationId = headers.getFirst(CORRELATION_ID_HEADER);

        if (correlationId == null) {
            correlationId = generateCorrelationId();
            exchange = exchange.mutate()
                    .request(exchange.getRequest().mutate().header(CORRELATION_ID_HEADER, correlationId).build())
                    .build();
            log.debug("Correlation id not found in request, generated new one: {}", correlationId);
        }else {
            log.debug("Correlation id found in request: {}", correlationId);
        }
        return chain.filter(exchange);
    }


    private String generateCorrelationId() {
        return UUID.randomUUID().toString();
    }
}
