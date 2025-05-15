package com.example.servicereaction.feign;

import feign.RequestInterceptor;
import io.micrometer.tracing.Tracer;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

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
                String traceId = tracer.currentSpan().context().traceId();
                requestTemplate.header("X-B3-TraceId", traceId);
                requestTemplate.header("X-B3-SpanId", tracer.currentSpan().context().spanId());
                requestTemplate.header("X-B3-Sampled", "1");
            }
        };
    }
}
