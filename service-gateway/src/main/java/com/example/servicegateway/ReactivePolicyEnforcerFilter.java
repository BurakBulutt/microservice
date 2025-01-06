package com.example.servicegateway;

import org.keycloak.adapters.authorization.spi.ConfigurationResolver;
import org.keycloak.adapters.authorization.spi.HttpRequest;
import org.keycloak.representations.adapters.config.PolicyEnforcerConfig;
import org.keycloak.util.JsonSerialization;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import org.springframework.web.server.WebFilter;
import org.springframework.web.server.WebFilterChain;
import reactor.core.publisher.Mono;

import java.io.IOException;

@Component
public class ReactivePolicyEnforcerFilter implements WebFilter {
    private static final String POLICY_JSON = "/policy-enforcer.json";

    private final ConfigurationResolver configurationResolver;

    public ReactivePolicyEnforcerFilter() {
        this.configurationResolver = new ConfigurationResolver() {
            @Override
            public PolicyEnforcerConfig resolve(HttpRequest httpRequest) {
                try {
                    return JsonSerialization.readValue(getClass().getResourceAsStream(POLICY_JSON),PolicyEnforcerConfig.class);
                } catch (IOException e) {
                    throw new RuntimeException("Failed to read policy", e);
                }
            }
        };
    }

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, WebFilterChain chain) {
        PolicyEnforcerConfig policyEnforcerConfig = configurationResolver.resolve((HttpRequest)exchange.getRequest());
        boolean isAuthorize = checkAuthorize(exchange,policyEnforcerConfig);

        if(!isAuthorize){
            exchange.getResponse().setStatusCode(HttpStatus.FORBIDDEN);
            return exchange.getResponse().setComplete();
        }

        return chain.filter(exchange);
    }

    private boolean checkAuthorize(ServerWebExchange exchange, PolicyEnforcerConfig policyEnforcerConfig) {
        return true;
    }
}
