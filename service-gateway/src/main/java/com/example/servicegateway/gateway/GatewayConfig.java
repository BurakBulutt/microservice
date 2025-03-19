package com.example.servicegateway.gateway;

import org.springframework.cloud.gateway.route.RouteLocator;
import org.springframework.cloud.gateway.route.builder.RouteLocatorBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

@Component
@Profile("default")
public class GatewayConfig {

    @Bean
    public RouteLocator routeConfig(RouteLocatorBuilder builder) {
        return builder.routes()
                .route(predicateSpec -> predicateSpec
                        .path("/api/medias/**","/api/contents/**","/api/categories/**")
                        .filters(filterSpec -> filterSpec.rewritePath("/api/(?<segment>.*)", "/${segment}")
                                .addResponseHeader("X-Response-Time", LocalDateTime.now().toString()))
                        .uri("lb://SERVICE-MEDIA"))
                .route(predicateSpec -> predicateSpec
                        .path("/api/comments/**","/api/likes/**")
                        .filters(filterSpec -> filterSpec.rewritePath("/api/(?<segment>.*)", "/${segment}")
                                .addResponseHeader("X-Response-Time", LocalDateTime.now().toString()))
                        .uri("lb://SERVICE-REACTION"))
                .route(predicateSpec -> predicateSpec
                        .path("/api/users/**")
                        .filters(filterSpec -> filterSpec.rewritePath("/api/(?<segment>.*)", "/${segment}")
                                .addResponseHeader("X-Response-Time", LocalDateTime.now().toString()))
                        .uri("lb://SERVICE-USERS"))
                .build();
    }
}
