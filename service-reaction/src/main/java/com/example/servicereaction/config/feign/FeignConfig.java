package com.example.servicereaction.config.feign;

import feign.RequestInterceptor;
import io.micrometer.tracing.TraceContext;
import io.micrometer.tracing.Tracer;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import java.util.Objects;

@Configuration
@RequiredArgsConstructor
public class FeignConfig {
    private final Tracer tracer;

    @Bean
    public RequestInterceptor requestInterceptor() {
        return requestTemplate -> {
            ServletRequestAttributes requestAttributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();

            assert requestAttributes != null;
            HttpServletRequest httpRequest = requestAttributes.getRequest();

            requestTemplate.header("X-User-Principal", httpRequest.getHeader("X-User-Principal"));

            if (tracer != null && tracer.currentSpan() != null) {
                TraceContext context = Objects.requireNonNull(tracer.currentSpan()).context();
                final String traceId = context.traceId();
                final String spanId = context.spanId();

                requestTemplate.header("X-B3-TraceId", traceId);
                requestTemplate.header("X-B3-SpanId", spanId);
                requestTemplate.header("X-B3-Sampled", "1");
            }
        };
    }
}
