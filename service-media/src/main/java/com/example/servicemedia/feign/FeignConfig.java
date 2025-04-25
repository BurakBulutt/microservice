package com.example.servicemedia.feign;

import feign.RequestInterceptor;
import io.micrometer.tracing.Tracer;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@RequiredArgsConstructor
public class FeignConfig {
    private final Tracer tracer;

    @Bean
    public RequestInterceptor requestInterceptor() {
        return requestTemplate -> {
            if (tracer != null && tracer.currentSpan() != null) {
                String traceId = tracer.currentSpan().context().traceId();
                requestTemplate.header("X-B3-TraceId", traceId);
                requestTemplate.header("X-B3-SpanId", tracer.currentSpan().context().spanId());
                requestTemplate.header("X-B3-Sampled", "1");
            }
        };
    }
}
