package com.example.servicegateway.gateway.config;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;


@Configuration
@Profile("prod")
public class RateLimiterProdConfig {
    private static final Logger log = LoggerFactory.getLogger(RateLimiterProdConfig.class);

/*    @Bean
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

 */
}
