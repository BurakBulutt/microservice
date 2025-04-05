package com.example.servicegateway.gateway.filters;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.http.server.reactive.ServerHttpResponse;
import reactor.core.publisher.Mono;

import static com.example.servicegateway.gateway.constants.GatewayConstants.CORRELATION_ID_HEADER;

public class ResponseTraceFilter {
    private static final Logger log = LoggerFactory.getLogger(ResponseTraceFilter.class);

    public GlobalFilter globalFilter(){
        return (exchange, chain) -> {
            return chain.filter(exchange).then(Mono.fromRunnable(() -> {
                ServerHttpResponse response = exchange.getResponse();
                String correlationId = exchange.getRequest().getHeaders().getFirst(CORRELATION_ID_HEADER);
                if (!response.getHeaders().containsKey(CORRELATION_ID_HEADER)) {
                    response.getHeaders().add(CORRELATION_ID_HEADER,correlationId);
                }
            }));
        };
    }
}
